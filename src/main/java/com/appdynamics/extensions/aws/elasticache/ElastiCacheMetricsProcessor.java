package com.appdynamics.extensions.aws.elasticache;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.appdynamics.extensions.aws.config.MetricType;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Florencio Sarmiento
 */
public class ElastiCacheMetricsProcessor implements MetricsProcessor {

    private static final String NAMESPACE = "AWS/ElastiCache";

    private static final String[] DIMENSIONS = {"CacheClusterId", "CacheNodeId"};

    private List<MetricType> metricTypes;

    private Pattern excludeMetricsPattern;

    private List<String> includeCacheClusters;

    public ElastiCacheMetricsProcessor(List<MetricType> metricTypes,
                                       Set<String> excludeMetrics, List<String> includeCacheClusters) {
        this.metricTypes = metricTypes;
        this.excludeMetricsPattern = MetricsProcessorHelper.createPattern(excludeMetrics);
        this.includeCacheClusters = includeCacheClusters;
    }

    public List<Metric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName) {


        List<DimensionFilter> dimensions = new ArrayList<DimensionFilter>();

        DimensionFilter dimensionFilter = new DimensionFilter();
        dimensionFilter.withName(DIMENSIONS[0]);

        dimensions.add(dimensionFilter);

        CacheClusterNameMatcherPredicate predicate = new CacheClusterNameMatcherPredicate(includeCacheClusters);

        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch,
                NAMESPACE,
                excludeMetricsPattern,
                dimensions,
                predicate);
    }

    public StatisticType getStatisticType(Metric metric) {
        return MetricsProcessorHelper.getStatisticType(metric, metricTypes);
    }

    public Map<String, Double> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[0], "Cache Cluster");
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[1], "Cache Node");

        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

}
