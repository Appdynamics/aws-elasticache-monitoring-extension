# AppDynamics Monitoring Extension for use with AWS ElastiCache

## Use Case
Captures ElastiCache statistics from Amazon CloudWatch and displays them in the AppDynamics Metric Browser.

**Note : By default, the Machine agent can only send a fixed number of metrics to the controller. This extension potentially reports thousands of metrics, so to change this limit, please follow the instructions mentioned [here](https://docs.appdynamics.com/display/PRO40/Metrics+Limits).**

## Prerequisites
1. Please give the following permissions to the account being used to with the extension.
**cloudwatch:ListMetrics**
**cloudwatch:GetMetricStatistics**

2. In order to use this extension, you do need a [Standalone JAVA Machine Agent](https://docs.appdynamics.com/display/PRO44/Standalone+Machine+Agents) or [SIM Agent](https://docs.appdynamics.com/display/PRO44/Server+Visibility).  For more details on downloading these products, please  visit [here](https://download.appdynamics.com/).

3. The extension needs to be able to connect to AWS Cloudwatch in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

## Installation

1. Run `mvn clean install` from aws-elasticache-monitoring-extension directory
2. Copy and unzip `AWSElasticacheMonitor-<version>.zip` from `target` directory into `<machine_agent_dir>/monitors/`
3. Edit config.yml file in AWSElasticacheMonitor and provide the required configuration (see Configuration section)
4. Restart the Machine Agent.

Please place the extension in the `monitors` directory of your Machine Agent installation directory. Do not place the extension in the `extensions` directory of your Machine Agent installation directory.

## Configuration
In order to use the extension, you need to update the config.yml file that is present in the extension folder. The following is an explanation of the configurable fields that are present in the config.yml file.
All ElastiCache metrics are available under the namespace AWS/ElastiCache and provide metrics for a single dimension, the "CacheNodeId". When retrieving metrics, you must supply both the CacheClusterId and CacheNodeId dimensions.

1. If SIM is enabled, then use the following metricPrefix `metricPrefix: "Custom Metrics|AWS Elasticache"` else configure the "COMPONENT_ID" under which the metrics need to be reported.
This can be done by changing the value of <COMPONENT_ID> in `metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|AWS Elasticache|"`.
   For example,
     ```
     metricPrefix: "Server|Component:100|Custom Metrics|AWS Elasticache|"
     ```
2. Provide accessKey(required) and secretKey(required) of AWS account(s), also provide displayAccountName(any name that represents your account) and regions(required).
If you are running this extension inside an EC2 instance which has IAM profile configured then awsAccessKey and awsSecretKey can be kept empty, extension will use IAM profile to authenticate.
   ```
   accounts:
     - awsAccessKey: "XXXXXXXX1"
       awsSecretKey: "XXXXXXXXXX1"
       displayAccountName: "TestAccount_1"
       regions: ["us-east-1","us-west-1","us-west-2"]

     - awsAccessKey: "XXXXXXXX2"
       awsSecretKey: "XXXXXXXXXX2"
       displayAccountName: "TestAccount_2"
       regions: ["eu-central-1","eu-west-1"]
   ```
3. If you want to encrypt the "awsAccessKey" and "awsSecretKey" then follow the "Credentials Encryption" section and provide the encrypted values in "awsAccessKey" and "awsSecretKey".
Configure "enableDecryption" of "credentialsDecryptionConfig" to true and provide the encryption key in "encryptionKey"
   For example,
   ```
   #Encryption key for Encrypted password.
   credentialsDecryptionConfig:
       enableDecryption: "true"
       encryptionKey: "XXXXXXXX"
   ```
4. Provide the dimension and the cache cluster/node you would like to monitor in that dimension.
    ```
    #Filters based on dimensions, values accepts comma separated values and regex patterns. If `.*` is used, all are monitored and if empty, none are monitored
    dimensions:
      - name: "CacheClusterId"
        displayName: "CacheClusterId"
        values: ["eretail-demo", "corpsite-blog"]

      - name: "CacheNodeId"
        displayName: "CacheNodeId"
        values: ["0001"]
    ```

5. All the metrics listed in the config.yml have been divided in three sections. The first one is for Host Level Metrics, the second one is for Redis Specific Metrics and the third one is for Memcache specific metrics.
When configuring the config.yml, please uncomment the metrics as per you cache and requirement and comment the rest out. Metrics for each of the cahce have already been configured and added to the config.yml.
6. Configure the metrics section.

     For configuring the metrics, the following properties can be used:

     |     Property      |   Default value |         Possible values         |                                              Description                                                                                                |
     | :---------------- | :-------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------- |
     | alias             | metric name     | Any string                      | The substitute name to be used in the metric browser instead of metric name.                                   |
     | statType          | "ave"           | "AVERAGE", "SUM", "MIN", "MAX"  | AWS configured values as returned by API                                                                       |
     | aggregationType   | "AVERAGE"       | "AVERAGE", "SUM", "OBSERVATION" | [Aggregation qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)    |
     | timeRollUpType    | "AVERAGE"       | "AVERAGE", "SUM", "CURRENT"     | [Time roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)   |
     | clusterRollUpType | "INDIVIDUAL"    | "INDIVIDUAL", "COLLECTIVE"      | [Cluster roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)|
     | multiplier        | 1               | Any number                      | Value with which the metric needs to be multiplied.                                                            |
     | convert           | null            | Any key value map               | Set of key value pairs that indicates the value to which the metrics need to be transformed. eg: UP:0, DOWN:1  |
     | delta             | false           | true, false                     | If enabled, gives the delta values of metrics instead of actual values.                                        |

    For example,
    ```
    - name: "CPUUtilization"
      alias: "CPUUtilization"
      statType: "ave"
      delta: false
      multiplier: 1
      aggregationType: "OBSERVATION"
      timeRollUpType: "CURRENT"
      clusterRollUpType: "COLLECTIVE"
    ```

    **All these metric properties are optional, and the default value shown in the table is applied to the metric(if a property has not been specified) by default.**
### config.yml

Please avoid using tab (\t) when editing yaml files. Please copy all the contents of the config.yml file and go to [Yaml Validator](http://www.yamllint.com/) . On reaching the website, paste the contents and press the “Go” button on the bottom left.
If you get a valid output, that means your formatting is correct and you may move on to the next step.

**Below is an example config for monitoring multiple accounts and regions:**
~~~
metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|Amazon Elasticache|"

accounts:
  - awsAccessKey: "XXXXXXXX1"
    awsSecretKey: "XXXXXXXXXX1"
    displayAccountName: "TestAccount_1"
    regions: ["us-east-1","us-west-1","us-west-2"]

  - awsAccessKey: "XXXXXXXX2"
    awsSecretKey: "XXXXXXXXXX2"
    displayAccountName: "TestAccount_2"
    regions: ["eu-central-1","eu-west-1"]

credentialsDecryptionConfig:
    enableDecryption: "false"
    encryptionKey:

proxyConfig:
    host:
    port:
    username:
    password:

concurrencyConfig:
  noOfAccountThreads: 3
  noOfRegionThreadsPerAccount: 3
  noOfMetricThreadsPerRegion: 3

#Filters based on dimensions, values accepts comma separated values and regex patterns. If `.*` is used, all are monitored and if empty, none are monitored
dimensions:
  - name: "CacheClusterId"
    displayName: "CacheClusterId"
    values: ["eretail*", "corpsite-blog"]

  - name: "CacheNodeId"
    displayName: "CacheNodeId"
    values: ["0001"]

#Allowed values are Basic and Detailed. Refer https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-cloudwatch-new.html for more information
# Basic will fire CloudWatch API calls every 5 minutes
# Detailed will fire CloudWatch API calls every 1 minutes
cloudWatchMonitoring: "Basic"

regionEndPoints:
    ap-southeast-1 : monitoring.ap-southeast-1.amazonaws.com
    ap-southeast-2 : monitoring.ap-southeast-2.amazonaws.com
    ap-northeast-1 : monitoring.ap-northeast-1.amazonaws.com
    eu-central-1 : monitoring.eu-central-1.amazonaws.com
    eu-west-1 : monitoring.eu-west-1.amazonaws.com
    us-east-1 : monitoring.us-east-1.amazonaws.com
    us-west-1 : monitoring.us-west-1.amazonaws.com
    us-west-2 : monitoring.us-west-2.amazonaws.com
    sa-east-1 : monitoring.sa-east-1.amazonaws.com


metricsConfig:
    includeMetrics:
       - name: "CPUUtilization"
         alias: "CPUUtilization"
         statType: "ave"
         delta: false
         multiplier: 1
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
    ...
    ...

    metricsTimeRange:
      startTimeInMinsBeforeNow: 5
      endTimeInMinsBeforeNow: 0

    getMetricStatisticsRateLimit: 400

    maxErrorRetrySize: 0
~~~

## Metrics
The extension reports the metrics listed at the link below:

- [Elaticache Host Level Metrics](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CacheMetrics.HostLevel.html)
- [Metrics for Redis](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CacheMetrics.Redis.html)
- [Metrics for Memcached](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CacheMetrics.Memcached.html)

## Credentials Encryption
Please visit [this page](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an inbuilt feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review the following document on [How to use the Extensions WorkBench](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130)

## Troubleshooting
Please follow the steps listed in this [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension. If these don't solve your issue, please follow the last step on the [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) to contact the support team.

## Support Tickets
If after going through the [Troubleshooting Document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) you have not been able to get your extension working, please file a ticket and add the following information.

Please provide the following in order for us to assist you better.

1. Stop the running machine agent.
2. Delete all existing logs under <MachineAgent>/logs.
3. Please enable debug logging by editing the file <MachineAgent>/conf/logging/log4j.xml. Change the level value of the following <logger> elements to debug.
   <logger name="com.singularity">
   <logger name="com.appdynamics">
4. Start the machine agent and please let it run for 10 mins. Then zip and upload all the logs in the directory <MachineAgent>/logs/*.
5. Attach the zipped <MachineAgent>/conf/* directory here.
6. Attach the zipped <MachineAgent>/monitors/ExtensionFolderYouAreHavingIssuesWith directory here.
   For any support related questions, you can also contact help@appdynamics.com.

## Contributing
Always feel free to fork and contribute any changes directly here on [GitHub](https://github.com/Appdynamics/aws-elasticache-monitoring-extension).

## Version
   |          Name            |  Version   |
   |--------------------------|------------|
   |Extension Version         |2.0.0       |
   |Controller Compatibility  |4.4 or Later|
   |Last Update               |13th July, 2018 |

List of changes to this extension can be found [here](https://github.com/Appdynamics/aws-elasticache-monitoring-extension/blob/master/CHANGELOG.md)