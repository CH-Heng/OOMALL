package cn.edu.xmu.oomall.liquidation.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.liquidation.mapper.ExpenditureItemPoMapper;
import cn.edu.xmu.oomall.liquidation.mapper.LiquidationPoMapper;
import cn.edu.xmu.oomall.liquidation.mapper.RevenueItemPoMapper;
import cn.edu.xmu.oomall.liquidation.model.bo.ExpenditureItem;
import cn.edu.xmu.oomall.liquidation.model.bo.Liquidation;
import cn.edu.xmu.oomall.liquidation.model.bo.RevenueItem;
import cn.edu.xmu.oomall.liquidation.model.po.*;
import cn.edu.xmu.oomall.liquidation.model.vo.ExpenditureItemRetVo;
import cn.edu.xmu.oomall.liquidation.model.vo.RevenueItemRetVo;
import cn.edu.xmu.oomall.liquidation.model.vo.SimpleLiquidationRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author wwk
 * @author wyg
 * @date 2021/12/15
 */
@Repository
public class LiquidationDao {

    @Autowired
    private LiquidationPoMapper liquidationPoMapper;

    @Autowired
    private RevenueItemPoMapper revenueItemPoMapper;

    @Autowired
    private ExpenditureItemPoMapper expenditureItemPoMapper;

