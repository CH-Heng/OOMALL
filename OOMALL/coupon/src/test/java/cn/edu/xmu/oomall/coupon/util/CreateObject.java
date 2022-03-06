package cn.edu.xmu.oomall.coupon.util;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.coupon.microservice.vo.OnsaleVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.PageVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.coupon.microservice.vo.ShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qingguo Hu 22920192204208
 */
public class CreateObject {

    public static InternalReturnObject createOnsaleVo(Long id) {
        OnsaleVo onsaleVo = new OnsaleVo();
        ProductVo productVo = new ProductVo();
        productVo.setId(id);
        onsaleVo.setProduct(productVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }

    public static InternalReturnObject createOnsaleVoList1() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(1L);
        onsaleVo1.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo1);
        OnsaleVo onsaleVo2 = new OnsaleVo();
        onsaleVo2.setId(2L);
        onsaleVo2.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo2);
        OnsaleVo onsaleVo3 = new OnsaleVo();
        onsaleVo3.setId(3L);
        onsaleVo3.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo3);
        OnsaleVo onsaleVo4 = new OnsaleVo();
        onsaleVo4.setId(4L);
        onsaleVo4.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo4);
        OnsaleVo onsaleVo5 = new OnsaleVo();
        onsaleVo5.setId(5L);
        onsaleVo5.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo5);
        OnsaleVo onsaleVo6 = new OnsaleVo();
        onsaleVo6.setId(6L);
        onsaleVo6.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo6);
        OnsaleVo onsaleVo7 = new OnsaleVo();
        onsaleVo7.setId(7L);
        onsaleVo7.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo7);
        OnsaleVo onsaleVo8 = new OnsaleVo();
        onsaleVo8.setId(8L);
        onsaleVo8.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo8);
        OnsaleVo onsaleVo9 = new OnsaleVo();
        onsaleVo9.setId(9L);
        onsaleVo9.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo9);
        OnsaleVo onsaleVo10 = new OnsaleVo();
        onsaleVo10.setId(10L);
        onsaleVo10.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo10);
        OnsaleVo onsaleVo11 = new OnsaleVo();
        onsaleVo11.setId(11L);
        onsaleVo11.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo11);
        OnsaleVo onsaleVo12 = new OnsaleVo();
        onsaleVo12.setId(12L);
        onsaleVo12.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo12);
        PageVo pageVo = new PageVo(1,100,12,1,onSaleVoList);
        return new InternalReturnObject(pageVo);
    }

    public static InternalReturnObject createOnsaleVoList2() {
        return new InternalReturnObject<>(1, "NoData");
    }


    public static InternalReturnObject createOnsaleVoList3() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3913L);
        onsaleVo1.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo1);
        ReturnObject ret = new ReturnObject<>(new PageInfo<>(onSaleVoList));
        return new InternalReturnObject<>(Common.getPageRetVo(ret, OnsaleVo.class).getData());
    }

    public static InternalReturnObject createOnsaleVoList4() {
        List<OnsaleVo> onSaleVoList = new ArrayList<>();
        OnsaleVo onsaleVo1 = new OnsaleVo();
        onsaleVo1.setId(3914L);
        onsaleVo1.setState(OnsaleVo.State.ONLINE.getCode());
        onSaleVoList.add(onsaleVo1);
        ReturnObject ret = new ReturnObject<>(new PageInfo<>(onSaleVoList));
        return new InternalReturnObject<>(Common.getPageRetVo(ret, OnsaleVo.class).getData());
    }

    public static InternalReturnObject createOnsaleVo1() {
        return new InternalReturnObject<>(1, "NoData");
    }

    public static InternalReturnObject createOnsaleVo2() {
        OnsaleVo onsaleVo = new OnsaleVo();
        ShopVo shopVo = new ShopVo();
        ProductVo productVo = new ProductVo();
        productVo.setId(10L);
        onsaleVo.setProduct(productVo);
        shopVo.setId(3L);
        onsaleVo.setId(1L);
        onsaleVo.setShop(shopVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }

    public static InternalReturnObject<OnsaleVo> createOnsaleVo3() {
        OnsaleVo onsaleVo = new OnsaleVo();
        ShopVo shopVo = new ShopVo();
        shopVo.setId(2L);
        onsaleVo.setId(2L);
        onsaleVo.setShop(shopVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }

    public static InternalReturnObject<OnsaleVo> createOnsaleVo4() {
        OnsaleVo onsaleVo = new OnsaleVo();
        ShopVo shopVo = new ShopVo();
        shopVo.setId(2L);
        onsaleVo.setId(3912L);
        onsaleVo.setShop(shopVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }

    public static InternalReturnObject<OnsaleVo> createOnsaleVo5() {
        OnsaleVo onsaleVo = new OnsaleVo();
        ShopVo shopVo = new ShopVo();
        ProductVo productVo = new ProductVo();
        productVo.setId(10L);
        shopVo.setId(3L);
        onsaleVo.setId(9L);
        onsaleVo.setProduct(productVo);
        onsaleVo.setShop(shopVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }

    public static InternalReturnObject<OnsaleVo> createOnsaleVo6() {
        OnsaleVo onsaleVo = new OnsaleVo();
        ShopVo shopVo = new ShopVo();
        ProductVo productVo = new ProductVo();
        productVo.setId(10L);
        shopVo.setId(1L);
        onsaleVo.setId(12L);
        onsaleVo.setProduct(productVo);
        onsaleVo.setShop(shopVo);
        onsaleVo.setState(OnsaleVo.State.ONLINE.getCode());
        return new InternalReturnObject<>(onsaleVo);
    }
}
