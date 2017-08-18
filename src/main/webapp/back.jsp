<html>
<body>

<textarea readonly id="text"></textarea>
<textarea placeholder="Input Message Here" id="input"></textarea>
<button onclick="send()">Send</button>

<script>
	var websocket = new WebSocket("ws://localhost:8080/websocket_test/chat");
	function send()
	{
		var text = document.getElementById("input").value;
		websocket.send(text);
	}
	
	websocket.onmessage = function(event){
		document.getElementById("text").value += event.data + "\n";
	}
	
</script>
</body>
</html>
