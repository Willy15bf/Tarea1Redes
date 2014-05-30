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
import com.google.gson.*;

public class Peticion implements Callable<Void> {

	private final Socket connection;
	private File rootDirectory;
	private String indexFileName = "index.html";
	private static final String contactosFilePath = "data/contactos.json";

	Peticion(File rootDirectory, String indexFileName, Socket connection) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException(
					"rootDirectory debe ser un directorio, no un archivo");
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
			OutputStream raw = new BufferedOutputStream(
					connection.getOutputStream());
			Writer out = new OutputStreamWriter(raw);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "US-ASCII"));
			String requestLine = in.readLine();
			String[] tokens = requestLine.split("\\s+");
			String method = tokens[0];
			String version = "";
			String queryString = tokens[1];

			if (method.equals("GET")) {
				if (queryString.endsWith("/")) {
					queryString += indexFileName;
				}

				// Separar query string entre el archivo requerido y los
				// posibles parametros enviados
				String[] urlParts = queryString.split("\\?");
				String pathName = urlParts[0];

				String contentType = URLConnection.getFileNameMap()
						.getContentTypeFor(pathName);
				if (tokens.length > 2) {
					version = tokens[2];
				}
				File theFile = new File(rootDirectory, pathName.substring(1,
						pathName.length()));
				
				
				if (theFile.canRead()
						&& theFile.getCanonicalPath().startsWith(root)) {

					String parentFolder = theFile.getParentFile().getName();
					String fileName = theFile.getName();

					if (parentFolder.equals("contactos")) {
						if (fileName.equals("index.html")) {
							// obtener todos los contactos y mostrarlos por la
							// tabla
							ContactoJson cj = new ContactoJson(contactosFile);
							List<Contacto> listaContactos = cj.retrieveAll();
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

							if (version.startsWith("HTTP/")) {
								sendHeader(out, "HTTP/1.1 200 OK", contentType,
										index.length());
							}

							out.write(index);
							out.flush();

						} else if (fileName.equals("view.html")) {
							if (urlParts.length == 2) { // tiene parametros
								Map<String, List<String>> params = getUrlParameters(queryString);								
								int contactoId = Integer.parseInt(params.get(
										"id").get(0));
								ContactoJson cj = new ContactoJson(
										contactosFile);
								Contacto contacto = cj.retriveOne(contactoId);

								if (contacto == null) {
									String body = HtmlBuilder.errorPage(404,
											"File Not Found");
									if (version.startsWith("HTTP/")) {
										sendHeader(out,
												"HTTP/1.1 404 Not Found",
												"text/html; charset=utf-8",
												body.length());
									}
									out.write(body);
									out.flush();
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

									if (version.startsWith("HTTP/")) {
										sendHeader(out, "HTTP/1.1 200 OK",
												contentType, view.length());
									}

									out.write(view);
									out.flush();

								}

							} else {// else si tiene parametros el query string
								String body = HtmlBuilder.errorPage(404,
										"File Not Found");
								if (version.startsWith("HTTP/")) {
									sendHeader(out, "HTTP/1.1 404 Not Found",
											"text/html; charset=utf-8",
											body.length());
								}
								out.write(body);
								out.flush();
							}// fin if si tiene parametros el query string

						} else if (fileName.equals("new.html") || fileName.equals("chat.html")) {
							byte[] theData = Files.readAllBytes(theFile.toPath());
							if (version.startsWith("HTTP/")) {
								sendHeader(out, "HTTP/1.1 200 OK", contentType,
										theData.length);
							}
							raw.write(theData);
							raw.flush();

						} else {
							String body = HtmlBuilder.errorPage(404,
									"File Not Found");
							if (version.startsWith("HTTP/")) {
								sendHeader(out, "HTTP/1.1 404 Not Found",
										"text/html; charset=utf-8",
										body.length());
							}
							out.write(body);
							out.flush();
						}
					} else {// else si esta dentro del directorio contactos
						byte[] theData = Files.readAllBytes(theFile.toPath());
						if (version.startsWith("HTTP/")) {
							sendHeader(out, "HTTP/1.1 200 OK", contentType,
									theData.length);
						}
						raw.write(theData);
						raw.flush();
					}

				} else {// Si simplemente no se encontro el archivo buscado
					String body = HtmlBuilder.errorPage(404, "File Not Found");
					if (version.startsWith("HTTP/")) {
						sendHeader(out, "HTTP/1.1 404 Not Found",
								"text/html; charset=utf-8", body.length());
					}
					out.write(body);
					out.flush();
				}
			} else if (method.equals("POST")) {

				int contentLength = 0;
				String line;
				String contentLengthHeader = "Content-Length: ";
				
				System.out.println("llego el request");

				while (!(line = in.readLine()).equals("")) {
					if (line.startsWith(contentLengthHeader)) {
						contentLength = Integer.parseInt(line
								.substring(contentLengthHeader.length()));
					}
				}

				StringBuilder bodyRequest = new StringBuilder();

				int c = 0;
				for (int i = 0; i < contentLength; i++) {
					c = in.read();
					bodyRequest.append((char) c);
				}

				Map<String, List<String>> params = getUrlParameters(bodyRequest
						.toString());
				// solo para chequear
				String[] urlParts = queryString.split("\\?");
				String action = urlParts[0];

				if (tokens.length > 2) {
					version = tokens[2];
				}
				
				if (action.startsWith("/contactos/")) {					
					if (action.endsWith("new.html")) {												
						ContactoJson cj = new ContactoJson(contactosFile);
						Contacto nuevoContacto = new Contacto();
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
						if (version.startsWith("HTTP/")) {
							sendHeader(out, "HTTP/1.1 200 OK",
									"text/html; charset=utf-8",
									responsePage.length());
						}
						out.write(responsePage);
						out.flush();
					} else if(action.endsWith("sendmessage")) {
						System.out.println(bodyRequest
								.toString());
						JsonObject jsonData = new JsonObject();
						jsonData.addProperty("response", "success");						
												
						if (version.startsWith("HTTP/")) {
							sendHeader(out, "HTTP/1.1 200 OK", "application/json; charset=utf-8", jsonData.toString().length());
						}
						out.write(jsonData.toString());
						out.flush();
					} else {
						String body = HtmlBuilder.errorPage(404, "Not Found");
						if (version.startsWith("HTTP/")) {
							sendHeader(out, "HTTP/1.1 404 Not Found",
									"text/html; charset=utf-8", body.length());
						}
						out.write(body);
						out.flush();
					}

				} else {
					String body = HtmlBuilder.errorPage(404, "Not Found");
					if (version.startsWith("HTTP/")) {
						sendHeader(out, "HTTP/1.1 404 Not Found",
								"text/html; charset=utf-8", body.length());
					}
					out.write(body);
					out.flush();
				}
				

			} else {
				String body = HtmlBuilder.errorPage(501, "Not Implemented");
				if (version.startsWith("HTTP/")) {
					sendHeader(out, "HTTP/1.1 501 Not Implemented",
							"text/html;charset=utf-8", body.length());
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

}
