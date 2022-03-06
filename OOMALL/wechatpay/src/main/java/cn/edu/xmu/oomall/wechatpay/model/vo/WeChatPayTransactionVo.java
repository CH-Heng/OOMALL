package cn.edu.xmu.oomall.wechatpay.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WeChatPayTransactionVo {

    @NotBlank
    private String appid;

    @NotBlank
    private String mchid;

    @NotBlank
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime timeExpire;

    @NotBlank
    private String outTradeNo;

    @NotNull
    private TransactionAmountVo amount;

    @NotNull
    private PayerVo payer;

    @NotBlank
    private String notifyUrl;

}
