package cn.edu.xmu.oomall.liquidation.util.base;

public interface Factory<T> {

    T create(Long id);
}
