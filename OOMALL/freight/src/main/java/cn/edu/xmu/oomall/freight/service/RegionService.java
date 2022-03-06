package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.model.bo.Region;
import cn.edu.xmu.oomall.freight.model.po.RegionPo;
import cn.edu.xmu.oomall.freight.model.vo.RegionRetVo;
import cn.edu.xmu.oomall.freight.model.vo.RegionVo;
import cn.edu.xmu.oomall.freight.model.vo.SimpleRegionRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author ziyi guo
 * @date 2021/11/10
 */
@Service
public class RegionService {

    private static final Byte STATE_EFFECTIVE=0;

    private static final Byte STATE_SUSPENDED=1;

    private static final Byte STATE_ABANDONED=2;

    @Autowired
    private RegionDao regionDao;

    /**
     * 通过id查找所有上级地区
     * @param id
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getParentRegion(Long id) {

        ReturnObject returnObject = regionDao.getParentRegion(id);

        return returnObject;
    }

    /**
     * 创建地区
     * @param regionVo,userId,userName
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject createRegion(RegionVo regionVo, Long pid, Long userId, String userName) {

        Region region = (Region) cloneVo(regionVo,Region.class);
        region.setPid(pid);
        region.setState(STATE_EFFECTIVE);

        ReturnObject retObj = regionDao.createRegion( (RegionPo) cloneVo(region, RegionPo.class), userId,userName);

        return retObj;
    }


    /**
     * 根据id查询子地区(管理员或普通)
     * @param id
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getChildRegion(Long id, Long did) {

        ReturnObject returnObject;
        if(did.equals(Long.valueOf(0))) {
            returnObject = regionDao.adminGetChildRegion(id);
        }
        else {
            returnObject = regionDao.getChildRegion(id);
        }

        return Common.getListRetVo(returnObject,RegionRetVo.class);
    }

    /**
     * 管理员修改地区
     * @param regionVo,userId,userName
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject modifyRegion(RegionVo regionVo, Long id, Long userId, String userName) {

        Region region = (Region) cloneVo(regionVo, Region.class);
        region.setId(id);

        return regionDao.modiRegion((RegionPo) cloneVo(region, RegionPo.class),userId,userName);
    }

    /**
     * 管理员废弃地区
     * @param id,userId,userName
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject abandonRegion(Long id, Long userId, String userName) {

        Region region=new Region();
        region.setId(id);
        region.setState(STATE_ABANDONED);

        return regionDao.abandonRegion( (RegionPo) cloneVo(region, RegionPo.class), userId,userName);
    }

    /**
     * 管理员停用地区
     * @param id,userId,userName
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject suspendRegion(Long id, Long userId, String userName) {

        Region region=new Region();
        region.setId(id);
        region.setState(STATE_SUSPENDED);

        return regionDao.modiStateRegion( (RegionPo) cloneVo(region, RegionPo.class), userId,userName);
    }

    /**
     * 管理员恢复地区
     * @param id,userId,userName
     * @return ReturnObject
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject resumeRegion(Long id, Long userId, String userName) {

        Region region=new Region();
        region.setId(id);
        region.setState(STATE_EFFECTIVE);

        return regionDao.modiStateRegion( (RegionPo) cloneVo(region, RegionPo.class), userId,userName);
    }

    /**
     * 根据id获取地区信息
     * @param id
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject getRegionInfo(Long id){
        return regionDao.getRegionById(id);
    }

    @Transactional(rollbackFor=Exception.class)
    public InternalReturnObject getRegionDetailInfo(Long id){
        ReturnObject retObj = regionDao.getRegionDetailById(id);
        if (retObj.getData() != null) {
            return new InternalReturnObject(retObj.getData());
        }
        return new InternalReturnObject(retObj.getCode().getCode(), retObj.getErrmsg());
    }

    /**
     * 返回简单地区
     */
    @Transactional(readOnly = true)
    public InternalReturnObject getRegion(Long id){
        ReturnObject ret = regionDao.getRegionById(id);
        if(ret.getCode()!= ReturnNo.OK){
            return new InternalReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode(),"操作的资源id不是自己的对象");
        }

        return new InternalReturnObject(cloneVo(ret.getData(), SimpleRegionRetVo.class));
    }


}
