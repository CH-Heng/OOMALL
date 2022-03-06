package cn.edu.xmu.oomall.payment.model.bo.flowbill;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/13 19:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeChatFlowBillItem {
    @CsvBindByPosition(position = 4)
    private String paymentOrRefund;
    @CsvBindByPosition(position = 5)
    private String amount;
    @CsvBindByPosition(position = 7)
    private String state;
    @CsvBindByPosition(position = 8)
    private String tradeSn;

    public void formatAmount(){
        if(!this.amount.isEmpty()){
            this.amount=this.amount.replace("¥","");
            this.amount=this.amount.replace(".","");
            this.amount=this.amount.replace(" ","");
            this.amount=this.amount.replace("\t","");
        }
        if(!this.tradeSn.isEmpty()){
            this.tradeSn=this.tradeSn.replace("\t","");
        }
    }
}