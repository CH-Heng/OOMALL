package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.FreightModelDao;
import cn.edu.xmu.oomall.freight.dao.PieceFreightDao;
import cn.edu.xmu.oomall.freight.model.bo.FreightModel;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreight;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightRetVo;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author 高艺桐 22920192204199
 */
@Service
public class PieceFreightService {
    @Autowired
    private PieceFreightDao pieceFreightDao;
    @Autowired
    private FreightModelDao freightModelDao;
    private static final Byte TYPE_WEIGHT=0;
    private static final Byte TYPE_PIECE=1;

    /**
     * 新增件数运费模板
     * @param userId
     * @param userName
     * @param pieceFreight
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addPieceFreight( String userName,Long userId, PieceFreight pieceFreight) {
        FreightModel freightModel=(FreightModel)freightModelDao.selectFreightModelById(pieceFreight.getFreightModelId()).getData();
        if(freightModel==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if(!freightModel.getType().equals(TYPE_PIECE))
        {
            return new ReturnObject(ReturnNo.FREIGHT_WRONGTYPE);

        }
        ReturnObject returnObject= pieceFreightDao.addPieceFreight(pieceFreight,userId, userName);
        PieceFreight returnPieceFreight=(PieceFreight)returnObject.getData();
        if (returnPieceFreight == null) {
            return returnObject;
        }
        PieceFreightRetVo pieceFreightRetVo = (PieceFreightRetVo) cloneVo(returnPieceFreight, PieceFreightRetVo.class);
        return new ReturnObject(pieceFreightRetVo);
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     * @param freightModelId
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getPieceFreight(Long freightModelId, Integer page, Integer pageSize) {
        FreightModel freightModel = (FreightModel) freightModelDao.selectFreightModelById(freightModelId).getData();
        if (freightModel==null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject pageInfoReturnObject = pieceFreightDao.getPieceFreight(freightModelId, page, pageSize);
        if (pageInfoReturnObject.getData() == null) {
            return pageInfoReturnObject;
        }
        return pageInfoReturnObject;
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deletePieceFreight(Long id) {
        ReturnObject returnObject = pieceFreightDao.deletePieceFreight(id);
        return returnObject;
    }

    /**
     * 店家或管理员修改件数运费模板明细
     *
     * @param pieceFreightVo
     * @param id
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updatePieceFreight(PieceFreightVo pieceFreightVo, Long id, Long userId, String userName) {
        PieceFreight pieceFreight = (PieceFreight) cloneVo(pieceFreightVo, PieceFreight.class);
        pieceFreight.setId(id);
        ReturnObject returnObject = pieceFreightDao.updatePieceFreight(pieceFreight, userId, userName);
        return returnObject;

    }


}
