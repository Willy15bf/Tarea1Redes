package tarea1;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

import chat.Message;

public class HttpServer {	
	
	private static final int NUM_THREADS = 20;
	private static final String INDEX_FILE = "index.html";	
	private final File rootDirectory;
	private final int port;
	private ExecutorService httpRequests;	
	//Para trabajar con tcp chat server
	private Socket chatConnection;
    private ObjectOutputStream outStream;
    private int chatPort;
    private String chatServer;
    private Message message;
    private ExecutorService listener;
    protected BlockingQueue<Message> messagesQueue;

	
	public HttpServer(File rootDirectory, int port, String chatServer, int chatPort) throws IOException {
		if (!rootDirectory.isDirectory()) {
			throw new IOException("No existe el directorio " + rootDirectory);
		}
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.chatServer = chatServer;
		this.chatPort = chatPort;

	}	

	public void start() throws IOException{
		httpRequests = Executors.newFixedThreadPool(NUM_THREADS);
		listener = Executors.newSingleThreadExecutor();
		messagesQueue = new LinkedBlockingQueue<Message>();
		try (ServerSocket server = new ServerSocket(port)) {
			
			chatConnection = new Socket(chatServer, chatPort);
			outStream = new ObjectOutputStream(chatConnection.getOutputStream());			
			listener.submit(new MessagesListener(chatConnection, messagesQueue));
			
			while (true) {
				try {
					Socket request = server.accept();
					Future<chat.Message> future = httpRequests.submit(new HttpRequestHandler(rootDirectory, INDEX_FILE, request, messagesQueue));
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
			HttpServer servidorWeb = new HttpServer(docroot, port, "127.0.0.1", 9000);
			servidorWeb.start();			
			System.out.println("Servidor escuchando en el puerto: " + port);
		} catch (IOException e) {			
			System.out.println("El servidor no pudo iniciar");
		}
					
	}

}