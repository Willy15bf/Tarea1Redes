package contacto;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class ContactJson {
	
	private File contactosFile;
	
	public ContactJson(File contactosFile) {
		this.setContactosFile(contactosFile);
	}
	
	public Contact retriveOne(int id) throws IOException{
		Contact contacto = null;
		FileReader reader = null;
		reader = new FileReader(contactosFile);
		Gson gson = new Gson();
		List<Contact> listaContactos = gson.fromJson(reader, new TypeToken<List<Contact>>(){}.getType());
		for(Contact item : listaContactos) {
			if(item.getId() == id){
				contacto = item;
			}
		}		
		reader.close();		
		return contacto;
		
	}
	
	public List<Contact> retrieveAll() {
		List<Contact> listaContactos = Collections.<Contact>emptyList();
		FileReader reader = null;
		try {
			reader = new FileReader(contactosFile);
			Gson gson = new Gson();
			listaContactos = gson.fromJson(reader, new TypeToken<List<Contact>>(){}.getType());
			reader.close();
			return listaContactos;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listaContactos;
		}
		
		
	}
	
	public void save(Contact nuevoContacto) {
		
		if(!contactosFile.exists()) {
			try {
				contactosFile.createNewFile();
				FileWriter writer = new FileWriter(contactosFile);
				List<Contact> listaContactos = new ArrayList<Contact>();
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
				List<Contact> listaContactos = gson.fromJson(reader, new TypeToken<List<Contact>>(){}.getType());
				reader.close();
				FileWriter writer = new FileWriter(contactosFile, false);
				//aqui calcular el id del nuevo registro
				Contact last = listaContactos.get(listaContactos.size() - 1);
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
