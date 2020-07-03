package cn.echo.sentinel.client.parser;

import cn.echo.sentinel.client.domain.ClusterGroupEntity;
import cn.echo.sentinel.client.utils.MachineUtils;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

public class ClusterServerFlowConfigParser implements Converter<String, ServerFlowConfig> {
    @Override
    public ServerFlowConfig convert(String source) {
        if (source == null) {
            return null;
        }
        RecordLog.info("[ClusterServerFlowConfigParser] Get data: " + source);
        List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {
        });
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return extractServerFlowConfig(groupList);
    }

    private ServerFlowConfig extractServerFlowConfig(List<ClusterGroupEntity> groupList) {
        for (ClusterGroupEntity group : groupList) {
            if (MachineUtils.isCurrentMachineEqual(group)) {
                return new ServerFlowConfig()
                        .setExceedCount(ClusterServerConfigManager.getExceedCount())
                        .setIntervalMs(ClusterServerConfigManager.getIntervalMs())
                        .setMaxAllowedQps(group.getMaxAllowedQps())
                        .setMaxOccupyRatio(ClusterServerConfigManager.getMaxOccupyRatio())
                        .setSampleCount(ClusterServerConfigManager.getSampleCount());
            }
        }
        return null;
    }

}
