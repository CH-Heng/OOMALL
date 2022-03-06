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
public class AliPayFlowBillItem {
    @CsvBindByPosition(position = 2)
    private String tradeSn;
    @CsvBindByPosition(position = 6)
    private Double revenue;
    @CsvBindByPosition(position = 7)
    private Double expenditure;

    public void formatAmount(){
        if(!this.tradeSn.isEmpty()){
            this.tradeSn=this.tradeSn.replace("\t","");
        }
        if(this.revenue!=null){
            this.revenue*=100;
            this.revenue=Math.abs(this.revenue);
        }
        if(this.expenditure!=null){
            this.expenditure*=100;
            this.expenditure=Math.abs(this.expenditure);
        }
    }
}
