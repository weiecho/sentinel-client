package cn.echo.sentinel.client.domain;

import java.util.HashSet;
import java.util.Set;

public class ClusterGroupEntity {

    private String machineId;

    private String ip;
    private Integer port;

    private Set<String> clientSet = new HashSet<>();

    private Boolean belongToApp;

    private Double maxAllowedQps;

    public String getMachineId() {
        return machineId;
    }

    public ClusterGroupEntity setMachineId(String machineId) {
        this.machineId = machineId;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ClusterGroupEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ClusterGroupEntity setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Set<String> getClientSet() {
        return clientSet;
    }

    public ClusterGroupEntity setClientSet(Set<String> clientSet) {
        this.clientSet = clientSet;
        return this;
    }

    public Boolean getBelongToApp() {
        return belongToApp;
    }

    public ClusterGroupEntity setBelongToApp(Boolean belongToApp) {
        this.belongToApp = belongToApp;
        return this;
    }

    public Double getMaxAllowedQps() {
        return maxAllowedQps;
    }

    public void setMaxAllowedQps(Double maxAllowedQps) {
        this.maxAllowedQps = maxAllowedQps;
    }

    @Override
    public String toString() {
        return "ClusterGroupEntity{" +
                "machineId='" + machineId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", clientSet=" + clientSet +
                ", belongToApp=" + belongToApp +
                '}';
    }
}
