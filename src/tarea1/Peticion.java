package tarea1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Peticion implements Callable<Void> {

	private final Socket connection;
	private File rootDirectory;
	private String indexFileName = "index.html";
	private static final String contactosFilePath = "data/contactos.json";

	Peticion(File rootDirectory, String indexFileName, Socket connection) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException("rootDirectory debe ser un directorio, no un archivo");
		}
		try {
			rootDirectory = rootDirectory.getCanonicalFile();			
		} catch (IOException ex) {
		}
		this.rootDirectory = rootDirectory;
		if (indexFileName != null)
			this.indexFileName = indexFileName;
		this.connection = connection;

	}

	@Override
	public Void call() throws Exception {
		// TODO Auto-generated method stub
		String root = rootDirectory.getPath();
		File contactosFile = new File(contactosFilePath);
		try {
			OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
			Writer out = new OutputStreamWriter(raw);			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "US-ASCII"));
			String requestLine = in.readLine();			
			String[] tokens = requestLine.split("\\s+");
			String method = tokens[0];			
			String version = "";
			String queryString = tokens[1];			
			
			if (method.equals("GET")) {								
				if (queryString.endsWith("/")){
					queryString += indexFileName;
				}
					
				//Separar query string entre el archivo requerido y los posibles parametros enviados
				String[] urlParts = queryString.split("\\?");
				String pathName = urlParts[0];
												
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(pathName);				
				if (tokens.length > 2) {
					version = tokens[2];
				}
				File theFile = new File(rootDirectory, pathName.substring(1, pathName.length()));
				
				if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
										
					String parentFolder = theFile.getParentFile().getName();
					String fileName = theFile.getName();
										
					if(parentFolder.equals("contactos")) {
						if(fileName.equals("index.html")) {
							//obtener todos los contactos y mostrarlos por la tabla
							ContactoJson cj = new ContactoJson(contactosFile);
							List<Contacto> listaContactos = cj.retrieveAll();							
							String table = createContactsTable(listaContactos);							
							String index = new StringBuilder("<!DOCTYPE html>\r\n")
							.append("<html>\r\n")
							.append("<head>\r\n")
							.append("<title>Lista de contactos</title>\r\n")
							.append("<meta charset='utf-8'>\r\n")
							.append("<link rel='stylesheet' href='../css/bootstrap.min.css'>\r\n")
							.append("</head>\r\n")
							.append("<body>\r\n")
							.append(table)
							.append("</body>\r\n</html>\r\n").toString();						
							
							if (version.startsWith("HTTP/")) { 
								sendHeader(out, "HTTP/1.1 200 OK", contentType, index.length());
							}	
							
							out.write(index);
							out.flush();
							
						} else if(fileName.equals("view.html")){
							if(urlParts.length == 2) { //tiene parametros								
								 Map<String, List<String>> params = getUrlParameters(queryString);
								 System.out.println(params);
								 int contactoId = Integer.parseInt(params.get("id").get(0));
								 ContactoJson cj = new ContactoJson(contactosFile);
								 Contacto contacto = cj.retriveOne(contactoId);
								 
								 if(contacto == null) {
									String body = clientError();
									if (version.startsWith("HTTP/")) { 
										sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
									}
									out.write(body);
									out.flush();
								 } else {
									 //construir la pagina con la tabla  y los contactos									
									 String perfil = createPerfilView(contacto);
									 String view = new StringBuilder("<!DOCTYPE html>\r\n")
										.append("<html>\r\n")
										.append("<head>\r\n")
										.append("<title>Lista de contactos</title>\r\n")
										.append("<meta charset='utf-8'>\r\n")
										.append("<link rel='stylesheet' href='../css/bootstrap.min.css'>\r\n")
										.append("</head>\r\n")
										.append("<body>\r\n")										
										.append(perfil)
										.append("</body>\r\n</html>\r\n").toString();	
									 
									 if (version.startsWith("HTTP/")) { 
										sendHeader(out, "HTTP/1.1 200 OK", contentType, view.length());
									 }	
										
									 out.write(view);
									 out.flush();
									 
								 }
			 
							} else {// else si tiene parametros el query string
								String body = clientError();
								if (version.startsWith("HTTP/")) { 
									sendHeader(out, "HTTP/1.1 404 File Not Found", "text/html; charset=utf-8", body.length());
								}
								out.write(body);
								out.flush();
							}//fin if si tiene parametros el query string							
						} else if(fileName.equals("new.html")) {
							byte[] theData = Files.readAllBytes(theFile.toPath());
							if (version.startsWith("HTTP/")) { 
								sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
							}					
							raw.write(theData);
							raw.flush();	
							
						} else {
							String body = clientError();
							if (version.startsWith("HTTP/")) { 
								sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
							}
							out.write(body);
							out.flush();
						}
					} else {// else si esta dentro del directorio contactos
						byte[] theData = Files.readAllBytes(theFile.toPath());
						if (version.startsWith("HTTP/")) { 
							sendHeader(out, "HTTP/1.1 200 OK", contentType, theData.length);
						}					
						raw.write(theData);
						raw.flush();						
					}					
					
				} else {//Si simplemente no se encontro el archivo buscado
					String body = clientError();
					if (version.startsWith("HTTP/")) { 
						sendHeader(out, "HTTP/1.1 404 File Not Found", "text/html; charset=utf-8", body.length());
					}
					out.write(body);
					out.flush();
				}
			} else if(method.equals("POST")) {
				//Leemos hasta encontrar una linea vacia
				//while(!(in.readLine().equals(""))){}
				//se lee la siguiente linea donde se encuentra el cuerpo de la peticion
				//String bodyRequest = in.readLine();
				//Map<String, List<String>> params = splitParameters(bodyRequest);
				//solo para chequear
				//String[] urlParts = queryString.split("\\?");
				
				//String pathName = urlParts[0];
				//System.out.println(pathName);
				//System.out.println(params);
				
				if (tokens.length > 2) {
					version = tokens[2];
				}
				
				//System.out.println(version);
				
				String contentType = "text/html; charset=UTF-8";
				/*
				 String responsePage = new StringBuilder("<!DOCTYPE html>\r\n")
				.append("<HTML>\r\n")
				.append("<HEAD><TITLE>Contacto guardado con exito</TITLE>\r\n")
				.append("</HEAD>\r\n")
				.append("<BODY>")
				.append("<H1>Contacto guardado con exito</H1>\r\n")
				.append("<a href='/contactos/index.html'>Volver</a>")
				.append("</BODY></HTML>\r\n").toString();
				*/
				/*
				if (version.startsWith("HTTP/")) {																
					sendHeader(out, "HTTP/1.1  FOUND", contentType, 0);
				}
				*/	
				sendHeader(out, "HTTP/1.1 200 OK", contentType, 0);
				String location = "Location: /contactos/";
				String connection = "Connection: close";
				out.write(location);
				out.write(connection);
				out.flush();
							
				/*
				if(pathName.startsWith("/contactos/")) {
					if(pathName.endsWith("new.html")){
						//if(!params.get("name").isEmpty() && !params.get("ip").isEmpty() && !params.get("port").isEmpty()) {
							ContactoJson cj = new ContactoJson(contactosFile);
							Contacto nuevoContacto = new Contacto();
							nuevoContacto.setNombre(params.get("name").get(0));
							nuevoContacto.setIp(params.get("ip").get(0));
							nuevoContacto.setPuerto(Integer.parseInt(params.get("name").get(0)));
							cj.save(nuevoContacto);
							String contentType = "text/html";
							String responsePage = new StringBuilder("<HTML>\r\n")
							.append("<HEAD><TITLE>Contacto guardado con exito</TITLE>\r\n")
							.append("</HEAD>\r\n")
							.append("<BODY>")
							.append("<H1>Contacto guardado con exito</H1>\r\n")
							.append("<a href='/contactos/index.html'>Volver</a>")
							.append("</BODY></HTML>\r\n").toString();
							if (version.startsWith("HTTP/")) {																
								sendHeader(out, "HTTP/1.1 201 CREATED", contentType, responsePage.length());
							}	
							out.write(responsePage);
							out.flush();
						//}
						
					} else {
						String body = clientError();
						if (version.startsWith("HTTP/")) { 
							sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
						}
						out.write(body);
						out.flush();
					}
					
				} else {
					String body = clientError();
					if (version.startsWith("HTTP/")) { 
						sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
					}
					out.write(body);
					out.flush();
				}	
				
				*/
				
				
			} else { 
				String body = serverError();
				if (version.startsWith("HTTP/")) { 
					sendHeader(out, "HTTP/1.1 501 Not Implemented", "text/html;charset=utf-8", body.length());
				}
				out.write(body);
				out.flush();
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

		return null;

	}
	
	private String clientError() {
		return new StringBuilder("<HTML>\r\n")
		.append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
		.append("</HEAD>\r\n")
		.append("<BODY>")
		.append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
		.append("</BODY></HTML>\r\n").toString();
	}
	
	private String serverError(){
		return new StringBuilder("<HTML>\r\n")
		.append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
		.append("</HEAD>\r\n").append("<BODY>")
		.append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
		.append("</BODY></HTML>\r\n").toString();	
	}

	private void sendHeader(Writer out, String responseCode, String contentType, int length) throws IOException {
		out.write(responseCode + "\r\n");
		Date now = new Date();
		out.write("Date: " + now + "\r\n");
		out.write("Servidor:ServidorHTTP  2.0\r\n");
		out.write("Content-length: " + length + "\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}
	
	private void sendPostHeader(Writer out, String responseCode, String contentType, int length) throws IOException {
		out.write(responseCode + "\r\n");
		Date now = new Date();
		out.write("Date: " + now + "\r\n");
		out.write("Servidor:ServidorHTTP  2.0\r\n");
		out.write("Content-length: " + length + "\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}
	
	private Map<String, List<String>> splitParameters(String parameters)
			throws UnsupportedEncodingException {
		Map<String, List<String>> params = new HashMap<String, List<String>>();

		for (String param : parameters.split("&")) {
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
	
	private Map<String, List<String>> getUrlParameters(String url)
	        throws UnsupportedEncodingException {
	    Map<String, List<String>> params = new HashMap<String, List<String>>();
	    String[] urlParts = url.split("\\?");
	    if (urlParts.length > 1) {
	        String query = urlParts[1];
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
	    }
	    return params;
	}
	
	private String createContactsTable(List<Contacto> listaContactos) {
		StringBuilder table = new StringBuilder("<table class='table table-striped'>")
		.append("<thead>\r\n")
		.append("<tr>\r\n")
		.append("<th>Nombre</th>\r\n")
		.append("<th>Dirección IP</th>\r\n")
		.append("<th>Puerto</th>\r\n")
		.append("</tr>\r\n")
		.append("</thead>\r\n")
		.append("<tbody>\r\n");
	
		for(Contacto contacto : listaContactos) {			
			table.append("<tr>\r\n")
			.append("<td><a href='/contactos/view.html?id=" + contacto.getId()  + "'>" + contacto.getNombre() + "</a></td>\r\n")
			.append("<td>" + contacto.getIp() + "</td>\r\n")
			.append("<td>" + contacto.getPuerto() + "</td>\r\n")
			.append("</tr>\r\n");		
					
		}
		table.append("</tbody>\r\n</table>\r\n");		
		
		return table.toString();
		
	}
	
	private String createPerfilView(Contacto contacto) {		
		StringBuilder perfil = new StringBuilder("<div class='panel panel-default'>\r\n")
		.append("<div class='panel-heading'>\r\n")
		.append("<h3 class='panel-title'>Detalles de contacto</h3>\r\n")
		.append("</div>\r\n")
		.append("<div class='panel-body'>\r\n")
		.append("<dl class='dl-horizontal'>\r\n")
		.append("<dt>Nombre</dt>")
		.append("<dd>" + contacto.getNombre() + "</dd>\r\n")
		.append("<dt>Dirección IP</dt>")
		.append("<dd>" + contacto.getIp() + "</dd>\r\n")
		.append("<dt>Puerto</dt>")
		.append("<dd>" + contacto.getPuerto() + "</dd>\r\n")
		.append("</dl>\r\n")
		.append("<a href='/contactos/index.html' type='button' class='btn btn-primary pull-right'>Volver</a>\r\n")
		.append("</div>\r\n")
		.append("</div>\r\n");		
		
		return perfil.toString();
		
	}

}
