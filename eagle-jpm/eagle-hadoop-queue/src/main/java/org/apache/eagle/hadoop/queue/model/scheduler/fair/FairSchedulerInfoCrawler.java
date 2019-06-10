package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import org.apache.eagle.hadoop.queue.common.HadoopYarnResourceUtils;
import org.apache.eagle.hadoop.queue.common.YarnClusterResourceURLBuilder;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FairSchedulerInfoCrawler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FairSchedulerInfoCrawler.class);

    private FairSchedulerInfoParseListener listener;
    private String urlString;

    public FairSchedulerInfoCrawler(String site, String baseUrl, SpoutOutputCollector collector) {
        this.urlString = YarnClusterResourceURLBuilder.buildSchedulerInfoURL(baseUrl);
        this.listener = new FairSchedulerInfoParseListener(site, collector);
    }

    @Override
    public void run() {
        try {
            //https://some.server.address:50030/ws/v1/cluster/scheduler?anonymous=true
            logger.info("Start to crawl cluster ] queues from {}" , this.urlString);
            FairSchedulerWrapper schedulerWrapper = (FairSchedulerWrapper) HadoopYarnResourceUtils.getObjectFromUrlStream(urlString, FairSchedulerWrapper.class);
            if (schedulerWrapper == null || schedulerWrapper.getScheduler() == null) {
                logger.error("Failed to crawl scheduler info with url = " + this.urlString);
            } else {
                FairSchedulerInfo scheduler = schedulerWrapper.getScheduler().getSchedulerInfo();
                logger.info("Crawled " + scheduler.getRootQueue().getChildQueues().size() + " rootQueue.childQueues of second level.");
                long currentTimestamp = System.currentTimeMillis();
                listener.onMetric(scheduler, currentTimestamp);
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