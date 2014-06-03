package chat;

import java.io.*;

import contacto.Contact;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 4631731085120441047L;
	public static final int USERS = 0;
	public static final int MESSAGE = 1;
	public static final int LOGOUT = 2;
	public static final int FILETRANS = 3;
	private int type;	
	private Contact userFrom;
	private Contact userTo;
	private String message;	
	
	public Message() {
		this.userFrom = new Contact();
		this.userTo = new Contact();
	}
	
	public Message(int type) {
		this.type = type;
		this.userFrom = new Contact();
		this.userTo = new Contact();
	}
	
	public Message(int type, String message) {
		this.type = type;		
		this.message = message;
		this.userFrom = new Contact();
		this.userTo = new Contact();				
	}	
	
	public Contact getUserFrom() {
		return userFrom;
	}

	public Contact getUserTo() {
		return userTo;
	}	
	
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
	
}
