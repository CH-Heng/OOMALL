package cn.edu.xmu.oomall.payment.microservice.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotifyReturnObject {
    private String code;
    private String message;
}
