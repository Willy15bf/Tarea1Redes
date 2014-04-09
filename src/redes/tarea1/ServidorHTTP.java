package redes.tarea1;
import java.net.*;
import java.io.*;
import java.util.Date;

public class ServidorHTTP {
	
	public static final int PORT = 9000;

	public static void main(String[] args) {
		try(ServerSocket server = new ServerSocket(PORT)){
			while(true) {
				try(Socket connection = server.accept()) {
					Writer out = new OutputStreamWriter(connection.getOutputStream());
					Date now = new Date();
					out.write(now.toString() +"\r\n");
					out.flush();
					connection.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
