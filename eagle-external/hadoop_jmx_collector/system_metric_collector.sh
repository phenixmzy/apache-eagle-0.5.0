export EAGLE_HOME=/usr/local/eagle-0.5.0
cd $EAGLE_HOME/lib/scripts/hadoop_jmx_collector
/usr/bin/python system_metric_collector.py system_metric_config.json
