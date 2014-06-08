package contacto;

import java.io.Serializable;

public class Contact implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2179862589068775223L;
	private int id;
	private String userName;
	private String ipAddress;
	private int port;
	
	public Contact() {}
	
	public Contact(String userName) {
		this.userName = userName;
	}
	
	public Contact(String userName, String ipAddress) {
		this.userName = userName;
		this.ipAddress = ipAddress;
	}
	
	public Contact(String userName, String ipAddress, int port) {
		this.userName = userName;
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public Contact(int id, String userName, String ipAddress, int port) {
		this.id = id;
		this.userName = userName;
		this.ipAddress = ipAddress;
		this.port = port;
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	

}
