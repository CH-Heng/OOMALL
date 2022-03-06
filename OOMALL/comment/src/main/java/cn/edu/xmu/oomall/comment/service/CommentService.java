package cn.edu.xmu.oomall.comment.service;


import cn.edu.xmu.oomall.comment.dao.CommentDao;
import cn.edu.xmu.oomall.comment.model.bo.Comment;
import cn.edu.xmu.oomall.comment.model.po.CommentPo;
import cn.edu.xmu.oomall.comment.model.vo.*;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 * @date:2021/11/21
 **/
@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;


    /**
     * 获取评论所有状态
     *
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getCommentStates() {
        return commentDao.getCommentStates();
    }

    /**
     * 买家新增评论
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InternalReturnObject newComment(Long productId, CommentVo commentVo, Long loginUser, String loginUsername) {

        Long shopId = commentVo.getShopId();
        CommentPo commentPo = new CommentPo();

        commentPo.setProductId(productId);
        commentPo.setContent(commentVo.getContent());
        commentPo.setShopId(shopId);
        commentPo.setType(commentVo.getType().byteValue());
        commentPo.setState(Comment.State.NOT_AUDIT.getCode());
        commentPo.setPostId(loginUser);
        commentPo.setPostName(loginUsername);
        commentPo.setPostTime(LocalDateTime.now());
        setPoCreatedFields(commentPo, loginUser, loginUsername);
        InternalReturnObject ret_insert = commentDao.insertComment(commentPo);
        if (ret_insert.getErrno().equals(0)) {
            CommentRetVo commentRetVo = (CommentRetVo) cloneVo(commentPo, CommentRetVo.class);
            return new InternalReturnObject(commentRetVo);
        }
        return ret_insert;
    }

    /**
     * 分页查询商品下所有已通过审核的评论
     *
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectAllPassCommentByProductId(Long productId, Integer pageNum, Integer pageSize) {
        ReturnObject ret = commentDao.selectAllPassCommentByProductId(productId, pageNum, pageSize);
        return ret;
    }

    /**
     * 查询商铺评论
     *
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectAllPassCommentByShopId(Long shopId, Integer pageNum, Integer pageSize, Long userId) {
        ReturnObject ret = commentDao.selectCommentByShopId(shopId, pageNum, pageSize,userId);
        return ret ;
    }

    /**
     * 管理员审核评论
     *
     * @param id
     * @param conclusion
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmCommnets(Long did, Long id, CommentConclusionVo conclusion, Long loginUser, String loginUserName) {
        CommentPo commentPo = new CommentPo();
        commentPo.setId(id);
        commentPo.setState(conclusion.getConclusion() == true ? Comment.State.PASS.getCode() : Comment.State.FORBID.getCode());
        commentPo.setAuditId(loginUser);
        commentPo.setAuditName(loginUserName);
        commentPo.setAuditTime(LocalDateTime.now());
        setPoModifiedFields(commentPo, loginUser, loginUserName);
        ReturnObject ret = commentDao.updateCommentState(commentPo);
        return ret;
    }

    /**
     * 买家查看自己的评价记录，包括评论状态
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectAllCommentsOfUser(Long userId, Integer pageNum, Integer pageSize) {
        ReturnObject ret = commentDao.selectAllCommentsOfUser(userId, pageNum, pageSize);
        return ret;
    }

    /**
     * 管理员查看未审核评论列表
     *
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject selectCommentsOfState(Long did, Integer state, Integer pageNum, Integer pageSize) {
        ReturnObject ret = commentDao.selelctCommentsOfState(state.byteValue(), pageNum, pageSize);
        return ret;
    }


}

