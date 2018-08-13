package com.ezeia.politicalparty.view.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.R;
import com.ezeia.politicalparty.utils.Utils;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static boolean isAppRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Fabric.with(this, new Crashlytics());

        setFirebase();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                Intent mInHome = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(mInHome);
                finish();
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }

    void setFirebase()
    {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
//            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//
//        /*
//         * Displaying a notification locally
//         */
//        MyNotificationManager.getInstance(this).displayNotification("Greetings", "Hello how are you?");

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Utils.insertDataDb(this);
    }

}
