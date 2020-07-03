package cn.echo.sentinel.client;

import cn.echo.sentinel.client.parser.ClusterTransportConfigParser;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.consul.ConsulDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import cn.echo.sentinel.client.config.SentinelConfigUtil;
import cn.echo.sentinel.client.parser.ClusterAssignConfigParser;
import cn.echo.sentinel.client.parser.ClusterAssignStateParser;
import cn.echo.sentinel.client.parser.ClusterServerFlowConfigParser;

import java.util.List;
import java.util.Set;

import static cn.echo.sentinel.client.config.SentinelProperties.ConsulHost;
import static cn.echo.sentinel.client.config.SentinelProperties.ConsulPort;
import static com.alibaba.csp.sentinel.config.SentinelConfig.APP_TYPE;


public class SentinelClientInitFunc implements InitFunc {

    final int waitTimeout = 10;
    static String flowRuleKey;
    static String degradeKey;
    static String systemRuleKey;
    static String paramFlowRuleKey;
    static String authorityRuleKey;
    static String gatewayFlowRuleKey;
    static String gatewayApiKey;
    static String clusterNamespaceSetKey;
    static String clusterServerTransportConfigKey;

    public void init() throws Exception {
        flowRuleKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getFlowDataId();
        degradeKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getDegradeDataId();
        systemRuleKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getSystemDataId();
        paramFlowRuleKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getParamFlowDataId();
        authorityRuleKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getAuthorityDataId();
        gatewayApiKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getGatewayApiDataId();
        gatewayFlowRuleKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getGatewayFlowDataId();
        clusterNamespaceSetKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getTokenServerClusterMapDataId();
        clusterServerTransportConfigKey = SentinelConfigUtil.getNamespaceName() + "/"+ SentinelConfig.getAppName() +"/" + SentinelConfigUtil.getTokenClusterConfigDataId();

        //初始化默认动态规则
        initDynamicRuleProperty();
        //初始化token server端口相关配置
        initServerTransportConfigProperty();
        //最大token qps配置
        initServerFlowConfig();
        //为token server添加命名空间动态监听器
        initClusterRuleSupplier();
        //为每个client设置目标token server
        initClientServerAssignProperty();
        //初始化token client通用超时配置
        initClientConfigProperty();
        //等待transport端口分配完毕
        while (TransportConfig.getRuntimePort() == -1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //初始化客户端状态为client 或者 server
        initStateProperty();
    }

    /**
     * 初始化默认动态规则
     */
    private void initDynamicRuleProperty() {

        //判断是否Gateway服务
        String type = System.getProperty(APP_TYPE);
        if ("1".equals(type) || "11".equals(type) || "12".equals(type)) {
            //GatewayApi
            Converter<String, Set<ApiDefinition>> gatewayApiConfigParser = source -> JSON.parseObject(source, new TypeReference<Set<ApiDefinition>>() {});
            ReadableDataSource<String, Set<ApiDefinition>> gatewayApiSource = new ConsulDataSource<>(ConsulHost, ConsulPort, gatewayApiKey, waitTimeout, gatewayApiConfigParser);
            GatewayApiDefinitionManager.register2Property(gatewayApiSource.getProperty());

            //GatewayApi
            Converter<String, Set<GatewayFlowRule>> gatewayFlowRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {});
            ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, gatewayFlowRuleKey, waitTimeout, gatewayFlowRuleConfigParser);
            GatewayRuleManager.register2Property(gatewayFlowRuleSource.getProperty());

            return;
        }

