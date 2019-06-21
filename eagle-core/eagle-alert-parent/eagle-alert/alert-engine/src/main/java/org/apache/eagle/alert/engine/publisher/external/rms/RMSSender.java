package org.apache.eagle.alert.engine.publisher.external.rms;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RMSSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RMSSender.class);
    private RMSContext context;
    private String threadName;

    public RMSSender(RMSContext context) {
        this.context = context;
        threadName = Thread.currentThread().getName();
    }

    @Override
    public void run() {
        try {
            executor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executor() throws Exception {
        CloseableHttpResponse response = null;
        JSONArray array = new JSONArray();

        try {
            HttpPost post = new HttpPost(this.context.getRmsServerUrl());
            post.addHeader("Content-Type", "application/json");
            array.add(createSendMessage());
            LOG.info("Send To RMS msg: {}", array.toJSONString());
            StringEntity entity = new StringEntity(array.toJSONString(), "utf-8");
            post.setEntity(entity);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response  = httpclient.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer res = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                res.append(line);
            }
            LOG.info("Send To RMS:{}!{}", res.toString(), this.context.toString());

        } catch (IOException e) {
            LOG.error("Failed to execute http get request!Send To Open-Falcon Failed. ", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private JSONObject createSendMessage() {
        JSONObject json = new JSONObject();
        json.put("token", context.getToken());
        json.put("point_code", context.getPointCode());
        json.put("error_code", context.getErrorCode());
        json.put("server_ip", context.getServerIp());
        json.put("server_name", context.getServerName());

        json.put("notice_time", context.getNoticeTime());
        json.put("content",context.getContent());
        json.put("level", context.getLevel());
        return json;
    }

    public static void main(String[] args) {
        RMSContext context = new RMSContext();
        context.setToken("6mNoX3qpCfFnvmsZTzMn");
        context.setPointCode("WFZ25228");
        context.setErrorCode("100007");
        context.setServerIp("127.0.0.1");
        context.setServerName("apache eagle");
        context.setRmsServerUrl("http://www.rms110.com/api-source?project_code=open-falcon-alarm");
        context.setLevel(1);
        context.setNoticeTime("2019-06-21 13:59:11");
        String cont = "{\"allowed\":true,\"cmd\":\"open\",\"dst\":null,\"host\":\"10.62.36.208\",\"securityZone\":\"NA\",\"sensitivityType\":\"NA\",\"src\":\"/user/spark/history/application_1559269751135_0201_1.lz4.inprogress\",\"timestamp\":1561096751363,\"user\":\"root\"}";
        context.setContent(cont);

        RMSSender sender = new RMSSender(context);
        Thread t = new Thread(sender);
        t.start();
    }
}
