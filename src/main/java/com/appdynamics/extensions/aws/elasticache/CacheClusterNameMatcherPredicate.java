package com.appdynamics.extensions.aws.elasticache;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.List;

/**
 * @author Satish Muddam
 */
public class CacheClusterNameMatcherPredicate implements Predicate<Metric> {

    private List<String> includeCacheClusters;
    private Predicate<CharSequence> patternPredicate;

    public CacheClusterNameMatcherPredicate(List<String> includeCacheClusters) {
        this.includeCacheClusters = includeCacheClusters;
        build();
    }

    private void build() {
        if (includeCacheClusters != null && !includeCacheClusters.isEmpty()) {
            for (String pattern : includeCacheClusters) {
                Predicate<CharSequence> charSequencePredicate = Predicates.containsPattern(pattern);
                if (patternPredicate == null) {
                    patternPredicate = charSequencePredicate;
                } else {
                    patternPredicate = Predicates.or(patternPredicate, charSequencePredicate);
                }
            }
        }
    }

    public boolean apply(Metric metric) {

        String cacheClusterName = metric.getDimensions().get(0).getValue();

        return patternPredicate.apply(cacheClusterName);
    }
}
