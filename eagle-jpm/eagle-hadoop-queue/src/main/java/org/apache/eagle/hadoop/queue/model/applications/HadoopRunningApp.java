package org.apache.eagle.hadoop.queue.model.applications;

import com.typesafe.config.Config;
import org.apache.eagle.app.StormApplication;
import org.apache.eagle.app.environment.impl.StormEnvironment;
import org.apache.eagle.app.messaging.StormStreamSink;
import org.apache.eagle.hadoop.queue.HadoopQueueRunningAppConfig;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

public class HadoopRunningApp extends StormApplication {
    @Override
    public StormTopology execute(Config config, StormEnvironment environment) {
        HadoopQueueRunningAppConfig appConfig = new HadoopQueueRunningAppConfig(config);

        String spoutName = "runningFairQueueSpout";
        String persistBoltName = "persistFairQueueBolt";

        IRichSpout spout = new HadoopRunningAppSpout(appConfig);

        //String acceptedAppStreamId = persistBoltName + "-to-" + DataSource.RUNNING_APPS.toString();
        //String schedulerStreamId = persistBoltName + "-to-" + DataSource.SCHEDULER.toString();
        //streamMaps.put(DataSource.RUNNING_APPS, acceptedAppStreamId);
        //streamMaps.put(DataSource.SCHEDULER, schedulerStreamId);

        int numOfPersistTasks = appConfig.topology.numPersistTasks;
        int numOfSinkTasks = appConfig.topology.numSinkTasks;
        int numOfSpoutTasks = 1;

        HadoopRunningAppPersistBolt bolt = new HadoopRunningAppPersistBolt(appConfig);
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(spoutName, spout, numOfSpoutTasks).setNumTasks(numOfSpoutTasks);
        builder.setBolt(persistBoltName, bolt, numOfPersistTasks).setNumTasks(numOfPersistTasks).shuffleGrouping(spoutName);

        StormStreamSink queueSinkBolt = environment.getStreamSink("HADOOP_YARN_RUNNING_APPLICATION_STREAM", config);
        builder.setBolt("appKafkaSink", queueSinkBolt, numOfSinkTasks)
                .setNumTasks(numOfSinkTasks).shuffleGrouping(persistBoltName);

        //StormStreamSink appSinkBolt = environment.getStreamSink("ACCEPTED_APP_STREAM", config);
        //builder.setBolt("appKafkaSink", appSinkBolt, numOfSinkTasks)
        //        .setNumTasks(numOfSinkTasks).shuffleGrouping(persistBoltName, acceptedAppStreamId);

        return builder.createTopology();
    }
}
