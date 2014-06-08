package tarea1;

import java.util.List;

import contacto.Contact;

public class HtmlBuilder {
	
	public static String createContactsTable(List<Contact> listaContactos) {
		StringBuilder table = new StringBuilder("<table class='table table-hover'>")
		.append("<thead>")
		.append("<tr>")
		.append("<th>Nombre</th>")
		.append("<th>Dirección IP</th>")
		.append("<th>Puerto</th>")
		.append("</tr>")
		.append("</thead>")
		.append("<tbody>");
		
		if(!listaContactos.isEmpty()) {
			for(Contact contacto : listaContactos) {			
				table.append("<tr>")
				.append("<td><a href='/contactos/view.html?id=" + contacto.getId()  + "'>" + contacto.getUserName() + "</a></td>")
				.append("<td>" + contacto.getIpAddress() + "</td>")
				.append("<td>" + contacto.getPort() + "</td>")
				.append("</tr>");		
						
			}
		}		
		table.append("</tbody></table>");		
		
		return table.toString();
		
	}
	
	public static String createPerfilView(Contact contacto) {		
		StringBuilder perfil = new StringBuilder("<div class='panel panel-default'>")
		.append("<div class='panel-heading'>")
		.append("<h3 class='panel-title'>Detalles de contacto</h3>")
		.append("</div>")
		.append("<div class='panel-body'>")
		.append("<dl class='dl-horizontal contact-details'>")
		.append("<dt>Nombre</dt>")
		.append("<dd id='contact-details-name'>" + contacto.getUserName() + "</dd>")
		.append("<dt>Dirección IP</dt>")
		.append("<dd id='contact-details-ip-address'>" + contacto.getIpAddress() + "</dd>")
		.append("<dt>Puerto</dt>")
		.append("<dd id='contact-details-port'>" + contacto.getPort() + "</dd>")
		.append("</dl>")		
		.append("</div>")
		.append("</div>");		
		
		return perfil.toString();
		
	}
	
	public static String createPageHeader(String title, boolean rootIndex, Contact user) {
		StringBuilder header = new StringBuilder("<!DOCTYPE html>")
		.append("<html>")
		.append("<head>")
		.append("<meta charset='utf-8'>")	
		.append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
		.append("<title>" + title + "</title>");
		
		if(rootIndex) {
			header.append("<link rel='shortcut icon' href='assets/ico/favicon.ico'>")
			.append("<link href='assets/css/bootstrap.min.css' rel='stylesheet'>")
			.append("<link href='assets/css/bootstrap-theme.min.css' rel='stylesheet'>")
			.append("<link href='assets/css/starter-template.css' rel='stylesheet'>")
			.append("<link href='assets/css/custom.css' rel='stylesheet'>")			
			.append("<link href='assets/css/chat.css' rel='stylesheet'>");
		} else {
			header.append("<link rel='shortcut icon' href='../assets/ico/favicon.ico'>")
			.append("<link href='../assets/css/bootstrap.min.css' rel='stylesheet'>")
			.append("<link href='../assets/css/bootstrap-theme.min.css' rel='stylesheet'>")
			.append("<link href='../assets/css/starter-template.css' rel='stylesheet'>")
			.append("<link href='../assets/css/custom.css' rel='stylesheet'>")
			.append("<link href='../assets/css/chat.css' rel='stylesheet'>");
		}		
		
		header.append("<body>")
		.append("<nav class='navbar navbar-inverse navbar-fixed-top' role='navigation'>")
		.append("<div class='container'>")
		.append("<div class='navbar-header'>")
		.append("<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='.navbar-collapse'>")
		.append("<span class='sr-only'>Toggle navigation</span>")
		.append("<span class='icon-bar'></span>")
		.append("<span class='icon-bar'></span>")
		.append("<span class='icon-bar'></span>")
		.append("</button>");
		
		if(rootIndex) {
			header.append("<a class='navbar-brand' href='index.html'>Avioncito de papel II</a>");
		} else {
			header.append("<a class='navbar-brand' href='../index.html'>Avioncito de papel II</a>");
		}		
		
		header.append("</div>")
		.append("<div class='collapse navbar-collapse'>")
		.append("<ul class='nav navbar-nav'>");
		
		if(rootIndex) {
			header.append("<li><a href='contactos/index.html'>Ver contactos</a></li>")
			.append("<li><a href='contactos/new.html'>Agregar contactos</a></li>");
		} else {
			header.append("<li><a href='index.html'>Ver contactos</a></li>")
			.append("<li><a href='new.html'>Agregar contactos</a></li>");
		}
		
		
		header.append("</ul>")
		.append("<p class='navbar-text navbar-right'>")
		.append("<span id='user-name'>")
		.append(user.getUserName())
		.append("</span>")
		.append("<span id='user-ip-address'>")
		.append(user.getIpAddress())
		.append("</span>")
		.append("<span id='user-port'>")
		.append(user.getPort())
		.append("</span>")
		.append("</p>")		
		.append("</div>")
		.append("</div>")
		.append("</nav>")
		.append("<div class='container'>");
		
		
		return header.toString();
	}

