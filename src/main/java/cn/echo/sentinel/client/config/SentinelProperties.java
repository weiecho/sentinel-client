package cn.echo.sentinel.client.config;

import com.alibaba.csp.sentinel.log.RecordLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import static com.alibaba.csp.sentinel.config.SentinelConfig.*;
import static com.alibaba.csp.sentinel.log.LogBase.*;
import static com.alibaba.csp.sentinel.transport.config.TransportConfig.*;
import static com.alibaba.csp.sentinel.util.AppNameUtil.APP_NAME;

@Slf4j
public class SentinelProperties {

    public static String ConsulHost;
    public static Integer ConsulPort;

    @Value("${spring.cloud.consul.host}")
    public void setConsulHost(String host) {
        ConsulHost = host;
    }
    @Value("${spring.cloud.consul.port: 8500}")
    public void setPort(Integer port){
        ConsulPort = port;
    }

    // 设置项目名称 - 强制设置 APP_NAME == APOLLO_ID
    @Value("${spring.application.name:}")
    public void setAppName(String appName) {
        this.setSysProperty(APP_NAME, appName);
    }

    // 设置dashboard admin主页
    @Value("${csp.sentinel.dashboard.server:}")
    public void setDashboardServer(String dashboardServer) {
        this.setSysProperty(CONSOLE_SERVER, dashboardServer);
    }


    // 设置端口
    @Value("${csp.sentinel.api.port: 8721}")
    public void setServerPort(Integer serverPort) {
        this.setSysProperty(SERVER_PORT, (10000 + serverPort) + "");
    }

    // 设置端口
    @Value("${csp.sentinel.charset:}")
    public void setCharSet(String charSet) {
        this.setSysProperty(CHARSET, charSet);
    }

    // 设置心跳间隔时间
    @Value("${csp.sentinel.heartbeat.interval.ms:}")
    public void setHeartbeatIntervalMs(String heartbeatIntervalMs) {
        this.setSysProperty(HEARTBEAT_INTERVAL_MS, heartbeatIntervalMs);
    }

    // 设置心跳客户端IP
    @Value("${csp.sentinel.heartbeat.client.ip:}")
    public void setHeartbeatClientIp(String heartbeatClientIp) {
        this.setSysProperty(HEARTBEAT_CLIENT_IP, heartbeatClientIp);
    }

    // 设置APP_TYPE
    @Value("${csp.sentinel.app.type:}")
    public void setAppType(String appType) {
        this.setSysProperty(APP_TYPE, appType);
    }

    // 设置METRIC_FILE_SINGLE_SIZE
    @Value("${csp.sentinel.metric.file.single.size:}")
    public void setSingleMetricFileSize(String singleMetricFileSize) {
        this.setSysProperty(SINGLE_METRIC_FILE_SIZE, singleMetricFileSize);
    }

    // 设置METRIC_FILE_TOTAL_COUNT
    @Value("${csp.sentinel.metric.file.total.count:}")
    public void setTotalMetricFileCount(String totalMetricFileCount) {
        this.setSysProperty(TOTAL_METRIC_FILE_COUNT, totalMetricFileCount);
    }

    // 设置冷启动因子
    @Value("${csp.sentinel.flow.cold.factor:}")
    public void setColdFactor(String coldFactor) {
        this.setSysProperty(COLD_FACTOR, coldFactor);
    }

    // 设置STATISTIC_MAX_RT
    @Value("${csp.sentinel.statistic.max.rt:}")
    public void setStatisticMaxRt(String statisticMaxRt) {
        this.setSysProperty(STATISTIC_MAX_RT, statisticMaxRt);
    }


    // 设置日志路径
    @Value("${csp.sentinel.log.dir:}")
    public void setLogDir(String logDir) {
        this.setSysProperty(LOG_DIR, logDir);
    }

    // 设置logpid
    @Value("${csp.sentinel.log.use.pid:}")
    public void setLogUsePid(String logUsePid) {
        this.setSysProperty(LOG_NAME_USE_PID, logUsePid);
    }

    // 设置logpid
    @Value("${csp.sentinel.log.output.type:}")
    public void setLogOutputType(String logOutputType) {
        this.setSysProperty(LOG_OUTPUT_TYPE, logOutputType);
    }



    /**
     * 系统配置
     */
    private void setSysProperty(String sysProperty, String value) {
        String val = System.getProperty(sysProperty);
        if (StringUtils.isEmpty(val) && !StringUtils.isEmpty(value)) {
            log.info(sysProperty+": "+ value);
            System.setProperty(sysProperty, value);
        }
    }
}
