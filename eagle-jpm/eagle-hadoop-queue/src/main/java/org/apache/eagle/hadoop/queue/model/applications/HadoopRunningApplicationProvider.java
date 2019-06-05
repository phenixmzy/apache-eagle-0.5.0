package org.apache.eagle.hadoop.queue.model.applications;

import com.codahale.metrics.health.HealthCheck;
import com.typesafe.config.Config;
import org.apache.eagle.app.spi.AbstractApplicationProvider;
import org.apache.eagle.hadoop.queue.HadoopQueueRunningApplicationHealthCheck;

import java.util.Optional;

public class HadoopRunningApplicationProvider extends AbstractApplicationProvider<HadoopRunningApp> {

    @Override
    public HadoopRunningApp getApplication() {
        return new HadoopRunningApp();
    }

    @Override
    public Optional<HealthCheck> getManagedHealthCheck(Config config) {
        return Optional.of(new HadoopQueueRunningApplicationHealthCheck(config));
    }
}
