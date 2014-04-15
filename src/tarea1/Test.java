package tarea1;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static Map<String, List<String>> getUrlParameters(String url)
			throws UnsupportedEncodingException {
		Map<String, List<String>> params = new HashMap<String, List<String>>();

		for (String param : url.split("&")) {
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File contactosFile = new File("data/contactos.json");
		
		ContactoJson cj = new ContactoJson(contactosFile);
		
		try {
			List<Contacto> listaContactos = cj.retrieveAll();
			System.out.println(listaContactos);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
