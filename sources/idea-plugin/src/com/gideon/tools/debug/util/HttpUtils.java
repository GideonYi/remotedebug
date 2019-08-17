package com.gideon.tools.debug.util;

/**
 * @author yixianhai
 * @date 2019-08-15
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static String postRequest(String url, String classValue) throws UnsupportedEncodingException, IllegalStateException, IOException {
        HttpPost httppost = new HttpPost(url);
        // post 参数 传递
        List<BasicNameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("classValue", classValue)); // 参数

        httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); // 设置参数给Post

        // 执行
        HttpResponse response = httpClient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // System.out.println("Response content length: " +
            // entity.getContentLength());
            // 显示结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                //skip the empty line, maybe different in different OS
                if (!line.trim().isEmpty()) {
                    stringBuilder.append(line);
                    stringBuilder.append("\r\n");
                }
            }
            reader.close();
            return stringBuilder.toString();
        }
        else {
            return "return entity is null";
        }
    }

}

