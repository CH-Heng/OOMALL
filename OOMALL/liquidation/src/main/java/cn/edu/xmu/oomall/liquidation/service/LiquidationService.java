package cn.edu.xmu.oomall.liquidation.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.liquidation.dao.LiquidationDao;
import cn.edu.xmu.oomall.liquidation.microservice.*;
import cn.edu.xmu.oomall.liquidation.microservice.vo.*;
import cn.edu.xmu.oomall.liquidation.model.bo.ExpenditureItem;
import cn.edu.xmu.oomall.liquidation.model.bo.Liquidation;
import cn.edu.xmu.oomall.liquidation.model.bo.RevenueItem;
import cn.edu.xmu.oomall.liquidation.model.po.ExpenditureItemPo;
import cn.edu.xmu.oomall.liquidation.model.po.RevenueItemPo;
import cn.edu.xmu.oomall.liquidation.model.vo.ExpenditureItemRetVo;
import cn.edu.xmu.oomall.liquidation.model.vo.LiquidationRetVo;
import cn.edu.xmu.oomall.liquidation.model.vo.RevenueItemRetVo;
import cn.edu.xmu.oomall.liquidation.model.vo.SimpleLiquidationRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

/**
 * @author wwk
 * @author wyg
 * @date 2021/12/15
 */
@Service
public class LiquidationService {

    @Autowired
    private LiquidationDao liquidationDao;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AftersaleService aftersaleService;

    /**
     * payment
     */
    private final static byte ORDER = 0;
    private final static byte DEPOSIT = 1;
    private final static byte AFTERSALE = 2;

    private final static byte RECONCILIATE = 2;

    /**
     * order
     */
    private final static int PAID = 201;

