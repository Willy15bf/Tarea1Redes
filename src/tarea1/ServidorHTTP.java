package tarea1;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class ServidorHTTP {	
	
	private static final int NUM_THREADS = 50;
	private static final String INDEX_FILE = "index.html";	
	private final File rootDirectory;
	private final int port;


	
	public ServidorHTTP(File rootDirectory, int port) throws IOException {
		if (!rootDirectory.isDirectory()) {
			throw new IOException("No existe el directorio " + rootDirectory);
		}
		this.rootDirectory = rootDirectory;
		this.port = port;

	}	

	public void start() throws IOException{
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		try (ServerSocket server = new ServerSocket(port)) {
			//Socket chatSocket = new Socket("127.0.0.1", 9000);
			while (true) {
				try {
					Socket request = server.accept();
					Future<Void> future = pool.submit(new Peticion(rootDirectory, INDEX_FILE, request));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		File docroot;
		int port;
		docroot = new File("pages");//donde estan las paginas			

		try {
			port = Integer.parseInt(args[0]);
			if(port < 0 || port > 65535) {
				port = 8080;//puerto por defecto
			}
			
		} catch(RuntimeException e) {
			port = 8080;
			
		}
		
				
		try {
			ServidorHTTP servidorWeb = new ServidorHTTP(docroot, port);
			servidorWeb.start();
			System.out.println("Servidor escuchando en el puerto: " + port);
		} catch (IOException e) {			
			System.out.println("El servidor no pudo iniciar");
		}
					
	}

}