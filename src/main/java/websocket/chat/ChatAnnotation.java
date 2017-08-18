package websocket.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.*;

@ServerEndpoint(value = "/chat")
public class ChatAnnotation {
	private static final ArrayList<ChatAnnotation> connections = new ArrayList<ChatAnnotation>();
	private Session session;
	
	private static void broadcast(String msg) throws IOException
	{
		for(ChatAnnotation ca:connections)
		{
			ca.session.getBasicRemote().sendText(msg);
		}
	}
	
	private static void broadcastExcept(String msg,ChatAnnotation except) throws IOException
	{
		for(ChatAnnotation ca:connections)
		{
			if(ca == except)
				continue;
			else
				ca.session.getBasicRemote().sendText(msg);
		}
	}
	
	@OnOpen
	public void start(Session session) throws IOException
	{
		this.session = session;
		connections.add(this);
	}
	
	@OnClose
	public void end() throws IOException
	{
		connections.remove(this);
	}
	
	@OnMessage
	public void incoming(String message) throws IOException
	{
		JSONObject json = new JSONObject(message);
		switch(json.getString("type"))
		{
		case "request":
			JSONObject response;
			if(connections.size() == 1) 		// send create
				response = createPackage("create",String.valueOf(connections.size()));
			else 								//send join
				response = createPackage("join",String.valueOf(connections.size()));
			this.session.getBasicRemote().sendText(response.toString());
			break;
		case "offer":
		case "answer":
			broadcastExcept(message,this);
			break;
		case "candidate":
			broadcastExcept(message,this);
			break;
		default:
			break;
		}
	}
	
	private static JSONObject createPackage(String type,String data)
	{
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("data", data);
		return json;
	}
}
