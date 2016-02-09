package dariogonzalez.fitplaygames.AlarmManager;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.parse.ParseUser;

import dariogonzalez.fitplaygames.R;
import dariogonzalez.fitplaygames.classes.ParentChallenge;
import dariogonzalez.fitplaygames.utils.FitbitHelper;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService
{
    public SchedulingService() {
        super("SchedulingService");
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO: Check we have all the data we need to update
        // - FitBit
        // - Games
        // - Others?

        /**** Start updating FitBit values ****/
        FitbitHelper fh = new FitbitHelper(this);

        if (fh.isFitbitUserAlive())
        {
            fh.getUserLastMonthData();

            //TODO: see how to update average after calling getUserLastMonthData(), both are async functions, so I need to be sure
            //getUserLastMonthData() finished before updating the next table....
            //fh.lastSevenDaySumAndAverage(ParseUser.getCurrentUser().getObjectId());

            fh.getStepsRangeDateTime();
        }

        /**** End updating FitBit values ****/

        /**** Update Games ****/
        //TODO: double check this method is working
        ParentChallenge.updateChallenges();
        /**** End Update Games ****/

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, dariogonzalez.fitplaygames.MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("I am not using this")
        .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
