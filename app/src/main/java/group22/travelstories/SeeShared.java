package group22.travelstories;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Callable;

/**
 * Created by vasin on 11/01/2017.
 */

public class SeeShared implements Callable {
    Context main;
    String triptoken;

    public SeeShared(Context main){
        this.main = main;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void callWithArg(String triptoken){
        this.triptoken = triptoken;
        seeShared();
    }

    public void seeShared(){
        Intent intent = new Intent(main, SharedActivity.class);
//        Gson gson = new Gson();
//        String places_gson = gson.toJson(places);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, triptoken);
        main.startActivity(intent);
    }
}
