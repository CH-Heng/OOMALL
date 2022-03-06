package cn.edu.xmu.oomall.aftersale.dao;

import cn.edu.xmu.oomall.aftersale.mapper.AfterSalePoMapper;
import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePoExample;
import cn.edu.xmu.oomall.aftersale.model.vo.SimpleAftersaleRetVo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
@Repository
public class AftersaleDao {
    @Autowired
    private AfterSalePoMapper afterSalePoMapper;

    private Byte BEDELETED=1;   //逻辑删除
    /**
    * @author wxt
    * 获得售后服务的状态
    */
    public ReturnObject getAftersaleState() {
        List<Map<String, Object>> stateList = new ArrayList<>();
        for (Aftersale.State states : Aftersale.State.values()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("code", states.getCode());
            temp.put("name", states.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }
    /**
     * @author wxt
     * 查看所有售后单信息
     * @return
     */
    public ReturnObject  getAllAftersales(Long userId, Long shopId, Integer state, Integer type, ZonedDateTime beginTime, ZonedDateTime endTime, Integer page, Integer pageSize)
    {
        AfterSalePoExample example=new AfterSalePoExample();
        AfterSalePoExample.Criteria criteria=example.createCriteria();
        try{
            if(userId!=null) {
                criteria.andCustomerIdEqualTo(userId);
            }
            if(shopId!=null&&shopId!=0) {
                criteria.andIdEqualTo(shopId);
            }
            if(state!=null) {
                criteria.andStateEqualTo(state.byteValue());
            }
            if(type!=null) {
                criteria.andTypeEqualTo(type.byteValue());
            }
            if(beginTime != null) {
                criteria.andGmtCreateGreaterThan(beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
            }
            if(endTime != null) criteria.andGmtCreateLessThanOrEqualTo(endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
            String orderBy="gmt_create desc";
            PageHelper.startPage(page,pageSize,orderBy);
            List<AfterSalePo> pos=new ArrayList<>();
            pos=afterSalePoMapper.selectByExample(example);
            PageInfo<AfterSalePo> pageInfo=new PageInfo<>(pos);
            ReturnObject returnObject = new ReturnObject(pageInfo);
            return Common.getPageRetVo(returnObject, SimpleAftersaleRetVo.class);
        }catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    /**
     * @author wxt
     * 根据id查看售后单信息
     * @return
     */
    public ReturnObject getAftersalesById(Long aftersaleId) {
        try {
            AfterSalePo afterSalePo = afterSalePoMapper.selectByPrimaryKey(aftersaleId);
            return new ReturnObject(afterSalePo);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());

        }
    }
    
    /**
     * @param aftersale
     * @return
     */
    public ReturnObject insertAftersale(Aftersale aftersale) {
        try {
            AfterSalePo afterSalePo=cloneVo(aftersale,AfterSalePo.class);
            int ret=afterSalePoMapper.insertSelective(afterSalePo);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }else {
                SimpleAftersaleRetVo simpleAftersaleRetVo = cloneVo(afterSalePo, SimpleAftersaleRetVo.class);
                return new ReturnObject(simpleAftersaleRetVo);
            }
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /** 买家修改售后单信息（店家发货前）
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject updateAftersale(Aftersale aftersale) {
        try{
            AfterSalePo afterSalePo=afterSalePoMapper.selectByPrimaryKey(aftersale.getId());
            if(afterSalePo==null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
            }
            //验证修改者是否与售后单中的买家一致
            if(!afterSalePo.getCustomerId().equals(aftersale.getModifierId())){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"没有修改权限");
            }
            //判断售后单的状态是否可以修改
            if(((int)afterSalePo.getState() >= (int)Aftersale.State.BE_DELIVERED_BY_SHOPKEEPER.getCode())){
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }

            AfterSalePo updateAftersalePo=cloneVo(aftersale,AfterSalePo.class);
            afterSalePoMapper.updateByPrimaryKeySelective(updateAftersalePo);
            return new ReturnObject();

        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /** 买家取消售后单和逻辑删除售后单
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject deleteAftersale(Long id, Long loginUser, String loginUserName) {
        try{
            AfterSalePo afterSalePo=afterSalePoMapper.selectByPrimaryKey(id);
            if(afterSalePo==null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
            }
            //验证删除者是否与售后单中的买家一致
            if(!afterSalePo.getCustomerId().equals(loginUser)){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"没有删除权限");
            }
            //若售后单已结束
            if(afterSalePo.getState().equals(Aftersale.State.END)){
                afterSalePo.setBeDeleted(BEDELETED);
            }
            else{
                afterSalePo.setState(Aftersale.State.CANCELLED.getCode());
            }

            setPoModifiedFields(afterSalePo,loginUser,loginUserName);

            afterSalePoMapper.updateByPrimaryKeySelective(afterSalePo);
            return new ReturnObject();

        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject fillCustomerLogSn(Aftersale aftersale) {
        try{
            AfterSalePo afterSalePo=afterSalePoMapper.selectByPrimaryKey(aftersale.getId());
            if(afterSalePo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
            }
            //验证修改者是否与售后单中的买家一致
            if(!afterSalePo.getCustomerId().equals(aftersale.getModifierId())){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"没有修改权限");
            }
            //判断售后单的状态是否可以修改
            if(((int)afterSalePo.getState() != Aftersale.State.TO_BE_DELIVERED_BY_CUSTOMER.getCode())){
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }

            AfterSalePo updateAftersalePo=cloneVo(aftersale,AfterSalePo.class);
            updateAftersalePo.setState(Aftersale.State.DELIVERED_BY_CUSTOMER.getCode());
            afterSalePoMapper.updateByPrimaryKeySelective(updateAftersalePo);
            return new ReturnObject();

        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /** 买家确认售后单结束
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmAftersaleByCustomer(Long id, Long loginUser, String loginUserName) {
        try{
            AfterSalePo afterSalePo=afterSalePoMapper.selectByPrimaryKey(id);
            if(afterSalePo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
            }
            //验证修改者是否与售后单中的买家一致
            if(!afterSalePo.getCustomerId().equals(loginUser)){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"没有修改权限");
            }
            //判断售后单的状态是否可以确认
            if(((int)afterSalePo.getState() != Aftersale.State.BE_DELIVERED_BY_SHOPKEEPER.getCode())){
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }
            afterSalePo.setState(Aftersale.State.END.getCode());
            setPoModifiedFields(afterSalePo,loginUser,loginUserName);

            afterSalePoMapper.updateByPrimaryKeySelective(afterSalePo);
            return new ReturnObject();
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /** 管理员同意/不同意（退款，换货，维修）
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmAftersaleByAdmin(Aftersale aftersale) {
        try{
            AfterSalePo afterSalePo=afterSalePoMapper.selectByPrimaryKey(aftersale.getId());
            if(afterSalePo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
            }
            //验证店铺id是否一致
            if(afterSalePo.getShopId()!=aftersale.getShopId()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"售后单id与店铺id不匹配");
            }
            //判断售后单的状态是否可以同意
            if(((int)afterSalePo.getState() != Aftersale.State.NEW.getCode())){
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }
            AfterSalePo updateAfterSalePo=cloneVo(aftersale,AfterSalePo.class);
            afterSalePoMapper.updateByPrimaryKeySelective(updateAfterSalePo);
            // 修改成功
            return new ReturnObject();

        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /** 店家确认收到买家的退（换）货
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmRecieveByShop(Aftersale aftersale) {
        try{
            AfterSalePo aftersalePo=cloneVo(aftersale,AfterSalePo.class);
            afterSalePoMapper.updateByPrimaryKeySelective(aftersalePo);
            return new ReturnObject();
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /** 店家寄出货物
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject deliverAgain(Aftersale aftersale) {
        try{
            ReturnObject returnObject=findAftersaleInShop(aftersale.getShopId(), aftersale.getId());
            if(returnObject==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"店铺不存在该售后单");
            }//判断状态
            AfterSalePo afterSalePo=(AfterSalePo) returnObject.getData();
            if(afterSalePo.getState()!=Aftersale.State.TO_BE_DELIVERED_BY_SHOPKEEPER.getCode()){
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }
            AfterSalePo updateAftersalePo=cloneVo(aftersale,AfterSalePo.class);
            updateAftersalePo.setState(Aftersale.State.BE_DELIVERED_BY_SHOPKEEPER.getCode());

            afterSalePoMapper.updateByPrimaryKeySelective(updateAftersalePo);
            return new ReturnObject();

        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }
    /** 查找店铺内该售后单
     * @author 张晖婧
     */
    public ReturnObject findAftersaleInShop(Long shopId, Long id) {
        try{
            AfterSalePoExample example=new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(id);
            criteria.andShopIdEqualTo(shopId);
            List<AfterSalePo> afterSalePos=afterSalePoMapper.selectByExample(example);
            if(afterSalePos.size()==0){
                return null;
            }else{
                return new ReturnObject(afterSalePos.get(0));
            }
            //criteria.and
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }
    /** 查找售后单序号对应的售后单
     */
    public ReturnObject findAftersaleByServiceSn(String serviceSn){
        try{
            AfterSalePoExample example=new AfterSalePoExample();
            AfterSalePoExample.Criteria criteria=example.createCriteria();
            criteria.andServiceSnEqualTo(serviceSn);
            List<AfterSalePo> afterSalePos=afterSalePoMapper.selectByExample(example);
            if(afterSalePos.size()==0){
                return null;
            }else{
                return new ReturnObject(afterSalePos.get(0));
            }
            //criteria.and
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }
    /**
     * 按照售后单id修改售后单
     */
    public ReturnObject updateByAftersaleIdSelective(AfterSalePo afterSalePo){
        try{
            int ret=afterSalePoMapper.updateByPrimaryKeySelective(afterSalePo);
            if(ret==0){
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
            }else{
                return new ReturnObject();
            }
        }catch (Exception e) {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }
}
