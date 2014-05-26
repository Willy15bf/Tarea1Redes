package tarea1;

import java.util.List;

public class HtmlBuilder {
	
	public static String createContactsTable(List<Contacto> listaContactos) {
		StringBuilder table = new StringBuilder("<table class='table table-hover'>")
		.append("<thead>\r\n")
		.append("<tr>\r\n")
		.append("<th>Nombre</th>\r\n")
		.append("<th>Dirección IP</th>\r\n")
		.append("<th>Puerto</th>\r\n")
		.append("<th>Chat</th>\r\n")
		.append("</tr>\r\n")
		.append("</thead>\r\n")
		.append("<tbody>\r\n");
		
		if(!listaContactos.isEmpty()) {
			for(Contacto contacto : listaContactos) {			
				table.append("<tr>\r\n")
				.append("<td><a href='/contactos/view.html?id=" + contacto.getId()  + "'>" + contacto.getNombre() + "</a></td>\r\n")
				.append("<td>" + contacto.getIp() + "</td>\r\n")
				.append("<td>" + contacto.getPuerto() + "</td>\r\n")
				.append("<td><a href='/contactos/chat.html?id=" + contacto.getId()  + "'>" + "Iniciar" + "</a></td>\r\n")
				.append("</tr>\r\n");		
						
			}
		}
		
		table.append("</tbody>\r\n</table>\r\n");		
		
		return table.toString();
		
	}
	
	public static String createPerfilView(Contacto contacto) {		
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
	
	
	
	
	
	
	//////////
	
	public static String createChat(Contacto contacto) {		
		StringBuilder chat = new StringBuilder("<div class='col-md-5'>\r\n")
		.append("<div class='panel panel-primary'>\r\n")
		.append("<div class='panel-heading'>\r\n")
		.append("</div>\r\n")
		
		.append("<div class='panel-body'>\r\n")

		.append("<h2>Pedro</h2>\r\n")
		.append("<p>Hola Juan</p>\r\n")
		
		.append("<h2>Juan</h2>\r\n")
		.append("<p>Hola Pedro</p>\r\n")
		
		.append("<div class='panel-footer'>\r\n")
                    .append("<div class='input-group'>\r\n")
                        .append("<input id='btn-input' type='text' class='form-control input-sm' placeholder='Escriba su mensaje aca...' />\r\n")
                        .append("<span class='input-group-btn'>\r\n")
                            .append("<button class='btn btn-warning btn-sm' id='btn-chat'> Enviar</button>\r\n")
                        .append("</span>\r\n")
                    .append("</div>\r\n")
        .append("</div>\r\n")        
		
		
		
		.append("</div>\r\n")		
		.append("</div>\r\n");
		
		return chat.toString();
		
	}
	
	
	
	
	
	
	
	////////////////
	
	
	public static String createPageHeader(String title, boolean rootIndex) {
		StringBuilder header = new StringBuilder("<!DOCTYPE html>")
		.append("\r\n")
		.append("<html>")
		.append("\r\n")
		.append("<head>")
		.append("\r\n")
		.append("<meta charset='utf-8'>")	
		.append("\r\n")
		.append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
		.append("\r\n")
		.append("<title>" + title + "</title>")
		.append("\r\n");
		
		if(rootIndex) {
			header.append("<link rel='shortcut icon' href='assets/ico/favicon.ico'>")
			.append("\r\n")
			.append("<link href='assets/css/bootstrap.min.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='assets/css/bootstrap-theme.min.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='assets/css/starter-template.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='assets/css/custom.css' rel='stylesheet'>");
		} else {
			header.append("<link rel='shortcut icon' href='../assets/ico/favicon.ico'>")
			.append("\r\n")
			.append("<link href='../assets/css/bootstrap.min.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='../assets/css/bootstrap-theme.min.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='../assets/css/starter-template.css' rel='stylesheet'>")
			.append("\r\n")
			.append("<link href='../assets/css/custom.css' rel='stylesheet'>");
		}
		
		
		header.append("\r\n")
		.append("<body>")
		.append("\r\n")
		.append("<nav class='navbar navbar-inverse navbar-fixed-top' role='navigation'>")
		.append("\r\n")
		.append("<div class='container'>")
		.append("\r\n")
		.append("<div class='navbar-header'>")
		.append("\r\n")
		.append("<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='.navbar-collapse'>")
		.append("\r\n")
		.append("<span class='sr-only'>Toggle navigation</span>")
		.append("\r\n")
		.append("<span class='icon-bar'></span>")
		.append("<span class='icon-bar'></span>")
		.append("<span class='icon-bar'></span>")
		.append("\r\n")
		.append("</button>")
		.append("\r\n");
		
		if(rootIndex) {
			header.append("<a class='navbar-brand' href='index.html'>Avioncito de papel I</a>");
		} else {
			header.append("<a class='navbar-brand' href='../index.html'>Avioncito de papel I</a>");
		}		
		
		header.append("\r\n")
		.append("</div>")
		.append("\r\n")
		.append("<div class='collapse navbar-collapse'>")
		.append("\r\n")
		.append("<ul class='nav navbar-nav'>")
		.append("\r\n");
		
		if(rootIndex) {
			header.append("<li><a href='contactos/index.html'>Ver contactos</a></li>")
			.append("\r\n")
			.append("<li><a href='contactos/new.html'>Agregar contactos</a></li>");
		} else {
			header.append("<li><a href='index.html'>Ver contactos</a></li>")
			.append("\r\n")
			.append("<li><a href='new.html'>Agregar contactos</a></li>");
		}
		
		
		header.append("\r\n")
		.append("</ul>")
		.append("\r\n")
		.append("</div>")
		.append("\r\n")
		.append("</div>")
		.append("\r\n")
		.append("</nav>")
		.append("\r\n")
		.append("<div class='container'>")
		.append("\r\n");
		
		
		return header.toString();
	}
	
	public static String createPageFooter(boolean rootIndex) {
		StringBuilder footer = new StringBuilder()
		.append("</div>")
		.append("\r\n");
		
		if(rootIndex) {
			footer.append("<script src='assets/js/jquery-2.1.0.min.js'></script>")
			.append("\r\n")
			.append("<script src='assets/js/bootstrap.min.js'></script>");
		} else {
			footer.append("<script src='../assets/js/jquery-2.1.0.min.js'></script>")
			.append("\r\n")
			.append("<script src='../assets/js/bootstrap.min.js'></script>");
		}	
		
		footer.append("\r\n")
		.append("</body>")
		.append("\r\n")
		.append("</html>");
		
		return footer.toString();
	}
	
	public static String errorPage(int code, String message) {
		return new StringBuilder("<!DOCTYPE html>")
		.append("\r\n")
		.append("<html>")
		.append("\r\n")
		.append("<head>")
		.append("<title>" + message + "</title>")
		.append("\r\n")
		.append("</head>")
		.append("\r\n")
		.append("<body>")
		.append("\r\n")
		.append("<h1>HTTP Error" +  code + " :"  + message + "</h1>")
		.append("\r\n")
		.append("</body>")
		.append("\r\n")
		.append("</html>").toString();
	}	


}
