package cn.edu.xmu.oomall.comment.dao;

import cn.edu.xmu.oomall.comment.mapper.CommentPoMapper;
import cn.edu.xmu.oomall.comment.model.bo.Comment;
import cn.edu.xmu.oomall.comment.model.po.CommentPo;
import cn.edu.xmu.oomall.comment.model.po.CommentPoExample;
import cn.edu.xmu.oomall.comment.model.vo.CommentRetVo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 * @date:2021/11/21
 **/
@Repository
public class CommentDao {
    @Autowired
    CommentPoMapper commentPoMapper;

    /**
     * 获取评论所有状态
     * @param[]
     * @return CommentPo
     */
    public ReturnObject getCommentStates(){
        List<Map<String,Object>> stateList=new ArrayList<>();
        for(Comment.State state:Comment.State.values()){
            Map<String,Object> temp=new HashMap<>();
            temp.put("code",state.getCode());
            temp.put("name",state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }


    /**
     * 买家新增一条评论
     * @param commentPo
     * @return
     */
    public InternalReturnObject insertComment(CommentPo commentPo){
        try{
            int ret=commentPoMapper.insertSelective(commentPo);
            if(ret==0){
                return new InternalReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new InternalReturnObject(commentPo);
        }catch (Exception e){
            return new InternalReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }



    /**
     * 分页查询product下所有已通过审核的评论
     *
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return ReturnObject<>
     */
    public ReturnObject selectAllPassCommentByProductId(Long productId,Integer pageNum,Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andProductIdEqualTo(productId);
            criteria.andStateEqualTo(Comment.State.PASS.getCode());
            commentPos=commentPoMapper.selectByExample(example);
            if (commentPos.isEmpty()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PageInfo pageInfo = new PageInfo(commentPos);
            ReturnObject ret = Common.getPageRetVo(new ReturnObject<>(pageInfo), CommentRetVo.class);
            return ret;
        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
    }
    /**
     * 分页查询店铺评论
     * @return ReturnObject<>
     */
    public ReturnObject selectCommentByShopId(Long shopId,Integer pageNum,Integer pageSize, Long userId){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            if (shopId!=0){
                criteria.andShopIdEqualTo(shopId);
            }
            commentPos=commentPoMapper.selectByExample(example);
            List<CommentPo> commentPos1 = new ArrayList<>();
            for (CommentPo commentPo : commentPos){
                if (userId.equals(commentPo.getAuditId())){
                    commentPos1.add(commentPo);
                }
            }
            PageInfo pageInfo = new PageInfo(commentPos1);
            ReturnObject ret = Common.getPageRetVo(new ReturnObject<>(pageInfo), CommentRetVo.class);
            return ret;
        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
    }

    /**
     * 修改评论状态
     */
    public ReturnObject updateCommentState(CommentPo commentPo){
        int ret;
        try{
            ret=commentPoMapper.updateByPrimaryKeySelective(commentPo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if (ret == 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {
            return new ReturnObject();
        }
    }


    /**
     * 买家查看自己的评价记录，包括评论状态
     */
    public ReturnObject selectAllCommentsOfUser(Long userId,Integer pageNum, Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andPostIdEqualTo(userId);
            commentPos=commentPoMapper.selectByExample(example);
            if (commentPos.isEmpty()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PageInfo pageInfo = new PageInfo(commentPos);
            ReturnObject ret = Common.getPageRetVo(new ReturnObject<>(pageInfo), CommentRetVo.class);
            return ret;
        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }

    }

    /**
     *管理员查看未审核评论列表
     */
    public ReturnObject selelctCommentsOfState(Byte state,Integer pageNum, Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andStateEqualTo(state);
            commentPos=commentPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(commentPos);
            ReturnObject ret = Common.getPageRetVo(new ReturnObject<>(pageInfo), CommentRetVo.class);
            return ret;
        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
    }
}


