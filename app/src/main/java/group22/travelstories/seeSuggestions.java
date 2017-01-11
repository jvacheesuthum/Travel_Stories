package group22.travelstories;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Callable;

/**
 * Created by vasin on 17/11/2016.
 */

public class SeeSuggestions implements Callable {

    Context main;
    String json;

    public SeeSuggestions(Context main){
        this.main = main;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void callWithArg(String json){
        System.out.println("DEBUG: arrive from main onstart" + json);
        this.json = json;
        seeSuggestions();
    }

    public void seeSuggestions(){
        Intent intent = new Intent(main, SuggestionActivity.class);
//        Gson gson = new Gson();
//        String places_gson = gson.toJson(places);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, json);
        main.startActivity(intent);
    }
}
