package com.mobile.ict.cart.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.activity.DashboardActivity;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vish on 13/5/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    public static String ACTION = "update";
    private static final String TAG = "MyGcmListenerService";
    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        List<NotificationBundle> notifsBundle = new ArrayList<NotificationBundle>();
        String message = data.getString(("Message"));
        String title = data.getString(("Title"));
        int id = Integer.parseInt(data.getString("id"));
        Type listOfTestObject = new TypeToken<List<NotificationBundle>>() {
        }.getType();
        Gson gson = new Gson();

        System.out.println("Title: " + title + ". Message: " + message + ". ID: " + id);

        if (id == 4) {
            /*Log.e("GCM Listener", "title: " + title + ". Message: " + message);
            sendNotification(message, title);*/
            if (!prefs.contains("orderNotifs")) {
                NotificationBundle bundle = new NotificationBundle(message, title);
                notifsBundle.add(bundle);
                String s = gson.toJson(notifsBundle, listOfTestObject);
                editor.putString("orderNotifs", s);
                editor.commit();
                sendNotification(message, title);
            } else {

                String s = prefs.getString("orderNotifs", "");
                notifsBundle = gson.fromJson(s, listOfTestObject);
                NotificationBundle bundle = new NotificationBundle(message, title);
                notifsBundle.add(bundle);
                String fin = gson.toJson(notifsBundle, listOfTestObject);
                editor.putString("orderNotifs", fin);
                editor.commit();

                sendNotificationWithStyle(message, title, notifsBundle);
            }
            Log.e("GCM Listener", "Size: " + notifsBundle.size());
        }
    }

    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("notification", "processed");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cart_white)
                .setContentTitle("Lokacart" + title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setGroup("processed_orders");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    private void sendNotificationWithStyle(String message, String title, List<NotificationBundle> notifsBundle) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(notifsBundle.size()+" orders processed");
        Iterator<NotificationBundle> iter = notifsBundle.iterator();
        while(iter.hasNext()) {
            NotificationBundle notif = iter.next();
            inboxStyle.addLine(notif.getText());
        }
        notificationBuilder.setStyle(inboxStyle);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}