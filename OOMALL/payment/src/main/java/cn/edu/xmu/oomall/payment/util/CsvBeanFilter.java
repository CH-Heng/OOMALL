package cn.edu.xmu.oomall.payment.util;

import com.opencsv.bean.CsvToBeanFilter;

import java.util.regex.Pattern;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/13 19:16
 * @description 过滤器，可以忽略一些文件头
 **/
public class CsvBeanFilter implements CsvToBeanFilter {
    @Override
    public boolean allowLine(String[] lines) {
        String pattern="#{1}[^#]+";
        return !Pattern.matches(pattern, lines[0]);
    }
}
