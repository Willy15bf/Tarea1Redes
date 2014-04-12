package tarea1;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File contactosFile = new File("data/contactos.json");
		if(!contactosFile.exists()) {
			try {
				contactosFile.createNewFile();
				FileWriter writer = new FileWriter(contactosFile);
				List<Contacto> listaContactos = new ArrayList<Contacto>();
				Contacto nuevoContacto = new Contacto("Ivan Gonzalez", "127.0.0.1", 9000);
				listaContactos.add(nuevoContacto);
				Gson gson = new Gson();
				String jsonNuevoContacto = gson.toJson(listaContactos);
				writer.write(jsonNuevoContacto);
				writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileReader reader = new FileReader(contactosFile);				
				Gson gson = new Gson();				
				List<Contacto> listaContactos = gson.fromJson(reader, new TypeToken<List<Contacto>>(){}.getType());
				reader.close();
				FileWriter writer = new FileWriter(contactosFile, false);
				
				
				listaContactos.add(new Contacto("Joaquin Gonzalez", "127.5.0.2", 3000));
				String json = gson.toJson(listaContactos);
				writer.write(json);
				writer.close();
				System.out.println(json);
				System.out.println(listaContactos);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
				
	}

}
