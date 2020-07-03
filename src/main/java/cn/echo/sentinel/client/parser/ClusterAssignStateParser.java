package cn.echo.sentinel.client.parser;

import cn.echo.sentinel.client.domain.ClusterGroupEntity;
import cn.echo.sentinel.client.utils.MachineUtils;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

public class ClusterAssignStateParser implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        if (source == null) {
            return null;
        }
        RecordLog.info("[ClusterClientAssignConfigParser] Get data: " + source);
        List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {
        });
        if (groupList == null || groupList.isEmpty()) {
            return ClusterStateManager.CLUSTER_NOT_STARTED;
        }
        return extractMode(groupList);
    }

    private int extractMode(List<ClusterGroupEntity> groupList) {
        // If any server group machine matches current, then it's token server.
        for (ClusterGroupEntity group : groupList) {
            if (MachineUtils.isCurrentMachineEqual(group)) {
                return ClusterStateManager.CLUSTER_SERVER;
            }
            if (group.getClientSet() != null) {
                for (String client : group.getClientSet()) {
                    if (client != null && client.equals(MachineUtils.getCurrentMachineId())) {
                        return ClusterStateManager.CLUSTER_CLIENT;
                    }
                }
            }
        }
        return ClusterStateManager.CLUSTER_NOT_STARTED;
    }
}