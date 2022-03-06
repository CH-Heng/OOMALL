package cn.edu.xmu.oomall.share.dao;

import cn.edu.xmu.oomall.share.mapper.SharePoMapper;
import cn.edu.xmu.oomall.share.mapper.SuccessfulSharePoMapper;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.SuccessfulShare;
import cn.edu.xmu.oomall.share.model.po.SharePo;
import cn.edu.xmu.oomall.share.model.po.SharePoExample;
import cn.edu.xmu.oomall.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.oomall.share.model.po.SuccessfulSharePoExample;
import cn.edu.xmu.oomall.share.model.vo.ShareRetVo;
import cn.edu.xmu.oomall.share.model.vo.SimpleObjectVo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class ShareDao {
    @Autowired
    private SharePoMapper sharePoMapper;

    @Autowired
    SuccessfulSharePoMapper successfulSharePoMapper;

    public ReturnObject insertShare(Share share) {
        try
        {
            SharePo sharePo= cloneVo(share,SharePo.class);
            sharePoMapper.insert(sharePo);
            ShareRetVo shareRetVo=cloneVo(sharePo, ShareRetVo.class);
            shareRetVo.setSharer(new SimpleObjectVo(sharePo.getCreatorId(),sharePo.getCreatorName()));
            shareRetVo.setCreator(new SimpleObjectVo(sharePo.getCreatorId(),sharePo.getCreatorName()));
            shareRetVo.setModifier(new SimpleObjectVo(sharePo.getModifierId(),sharePo.getModifierName()));
            return new ReturnObject(shareRetVo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject insertSuccessfulShare(SuccessfulShare successfulShare) {
        try
        {
            SuccessfulSharePo successfulSharePo= cloneVo(successfulShare,SuccessfulSharePo.class);
            successfulSharePoMapper.insert(successfulSharePo);
            return new ReturnObject();
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getShares(Long productId, LocalDateTime beginTime, LocalDateTime endTime, Long loginUserId, Integer page, Integer pageSize) {
        try {

            SharePoExample sharePoExample = new SharePoExample();
            SharePoExample.Criteria criteria = sharePoExample.createCriteria();
            if (beginTime!=null){
                criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime!=null){
                criteria.andGmtCreateLessThanOrEqualTo(endTime);
            }
            if (loginUserId!=null){
                criteria.andSharerIdEqualTo(loginUserId);
            }
            if (productId!=null){
                criteria.andProductIdEqualTo(productId);
            }
            PageHelper.startPage(page, pageSize);
            List<SharePo> sharePoList = sharePoMapper.selectByExample(sharePoExample);
            ReturnObject<PageInfo<Object>> ret=new ReturnObject(new PageInfo<SharePo>(sharePoList));
            return ret;
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getShareById(Long id){
        try {
            SharePo sharePo=sharePoMapper.selectByPrimaryKey(id);
            if (sharePo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(sharePo);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    //不分页返回list，否则pageInfo
    public ReturnObject getSuccessfulShares(SuccessfulShare successfulShare, LocalDateTime beginTime, LocalDateTime endTime, Long loginUserId, Integer page, Integer pageSize) {
        try {
            SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
            SuccessfulSharePoExample.Criteria criteria1 = successfulSharePoExample.createCriteria();

            if (loginUserId!=null){
                SharePoExample sharePoExample=new SharePoExample();
                SharePoExample.Criteria criteria=sharePoExample.createCriteria();
                criteria.andSharerIdEqualTo(loginUserId);
                List<SharePo> sharePoList=sharePoMapper.selectByExample(sharePoExample);
                List<Long> shareIdList=new ArrayList<>(sharePoList.stream().map(x->x.getId()).collect(Collectors.toSet()));
                if (shareIdList.size()>0){
                    criteria1.andShareIdIn(shareIdList);
                }else {
                    criteria1.andStateEqualTo((byte)10);
                }
            }

            if (beginTime!=null){
                criteria1.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime!=null){
                criteria1.andGmtCreateLessThanOrEqualTo(endTime);
            }
            if (successfulShare.getProductId()!=null){
                criteria1.andProductIdEqualTo(successfulShare.getProductId());
            }
            if (successfulShare.getOnsaleId()!=null) {
                criteria1.andOnsaleIdEqualTo(successfulShare.getOnsaleId());
            }
            if (successfulShare.getState()!=null){
                criteria1.andStateEqualTo(successfulShare.getState());
            }

            if (page==null&&pageSize==null){
                List<SuccessfulSharePo> successfulSharePoList = successfulSharePoMapper.selectByExample(successfulSharePoExample);
                return new ReturnObject(successfulSharePoList);
            }

            PageHelper.startPage(page, pageSize);
            List<SuccessfulSharePo> successfulSharePoList = successfulSharePoMapper.selectByExample(successfulSharePoExample);
            ReturnObject<PageInfo<Object>> ret=new ReturnObject(new PageInfo<SuccessfulSharePo>(successfulSharePoList));
            return ret;
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject setSuccessfulShareState(Long id, Byte state) {
        try {
            SuccessfulSharePo successfulSharePo=new SuccessfulSharePo();
            successfulSharePo.setId(id);
            successfulSharePo.setState(state);
            int ret=successfulSharePoMapper.updateByPrimaryKeySelective(successfulSharePo);
            if (ret==0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"操作的资源id不存在");
            }
            return new ReturnObject();
        }catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
