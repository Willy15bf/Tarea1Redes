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
			while (true) {
				try {
					Socket request = server.accept();
					pool.submit(new Peticion(rootDirectory, INDEX_FILE, request));
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
		int port = 9000;
		try {
			docroot = new File("pages");//donde estan las paginas
			System.out.println(docroot.getPath());
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}
				
		try {
			ServidorHTTP servidorWeb = new ServidorHTTP(docroot, port);
			servidorWeb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
					
	}

}