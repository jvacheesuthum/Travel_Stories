package group22.travelstories;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vasin on 16/11/2016.
 */

public class HiService extends Service {

    private Timer mTimer = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        System.out.println("HiService starts!");
        System.out.println("HiService created");
        if(mTimer != null){
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new HiPrintTimerTask(), 0, 2*1000);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        System.out.println("HiService destroyed!");
        super.onDestroy();
    }

    class HiPrintTimerTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("HI from service");
        }
    }
}
