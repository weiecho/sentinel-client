/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.echo.sentinel.client.config;


/**
 * @author lonyee
 */
public final class SentinelConfigUtil {
    /**
     * 规则存储nameSpace
     */
    public static final String NAMESPACE_NAME = "sentinel";
    /**
     * 流控规则id
     */
    public static final String FLOW_DATA_ID_POSTFIX = "sentinel-flow-rules";
    /**
     * 降级规则id
     */
    public static final String DEGRADE_DATA_ID_POSTFIX = "sentinel-degrade-rules";
    /**
     * 热点规则id
     */
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "sentinel-param-flow-rules";
    /**
     * 系统规则id
     */
    public static final String SYSTEM_DATA_ID_POSTFIX = "sentinel-system-rules";
    /**
     * 授权规则id
     */
    public static final String AUTHORITY_DATA_ID_POSTFIX = "sentinel-authority-rules";
    /**
     * 网关apis
     */
    public static final String GATEWAY_API_DATA_ID_POSTFIX = "sentinel-gateway-api-rules";
    /**
     * 网关流控规则id
     */
    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "sentinel-gateway-flow-rules";

    /**
     * cluster-client
     */
    public static final String TOKEN_CLIENT_CLUSTER_CONFIG_DATA_ID_POSTFIX = "sentinel-token-client-cluster-config"; //集群客户端配置 cluster-client

    /**
     * cluster-server
     */
    public static final String TOKEN_SERVER_CLUSTER_MAP_DATA_ID_POSTFIX = "sentinel-token-server-cluster-map";


    private SentinelConfigUtil() {
    }

    public static String getFlowDataId() {
        return FLOW_DATA_ID_POSTFIX;
    }

    public static String getDegradeDataId() {
        return DEGRADE_DATA_ID_POSTFIX;
    }

    public static String getParamFlowDataId() {
        return PARAM_FLOW_DATA_ID_POSTFIX;
    }

    public static String getSystemDataId() {
        return SYSTEM_DATA_ID_POSTFIX;
    }

    public static String getAuthorityDataId() {
        return AUTHORITY_DATA_ID_POSTFIX;
    }

    public static String getGatewayApiDataId() {
        return GATEWAY_API_DATA_ID_POSTFIX;
    }

    public static String getGatewayFlowDataId() {
        return GATEWAY_FLOW_DATA_ID_POSTFIX;
    }

    public static String getNamespaceName() {
        return NAMESPACE_NAME;
    }

    public static String getTokenClusterConfigDataId() {
        return TOKEN_CLIENT_CLUSTER_CONFIG_DATA_ID_POSTFIX;
    }

    public static String getTokenServerClusterMapDataId() {
        return TOKEN_SERVER_CLUSTER_MAP_DATA_ID_POSTFIX;
    }
}
