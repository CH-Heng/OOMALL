package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.dao.PaymentDao;
import cn.edu.xmu.oomall.payment.dao.ReconciliationDao;
import cn.edu.xmu.oomall.payment.microservice.AlipayService;
import cn.edu.xmu.oomall.payment.microservice.WechatPayService;
import cn.edu.xmu.oomall.payment.microservice.util.WarpRetObject;
import cn.edu.xmu.oomall.payment.microservice.util.WeChatPayReturnObject;
import cn.edu.xmu.oomall.payment.microservice.vo.DownloadUrlQueryRetVo;
import cn.edu.xmu.oomall.payment.microservice.vo.WeChatPayFundFlowBillRetVo;
import cn.edu.xmu.oomall.payment.model.bo.ErrorPayment;
import cn.edu.xmu.oomall.payment.model.bo.Payment;
import cn.edu.xmu.oomall.payment.model.bo.Refund;
import cn.edu.xmu.oomall.payment.model.bo.flowbill.AliPayFlowBillItem;
import cn.edu.xmu.oomall.payment.model.bo.flowbill.WeChatFlowBillItem;
import cn.edu.xmu.oomall.payment.model.po.ErrorPaymentPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPo;
import cn.edu.xmu.oomall.payment.model.po.RefundPo;
import cn.edu.xmu.oomall.payment.model.vo.ErrorPaymentRetVo;
import cn.edu.xmu.oomall.payment.model.vo.ErrorPaymentUpdateVo;
import cn.edu.xmu.oomall.payment.model.vo.ReconciliationRetVo;
import cn.edu.xmu.oomall.payment.util.FileCommon;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 10:55
 **/
@Service
public class ReconciliationService {
    @Autowired
    private ReconciliationDao reconciliationDao;
    @Autowired
    private PaymentDao paymentDao;
    @Resource
    private AlipayService alipayService;
    @Resource
    private WechatPayService wechatPayService;

    private final static Long WECHAT_PATTERN_ID = 1L;
    private final static Long ALIPAY_PATTERN_ID = 2L;

    private final static String path="src/main/resources/fundflowbills/";