    /**
     * 用id获取清算单
     */
    public ReturnObject<Liquidation> getLiquidationById(Long id) {
        Liquidation liquidation;
        try {
            LiquidationPo liquidationPo = liquidationPoMapper.selectByPrimaryKey(id);
            if(liquidationPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            liquidation = cloneVo(liquidationPo, Liquidation.class);
            return new ReturnObject(liquidation);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 用id获取进账单
     */
    public ReturnObject<RevenueItem> getRevenueItemById(Long id) {
        RevenueItem revenueItem;
        try {
            RevenueItemPo revenueItemPo = revenueItemPoMapper.selectByPrimaryKey(id);
            if(revenueItemPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            revenueItem = cloneVo(revenueItemPo, RevenueItem.class);
            return new ReturnObject(revenueItem);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 用id获取出账单
     */
    public ReturnObject<ExpenditureItem> getExpenditureItemById(Long id) {
        ExpenditureItem expenditureItem;
        try {
            ExpenditureItemPo expenditureItemPo = expenditureItemPoMapper.selectByPrimaryKey(id);
            if(expenditureItemPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            expenditureItem = cloneVo(expenditureItemPo, ExpenditureItem.class);
            return new ReturnObject(expenditureItem);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 模糊查找Liquidation(现在可用shopId、state和创建时间查找)
     * @return
     */
    public ReturnObject getLiquidations(Liquidation liquidation,LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize, true, true, true);
            LiquidationPoExample liquidationPoExample = new LiquidationPoExample();
            LiquidationPoExample.Criteria c = liquidationPoExample.createCriteria();
            if (liquidation.getShopId() != null) {
                c.andShopIdEqualTo(liquidation.getShopId());
            }
            if (beginTime != null) {
                c.andLiquidDateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime != null) {
                c.andLiquidDateLessThanOrEqualTo(endTime);
            }
            if (liquidation.getState() != null) {
                c.andStateEqualTo(liquidation.getState());
            }
            List<LiquidationPo> liquidationPos = liquidationPoMapper.selectByExample(liquidationPoExample);
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<>(liquidationPos));
            return ret;
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 根据shopId获得当日的liquidation
     *
     * @return Liquidation
     */
    public ReturnObject getLiquidationCertainDayByShopId(Long shopId, LocalDateTime now) {
        try {
            LiquidationPoExample liquidationPoExample = new LiquidationPoExample();
            LiquidationPoExample.Criteria criteria = liquidationPoExample.createCriteria();

            LocalDateTime begin = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59);

            criteria.andShopIdEqualTo(shopId);
            criteria.andLiquidDateGreaterThanOrEqualTo(begin);
            criteria.andLiquidDateLessThanOrEqualTo(end);

            List<LiquidationPo> pos = liquidationPoMapper.selectByExample(liquidationPoExample);
            if (pos.isEmpty()) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return Common.getRetVo(new ReturnObject(pos.get(0)), Liquidation.class);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 模糊查找Revenue(现在可用sharerId、shopId、paymentId、orderId、productId、orderItemId和创建时间查找）
     *
     * @return
     */
    public ReturnObject getRevenues(RevenueItem bo, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        try {
            RevenueItemPoExample revenueItemPoExample = new RevenueItemPoExample();
            RevenueItemPoExample.Criteria c = revenueItemPoExample.createCriteria();
            if (bo.getSharerId() != null) {
                c.andSharerIdEqualTo(bo.getSharerId());
            }
            if (bo.getShopId() != null) {
                c.andShopIdEqualTo(bo.getShopId());
            }
            if(bo.getPaymentId()!=null){
                c.andPaymentIdEqualTo(bo.getPaymentId());
            }
            if (bo.getOrderId() != null) {
                c.andOrderIdEqualTo(bo.getOrderId());
            }
            if (beginTime != null) {
                c.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime != null) {
                c.andGmtCreateLessThanOrEqualTo(endTime);
            }
            if (bo.getProductId() != null) {
                c.andProductIdEqualTo(bo.getProductId());
            }
            if(bo.getOrderitemId()!=null){
                c.andOrderitemIdEqualTo(bo.getOrderitemId());
            }

            if (page != null && pageSize != null) {
                PageHelper.startPage(page, pageSize);
                List<RevenueItemPo> pos = revenueItemPoMapper.selectByExample(revenueItemPoExample);
                ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<>(pos));
                return ret;
            } else {
                List<RevenueItemPo> pos = revenueItemPoMapper.selectByExample(revenueItemPoExample);
                if (pos.isEmpty()) {
                    return new ReturnObject(new ArrayList<RevenueItem>());
                }
                return Common.getListRetVo(new ReturnObject(pos), RevenueItem.class);
            }
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 模糊查找Expenditure(现在可用sharerId、shopId、paymentId、orderId、productId、liquidId、orderItemId和创建时间查找)
     *
     * @return
     */
    public ReturnObject getExpenditure(ExpenditureItem bo, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        try {
            ExpenditureItemPoExample expenditureItemPoExample = new ExpenditureItemPoExample();
            ExpenditureItemPoExample.Criteria c = expenditureItemPoExample.createCriteria();
            if (bo.getSharerId() != null) {
                c.andSharerIdEqualTo(bo.getSharerId());
            }
            if (bo.getShopId() != null) {
                c.andShopIdEqualTo(bo.getShopId());
            }
            if (beginTime != null) {
                c.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime != null) {
                c.andGmtCreateLessThanOrEqualTo(endTime);
            }
            if (bo.getOrderId() != null) {
                c.andOrderIdEqualTo(bo.getOrderId());
            }
            if (bo.getProductId() != null) {
                c.andProductIdEqualTo(bo.getProductId());
            }
            if (bo.getLiquidId() != null) {
                c.andLiquidIdEqualTo(bo.getLiquidId());
            }
            if(bo.getOrderitemId()!=null){
                c.andOrderitemIdEqualTo(bo.getOrderitemId());
            }
            if (page != null && pageSize != null) {
                PageHelper.startPage(page, pageSize, true, true, true);
                List<ExpenditureItemPo> pos = expenditureItemPoMapper.selectByExample(expenditureItemPoExample);
                ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<>(pos));
                return ret;
            } else {
                List<ExpenditureItemPo> pos = expenditureItemPoMapper.selectByExample(expenditureItemPoExample);
                if (pos.isEmpty()) {
                    return new ReturnObject(new ArrayList<ExpenditureItem>());
                }
                return Common.getListRetVo(new ReturnObject(pos), ExpenditureItem.class);
            }
        } catch (Exception exception) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, exception.getMessage());
        }
    }

    /**
     * 匹配shopId和liquidationId
     */
    public ReturnObject matchShopAndLiquidation(Long shopId, Long id) {
        if (shopId == 0) {
            return new ReturnObject();
        }
        try {
            LiquidationPo liquidationPo = liquidationPoMapper.selectByPrimaryKey(id);
            if(liquidationPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if (shopId.equals(liquidationPo.getShopId())) {
                return new ReturnObject();
            }
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 匹配shopId和expenditureItemId
     */
    public ReturnObject matchShopAndExpenditure(Long shopId, Long id) {
        if (shopId == 0) {
            return new ReturnObject();
        }
        try {
            ExpenditureItemPo expenditureItemPo = expenditureItemPoMapper.selectByPrimaryKey(id);
            if(expenditureItemPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if (shopId.equals(expenditureItemPo.getShopId())) {
                return new ReturnObject();
            }
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 添加清算单
     */
    public ReturnObject insertLiquidation(Liquidation bo) {
        try {
            LiquidationPo po = cloneVo(bo, LiquidationPo.class);

            int ret = liquidationPoMapper.insert(po);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return Common.getRetVo(new ReturnObject(po), Liquidation.class);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 添加进账单
     */
    public ReturnObject insertRevenueItem(RevenueItem bo) {
        try {
            RevenueItemPo po = cloneVo(bo, RevenueItemPo.class);

            int ret = revenueItemPoMapper.insert(po);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return Common.getRetVo(new ReturnObject(po), RevenueItem.class);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 修改清算单
     */
    public ReturnObject updateLiquidation(Liquidation bo) {
        try {
            LiquidationPo po = cloneVo(bo, LiquidationPo.class);

            int ret = liquidationPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject();
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 添加进账单
     */
    public ReturnObject insertExpenditureItem(ExpenditureItem bo) {
        try {
            ExpenditureItemPo po = cloneVo(bo, ExpenditureItemPo.class);

            int ret = expenditureItemPoMapper.insert(po);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return Common.getRetVo(new ReturnObject(po), ExpenditureItem.class);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
