package com.appdynamics.extensions.aws.elasticache;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;

public class ElastiCacheMonitorITest {
	
	private ElastiCacheMonitor classUnderTest = new ElastiCacheMonitor();
	
	@Test
	public void testMetricsCollection() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/conf/itest-config.yaml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("successfully completed"));
	}
}
