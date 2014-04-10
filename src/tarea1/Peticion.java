package tarea1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.Callable;

public class Peticion implements Callable<Void> {

	private final Socket connection;
	private File rootDirectory;
	private String indexFileName = "index.html";

	Peticion(File rootDirectory, String indexFileName, Socket connection) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException("rootDirectory debe ser un directorio, no un archivo");
		}
		try {
			rootDirectory = rootDirectory.getCanonicalFile();
			System.out.println(rootDirectory.getPath());
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
		try {
			OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
			Writer out = new OutputStreamWriter(raw);
			Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "US-ASCII");
			StringBuilder requestLine = new StringBuilder();
			while (true) {
				int c = in.read();
				if (c == '\r' || c == '\n')
					break;
				requestLine.append((char) c);
			}
			String get = requestLine.toString();
			String[] tokens = get.split("\\s+");
			String method = tokens[0];
			String version = "";
			if (method.equals("GET")) {
				String fileName = tokens[1];
				System.out.println(fileName);
				if (fileName.endsWith("/"))
					fileName += indexFileName;
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
				System.out.println(contentType);
				if (tokens.length > 2) {
					version = tokens[2];
				}
				System.out.println(fileName.substring(1, fileName.length()));
				File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));
				
				if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
					byte[] theData = Files.readAllBytes(theFile.toPath());
					if (version.startsWith("HTTP/")) { 
						sendHeader(out, "HTTP/1.0 200 OK", contentType,
								theData.length);
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
			} else { 
				String body = serverError();
				if (version.startsWith("HTTP/")) { 
					sendHeader(out, "HTTP/1.0 501 Not Implemented", "text/html;charset=utf-8", body.length());
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
		out.write("Server: JHTTP 2.0\r\n");
		out.write("Content-length: " + length + "\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}

}
