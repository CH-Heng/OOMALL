package cn.edu.xmu.oomall.payment.util;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import io.swagger.models.auth.In;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/14 16:40
 **/
public class FileCommon {

    /**
     * 从url下载zip文件
     */
    public static ReturnObject downloadFlowBillsFromUrl(String weChatFlowBillsUrl,String fileName) throws IOException {
        URL url=new URL(weChatFlowBillsUrl);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        File file=new File(fileName);
        FileOutputStream fileOutputStream=new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len);
        }
        fileOutputStream.close();
        in.close();
        return new ReturnObject<>(file);
    }

    /**
     * 解压文件zip，返回List<File>，均为csv文件
     */
    public static ReturnObject unzipFiles(String path) throws Exception {
        File srcFile = new File(path);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        String destDirPath = path.replace(".zip", "");
        //创建压缩文件对象
        ZipFile zipFile = new ZipFile(srcFile, Charset.forName("GBK"));
        List<File>files=new ArrayList<>();
        //开始解压
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                srcFile.mkdirs();
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + "/" + entry.getName());
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
                // 将压缩文件内容写入到这个文件中
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(targetFile);
                int len;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                files.add(targetFile);
                // 关流顺序，先打开的后关闭
                fos.close();
                is.close();
            }
        }
        return new ReturnObject<>(files);
    }

    /**
     * 将支付流水单解析为List<T>
     */
    public static <T> ReturnObject<List<T>> parseFlowBill(String path, Class<T> clazz,Integer skipLines) throws IOException {
        ColumnPositionMappingStrategy<T> mapper = new ColumnPositionMappingStrategy<>();
        mapper.setType(clazz);
        CsvToBean<T> build =new CsvToBeanBuilder<T>(new FileReader(path)).withMappingStrategy(mapper)
                .withSkipLines(skipLines).withFilter(new CsvBeanFilter()).withSeparator(',').build();
        List<T> list=build.parse();
        return new ReturnObject<>(list);
    }
}
