package tarea1;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import chat.Message;

public class ServidorHTTP {	
	
	private static final int NUM_THREADS = 10;
	private static final String INDEX_FILE = "index.html";	
	private final File rootDirectory;
	private final int port;
	private ExecutorService pool;
	//Para trabajar con tcp chat server
	private Socket chatConnection;
	//private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private int chatPort;
    private String chatServer;
    private Message message;


	
	public ServidorHTTP(File rootDirectory, int port, String chatServer, int chatPort) throws IOException {
		if (!rootDirectory.isDirectory()) {
			throw new IOException("No existe el directorio " + rootDirectory);
		}
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.chatServer = chatServer;
		this.chatPort = chatPort;

	}	

	public void start() throws IOException{
		this.pool = Executors.newFixedThreadPool(NUM_THREADS);
		try (ServerSocket server = new ServerSocket(port)) {
			
			this.chatConnection = new Socket(this.chatServer, this.chatPort);
			//this.inStream = new ObjectInputStream(this.chatConnection.getInputStream());
			this.outStream = new ObjectOutputStream(this.chatConnection.getOutputStream());
			
			while (true) {
				try {
					Socket request = server.accept();
					Future<chat.Message> future = pool.submit(new Peticion(rootDirectory, INDEX_FILE, request));
					message = future.get();
					
					if(message != null) {						
						System.out.println(message.getMessage());
						outStream.writeObject(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(chatConnection != null) {
				chatConnection.close();
			}
			
		}
	}
	
	
	public static void main(String[] args) {
		File docroot;
		int port;
		docroot = new File("pages");//donde estan las paginas			

		try {
			port = Integer.parseInt(args[0]);
			if(port < 0 || port > 65535 || port == 9000) {
				port = 8080;//puerto por defecto
			}
			
		} catch(RuntimeException e) {
			port = 8080;
			
		}
		
				
		try {
			ServidorHTTP servidorWeb = new ServidorHTTP(docroot, port, "127.0.0.1", 9000);
			servidorWeb.start();
			//servidorWeb.connect();
			System.out.println("Servidor escuchando en el puerto: " + port);
		} catch (IOException e) {			
			System.out.println("El servidor no pudo iniciar");
		}
					
	}

}