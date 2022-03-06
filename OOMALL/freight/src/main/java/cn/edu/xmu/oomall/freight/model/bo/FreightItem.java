package cn.edu.xmu.oomall.freight.model.bo;

import java.util.Objects;

/**
 * @author Gao Yanfeng
 * @date 2021/11/24
 */
public abstract class FreightItem {

    abstract public Long calculate(Integer amount, Integer unit);

    protected Long calculatePart(Integer level, Integer nextLevel, Integer weight, Integer unit, Long price) {
        if (weight <= level) {
            return 0L;
        } else if (Objects.isNull(nextLevel) || weight <= nextLevel) {
            return (weight - level + unit - 1) / unit * price;
        } else {
            return (nextLevel - level + unit - 1) / unit * price;
        }
    }
}
