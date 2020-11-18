package com.xzh.gateway.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件加载
 *
 * @author 向振华
 * @date 2020/09/10 13:41
 */
public class FileLoaderUtils {

    /**
     * 读resources下文件
     *
     * @param path
     * @return
     */
    public static String read(String path) {
        StringBuilder sb = new StringBuilder();
        Resource resource = new ClassPathResource(path);
        try {
            InputStream is = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String data;
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
