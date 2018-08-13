package com.ezeia.politicalparty.services;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ezeia.politicalparty.pref.Preferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.fabric.sdk.android.Fabric;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Fabric.with(this, new Crashlytics());

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TAG", "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        Preferences.setDeviceToken(this,refreshedToken);
    }
}