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
import org.apache.commons.codec.digest.DigestUtils;

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
        try {
            HttpPost post = new HttpPost(this.context.getRmsServerUrl());
            post.addHeader("Content-Type", "application/json");
            String requstData = createSendMessage();
            StringEntity entity = new StringEntity(requstData, "utf-8");
            post.setEntity(entity);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response  = httpclient.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer responseContent = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                responseContent.append(line);
            }
            LOG.info("SenderThread Send msg To RMS. msg:[{}]. responses:[{}]", requstData, responseContent.toString());

        } catch (IOException e) {
            LOG.error("Failed to execute http get request!Send To Open-Falcon Failed. ", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private String createSendMessage() {
        JSONObject dataJson = new JSONObject();

        JSONObject item = new JSONObject();
        item.put("point_code", context.getPointCode());
        item.put("error_code", context.getErrorCode());
        item.put("server_ip", context.getServerIp());
        item.put("server_name", context.getServerName());
        item.put("notice_time", context.getNoticeTime());
        item.put("content",context.getContent());
        item.put("level", context.getLevel());

        JSONArray dataArray = new JSONArray();
        dataArray.add(item);

        dataJson.put("token", getTokenByKeyAndDataJson(context.getKey(), dataArray.toJSONString()));
        dataJson.put("data", dataArray.toJSONString());

        /*StringBuilder params = new StringBuilder("token=");
        params.append(getTokenByKeyAndDataJson(context.getKey(), dataArray.toJSONString()))
                .append("&data=").append(dataArray.toJSONString());*/

        return dataJson.toJSONString();
    }

    public String getTokenByKeyAndDataJson(String key, String dataJson) {
        StringBuilder data = new StringBuilder(key);
        data.append(dataJson);
        return DigestUtils.md5Hex(data.toString());
    }
}
