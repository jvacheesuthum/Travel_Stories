package group22.travelstories;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.math.BigInteger;
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
    public BigInteger userid;

    public SeeSummary(Context v){
        this.main = v;
    }

    @Override
    public Object call() throws Exception {
        //seeSummary();
        return null;
    }

    public void callWithArg(String[] messages, String s){
        for(int i = 0; i<messages.length; i++){
            if(messages[i] != null){
                String[] id_name = messages[i].split(",");
                timeLine.get(i).setPlaceId(id_name[0]);
                timeLine.get(i).setAddress(id_name[1]);
            }
        }
        seeSummary(s);
    }

    public void seeSummary(String trip_token){
        Intent intent = new Intent(main, DisplayStoryActivity.class);
        ArrayList list = new ArrayList();
        for (int i = 0 ; i < timeLine.size() ; i++ ){

//            list.add(i, new String[]{timeLine.get(i).getLocationName(),
//                                    timeLine.get(i).getTime()});
            list.add(i, timeLine.get(i));
        }
        intent.putParcelableArrayListExtra(EXTRA_MESSAGE, (ArrayList) list);
        intent.putExtra("UserId", userid.toString());
        intent.putExtra("TripToken", trip_token);
        main.startActivity(intent);
    }

    public void setTimeLine(List<TimeLineEntry> timeLine) {
        this.timeLine = timeLine;
    }

    public void setUserId(BigInteger userId) {
        this.userid = userId;
    }
}
