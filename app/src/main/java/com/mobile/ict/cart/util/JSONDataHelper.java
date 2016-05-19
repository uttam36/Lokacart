package com.mobile.ict.cart.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vish on 5/4/16.
 */
public class JSONDataHelper {
    public static ArrayList<Organisations> getOrganisationListFromJson(Context context, String JSON)
    {
        ArrayList<Organisations> list = new ArrayList<Organisations>();
        System.out.println(JSON);
        if(JSON.equals(""))
        {
            Log.e("Change org", "JSON not found");
            Toast.makeText(context, R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
        }
        else {

            try
            {
                JSONObject jsonObject = new JSONObject(JSON);
                JSONArray jsonArray = jsonObject.getJSONArray(Master.ORGANISATIONS);
                System.out.println(jsonArray);
                if(jsonArray.length() == 0)
                {
                    //zero organisation
                    Log.e("Change org", "0 org");
                    Toast.makeText(context, R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.e("JSONArray length", jsonArray.length() + "");
                    System.out.println(jsonArray);
                    for(int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject tempObj = jsonArray.getJSONObject(i);
                        Organisations organisations = new Organisations(tempObj);
                        list.add(organisations);
                    }

                    for(int i = 0; i < list.size(); ++i)
                    {
                        System.out.println("-----------------------------");
                        System.out.println(list.get(i).getName());
                        System.out.println(list.get(i).getIsChecked());
                        System.out.println(list.get(i).getOrgabbr());
                    }
                }
            }
            catch (JSONException e)
            {
                Log.e("Change org", "json execpt");
                Toast.makeText(context, R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
        return list;
    }
}
