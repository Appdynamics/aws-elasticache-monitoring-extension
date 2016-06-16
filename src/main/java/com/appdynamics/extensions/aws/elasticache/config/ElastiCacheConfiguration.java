package com.appdynamics.extensions.aws.elasticache.config;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.List;

/**
 * @author Satish Muddam
 */
public class ElastiCacheConfiguration extends Configuration {

    private List<String> includeCacheClusters;

    public List<String> getIncludeCacheClusters() {
        return includeCacheClusters;
    }

    public void setIncludeCacheClusters(List<String> includeCacheClusters) {
        this.includeCacheClusters = includeCacheClusters;
    }
}
