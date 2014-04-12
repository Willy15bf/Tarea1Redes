package tarea1;

public class Contacto {
	private String nombre;
	private String ip;
	private int puerto;
	
	public Contacto(String nombre, String ip, int puerto) {
		this.nombre = nombre;
		this.ip = ip;
		this.puerto = puerto;
	}	
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPuerto() {
		return puerto;
	}
	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

}
