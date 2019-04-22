/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.eagle.service.topology.resource.impl

import org.apache.eagle.alert.coordination.model.internal.Topology
import org.apache.eagle.alert.engine.UnitTopologyMain
import org.apache.eagle.alert.engine.coordinator.StreamingCluster
import org.apache.eagle.alert.engine.runner.UnitTopologyRunner
import org.apache.eagle.alert.metadata.IMetadataDao
import org.apache.eagle.alert.metadata.impl.MetadataDaoFactory
import org.apache.storm.Config
import org.apache.storm.StormSubmitter
import org.apache.storm.generated.Nimbus
import org.apache.storm.generated.StormTopology
import org.apache.storm.generated.TopologySummary
import org.apache.storm.utils.NimbusClient
import org.apache.storm.utils.Utils
import com.typesafe.config.ConfigFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.*


class TopologyMgmtResourceImpl {

    val topologies: List<TopologyStatus>
        @Throws(Exception::class)
        get() {
            val topologyDefinitions = dao.listTopologies()
            val clusters = dao.listClusters()

            val topologies = ArrayList<TopologyStatus>()
            for (topologyDef in topologyDefinitions) {
                val topologySummary = getTopologySummery(clusters, topologyDef)
                if (topologySummary != null) {
                    val t = TopologyStatus()
                    t.name = topologySummary._name
                    t.id = topologySummary._id
                    t.state = topologySummary._status
                    t.topology = topologyDef
                    topologies.add(t)
                }
            }
            return topologies
        }


    @Throws(Exception::class)
    private fun getStormConf(clusters: List<StreamingCluster>?, clusterId: String?): Map<*, *> {
        var clusters = clusters
        val stormConf = Utils.readStormConfig()
        val nimbusList = LinkedList<String>()
        if (clusterId == null) {
            //stormConf.put(Config.NIMBUS_HOST, DEFAULT_NIMBUS_HOST);
            /*1.x Later API Used nimbus.seed*/
            LOG.info("clusterId is null. NIMBUS_HOST = {}", DEFAULT_NIMBUS_HOST)
            nimbusList.add(DEFAULT_NIMBUS_HOST)
            stormConf[Config.NIMBUS_SEEDS] = nimbusList
            stormConf[Config.NIMBUS_THRIFT_PORT] = DEFAULT_NIMBUS_THRIFT_PORT
        } else {
            if (clusters == null) {
                clusters = dao.listClusters()
            }
            val scOp = TopologyMgmtResourceHelper.findById(clusters!!, clusterId)
            val cluster: StreamingCluster
            if (scOp.isPresent) {
                cluster = scOp.get()
            } else {
                throw Exception("Fail to find cluster: $clusterId")
            }
            //stormConf.put(Config.NIMBUS_HOST, cluster.getDeployments().getOrDefault(StreamingCluster.NIMBUS_HOST, DEFAULT_NIMBUS_HOST));

            /* 1.x Later API Used nimbus.seed*/
            val nimbusSeed = (cluster.deployments as java.util.Map<String, String>).getOrDefault(StreamingCluster.NIMBUS_SEED, DEFAULT_NIMBUS_HOST)

            val stoken = StringTokenizer(nimbusSeed, ",")
            while (stoken.hasMoreTokens()) {
                nimbusList.add(stoken.nextToken())
            }
            stormConf[Config.NIMBUS_SEEDS] = nimbusList
            stormConf[Config.NIMBUS_THRIFT_PORT] = Integer.valueOf(cluster.deployments[StreamingCluster.NIMBUS_THRIFT_PORT])

            LOG.info("clusterId not null. Used nimbus seed. seed count = {} list = {} port = {} ", nimbusSeed, nimbusList.size, stormConf[Config.NIMBUS_THRIFT_PORT])
        }
        return stormConf
    }

    private fun createTopologyHelper(topologyDef: Topology, config: com.typesafe.config.Config) {
        val numOfSpoutTasks = config.getInt(UnitTopologyRunner.SPOUT_TASK_NUM)
        val numOfRouterBolts = config.getInt(UnitTopologyRunner.ROUTER_TASK_NUM)
        val numOfAlertBolts = config.getInt(UnitTopologyRunner.ALERT_TASK_NUM)
        val numOfPublishTasks = config.getInt(UnitTopologyRunner.PUBLISH_TASK_NUM)
        topologyDef.spoutId = UnitTopologyRunner.spoutName
        topologyDef.pubBoltId = UnitTopologyRunner.alertPublishBoltName
        topologyDef.numOfSpout = numOfSpoutTasks
        topologyDef.numOfGroupBolt = numOfRouterBolts
        topologyDef.numOfAlertBolt = numOfAlertBolts
        topologyDef.numOfPublishBolt = numOfPublishTasks
        dao.addTopology(topologyDef)
    }

    private fun createTopology(topologyDef: Topology): StormTopology {
        val topologyConf = ConfigFactory.load("topology-sample-definition.conf")
        var stormJarPath = ""
        if (topologyConf.hasPath(STORM_JAR_PATH)) {
            stormJarPath = topologyConf.getString(STORM_JAR_PATH)
        }
        System.setProperty("storm.jar", stormJarPath)
        createTopologyHelper(topologyDef, topologyConf)
        return UnitTopologyMain.createTopology(topologyConf)
    }

    @Throws(Exception::class)
    fun startTopology(topologyName: String) {
        val tdop = TopologyMgmtResourceHelper.findById(dao.listTopologies(), topologyName)
        val topologyDef: Topology
        if (tdop.isPresent) {
            topologyDef = tdop.get()
        } else {
            topologyDef = Topology()
            topologyDef.name = topologyName
        }
        StormSubmitter.submitTopology(topologyName, getStormConf(null, topologyDef.clusterName), createTopology(topologyDef))
    }

    @Throws(Exception::class)
    fun stopTopology(topologyName: String) {
        val tdop = TopologyMgmtResourceHelper.findById(dao.listTopologies(), topologyName)
        val topologyDef: Topology
        if (tdop.isPresent) {
            topologyDef = tdop.get()
        } else {
            throw Exception("Fail to find topology $topologyName")
        }
        val stormClient = NimbusClient.getConfiguredClient(getStormConf(null, topologyDef.clusterName)).client
        stormClient.killTopology(topologyName)
    }

    @Throws(Exception::class)
    private fun getTopologySummery(clusters: List<StreamingCluster>, topologyDef: Topology): TopologySummary? {
        val stormConf = getStormConf(clusters, topologyDef.clusterName)
        val stormClient = NimbusClient.getConfiguredClient(stormConf).client
        val tOp = stormClient.clusterInfo._topologies.stream().filter({ topology -> topology.get_name().equals(topologyDef.name, ignoreCase = true) }).findFirst()
        if (tOp.isPresent()) {
            val id = tOp.get().get_id()
            //StormTopology stormTopology= stormClient.getTopology(id);
            return tOp.get()
        } else {
            return null
        }
    }

    companion object {
        private val dao = MetadataDaoFactory.getInstance().metadataDao
        private val LOG = LoggerFactory.getLogger(TopologyMgmtResourceImpl::class.java)

        private val DEFAULT_NIMBUS_HOST = "sandbox.hortonworks.com"
        private val DEFAULT_NIMBUS_THRIFT_PORT = 6627
        private val STORM_JAR_PATH = "topology.stormJarPath"
    }

}