    /**
     * 平台管理员查询错帐信息
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getErrorPayment(String documentId, Byte state, LocalDateTime beginTime,
                                        LocalDateTime endTime, Integer page, Integer pageSize){
        ReturnObject returnObject=reconciliationDao.getErrorPayment(documentId,state,beginTime,endTime,page,pageSize);
        return returnObject;
    }

    /**
     * 平台管理员查询错帐信息详情
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getErrorPaymentById(Long id){
        ReturnObject objSelect=reconciliationDao.getErrorPaymentById(id);
        if(!objSelect.getCode().equals(ReturnNo.OK)){
            return objSelect;
        }
        ErrorPaymentPo errorPaymentPo=(ErrorPaymentPo)objSelect.getData();
        ErrorPaymentRetVo errorPaymentRetVo = cloneVo(errorPaymentPo, ErrorPaymentRetVo.class);
        errorPaymentRetVo.updateByPo(errorPaymentPo);
        return new ReturnObject<>(errorPaymentRetVo);
    }

    /**
     * 平台管理员修改错帐信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateErrorPayment(Long id,ErrorPaymentUpdateVo errorPaymentUpdateVo,Long loginUser,String loginName){
        ReturnObject objSelect=reconciliationDao.getErrorPaymentById(id);
        if(!objSelect.getCode().equals(ReturnNo.OK)){
            return objSelect;
        }
        ErrorPaymentPo errorPaymentPo=(ErrorPaymentPo)objSelect.getData();
        if(errorPaymentPo.getState().equals(ErrorPayment.State.SOLVED.getCode())){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        if(!errorPaymentUpdateVo.getDescr().isEmpty()){
            errorPaymentPo.setDescr(errorPaymentUpdateVo.getDescr());
        }
        if(errorPaymentUpdateVo.getState()!=null){
            errorPaymentPo.setState(errorPaymentUpdateVo.getState());
        }
        Common.setPoModifiedFields(errorPaymentPo,loginUser,loginName);
        ReturnObject objUpdate=reconciliationDao.updateErrorPayment(errorPaymentPo);
        if(!objUpdate.getCode().equals(ReturnNo.OK)){
            return objUpdate;
        }
        ErrorPaymentRetVo errorPaymentRetVo = cloneVo(errorPaymentPo, ErrorPaymentRetVo.class);
        errorPaymentRetVo.updateByPo(errorPaymentPo);
        return new ReturnObject<>(errorPaymentRetVo);
    }

    /**
     * 对csv文件列表解析，选择其中要解析的文件进行解析
     * @param csvFiles List<File>，获取的是csv文件列表，选择其中要解析的文件进行解析
     * @param clazz 转化的类型，WeChatFlowBillItem.class和AliPayFlowBillItem.class
     * @param pattern 通配符，微信：微信支付账单(20211011-20211211).csv；支付宝：20882029918150140156_20211115_账务明细.csv
     * @param <T> WeChatFlowBillItem、AliPayFlowBillItem
     * @return List<T>itemList，账单明细的列表
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> ReturnObject getFlowBills(List<File> csvFiles, Class<T> clazz,String pattern,Integer skipLines) throws IOException {
        List<T>itemList=new ArrayList<>();
        if(csvFiles.isEmpty()){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        for(File csv:csvFiles){
            if(!csv.exists()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(Pattern.matches(pattern,csv.getName())) {
                ReturnObject<List<T>> objParse = FileCommon.parseFlowBill(csv.getAbsolutePath(), clazz,skipLines);
                if (!objParse.getCode().equals(ReturnNo.OK)) {
                    return objParse;
                }
                itemList.addAll(objParse.getData());
                break;
            }
        }
        return new ReturnObject<>(itemList);
    }

    /**
     * 增加一笔长账
     * @param tradeSn 商户账单编号
     * @param patternId 支付渠道
     * @return 正常情况下，返回空值
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addExtraErrorApyment(String tradeSn,Long patternId,Long loginUser,String loginName){
        ErrorPayment errorPayment = new ErrorPayment(null, tradeSn, patternId, null, null,
                null, (byte) 0, LocalDateTime.now(), null, null, null, null,
                loginUser, loginName, null, null, LocalDateTime.now(), null);
        ReturnObject objAddError = reconciliationDao.addErrorPayment(errorPayment,loginUser,loginName);
        if (!objAddError.getCode().equals(ReturnNo.OK)) {
            return objAddError;
        }
        return new ReturnObject<>();
    }

    /**
     * 插入一笔长账，payment和refund有且仅有一个不为空
     * @param payment 数据库中payment
     * @param refund 数据库中refund
     * @return 正常情况，返回值为空
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addErrorPayment(Payment payment,Refund refund,Long loginUser,String loginName){
        //插入长账
        ErrorPayment errorPayment=new ErrorPayment();
        if(payment!=null){
            errorPayment = cloneVo(payment, ErrorPayment.class);
            errorPayment.setIncome(payment.getActualAmount());
        }else if(refund!=null){
            errorPayment = cloneVo(refund, ErrorPayment.class);
            errorPayment.setExpenditure(refund.getAmount());
        }else{
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        errorPayment.setState((byte) 0);
        errorPayment.setTime(LocalDateTime.now());
        ReturnObject objAddError = reconciliationDao.addErrorPayment(errorPayment,loginUser,loginName);
        if (!objAddError.getCode().equals(ReturnNo.OK)) {
            return objAddError;
        }
        return new ReturnObject<>();
    }

    /**
     * 判断是否一致，将payment或refund状态修改为已对账
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateReconciled(Payment payment,Refund refund,Boolean state,
                                         ReconciliationRetVo reconciliationRetVo,Long loginUser,String loginName){
        if(!state){
            //状态不一致或金额不一致
            ReturnObject objError=addErrorPayment(payment,refund,loginUser,loginName);
            if(!objError.getCode().equals(ReturnNo.OK)){
                return objError;
            }
            reconciliationRetVo.setError(reconciliationRetVo.getError() + 1);
        }else{
            reconciliationRetVo.setSuccess(reconciliationRetVo.getSuccess() + 1);
        }
        if(payment!=null){
            payment.setState(Payment.State.RECONCILED.getCode());
            PaymentPo paymentPo = cloneVo(payment, PaymentPo.class);
            Common.setPoModifiedFields(paymentPo,loginUser,loginName);
            ReturnObject objUpdatePay = paymentDao.modifyPayment(paymentPo);
            if (!objUpdatePay.getCode().equals(ReturnNo.OK)) {
                return objUpdatePay;
            }
        }else if(refund!=null){
            refund.setState(Refund.State.RECONCILED.getCode());
            RefundPo refundPo = cloneVo(refund, RefundPo.class);
            Common.setPoModifiedFields(refundPo,loginUser,loginName);
            ReturnObject objUpdateRefund = paymentDao.modifyRefund(refundPo);
            if (!objUpdateRefund.getCode().equals(ReturnNo.OK)) {
                return objUpdateRefund;
            }
        }else{
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(reconciliationRetVo);
    }

    /**
     * 对账，微信
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject reconcileWeChatBills(LocalDateTime beginTime, LocalDateTime endTime,ReconciliationRetVo reconciliationRetVo,Long loginUser,String loginName) throws Exception {
        for(LocalDateTime currentTime=beginTime;currentTime.isBefore(endTime)||currentTime.equals(endTime);currentTime=currentTime.plusDays(1)) {
            InternalReturnObject objWeChat = wechatPayService.getFundFlowBill("");
            if (!objWeChat.getErrno().equals(0)) {
                return new ReturnObject(objWeChat);
            }
            String weChatUrl =((WeChatPayFundFlowBillRetVo) ((WeChatPayReturnObject) objWeChat.getData()).getData()).getDownloadUrl();

            //TODO:微信从url下载zip文件
//            ReturnObject objWeChatDownload= FileCommon.downloadFlowBillsFromUrl(weChatUrl,fileName);
//            if(!objWeChatDownload.getCode().equals(ReturnNo.OK)){
//                return objWeChatDownload;
//            }
//            File zipFile=(File) objWeChatDownload.getData();

            File zipFile=new File(path+"微信支付账单(20211011-20211211).zip");
            if(!zipFile.exists()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            //解压文件
            ReturnObject objUnZip=FileCommon.unzipFiles(zipFile.getCanonicalPath());
            if(!objUnZip.getCode().equals(ReturnNo.OK)){
                return objUnZip;
            }
            List<File>csvFiles=(List<File>)objUnZip.getData();
            if(csvFiles.isEmpty()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            //解析文件
            String pattern="微信支付账单.*.csv";
            ReturnObject objBills=getFlowBills(csvFiles,WeChatFlowBillItem.class,pattern,17);
            if(!objBills.getCode().equals(ReturnNo.OK)){
                return objBills;
            }
            List<WeChatFlowBillItem>itemList=(List<WeChatFlowBillItem>) objBills.getData();

            Boolean stateSuccess=false, amountSame=false;
            Payment payment=new Payment();
            Refund refund=new Refund();
            for(WeChatFlowBillItem item:itemList){
                //将¥和.去掉
                item.formatAmount();
                if("收入".equals(item.getPaymentOrRefund())) {
                    ReturnObject<PaymentPo> objPayment = paymentDao.getPaymentByOutTradeNo(item.getTradeSn());
                    if (objPayment.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
                        //长账
                        ReturnObject objExtra=addExtraErrorApyment(item.getTradeSn(), WECHAT_PATTERN_ID,loginUser,loginName);
                        if(!objExtra.getCode().equals(ReturnNo.OK)){
                            return objExtra;
                        }
                        reconciliationRetVo.setExtra(reconciliationRetVo.getExtra() + 1);
                        continue;
                    }else if(!objPayment.getCode().equals(ReturnNo.OK)){
                        return objPayment;
                    }
                    payment = cloneVo(objPayment.getData(),Payment.class);
                    stateSuccess = payment.getState().equals(Payment.State.PAID.getCode()) && ("支付成功".equals(item.getState()) || "已转账".equals(item.getState()));
                    amountSame = payment.getActualAmount().toString().equals(item.getAmount());
                }else if("支出".equals(item.getPaymentOrRefund())){
                    ReturnObject<RefundPo> objRefund = paymentDao.getRefundByOutTradeNo(item.getTradeSn());
                    if (objRefund.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
                        ReturnObject objExtra=addExtraErrorApyment(item.getTradeSn(), WECHAT_PATTERN_ID,loginUser,loginName);
                        if(!objExtra.getCode().equals(ReturnNo.OK)){
                            return objExtra;
                        }
                        reconciliationRetVo.setExtra(reconciliationRetVo.getExtra() + 1);
                        continue;
                    }else if(!objRefund.getCode().equals(ReturnNo.OK)){
                        return objRefund;
                    }
                    refund = cloneVo(objRefund.getData(),Refund.class);
                    stateSuccess = refund.getState().equals(Refund.State.REFUNDED.getCode()) && "已全额退款".equals(item.getState());
                    amountSame = refund.getAmount().toString().equals(item.getAmount());
                }
                ReturnObject objUpdate=updateReconciled(payment,refund,stateSuccess&&amountSame,reconciliationRetVo,loginUser,loginName);
                if(!objUpdate.getCode().equals(ReturnNo.OK)){
                    return objUpdate;
                }
                reconciliationRetVo=(ReconciliationRetVo) objUpdate.getData();
            }
        }
        return new ReturnObject<>(reconciliationRetVo);
    }

    /**
     * 对账，支付宝
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject reconcileAliPayBills(LocalDateTime beginTime, LocalDateTime endTime,ReconciliationRetVo reconciliationRetVo,Long loginUser,String loginName) throws Exception {
        for(LocalDateTime currentTime=beginTime;currentTime.isBefore(endTime)||currentTime.equals(endTime);currentTime=currentTime.plusDays(1)) {
            InternalReturnObject<WarpRetObject> objAlipay = alipayService.gatewayDo(null, "alipay.data.dataservice.bill.downloadurl.query", null, "utf-8",
                    "RSA2", null, null, null, "{\"bill_date\":\"\"}");
            if(!objAlipay.getErrno().equals(0)){
                return new ReturnObject(objAlipay);
            }
            WarpRetObject warpRetObject=(WarpRetObject)objAlipay.getData();
//            String alipayUrl = warpRetObject.getDownloadUrlQueryRetVo().getBillDownloadUrl();

            //TODO:支付宝从url下载zip文件
            File zipFile=new File(path+"20211105_2088202991815014.zip");
            if(!zipFile.exists()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            //解压文件
            ReturnObject objUnZip=FileCommon.unzipFiles(zipFile.getCanonicalPath());
            if(!objUnZip.getCode().equals(ReturnNo.OK)){
                return objUnZip;
            }
            List<File>csvFiles=(List<File>)objUnZip.getData();
            if(csvFiles.isEmpty()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            //解析文件
            String pattern=".+_.+_账务明细[_]*[1]*.csv";
            ReturnObject objBills=getFlowBills(csvFiles, AliPayFlowBillItem.class,pattern,5);
            if(!objBills.getCode().equals(ReturnNo.OK)){
                return objBills;
            }
            List<AliPayFlowBillItem>itemList=(List<AliPayFlowBillItem>) objBills.getData();

            Boolean amountSame=false;
            Payment payment=new Payment();
            Refund refund=new Refund();
            for(AliPayFlowBillItem item:itemList){
                if(item.getRevenue()!=0.) {
                    //收入
                    item.formatAmount();
                    ReturnObject<PaymentPo> objPayment = paymentDao.getPaymentByOutTradeNo(item.getTradeSn());
                    if (objPayment.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
                        //长账
                        ReturnObject objExtra=addExtraErrorApyment(item.getTradeSn(), ALIPAY_PATTERN_ID,loginUser,loginName);
                        if(!objExtra.getCode().equals(ReturnNo.OK)){
                            return objExtra;
                        }
                        reconciliationRetVo.setExtra(reconciliationRetVo.getExtra() + 1);
                        continue;
                    }else if(!objPayment.getCode().equals(ReturnNo.OK)){
                        return objPayment;
                    }
                    payment = cloneVo(objPayment.getData(),Payment.class);
                    amountSame = payment.getActualAmount().equals(item.getRevenue().longValue());
                }else if(item.getExpenditure()!=0.){
                    //支出
                    item.formatAmount();
                    ReturnObject<RefundPo> objRefund = paymentDao.getRefundByOutTradeNo(item.getTradeSn());
                    if (objRefund.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
                        ReturnObject objExtra=addExtraErrorApyment(item.getTradeSn(), ALIPAY_PATTERN_ID,loginUser,loginName);
                        if(!objExtra.getCode().equals(ReturnNo.OK)){
                            return objExtra;
                        }
                        reconciliationRetVo.setExtra(reconciliationRetVo.getExtra() + 1);
                        continue;
                    }else if(!objRefund.getCode().equals(ReturnNo.OK)){
                        return objRefund;
                    }
                    refund = cloneVo(objRefund.getData(),Refund.class);
                    amountSame = refund.getAmount().equals(Math.abs(item.getExpenditure().longValue()));
                }
                ReturnObject objUpdate=updateReconciled(payment,refund,amountSame,reconciliationRetVo,loginUser,loginName);
                if(!objUpdate.getCode().equals(ReturnNo.OK)){
                    return objUpdate;
                }
                reconciliationRetVo=(ReconciliationRetVo) objUpdate.getData();
            }
        }
        return new ReturnObject<>(reconciliationRetVo);
    }

    /**
     * 对账
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject reconcile(LocalDateTime beginTime, LocalDateTime endTime,Long loginUser,String loginName) throws Exception {
        ReconciliationRetVo reconciliationRetVo = new ReconciliationRetVo();
        ReturnObject objWeChat = reconcileWeChatBills(beginTime, endTime, reconciliationRetVo,loginUser,loginName);
        if (!objWeChat.getCode().equals(ReturnNo.OK)) {
            return objWeChat;
        }
        ReturnObject objAliPay = reconcileAliPayBills(beginTime, endTime, reconciliationRetVo,loginUser,loginName);
        if (!objAliPay.getCode().equals(ReturnNo.OK)) {
            return objAliPay;
        }
        return new ReturnObject<>(reconciliationRetVo);
    }

}
