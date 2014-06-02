package tarea1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.*;

import chat.Message;

public class HttpRequestHandler implements Callable<Message> {

	private final Socket connection;
	private File rootDirectory;
	private String indexFileName = "index.html";
	private static final String contactosFilePath = "data/contactos.json";
	private BlockingQueue<Message> messagesQueue;
	private OutputStream raw;
	private Writer out;
	private BufferedReader in;
	private Message chatMessage = null;
	
	
	HttpRequestHandler(File rootDirectory, String indexFileName, Socket connection, BlockingQueue<Message> messagesQueue) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException(
					"rootDirectory debe ser un directorio, no un archivo");
		}
		this.connection = connection;	
		this.messagesQueue = messagesQueue;
		try {
			rootDirectory = rootDirectory.getCanonicalFile();
			this.raw = new BufferedOutputStream(
					connection.getOutputStream());
			this.out = new OutputStreamWriter(raw);
			this.in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "US-ASCII"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rootDirectory = rootDirectory;
		if (indexFileName != null)
			this.indexFileName = indexFileName;
		

	}
	
	
	@Override
	public Message call() throws Exception {		
		try {			
			String requestLine = in.readLine();
			String[] tokens = requestLine.split("\\s+");
			String method = tokens[0];			
			String queryString = tokens[1];

			if (method.equals("GET")) {
				doGet(raw, out, in, queryString);			
				
			} else if (method.equals("POST")) {
				doPost(raw, out, in, queryString);								

			} else {
				sendNotImplementedPage();
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				connection.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return chatMessage;

	}
	
	
	private void sendNotFoundPage() throws IOException {
		String body = HtmlBuilder.errorPage(404, "Not Found");			
		sendHeader(out, "HTTP/1.1 404 Not Found",
					"text/html; charset=utf-8", body.length());
		
		out.write(body);
		out.flush();
	}
	
	private void sendNotImplementedPage() throws IOException {
		String body = HtmlBuilder.errorPage(501, "Not Implemented");		
		sendHeader(out, "HTTP/1.1 501 Not Implemented",
					"text/html;charset=utf-8", body.length());
		out.write(body);
	}

	private void sendHeader(Writer out, String responseCode,
			String contentType, int length) throws IOException {
		out.write(responseCode + "\r\n");
		Date now = new Date();
		out.write("Date: " + now + "\r\n");
		out.write("Servidor:ServidorHTTP  1.0\r\n");
		out.write("Content-length: " + length + "\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}
	
	private void getMessages() throws IOException, InterruptedException {
		List<Message> messageList = new ArrayList<Message>();
		if(!messagesQueue.isEmpty()) {
			messageList.add(messagesQueue.take());
		}						
		String jsonData = new Gson().toJson(messageList);		
		sendHeader(out, "HTTP/1.1 200 OK", "application/json; charset=utf-8", jsonData.toString().length());
		
		out.write(jsonData.toString());
		out.flush();
	}
	
	private void sendMessage(Map<String, List<String>> params) throws IOException{
		JsonObject jsonData = new JsonObject();
		jsonData.addProperty("response", "success");						
		chatMessage = new Message();
		chatMessage.setType(Message.MESSAGE);
		chatMessage.setMessage(params.get("message").get(0));	
		sendHeader(out, "HTTP/1.1 200 OK", "application/json; charset=utf-8", jsonData.toString().length());
		out.write(jsonData.toString());
		out.flush();		
				
	}

	private Map<String, List<String>> getUrlParameters(String url)
			throws UnsupportedEncodingException {
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		String[] urlParts = url.split("\\?");		
		String query = (urlParts.length > 1) ? urlParts[1] : urlParts[0];
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			String key = URLDecoder.decode(pair[0], "UTF-8");
			String value = "";
			if (pair.length > 1) {
				value = URLDecoder.decode(pair[1], "UTF-8");
			}
			List<String> values = params.get(key);
			if (values == null) {
				values = new ArrayList<String>();
				params.put(key, values);
			}
			values.add(value);
		}
		
		return params;
	}
	
	public void doPost(OutputStream raw, Writer out, BufferedReader in, String queryString ) throws IOException, InterruptedException {
		File contactosFile = new File(contactosFilePath);
		int contentLength = 0;
		String line;
		String contentLengthHeader = "Content-Length: ";

		while (!(line = in.readLine()).equals("")) {
			if (line.startsWith(contentLengthHeader)) {
				contentLength = Integer.parseInt(line
						.substring(contentLengthHeader.length()));
			}
		}

		StringBuilder bodyRequest = new StringBuilder();
		//leemos el body del request
		int c = 0;
		for (int i = 0; i < contentLength; i++) {
			c = in.read();
			bodyRequest.append((char) c);
		}
		
		//separamos los paramatros enviados
		Map<String, List<String>> params = getUrlParameters(bodyRequest
				.toString());
		// solo para chequear
		String[] urlParts = queryString.split("\\?");
		String action = urlParts[0];
			
		if (action.startsWith("/contactos/")) {					
			if (action.endsWith("new.html")) {												
				ContactJson cj = new ContactJson(contactosFile);
				Contact nuevoContacto = new Contact();
				nuevoContacto.setNombre(params.get("name").get(0));
				nuevoContacto.setIp(params.get("ip").get(0));
				nuevoContacto.setPuerto(Integer.parseInt(params.get(
						"port").get(0)));
				cj.save(nuevoContacto);						
										
				String responsePage = new StringBuilder(
						HtmlBuilder.createPageHeader(
								"Contacto agregado con éxito", false))
						.append("<row>").append("\r\n")
						.append("<h3>Contacto agregado con éxito</h3>")
						.append("\r\n")
						.append("</row>")
						.append("\r\n")
						.append(HtmlBuilder.createPageFooter(false))
						.toString();
				
					sendHeader(out, "HTTP/1.1 200 OK",
							"text/html; charset=utf-8",
							responsePage.length());
				
				out.write(responsePage);
				out.flush();
			} else if(action.endsWith("sendmessage")) {
				sendMessage(params);				
				
			} else if(action.endsWith("getmessages")) {
				getMessages();
			} else {
				sendNotFoundPage();
			}

		} else {
			sendNotFoundPage();
		}

	}
	
	public void doGet(OutputStream raw, Writer out, BufferedReader in, String queryString) throws IOException {
		if (queryString.endsWith("/")) {
			queryString += indexFileName;
		}
		String root = rootDirectory.getPath();
		File contactosFile = new File(contactosFilePath);
		String[] urlParts = queryString.split("\\?");
		String pathName = urlParts[0];

		String contentType = URLConnection.getFileNameMap()
				.getContentTypeFor(pathName);
		
		File theFile = new File(rootDirectory, pathName.substring(1,
				pathName.length()));
		String ext = FilenameUtils.getExtension(theFile.getAbsolutePath());
		
		//fix para el chromium o chrome 
		if(ext.equals("js")) {
			contentType = "application/x-javascript";
		} else if (ext.equals("css")) {
			contentType = "text/css";
		}
		
		if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
			String parentFolder = theFile.getParentFile().getName();
			String fileName = theFile.getName();

			if (parentFolder.equals("contactos")) {
				if (fileName.equals("index.html")) {
					// obtener todos los contactos y mostrarlos por la
					// tabla
					ContactJson cj = new ContactJson(contactosFile);
					List<Contact> listaContactos = cj.retrieveAll();
					String table = HtmlBuilder
							.createContactsTable(listaContactos);
					String index = new StringBuilder(
							HtmlBuilder.createPageHeader(
									"Lista de contactos", false))
							.append("<row>")									
							.append("\r\n")
							.append("<div class='page-header'>")
							.append("\r\n")
							.append("<h1>Lista de contactos</h1>")
							.append("\r\n")
							.append("</div>")
							.append("\r\n")
							.append(table)
							.append("</row>")
							.append(HtmlBuilder.createPageFooter(false))
							.toString();
					
					sendHeader(out, "HTTP/1.1 200 OK", contentType, index.length());
					out.write(index);
					out.flush();

				} else if (fileName.equals("view.html")) {
					if (urlParts.length == 2) { // tiene parametros
						Map<String, List<String>> params = getUrlParameters(queryString);								
						int contactoId = Integer.parseInt(params.get("id").get(0));
						ContactJson cj = new ContactJson(contactosFile);
						Contact contacto = cj.retriveOne(contactoId);

						if (contacto == null) {
							sendNotFoundPage();
						} else {
							// construir la pagina con la tabla y los
							// contactos
							String perfil = HtmlBuilder
									.createPerfilView(contacto);
							String view = new StringBuilder(
									HtmlBuilder.createPageHeader(
											"Ver perfil", false))
									.append("<row>")
									.append("\r\n")
									.append("<div class='page-header'>")
									.append("\r\n")
									.append("<h1>Ver perfil de contacto</h1>")
									.append("\r\n")
									.append("</div>")
									.append("\r\n")
									.append(perfil)
									.append("</row>")
									.append(HtmlBuilder
											.createPageFooter(false))
									.toString();

							sendHeader(out, "HTTP/1.1 200 OK", contentType, view.length());
							out.write(view);
							out.flush();

						}

					} else {// else si tiene parametros el query string
						sendNotFoundPage();
					}// fin if si tiene parametros el query string

				} else if (fileName.equals("new.html") || fileName.equals("chat.html")) {
					byte[] theData = Files.readAllBytes(theFile.toPath());
					
					sendHeader(out, "HTTP/1.1 200 OK", contentType, theData.length);					
					raw.write(theData);
					raw.flush();

				} else {
					sendNotFoundPage();
				}
			} else {// cualquier cosa fuera del directorio contactos
				byte[] theData = Files.readAllBytes(theFile.toPath());				
				sendHeader(out, "HTTP/1.1 200 OK", contentType, theData.length);				
				raw.write(theData);
				raw.flush();
			}

		} else {// Si simplemente no se encontro el archivo buscado
			sendNotFoundPage();
		}

		
	}


}
