package scanner;

public class ScanResult {
    private String ip;
    private String hostname;
    private boolean active;
    private long responseTime;

    public ScanResult(String ip, String hostname, boolean active, long responseTime) {
        this.ip = ip;
        this.hostname = hostname;
        this.active = active;
        this.responseTime = responseTime;
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public boolean isActive() {
        return active;
    }

    public long getResponseTime() {
        return responseTime;
    }
}
