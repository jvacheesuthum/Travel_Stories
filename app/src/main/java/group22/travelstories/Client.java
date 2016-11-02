package group22.travelstories;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by vasin on 01/11/2016.
 */

public class Client extends WebSocketClient{

    String message = null;

    public Client(String url) throws URISyntaxException {

        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Client open");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("message received: " + message);
        String[] x = message.split(":");
        switch(x[0]){
            case "timeline_address" : this.message = x[1]; break;
            default: System.out.println("got wrong type message back from server");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Client closed");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Client on error!");
    }
}
