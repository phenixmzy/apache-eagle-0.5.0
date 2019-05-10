package org.apache.eagle.alert.engine.publisher.external.openfalcon;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenFalconSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(OpenFalconSender.class);
    private OpenFalconContext context;
    private String threadName;

    public OpenFalconSender(OpenFalconContext context) {
        this.context = context;
        threadName = Thread.currentThread().getName();
        LOG.info("Initialized " + threadName + ": serverUrl is : " + this.context.getOpenFalconServerUrl());
        LOG.info("Initialized {}" + this.context.toString());
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
            HttpPost post = new HttpPost(this.context.getOpenFalconServerUrl());
            post.addHeader("Content-Type", "application/json");
            array.add(createSendMessage());

            StringEntity entity = new StringEntity(array.toString(), "utf-8");
            post.setEntity(entity);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response  = httpclient.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer res = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                res.append(line);
            }
            LOG.info("Send To Open-Falcon:{}!{}", res.toString(), this.context.toString());

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
        json.put("metric", this.context.getOpenFalconMetric());
        json.put("endpoint", this.context.getOpenFalconEndpoint());
        json.put("timestamp", this.context.getOpenFalconTimestamp());
        json.put("step", this.context.getOpenFalconStep());
        json.put("value",this.context.getOpenFalconValue());
        json.put("counterType", this.context.getOpenFalconCounterType());
        json.put("tag", this.context.getOpenFalconTag());
        return json;
    }
}
