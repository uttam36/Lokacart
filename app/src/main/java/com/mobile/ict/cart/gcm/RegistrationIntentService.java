package com.mobile.ict.cart.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vish on 13/5/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Master.setToken(token);
            Toast.makeText(getBaseContext(), "Token" + token, Toast.LENGTH_LONG);
            Log.e("Reg intent service", "Token: " + token);
            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {
        JSONObject obj = new JSONObject();
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            obj.put("token",token);
            obj.put("number","91"+ MemberDetails.getMobileNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendTokenTask task = new SendTokenTask(this.getBaseContext());
        task.execute(obj);
    }

    class SendTokenTask extends AsyncTask<JSONObject, String, Void> {
        Context context;
        SendTokenTask(Context context)
        {
            this.context = context;
        }

        protected Void doInBackground(JSONObject... params) {
            GetJSON getJson = new GetJSON();
            String response = getJson.getJSONFromUrl(Master.sendGCMTokenUrl(), params[0], "POST", false, null, null);
            System.out.println("Intent Response: " + response);
            return null;
        }
    }

}