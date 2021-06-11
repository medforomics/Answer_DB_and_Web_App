package utsw.bicf.answer.security;

public class MongoProperties {
	
	String url;
	String port;
	String username;
	String password;
	String fullUrl;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getFullUrl() {
		if (fullUrl == null) {
			fullUrl = url + ":" + port + "/";
		}
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

}