	public static String createChatWindow() {
		StringBuilder chatWindow = new StringBuilder()
		.append("<div class='panel panel-default'>")
		.append("<div class='panel-heading'>")
		.append("<span class='glyphicon glyphicon-comment'></span> Chat")
		.append("<div class='btn-group pull-right'>")
		.append("<button type='button' class='btn btn-default btn-xs dropdown-toggle' data-toggle='dropdown'>")
		.append("<span class='glyphicon glyphicon-chevron-down'></span>")
		.append("</button>")
		.append("<ul class='dropdown-menu slidedown'>")
		.append("<li><a id='get-messages' href='#''><span class='glyphicon glyphicon-refresh'></span>Actualizar</a></li>")
		.append("<li class='divider'></li>")
		.append("<li><a id='stop-everything' href='#'><span class='glyphicon glyphicon-off'></span>Salir</a></li>")
		.append("</ul>")
		.append("</div>")
		.append("</div>")
		.append("<div class='panel-body'>")
		.append("<ul class='chat'></ul>")
		.append("</div>")
		.append("<div class='panel-footer'>")
		.append("<div class='input-group'>")
		.append("<input id='message-input' type='text' class='form-control input-sm' placeholder='Escriba su mensaje aquí...' />")
		.append("<span class='input-group-btn'>")
		.append("<button class='btn btn-warning btn-sm' id='btn-send-message'>Enviar</button>")
		.append("</span>")
		.append("</div>")
		.append("</div>")
		.append("</div>");													
		
		return chatWindow.toString();	
		
	}
	
	public static String createPageFooter(boolean rootIndex) {
		StringBuilder footer = new StringBuilder()
		.append("</div>");
		
		if(rootIndex) {
			footer.append("<script src='assets/js/jquery-2.1.0.min.js'></script>")			
			.append("<script src='assets/js/bootstrap.min.js'></script>")
			.append("<script src='assets/js/jquery.validate.min.js'></script>")
			.append("<script src='assets/js/chat.js'></script>");
		} else {
			footer.append("<script src='../assets/js/jquery-2.1.0.min.js'></script>")
			.append("<script src='../assets/js/bootstrap.min.js'></script>")
			.append("<script src='../assets/js/jquery.validate.min.js'></script>")
			.append("<script src='../assets/js/chat.js'></script>");
		}	
		
		footer.append("</body>")
		.append("</html>");
		
		return footer.toString();
	}
	
	public static String errorPage(int code, String message) {
		return new StringBuilder("<!DOCTYPE html>")
		.append("<html>")
		.append("<head>")
		.append("<title>" + message + "</title>")
		.append("</head>")
		.append("<body>")
		.append("<h1>HTTP Error" +  code + " :"  + message + "</h1>")
		.append("</body>")
		.append("</html>").toString();
	}	

}
