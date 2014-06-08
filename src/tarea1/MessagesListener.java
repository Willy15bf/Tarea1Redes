package tarea1;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import chat.Message;
public class MessagesListener implements Callable<Void>{
	
	private Socket socket;
	private BlockingQueue<Message> messagesQueue;
	public MessagesListener(Socket socket, BlockingQueue<Message> messagesQueue) {
		this.socket = socket;
		this.messagesQueue = messagesQueue;
	}

	@Override
	public Void call() throws Exception {
		// TODO Auto-generated method stub
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		Object o;
		Message message;	
		
		try {
		
			while((o = inStream.readObject()) != null) {
				
				message = (Message)o;
				
				switch(message.getType()) {
					
					case Message.MESSAGE:
						System.out.println(message.getMessage());
						messagesQueue.put(message);
						break;
				
				}
				
			
			}
			
		} catch(IOException e) {
			System.out.println("El servidor ha cerrado la conexión");
		}
		
		return null;
	}

}
