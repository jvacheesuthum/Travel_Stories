package group22.travelstories;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by vasin on 08/11/2016.
 */

public class SeeSummary implements Callable {

    List<TimeLineEntry> timeLine;
    Context main;
    public final static String EXTRA_MESSAGE = "com.travelstories.timeline"; //dodgy restrictions

    public SeeSummary(List<TimeLineEntry> timeline, Context v){
        this.main = v;
        this.timeLine = timeline;
    }

    @Override
    public Object call() throws Exception {
        seeSummary();
        return null;
    }

    public void callWithArg(String[] messages){
        for(int i = 0; i<messages.length; i++){
            System.out.println("asdfasfafds");
            timeLine.get(i).setAddress(messages[i]);
        }
        seeSummary();
    }

    public void seeSummary(){
        Intent intent = new Intent(main, DisplayStoryActivity.class);
        ArrayList list = new ArrayList();
        for (int i = 0 ; i < timeLine.size() ; i++ ){

//            list.add(i, new String[]{timeLine.get(i).getLocationName(),
//                                    timeLine.get(i).getTime()});
            list.add(i, timeLine.get(i));
        }
        intent.putParcelableArrayListExtra(EXTRA_MESSAGE, (ArrayList) list);
        main.startActivity(intent);
    }
}
