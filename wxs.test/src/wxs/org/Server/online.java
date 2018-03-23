package wxs.org.Server;

public class online {
	private String user;
	private String ip;
	private String port;
	
	public online(String user, String ip, String port) {
		this.user = user;
		this.ip = ip;
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}