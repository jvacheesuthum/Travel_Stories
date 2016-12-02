package group22.travelstories;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

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
        System.out.println("DEBUG: arrive from main onstart" + this.json);
        seeSuggestions();
    }

    public void seeSuggestions(){
        System.out.println("DEBUG: arrive from seeSuggestions callwith Arg method");
        Intent intent = new Intent(main, SuggestionActivity.class);
//        Gson gson = new Gson();
//        String places_gson = gson.toJson(places);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, json);
        main.startActivity(intent);
    }
}