        //FlowRule
        Converter<String, List<FlowRule>> flowRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {});
        ReadableDataSource<String, List<FlowRule>> flowRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, flowRuleKey, waitTimeout, flowRuleConfigParser);
        FlowRuleManager.register2Property(flowRuleSource.getProperty());

        //DegradeRule
        Converter<String, List<DegradeRule>> degradeRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {});
        ReadableDataSource<String, List<DegradeRule>> degradeRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, degradeKey, waitTimeout, degradeRuleConfigParser);
        DegradeRuleManager.register2Property(degradeRuleSource.getProperty());

        //SystemRule
        Converter<String, List<SystemRule>> systemRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {});
        ReadableDataSource<String, List<SystemRule>> systemRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, systemRuleKey, waitTimeout, systemRuleConfigParser);
        SystemRuleManager.register2Property(systemRuleSource.getProperty());

        //ParamFlowRule
        Converter<String, List<ParamFlowRule>> paramFlowRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {});
        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, paramFlowRuleKey, waitTimeout, paramFlowRuleConfigParser);
        ParamFlowRuleManager.register2Property(paramFlowRuleSource.getProperty());

        //AuthorityRule
        Converter<String, List<AuthorityRule>> authorityRuleConfigParser = source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {});
        ReadableDataSource<String, List<AuthorityRule>> authorityRuleSource = new ConsulDataSource<>(ConsulHost, ConsulPort, authorityRuleKey, waitTimeout, authorityRuleConfigParser);
        AuthorityRuleManager.register2Property(authorityRuleSource.getProperty());

    }

    /**
     * 初始化集群限流规则监听器
     */
    private void initClusterRuleSupplier() {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = new ConsulDataSource<>(ConsulHost, ConsulPort, flowRuleKey, waitTimeout, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
            return ds.getProperty();
        });

        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new ConsulDataSource<>(ConsulHost, ConsulPort, paramFlowRuleKey, waitTimeout, source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
            return ds.getProperty();
        });
    }

    private void initServerFlowConfig() {
        ClusterServerFlowConfigParser serverFlowConfigParser = new ClusterServerFlowConfigParser();
        ReadableDataSource<String, ServerFlowConfig> serverFlowConfigDs = new ConsulDataSource<>(ConsulHost, ConsulPort, clusterNamespaceSetKey, waitTimeout,
          s -> {
            ServerFlowConfig config = serverFlowConfigParser.convert(s);
            if (config != null) {
                ClusterServerConfigManager.loadGlobalFlowConfig(config);
            }
            return config;
        });
    }

    private void initStateProperty() {
        // Cluster map format:
        // [{"clientSet":["112.12.88.66@8729","112.12.88.67@8727"],"ip":"112.12.88.68","machineId":"112.12.88.68@8728","port":11111}]
        // machineId: <ip@commandPort>, commandPort for port exposed to Sentinel dashboard (transport module)
        ReadableDataSource<String, Integer> clusterModeDs = new ConsulDataSource<>(ConsulHost, ConsulPort, clusterNamespaceSetKey, waitTimeout, new ClusterAssignStateParser());
        ClusterStateManager.registerProperty(clusterModeDs.getProperty());
    }

    private void initServerTransportConfigProperty() {

        ReadableDataSource<String, ServerTransportConfig> serverTransportDs = new ConsulDataSource<>(ConsulHost, ConsulPort, clusterNamespaceSetKey, waitTimeout, new ClusterTransportConfigParser());
        ClusterServerConfigManager.registerServerTransportProperty(serverTransportDs.getProperty());
    }

    private void initClientServerAssignProperty() {
        // Cluster map format:
        // [{"clientSet":["112.12.88.66@8729","112.12.88.67@8727"],"ip":"112.12.88.68","machineId":"112.12.88.68@8728","port":11111}]
        // machineId: <ip@commandPort>, commandPort for port exposed to Sentinel dashboard (transport module)
        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignDs = new ConsulDataSource<>(ConsulHost, ConsulPort, clusterNamespaceSetKey, waitTimeout, new ClusterAssignConfigParser());
        ClusterClientConfigManager.registerServerAssignProperty(clientAssignDs.getProperty());
    }

    private void initClientConfigProperty() {
        ReadableDataSource<String, ClusterClientConfig> clientConfigDs = new ConsulDataSource<>(ConsulHost, ConsulPort, clusterServerTransportConfigKey, waitTimeout, source -> JSON.parseObject(source, new TypeReference<ClusterClientConfig>() {}));
        ClusterClientConfigManager.registerClientConfigProperty(clientConfigDs.getProperty());
    }

}
