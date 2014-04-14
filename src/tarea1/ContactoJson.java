package tarea1;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class ContactoJson {
	
	private File contactosFile;
	
	ContactoJson(File contactosFile) {
		this.setContactosFile(contactosFile);
	}
	
	public Contacto retriveOne(int id) throws IOException{
		Contacto contacto = null;
		FileReader reader = null;
		reader = new FileReader(contactosFile);
		Gson gson = new Gson();
		List<Contacto> listaContactos = gson.fromJson(reader, new TypeToken<List<Contacto>>(){}.getType());
		for(Contacto item : listaContactos) {
			if(item.getId() == id){
				contacto = item;
			}
		}		
		reader.close();		
		return contacto;
		
	}
	
	public List<Contacto> retrieveAll() throws IOException {
		List<Contacto> listaContactos = null;
		FileReader reader = null;
		reader = new FileReader(contactosFile);
		Gson gson = new Gson();
		listaContactos = gson.fromJson(reader, new TypeToken<List<Contacto>>(){}.getType());
		reader.close();
		return listaContactos;
		
	}
	
	public void save(Contacto nuevoContacto) {
		//aqui determinar el id del nuevo contacto
		if(!contactosFile.exists()) {
			try {
				contactosFile.createNewFile();
				FileWriter writer = new FileWriter(contactosFile);
				List<Contacto> listaContactos = new ArrayList<Contacto>();
				nuevoContacto.setId(1);
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
				//aqui calcular el id del nuevo registro
				Contacto last = listaContactos.get(listaContactos.size() - 1);
				nuevoContacto.setId(last.getId() + 1);				
				listaContactos.add(nuevoContacto);
				String json = gson.toJson(listaContactos);
				writer.write(json);
				writer.close();				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public File getContactosFile() {
		return contactosFile;
	}

	public void setContactosFile(File contactosFile) {
		this.contactosFile = contactosFile;
	}

}
