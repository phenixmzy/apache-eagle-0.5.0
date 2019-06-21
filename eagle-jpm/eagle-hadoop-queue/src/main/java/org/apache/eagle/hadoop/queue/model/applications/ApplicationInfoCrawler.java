package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.hadoop.queue.common.HadoopYarnResourceUtils;
import org.apache.eagle.hadoop.queue.common.YarnClusterResourceURLBuilder;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApplicationInfoCrawler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationInfoCrawler.class);
    private String urlString;
    private ApplicationInfoParseListener listener;

    public ApplicationInfoCrawler(String site, String baseUrl, SpoutOutputCollector collector) {
        this.urlString = YarnClusterResourceURLBuilder.buildRunningAppsURL(baseUrl);
        this.listener = new ApplicationInfoParseListener(site, collector);
    }

    @Override
    public void run() {
        try {
            //https://some.server.address:50030/ws/v1/cluster/scheduler?anonymous=true
            logger.info("Start to crawl cluster applications info from {}", this.urlString);
            AppsWrapper appWrapper = (AppsWrapper) HadoopYarnResourceUtils.getObjectFromUrlStream(urlString, AppsWrapper.class);
            if (appWrapper == null) {
                logger.error("Failed to crawl scheduler info with url = " + this.urlString);
            } else if (appWrapper.getApps().getApp() != null) {
                logger.info("Crawled " + appWrapper.getApps().getApp().size() + " application.");
                long currentTimestamp = System.currentTimeMillis();
                for (App app : appWrapper.getApps().getApp()) {
                    long runTimeLenSecond = (currentTimestamp - app.getStartedTime()) / 1000;
                    app.setRunningTimeLenSecond(runTimeLenSecond);
                }
                listener.onStored(appWrapper);
            } else {
                logger.info("Yarn was Not Any Running Of Application.");
            }
        } catch (IOException e) {
            logger.error("Got IO exception while connecting to " + this.urlString + " : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Got exception while crawling queues:" + e.getMessage(), e);
        } catch (Throwable e) {
            logger.error("Got throwable exception while crawling queues:" + e.getMessage(), e);
        } finally {
            listener.flush();
        }
    }
}