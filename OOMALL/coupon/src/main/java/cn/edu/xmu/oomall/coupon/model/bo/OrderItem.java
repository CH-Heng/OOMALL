package cn.edu.xmu.oomall.coupon.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xincong yao
 * @modified Zijun Min
 * @date 2020-11-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

	private Long id;
	private String name;
	private Long productId;
	private Long onsaleId;
	private Long categoryId;
	private Long quantity;
	private Long price;
	private Long discount;
	private Long couponActivityId;


}
