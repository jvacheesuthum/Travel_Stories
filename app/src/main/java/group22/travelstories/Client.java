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
