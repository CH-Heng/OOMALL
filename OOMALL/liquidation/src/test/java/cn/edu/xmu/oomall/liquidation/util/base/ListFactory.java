package cn.edu.xmu.oomall.liquidation.util.base;

import java.util.ArrayList;
import java.util.List;

public class ListFactory {

    private Factory factory;

    public ListFactory(Factory factory) {
        this.factory = factory;
    }

    public List create(int length) {
        List ret = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            ret.add(factory.create((long) (i + 1)));
        }
        return ret;
    }

    public static List create(Factory f, int length) {
        List ret = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            ret.add(f.create((long) (i + 1)));
        }
        return ret;
    }
}
