package chat;

import java.io.*;

import contacto.Contact;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 4631731085120441047L;
	public static final int LOGIN = 0;
	public static final int USERS = 1;	
	public static final int MESSAGE = 2;	
	public static final int LOGOUT = 3;	
	private int type;	
	//private boolean fileTransfer;
	//private byte[] file;
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
	
//	public boolean isFileTransfer() {
//		return fileTransfer;
//	}
//
//	public void setFileTransfer(boolean fileTransfer) {
//		this.fileTransfer = fileTransfer;
//	}
	
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

//	public byte[] getFile() {
//		return file;
//	}
//
//	public void setFile(byte[] file) {
//		this.file = file;
//	}
	
}