    /**
     * 获得清算单的所有状态
     */
    @Transactional(readOnly = true)
    public ReturnObject getStates() {
        List<Map<String, Object>> stateList = new ArrayList<>();
        for (Liquidation.State state : Liquidation.State.values()) {
            Map<String, Object> temp = new TreeMap<>();
            temp.put("code", state.getCode());
            temp.put("name", state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 平台管理员或商家获取符合条件的清算单简单信息
     */
    @Transactional(readOnly = true)
    public ReturnObject getSimpleLiquidation(Liquidation liquidation, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        if(liquidation.getShopId()==0){
            liquidation.setShopId(null);
        }
        ReturnObject ret = liquidationDao.getLiquidations(liquidation,beginTime,endTime,page,pageSize);
        return Common.getPageRetVo(ret, SimpleLiquidationRetVo.class);
    }

    /**
     * 查询指定清算单详情
     */
    @Transactional(readOnly = true)
    public ReturnObject getLiquidationInfo(Long shopId, Long id) {
        ReturnObject check = liquidationDao.matchShopAndLiquidation(shopId, id);
        if (check.getCode() != ReturnNo.OK) {
            return check;
        }
        return new ReturnObject(cloneVo(liquidationDao.getLiquidationById(id).getData(), LiquidationRetVo.class));
    }

    /**
     * 管理员按条件查某笔的进账
     */
    @Transactional(readOnly = true)
    public ReturnObject getRevenue(RevenueItem revenueItem, Integer page, Integer pageSize) {
        if(revenueItem.getShopId()==0){
            revenueItem.setShopId(null);
        }
        ReturnObject ret = liquidationDao.getRevenues(revenueItem,null,null, page,pageSize);
        return Common.getPageRetVo(ret, RevenueItemRetVo.class);
    }

    /**
     * 管理员按条件查某笔的出账
     */
    @Transactional(readOnly = true)
    public ReturnObject getExpenditure(ExpenditureItem expenditureItem, Integer page, Integer pageSize) {
        if(expenditureItem.getShopId()==0){
            expenditureItem.setShopId(null);
        }
        ReturnObject ret = liquidationDao.getExpenditure(expenditureItem,null,null, page,pageSize);
        return Common.getPageRetVo(ret, ExpenditureItemRetVo.class);
    }

    /**
     * 管理员按id查出账对应的进账
     */
    @Transactional(readOnly = true)
    public ReturnObject getRevenueByExpenditureId(Long shopId, Long id) {
        ReturnObject check = liquidationDao.matchShopAndExpenditure(shopId, id);
        if (check.getCode() != ReturnNo.OK) {
            return check;
        }
        ExpenditureItem expenditureItem = liquidationDao.getExpenditureItemById(id).getData();
        RevenueItem revenueItem = liquidationDao.getRevenueItemById(expenditureItem.getRevenueId()).getData();
        return new ReturnObject(cloneVo(revenueItem, RevenueItemRetVo.class));
    }

    /**
     * 用户获取自己因分享得到返点的记录(进账)
     */
    @Transactional(readOnly = true)
    public ReturnObject getUserRevenue(RevenueItem revenueItem, LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize){
        ReturnObject ret = liquidationDao.getRevenues(revenueItem,beginTime,endTime, page,pageSize);
        PageInfo revenueItemPageInfo = (PageInfo) ret.getData();
        List<RevenueItemPo> revenueItemPos = revenueItemPageInfo.getList();
        Collections.sort(revenueItemPos, (o1, o2) -> -(o1.getGmtCreate().compareTo(o2.getGmtCreate())));
        revenueItemPageInfo.setList(revenueItemPos);
        return Common.getPageRetVo(new ReturnObject<>(revenueItemPageInfo),RevenueItemRetVo.class);
    }

    /**
     * 用户获取自己因分享得到返点的记录(出账)
     */
    @Transactional(readOnly = true)
    public ReturnObject getUserExpenditure(ExpenditureItem expenditureItem, LocalDateTime beginTime,LocalDateTime endTime,Integer page,Integer pageSize) {
        ReturnObject ret = liquidationDao.getExpenditure(expenditureItem, beginTime, endTime, page, pageSize);
        PageInfo expenditurePageInfo = (PageInfo) ret.getData();
        List<ExpenditureItemPo> expenditureItemPos = expenditurePageInfo.getList();
        Collections.sort(expenditureItemPos, (o1, o2) -> -(o1.getGmtCreate().compareTo(o2.getGmtCreate())));
        expenditurePageInfo.setList(expenditureItemPos);
        return Common.getPageRetVo(new ReturnObject<>(expenditurePageInfo), RevenueItemRetVo.class);
    }

    /**
     * 开始清算
     */
    public ReturnObject startLiquidation(Long loginId, String loginUserName, ZonedDateTime beginTime, ZonedDateTime endTime) {
        ReturnObject retObj;
        int page;
        int pageSize = 50;
        LocalDateTime liquidDate = LocalDateTime.now();
        // 初始化当日清算单
        retObj = initializeLiquidation(loginId, loginUserName, liquidDate);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        // 清算支付
        page = 1;
        while (true) {
            ReturnObject paymentRetObj = paymentService.getPayment(0L, beginTime, endTime, page, pageSize);
            if (paymentRetObj.getData() == null) {
                return paymentRetObj;
            }
            List<PaymentRetVo> payments = (List<PaymentRetVo>) paymentRetObj.getData();
            if (payments.isEmpty()) {
                break;
            }
            retObj = liquidatePayment(loginId, loginUserName, payments);
            if (retObj.getCode() != ReturnNo.OK) {
                return retObj;
            }
            page++;
        }
        // 清算退款
        page = 1;
        while (true) {
            ReturnObject refundRetObj = paymentService.getRefund(0L, beginTime, endTime, page, pageSize);
            if (refundRetObj.getData() == null) {
                return refundRetObj;
            }
            List<RefundRetVo> refunds = (List<RefundRetVo>) refundRetObj.getData();
            if (refunds.isEmpty()) {
                break;
            }
            retObj = liquidateRefund(loginId, loginUserName, refunds);
            if (retObj.getCode() != ReturnNo.OK) {
                return retObj;
            }
            page++;
        }
        // 汇总
        retObj = summary(liquidDate);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        return new ReturnObject();
    }

    /**
     * 初始化当日每个店铺清算单
     */
    private ReturnObject initializeLiquidation(Long loginUser, String loginUserName, LocalDateTime liquidDate) {
        int page = 1;
        int pageSize = 50;
        // 为平台添加初始 liquidation
        Liquidation bo = new Liquidation();
        bo.setShopId(0L);
        bo.setShopName("oomall");
        bo.setLiquidDate(liquidDate);
        bo.setState(Liquidation.State.NOT_REMITTED.getCode());
        setPoCreatedFields(bo, loginUser, loginUserName);
        liquidationDao.insertLiquidation(bo);
        // 为商铺添加初始 liquidation
        while (true) {
            // 获得 shop
            InternalReturnObject shopRetObj = shopService.getShop(page, pageSize);
            if (shopRetObj.getData() == null) {
                return new ReturnObject(ReturnNo.getReturnNoByCode(shopRetObj.getErrno()), "获取商铺错误");
            }
            List<ShopRetVo> shopRetVos = (List<ShopRetVo>) shopRetObj.getData();
            if (shopRetVos.isEmpty()) {
                break;
            }
            // 添加初始 liquidation
            for (ShopRetVo shop : shopRetVos) {
                bo = new Liquidation();
                bo.setShopId(shop.getId());
                bo.setShopName(shop.getName());
                bo.setLiquidDate(LocalDateTime.now());
                bo.setState(Liquidation.State.NOT_REMITTED.getCode());
                setPoCreatedFields(bo, loginUser, loginUserName);
                liquidationDao.insertLiquidation(bo);
            }
            page++;
        }
        return new ReturnObject();
    }

    /**
     * 清算支付
     */
    private ReturnObject liquidatePayment(Long loginId, String loginUserName, List<PaymentRetVo> payments) {
        for (PaymentRetVo payment : payments) {
            // 只清算订单类型的支付单 && 只清算已对账的支付单
            if (payment.getDocumentType() != ORDER || payment.getState() != RECONCILIATE) {
                continue;
            }
            // 获得 order
            InternalReturnObject orderRetObj = orderService.getOrderByOrderSn(payment.getDocumentId());
            if (orderRetObj.getData() == null) {
                return new ReturnObject(ReturnNo.getReturnNoByCode(orderRetObj.getErrno()), "获取订单错误");
            }
            OrderRetVo orderRetVo = (OrderRetVo) orderRetObj.getData();
            // 只清算支付完成的订单
            if (orderRetVo.getState() != PAID) {
                continue;
            }
            // 创建快递费 revenue
            ReturnObject expressFeeRevenueItemRetObj = createRevenueItem(loginId, loginUserName, payment, orderRetVo);
            if (expressFeeRevenueItemRetObj.getCode() != ReturnNo.OK) {
                return expressFeeRevenueItemRetObj;
            }
            // 获得 orderItem
            ReturnObject orderItemRetObj = getOrderItemByOrderId(orderRetVo.getId());
            if (orderItemRetObj.getData() == null) {
                return orderItemRetObj;
            }
            List<OrderItemRetVo> orderItemRetVos = (List<OrderItemRetVo>) orderItemRetObj.getData();
            // 创建 revenue
            for (OrderItemRetVo orderItemRetVo : orderItemRetVos) {
                ReturnObject revenueItemRetObj = createRevenueItem(loginId, loginUserName, payment, orderItemRetVo);
                if (revenueItemRetObj.getCode() != ReturnNo.OK) {
                    return revenueItemRetObj;
                }
            }
        }
        return new ReturnObject();
    }

    /**
     * 清算退款
     */
    private ReturnObject liquidateRefund(Long loginId, String loginUserName, List<RefundRetVo> refunds) {
        for (RefundRetVo refundRetVo : refunds) {
            if (refundRetVo.getState()!=RECONCILIATE){
                continue;
            }
            if (refundRetVo.getDocumentType().equals(ORDER)) {
                ReturnObject returnObject = liquidateOrderRefund(loginId, loginUserName, refundRetVo);
                if (returnObject.getCode() != ReturnNo.OK) {
                    return returnObject;
                }
            } else if (refundRetVo.getDocumentType().equals(AFTERSALE)) {
                ReturnObject returnObject = liquidateAftersaleRefund(loginId, loginUserName, refundRetVo);
                if (returnObject.getCode() != ReturnNo.OK) {
                    return returnObject;
                }
            }
        }
        return new ReturnObject();
    }

    /**
     * 清算一条订单退款
     *
     * @param loginId
     * @param loginUserName
     * @param refundRetVo
     * @return
     */
    private ReturnObject liquidateOrderRefund(Long loginId, String loginUserName, RefundRetVo refundRetVo) {
        //根据paymentId获取对应的revenueItem
        RevenueItem revenueItem = new RevenueItem();
        revenueItem.setPaymentId(refundRetVo.getPaymentId());
        ReturnObject revenueRet = liquidationDao.getRevenues(revenueItem, null, null, null, null);
        if (revenueRet.getCode() != ReturnNo.OK) {
            return revenueRet;
        }
        List<RevenueItem> revenueItemList = (List<RevenueItem>) revenueRet.getData();
        for (RevenueItem revenueItem1 : revenueItemList) {
            ReturnObject ret = createExpenditureByRevenue(revenueItem1, revenueItem1.getQuantity(), refundRetVo.getId(), loginId, loginUserName);
            if (ret.getCode() != ReturnNo.OK) {
                return ret;
            }
        }
        return new ReturnObject();
    }

    /**
     * 清算一条售后退款
     *
     * @param loginId
     * @param loginUserName
     * @param refundRetVo
     * @return
     */
    private ReturnObject liquidateAftersaleRefund(Long loginId, String loginUserName, RefundRetVo refundRetVo) {

        InternalReturnObject aftersaleRet = aftersaleService.getAftersaleBySn(refundRetVo.getDocumentId());
        if (aftersaleRet.getErrno() != 0) {
            return new ReturnObject(ReturnNo.getReturnNoByCode(aftersaleRet.getErrno()), aftersaleRet.getErrmsg(), aftersaleRet.getData());
        }
        AftersaleRetVo aftersaleRetVo = (AftersaleRetVo) aftersaleRet.getData();
        //根据orderItemId获取对应的revenueItem
        RevenueItem revenueItem = new RevenueItem();
        revenueItem.setOrderitemId(aftersaleRetVo.getOrderItemId());
        ReturnObject revenueRet = liquidationDao.getRevenues(revenueItem, null, null, null, null);
        if (revenueRet.getCode() != ReturnNo.OK) {
            return revenueRet;
        }
        List<RevenueItem> revenueItemList = (List<RevenueItem>) revenueRet.getData();
        for (RevenueItem revenueItem1 : revenueItemList) {
            ReturnObject ret = createExpenditureByRevenue(revenueItem1, aftersaleRetVo.getQuantity(), refundRetVo.getId(), loginId, loginUserName);
            if (ret.getCode() != ReturnNo.OK) {
                return ret;
            }
        }
        return new ReturnObject();
    }

    /**
     * 根据进账单创建出账单
     *
     * @param revenueItem
     * @param expenditureQuantity
     * @param refundId
     * @param loginId
     * @param loginUserName
     * @return
     */
    private ReturnObject createExpenditureByRevenue(RevenueItem revenueItem, Integer expenditureQuantity, Long refundId, Long loginId, String loginUserName) {
        ExpenditureItem expenditureItem = cloneVo(revenueItem, ExpenditureItem.class);
        expenditureItem.setRefundId(refundId);
        // 查询对应的 liquidation
        ReturnObject liquidationRetObj = liquidationDao.getLiquidationCertainDayByShopId(revenueItem.getShopId(), LocalDateTime.now());
        if (liquidationRetObj.getData() == null) {
            return liquidationRetObj;
        }
        Liquidation liquidation = (Liquidation) liquidationRetObj.getData();
        expenditureItem.setLiquidId(liquidation.getId());
        expenditureItem.setQuantity(expenditureQuantity);
        if (revenueItem.getShopId()==0){
            revenueItem.setQuantity(1);
            expenditureItem.setQuantity(1);
        }
        expenditureItem.setExpressFee(-revenueItem.getExpressFee() * expenditureItem.getQuantity() / revenueItem.getQuantity());
        expenditureItem.setAmount(-revenueItem.getAmount() * expenditureQuantity / revenueItem.getQuantity());
        expenditureItem.setCommission(-revenueItem.getCommission() * expenditureQuantity / revenueItem.getQuantity());
        expenditureItem.setPoint(-revenueItem.getPoint() * expenditureQuantity / revenueItem.getQuantity());
        expenditureItem.setShopRevenue(-revenueItem.getShopRevenue() * expenditureQuantity / revenueItem.getQuantity());
        setPoCreatedFields(expenditureItem, loginId, loginUserName);
        if (revenueItem.getShopId()==0){
            revenueItem.setQuantity(0);
            expenditureItem.setQuantity(0);
        }
        customerService.modifyCustomerPoint(expenditureItem.getSharerId(), new CustomerPointVo(expenditureItem.getPoint()));
        return liquidationDao.insertExpenditureItem(expenditureItem);
    }

    /**
     * 汇总
     */
    private ReturnObject summary(LocalDateTime liquidDate) {
        ReturnObject retObj;
        int page;
        int pageSize = 50;
        // 清算日期
        LocalDateTime begin = LocalDateTime.of(liquidDate.getYear(), liquidDate.getMonth(), liquidDate.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(liquidDate.getYear(), liquidDate.getMonth(), liquidDate.getDayOfMonth(), 23, 59, 59);
        // 平台当日获取的佣金
        Long commission = 0;
        // 为商铺汇总
        page = 1;
        while (true) {
            // 获得 shop
            InternalReturnObject shopRetObj = shopService.getShop(page, pageSize);
            if (shopRetObj.getData() == null) {
                return new ReturnObject(ReturnNo.getReturnNoByCode(shopRetObj.getErrno()), "获取商铺错误");
            }
            List<ShopRetVo> shopRetVos = (List<ShopRetVo>) shopRetObj.getData();
            if (shopRetVos.isEmpty()) {
                break;
            }
            for (ShopRetVo shop : shopRetVos) {
                retObj = summary(shop.getId(), 0L, begin, end);
                if (retObj.getData() == null) {
                    return retObj;
                }
                commission += (long) retObj.getData();
            }
            page++;
        }
        // 为平台汇总
        retObj = summary(0L, commission, begin, end);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        return new ReturnObject();
    }

    /**
     * 汇总 shop 的当日清算
     * shopId == 0 为平台生成清算单
     * shopId != 0 为商铺生成清算单
     *
     * @param commission 为平台生成清算单时传入的佣金
     * @return ReturnObject<Long> 收取商铺的佣金
     */
    private ReturnObject summary(Long shopId, Long commission, LocalDateTime begin, LocalDateTime end) {
        Long point = 0;
        Long expressFee = 0;
        Long shopRevenue = 0;
        Long innerCommission = 0;

        // 获取 shop 的 liquidation
        ReturnObject liquidationRetObj = liquidationDao.getLiquidationCertainDayByShopId(shopId, LocalDateTime.now());
        if (liquidationRetObj.getData() == null) {
            return liquidationRetObj;
        }
        Liquidation liquidation = (Liquidation) liquidationRetObj.getData();
        // 进账汇总
        RevenueItem revenueItem = new RevenueItem();
        revenueItem.setShopId(shopId);
        ReturnObject revenueRetObj = liquidationDao.getRevenues(revenueItem, begin, end, null, null);
        if (revenueRetObj.getData() == null) {
            return revenueRetObj;
        }
        List<RevenueItem> revenueItems = (List<RevenueItem>) revenueRetObj.getData();
        for (RevenueItem r : revenueItems) {
            point += r.getPoint();
            expressFee += r.getExpressFee();
            shopRevenue += r.getShopRevenue();
            innerCommission += r.getCommission();
        }
        // 出账汇总
        ExpenditureItem expenditureItem = new ExpenditureItem();
        expenditureItem.setShopId(shopId);
        ReturnObject expenditureRetObj = liquidationDao.getExpenditure(expenditureItem, begin, end, null, null);
        if (expenditureRetObj.getData() == null) {
            return expenditureRetObj;
        }
        List<ExpenditureItem> expenditureItems = (List<ExpenditureItem>) expenditureRetObj.getData();
        for (ExpenditureItem e : expenditureItems) {
            point += e.getPoint();
            expressFee += e.getExpressFee();
            shopRevenue += e.getShopRevenue();
            innerCommission += e.getCommission();
        }
        // 更新 liquidation
        if (shopId == 0) {
            liquidation.setExpressFee(expressFee);
            liquidation.setCommission(commission);
            liquidation.setPoint(0L);
            liquidation.setShopRevenue(0L);
        } else {
            liquidation.setExpressFee(0L);
            liquidation.setCommission(0L);
            liquidation.setPoint(point);
            liquidation.setShopRevenue(shopRevenue);
        }
        ReturnObject retObj = liquidationDao.updateLiquidation(liquidation);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        return new ReturnObject(innerCommission);
    }

    /**
     * 获得order下的所有orderItem
     * 汇总分页查询的结果
     */
    private ReturnObject getOrderItemByOrderId(Long orderId) {
        int page = 1;
        int pageSize = 10;
        List<OrderItemRetVo> orderItemRetVos = new LinkedList<>();

        while (true) {
            InternalReturnObject orderItemRetObj = orderService.getOrderItemByOrderId(orderId, page, pageSize);
            if (orderItemRetObj.getData() == null) {
                return new ReturnObject(ReturnNo.getReturnNoByCode(orderItemRetObj.getErrno()), "获取订单明细错误");
            }
            List<OrderItemRetVo> innerList = (List<OrderItemRetVo>) orderItemRetObj.getData();
            if (innerList.isEmpty()) {
                break;
            }
            orderItemRetVos.addAll(innerList);

            page++;
        }
        return new ReturnObject(orderItemRetVos);
    }

    /**
     * 创建快递费进账单
     */
    private ReturnObject createRevenueItem(Long loginId, String loginUserName, PaymentRetVo payment, OrderRetVo order) {
        RevenueItem bo = new RevenueItem();
        // 查询对应的 liquidation
        ReturnObject liquidationRetObj = liquidationDao.getLiquidationCertainDayByShopId(0L, LocalDateTime.now());
        if (liquidationRetObj.getData() == null) {
            return liquidationRetObj;
        }
        Liquidation liquidation = (Liquidation) liquidationRetObj.getData();
        bo.setLiquidId(liquidation.getId());
        bo.setPaymentId(payment.getId());
        bo.setShopId(0L);
        bo.setOrderId(order.getId());
        bo.setOrderitemId(0L);
        bo.setAmount(order.getExpressFee());
        bo.setExpressFee(order.getExpressFee());
        bo.setCommission(0L);
        bo.setPoint(0L);
        bo.setShopRevenue(0L);
        bo.setQuantity(0);
        setPoCreatedFields(bo, loginId, loginUserName);
        // 插入 revenue
        ReturnObject retObj = liquidationDao.insertRevenueItem(bo);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        return new ReturnObject();
    }

    /**
     * 创建进账单
     */
    private ReturnObject createRevenueItem(Long loginId, String loginUserName, PaymentRetVo payment, OrderItemRetVo orderItem) {
        RevenueItem bo = new RevenueItem();
        // 查询对应的 liquidation
        ReturnObject liquidationRetObj = liquidationDao.getLiquidationCertainDayByShopId(orderItem.getShopId(), LocalDateTime.now());
        if (liquidationRetObj.getData() == null) {
            return liquidationRetObj;
        }
        Liquidation liquidation = (Liquidation) liquidationRetObj.getData();
        bo.setLiquidId(liquidation.getId());
        bo.setPaymentId(payment.getId());
        bo.setShopId(orderItem.getShopId());
        bo.setOrderId(orderItem.getOrderId());
        bo.setOrderitemId(orderItem.getId());
        bo.setQuantity(orderItem.getQuantity());
        bo.setProductId(orderItem.getProductId());
        // 查询对应的 product
        InternalReturnObject productRetObj = goodsService.getProduct(orderItem.getProductId());
        if (productRetObj.getData() == null) {
            return new ReturnObject(ReturnNo.getReturnNoByCode(productRetObj.getErrno()), productRetObj.getErrmsg());
        }
        ProductRetVo productRetVo = (ProductRetVo) productRetObj.getData();
        bo.setProductName(productRetVo.getName());
        bo.setAmount(orderItem.getPrice() - orderItem.getDiscountPrice());
        bo.setExpressFee(0L);
        bo.setCommission((long) (bo.getAmount() * (double) productRetVo.getCategory().getCommissionRatio() / 1000));
        // 查询 successfulShare
        InternalReturnObject successfulShareRetObj = shareService.getSuccessfulShareByOnSaleIdAdnCustomerId(orderItem.getOnsaleId(), orderItem.getCustomerId());
        if (successfulShareRetObj.getErrno() != ReturnNo.OK.getCode()) {
            return new ReturnObject(ReturnNo.getReturnNoByCode(successfulShareRetObj.getErrno()), "获取分享错误");
        }
        SuccessfulShareRetVo successfulShareRetVo = (SuccessfulShareRetVo) successfulShareRetObj.getData();
        if (successfulShareRetVo != null) {
            bo.setPoint(calculatePoint(bo.getAmount(), orderItem.getQuantity(), successfulShareRetVo.getStrategy()));
            bo.setSharerId(successfulShareRetVo.getSharerId());
        } else {
            bo.setPoint(0L);
            bo.setSharerId(null);
        }
        bo.setShopRevenue(bo.getAmount() - bo.getCommission() - bo.getPoint());
        setPoCreatedFields(bo, loginId, loginUserName);
        // 插入 revenue
        ReturnObject retObj = liquidationDao.insertRevenueItem(bo);
        if (retObj.getCode() != ReturnNo.OK) {
            return retObj;
        }
        // 给用户返点
        InternalReturnObject customerRetObj = customerService.modifyCustomerPoint(bo.getSharerId(), new CustomerPointVo(bo.getPoint()));
        if (customerRetObj.getErrno() != ReturnNo.OK.getCode()) {
            return new ReturnObject(ReturnNo.getReturnNoByCode(customerRetObj.getErrno()), customerRetObj.getErrmsg());
        }
        // 设置 successfulShare 为已清算
        if (successfulShareRetVo != null) {
            successfulShareRetObj = shareService.setSuccessfulShareLiquidated(successfulShareRetVo.getId());
            if (successfulShareRetObj.getErrno() != ReturnNo.OK.getCode()) {
                return new ReturnObject(ReturnNo.getReturnNoByCode(successfulShareRetObj.getErrno()), successfulShareRetObj.getErrmsg());
            }
        }
        return new ReturnObject();
    }


    /**
     * 计算返点
     */
    private Long calculatePoint(Long amount, Integer quantity, List<StrategyRetVo> strategyList) {
        // 保证按降序排列
        Collections.sort(strategyList, (o1, o2) -> -(int) (o1.getQuantity() - o2.getQuantity()));

        for (StrategyRetVo strategy : strategyList) {
            if (quantity >= strategy.getQuantity()) {
                return (long) (amount * ((double) strategy.getPercentage() / 1000));
            }
        }
        return 0L;
    }
}
