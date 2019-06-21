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
            StringEntity entity = new StringEntity(array.toJSONString(), "utf-8");
            post.setEntity(entity);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response  = httpclient.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer responseContent = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                responseContent.append(line);
            }
            LOG.info("Send To RMS:Send To RMS msg[{}] and responses[{}]", responseContent.toString(), this.context.toString());

        } catch (IOException e) {
            LOG.error("Failed to execute http get request!Send To Open-Falcon Failed. ", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private JSONObject createSendMessage() {
        JSONObject data = new JSONObject();
        data.put("point_code", context.getPointCode());
        data.put("error_code", context.getErrorCode());
        data.put("server_ip", context.getServerIp());
        data.put("server_name", context.getServerName());
        data.put("notice_time", context.getNoticeTime());
        data.put("content",context.getContent());
        data.put("level", context.getLevel());

        JSONObject json = new JSONObject();
        json.put("key", context.getToken());
        json.put("data", data);

        return json;
    }
}
