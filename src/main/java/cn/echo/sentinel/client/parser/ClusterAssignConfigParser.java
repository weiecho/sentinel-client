package cn.echo.sentinel.client.parser;

import cn.echo.sentinel.client.domain.ClusterGroupEntity;
import cn.echo.sentinel.client.utils.MachineUtils;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

public class ClusterAssignConfigParser  implements Converter<String, ClusterClientAssignConfig> {
    @Override
    public ClusterClientAssignConfig convert(String source) {
        if (source == null) {
            return null;
        }
        RecordLog.info("[ClusterClientAssignConfigParser] Get data: " + source);
        List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {
        });
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return extractClientAssignment(groupList);
    }

    private ClusterClientAssignConfig extractClientAssignment(List<ClusterGroupEntity> groupList) {
        // Skip the token servers.
        for (ClusterGroupEntity group : groupList) {
            if (MachineUtils.isCurrentMachineEqual(group)) {
                return null;
            }
        }
        // Build client assign config from the client set of target server group.
        for (ClusterGroupEntity group : groupList) {
            if (group.getClientSet().contains(MachineUtils.getCurrentMachineId())) {
                String ip = group.getIp();
                Integer port = group.getPort();
                return new ClusterClientAssignConfig(ip, port);
            }
        }
        // Not assigned.
        return null;
    }
}
