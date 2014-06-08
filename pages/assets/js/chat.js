function Message(type, sender, userToName, userToIpAddress, userToPort, message) {
	this.type = type;
	this.sender = sender;
	this.userName = userToName;
	this.ipAddress = userToIpAddress;
	this.port = userToPort;
	this.message = message;
}

function sendMessage(message) {
	$.ajax({
		crossDomain: true,
		url: 'http://localhost:' + port + '/contactos/sendmessage',
		type: 'POST',
		dataType: 'json',
		data: message,				
	})
	.done(function(data) {
		console.log("success");
		console.log(data);
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {
		console.log("complete");
	});
	
}

function appendMessage(element, message, outcome) {
	outcome = typeof outcome !== 'undefined' ? outcome : true;	
	var $messageList = $(element),
		$li = $('<li></li>', {'class':'clearfix'})
		userName = outcome ? message.sender : message.userFrom.userName;

	$li.append(								
		$('<div></div>', {'class':'user-name pull-left'}).append(
			$('<strong></strong>', {
				'class':'primary-font'							
			}).text(userName)
		),
		$('<p></p>').text(message.message)
		
	);			
	$messageList.append($li);
	$parent = $messageList.parent('.panel-body')
	$parent.animate({ scrollTop: $messageList.height() }, 100);
} 

function getMessages(from) {
	$.ajax({
		crossDomain: true,
		url: 'http://localhost:' + port + '/contactos/getmessages',
		type: 'post',
		dataType: 'json',
		data: {
			from: from
		}
	})
	.done(function(data) {				
		console.log(data);
		if(!($.isEmptyObject(data))) {
			$.each(data, function(index, val) {
				console.log(val);
				appendMessage('.chat', val, false);
			});
		}				
	})
	.fail(function() {
		console.log("error");
	})
	.always(function() {
		console.log("complete");
	});
	
}

$(document).ready(function(){
	$.support.cors = true;
	var $messageInput = $('#message-input'),
		$btnSendMessage = $('#btn-send-message'),
		$update = $('#get-messages'),
		$userName = $('#user-name'),
		$userIpAddress = $('#user-ip-address'),
		$userPort = $('#user-port'),
		$contactDetailsName = $('#contact-details-name'),
		$contactDetailsIpAddress = $('#contact-details-ip-address'),
		$contactDetailsPort = $('#contact-details-port'),
		$stop = $('#stop-everything');
	
	var	updateMessagesInterval = setInterval(function() {getMessages($contactDetailsName.text())}, 5000);
	

	$stop.on('click', function(event) {
		clearInterval(updateMessagesInterval);
		message = new Message(3, $userName.text(), "server", "127.0.0.1", 0, "exit"); 
		sendMessage(message);
		//enviar mensaje de logout
	});
		
	$update.on('click', function() {
		getMessages($contactDetailsName.text());
	});

	$btnSendMessage.on('click', function(event) {
		var messageBody = $messageInput.val();
		
		if($.trim(messageBody)) {
			
			message = new Message(2, $userName.text(), $contactDetailsName.text(), $contactDetailsIpAddress.text(), $contactDetailsPort.text(), messageBody);
			//mandar el mensaje
			sendMessage(message);
			
			//agregar a la lista
			appendMessage('.chat', message);
			$messageInput.val('');		
		}
		
	});

	$messageInput.on('keypress', function(event) {
		/* Act on the event */
		if (event.which === 13 && event.shiftKey === false) {					
			var messageBody = $messageInput.val();					
			
			if($.trim(messageBody)) {
				
				message = new Message(2, $userName.text(), $contactDetailsName.text(), $contactDetailsIpAddress.text(), $contactDetailsPort.text(), messageBody);
				//mandar el mensaje
				sendMessage(message);
				
				//agregar a la lista
				appendMessage('.chat', message);
				$messageInput.val('');		
			}

		}
	});

});