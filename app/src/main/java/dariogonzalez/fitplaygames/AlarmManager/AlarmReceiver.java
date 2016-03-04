package dariogonzalez.fitplaygames.AlarmManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver
{
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service = new Intent(context, SchedulingService.class);
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }

    /**
     * Start the alarm. Setting an alarm than run every 30 minutes
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);
//        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 5*1000, //In 5 seconds
//                5 * 1000,
//                alarmIntent);
    }

    /**
     * Cancel the alarm
     *
     */
    public void cancelAlarm() {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
