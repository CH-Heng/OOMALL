package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.shop.dao.CategoryDao;
import cn.edu.xmu.oomall.shop.model.bo.Category;
import cn.edu.xmu.oomall.shop.model.po.CategoryPo;
import cn.edu.xmu.oomall.shop.model.vo.CategoryRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * 商品分类Service
 *
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/14
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 通过id查找子分类
     * 若为二级分类返回空数组
     *
     * @param id
     * @return ReturnObject
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getSubCategories(Long id) {
        if (categoryDao.getCategoryById(id).getData() == null && id > 0) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return Common.getListRetVo(categoryDao.getSubCategories(id), CategoryRetVo.class);
    }

    /**
     * 通过id查找子分类
     * 若为二级分类返回空数组
     *
     * @param id
     * @return ReturnObject
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getParentCategoryById(Long id) {
        /* 获取该分类的信息 */
        ReturnObject ret = categoryDao.getCategoryById(id);
        if (ret.getData() == null) {
            return ret;
        }

        /* 获取父分类的信息 */
        Long pid = ((Category) (ret.getData())).getPid();
        ret = categoryDao.getCategoryById(pid);
        if (ret.getData() == null) {
            return ret;
        } else {
            Category pCategory = (Category) ret.getData();
            return new ReturnObject(cloneVo(pCategory, CategoryRetVo.class));
        }
    }

    /**
     * 创建新分类
     * id=0为一级分类，>0为二级分类（考虑父类别为二级分类或单独分类的异常情况）
     * id<0在controller层拦截
     *
     * @param id
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject newCategory(Long id, Category category, Long createId, String createName) {
        Category pCategory = (Category) categoryDao.getCategoryById(id).getData();
        if (pCategory == null && id > 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        /** 防止出现3级类别的情形或将单独类别（-1）作为父类别 **/
        if (pCategory != null && pCategory.getPid() != 0L) {
            return new ReturnObject(ReturnNo.SHOP_CATEGORY_NOTPERMIT);
        }
        if (categoryDao.hasSameName(category.getName())) {
            return new ReturnObject(ReturnNo.GOODS_CATEGORY_SAME);
        }
        CategoryPo categoryPo = (CategoryPo) cloneVo(category, CategoryPo.class);
        setPoCreatedFields(categoryPo, createId, createName);
        categoryPo.setPid(id.longValue());
        ReturnObject ret = categoryDao.insertCategory(categoryPo);
        return ret;
    }

    /**
     * 更新分类
     *
     * @param id,category,modifyId,modiName
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject changeCategory(Long id, Category category, Long modifyId, String modiName) {
        if (categoryDao.getCategoryById(id).getData() == null) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        if (category.getName()!=null&&categoryDao.hasSameName(category.getName())) {
            return new ReturnObject<>(ReturnNo.GOODS_CATEGORY_SAME);
        }
        CategoryPo po = cloneVo(category, CategoryPo.class);
        setPoModifiedFields(po, modifyId, modiName);
        po.setId(id.longValue());

        ReturnObject ret = categoryDao.updateCategory(po);
        return ret;
    }

    /**
     * 删除分类
     *
     * @param id
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteCategoryById(Long id) {
        /** 若有子类别，将子类别设为单独分类（pid=-1）**/
        var sub = categoryDao.getSubCategories(id);
        if (sub.getCode().equals(ReturnNo.OK)) {
            List<Category> categoryList = sub.getData();
            for (Category category : categoryList) {
                category.setPid(-1L);
                CategoryPo categoryPo = cloneVo(category, CategoryPo.class);
                categoryDao.updateCategory(categoryPo);
            }
        }
        ReturnObject ret = categoryDao.deleteCategoryById(id.longValue());
        return ret;
    }

    public ReturnObject getCategoryById(Long id) {

        ReturnObject ret = categoryDao.getCategoryById(id);
        if(ret.getCode()!=ReturnNo.OK){
            return ret;
        }
        Category vo = cloneVo(ret.getData(), Category.class);
        return new ReturnObject<>(vo);
    }
}
