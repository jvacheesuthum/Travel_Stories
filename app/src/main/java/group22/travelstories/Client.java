package group22.travelstories;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import com.google.gson.*;

/**
 * Created by vasin on 01/11/2016.
 */

public class Client extends WebSocketClient{

    //String message = null;
    //String message = "British Museum@Blah@Ha";
    SeeSummary seeSummary;
    SeeSuggestions seeSuggestions;
    SeeShared seeShared;
    String[] locations;

    public Client(String url, Callable seeSummary, Callable seeSomething) throws URISyntaxException {
        super(new URI(url));
        this.seeSummary = (SeeSummary) seeSummary;
        if(seeSomething instanceof SeeSuggestions){
            this.seeSuggestions = (SeeSuggestions) seeSomething;
        } else {
            this.seeShared = (SeeShared) seeSomething;
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Client open");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("message received: " + message);
        /*
        String[] x = message.split(":");
        switch(x[0]){
            case "timeline_address" : this.message = x[1]; break;
            default: System.out.println("got wrong type message back from server");
        }*/

        // real thing
        if(message.split(":")[0].equals("timeline_address")) {
            try {
                locations = message.split(":")[1].split("@");
                ((MainActivity) seeSummary.main).sendLocationTrace(this);
                //////((SeeSummary) seeSummary).callWithArg(locations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(message.split(":")[0].equals("Final_map_trace")){
            try {
                ((SeeSummary) seeSummary).callWithArg(locations,message.split(":")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(message.split(":")[0].equals("nearby_place")){
//            Gson gson = new Gson();
//            Place[] places = gson.fromJson(message.split(":")[1],Place[].class);
//            for(Place each : places){
//                System.out.println(each.toString());
            try {
                seeSuggestions.callWithArg(message.substring(message.indexOf(':')+1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(message.split(":")[0].equals("timeline_share")){
              String triptoken = message.split(":")[1];
            try {
                seeShared.callWithArg(triptoken);
            } catch (Exception e) {
                e.printStackTrace();
            }
    } else if(message.split(":")[0].equals("get_location")){
//            System.out.println("HERERERERERERERERE===============================");
//            try {
//                String msg = message.split(":")[1];
//                ((SeeSummary) seeSummary).callWithArgEntry(msg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("ASDFADSFDSAFFADS");
        }

        // test thing
//        else if(!message.equals("Connected to Server")){
//            try {
//                seeSummary.call();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
