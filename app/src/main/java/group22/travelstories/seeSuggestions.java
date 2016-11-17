package group22.travelstories;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Callable;

/**
 * Created by vasin on 17/11/2016.
 */

public class SeeSuggestions implements Callable {

    Context main;
    Place[] places;

    public SeeSuggestions(Context main){
        this.main = main;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }

    public void callWithArg(Place[] places){
        this.places = places;
        seeSuggestions();
    }

    public void seeSuggestions(){
        Intent intent = new Intent(main, suggestionActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, places);
        main.startActivity(intent);
    }
}
