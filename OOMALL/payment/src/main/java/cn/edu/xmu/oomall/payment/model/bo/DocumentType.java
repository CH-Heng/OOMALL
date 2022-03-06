package cn.edu.xmu.oomall.payment.model.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/12/18:34
 */
public enum DocumentType {
    ORDER((byte)0,"订单"),
    MARGIN((byte)1,"保证金"),
    ORDERHEAD((byte)2,"订单订金"),
    ORDERTAIL((byte)3,"订单尾款");
    private Byte code;
    private String description;

    DocumentType(Byte code, String description) {
        this.code=code;
        this.description=description;
    }


    public Byte getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public DocumentType getDescriptionByCode(Byte code){
        return stateMap.get(code);
    }
    private static final Map<Byte, DocumentType> stateMap;
    static {
        stateMap = new HashMap();
        for (DocumentType enum1 : values()) {
            stateMap.put(enum1.code, enum1);
        }
    }
}
