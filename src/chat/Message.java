package chat;

import java.io.*;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 4631731085120441047L;
	static final int USERS = 0;
	public static final int MESSAGE = 1;
	public static final int LOGOUT = 2;
	public static final int FILETRANS = 3;
	private int type;
	private String message;
	private String userNameFrom;
	private String userNameTo;
	private String ipFrom;
	private String ipTo;
	
	public Message() {}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserNameFrom() {
		return userNameFrom;
	}
	public void setUserNameFrom(String userNameFrom) {
		this.userNameFrom = userNameFrom;
	}
	public String getUserNameTo() {
		return userNameTo;
	}
	public void setUserNameTo(String userNameTo) {
		this.userNameTo = userNameTo;
	}
	public String getIpFrom() {
		return ipFrom;
	}
	public void setIpFrom(String ipFrom) {
		this.ipFrom = ipFrom;
	}
	public String getIpTo() {
		return ipTo;
	}
	public void setIpTo(String ipTo) {
		this.ipTo = ipTo;
	}
}
