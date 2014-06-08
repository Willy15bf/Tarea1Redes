package tarea1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.*;

import contacto.Contact;
import contacto.ContactJson;
import chat.Message;

public class HttpRequestHandler implements Callable<Message> {

	private HttpServer httpServer;
	private final Socket connection;
	private File rootDirectory;
	private String indexFileName = "index.html";
	private static final String contactosFilePath = "data/contactos.json";
	private OutputStream raw;
	private Writer out;
	private BufferedReader in;
	private Message chatMessage = null;
	
	
	HttpRequestHandler(File rootDirectory, String indexFileName, Socket connection, HttpServer httpServer) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException(
					"rootDirectory debe ser un directorio, no un archivo");
		}
		this.connection = connection;	
		this.httpServer = httpServer;
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
	
	private void getMessages(Map<String, List<String>> params) throws IOException, InterruptedException {
		//String userFrom = params.get("from").get(0);
		List<Message> messageList = new ArrayList<Message>();
		if(!httpServer.getMessagesQueue().isEmpty()) {
			messageList.add(httpServer.getMessagesQueue().take());
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
		//chatMessage.setFileTransfer(false);
		//usuario emisor
		chatMessage.getUserFrom().setIpAddress(httpServer.getUser().getIpAddress());
		chatMessage.getUserFrom().setUserName(httpServer.getUser().getUserName());
		chatMessage.getUserFrom().setPort(httpServer.getPort());
		//mensaje
		chatMessage.setType(Integer.parseInt(params.get("type").get(0)));
		chatMessage.setMessage(params.get("message").get(0));
		//falta usuario receptor
		chatMessage.getUserTo().setIpAddress(params.get("ipAddress").get(0));
		chatMessage.getUserTo().setPort(Integer.parseInt(params.get("port").get(0)));
		chatMessage.getUserTo().setUserName(params.get("userName").get(0));
		
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
				nuevoContacto.setUserName(params.get("name").get(0));
				nuevoContacto.setIpAddress(params.get("ip").get(0));
				nuevoContacto.setPort(Integer.parseInt(params.get(
						"port").get(0)));
				cj.save(nuevoContacto);						
										
				String responsePage = new StringBuilder(
						HtmlBuilder.createPageHeader(
								"Contacto agregado con éxito", false, httpServer.getUser()))
						.append("<row>").append("<h3>Contacto agregado con éxito</h3>")
						.append("</row>")
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
				getMessages(params);
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
					String index = buildContactIndex(listaContactos);					
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
							String view = buildContactView(contacto);
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
	
	public String buildContactIndex(List<Contact> listaContactos) {
		String table = HtmlBuilder
				.createContactsTable(listaContactos);
		String index = new StringBuilder(
				HtmlBuilder.createPageHeader(
						"Lista de contactos", false, httpServer.getUser()))
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
		return index;
		
	}
	
	public String buildContactView(Contact contacto) {
		String perfil = HtmlBuilder
				.createPerfilView(contacto);
		String chatWindow = HtmlBuilder.createChatWindow();
		String view = new StringBuilder(
				HtmlBuilder.createPageHeader(
						"Ver perfil", false, httpServer.getUser()))
				.append("<row>")
				.append("<script>window.port = " + httpServer.getPort() +  ";</script>")
				.append("<div class='page-header'>")
				.append("<h1>Ver perfil de contacto</h1>")
				.append("</div>")
				.append("<div class='col-md-6'>")				
				.append(perfil)
				.append("</div>")
				.append("<div class='col-md-6'>")
				.append(chatWindow)
				.append("</div>")
				.append("</row>")
				.append(HtmlBuilder
						.createPageFooter(false))
				.toString();
		return view;
	}


}
