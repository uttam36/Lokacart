package com.mobile.ict.cart.Container;

import android.util.Log;

import com.mobile.ict.cart.util.Master;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vish on 4/4/16.
 */
public class Organisations {

    String name;
    Boolean isChecked;
    String orgabbr;
    public static ArrayList<Organisations> organisationList;

    public Organisations(JSONObject object)
    {
        try
        {
            System.out.println(object.toString());
            this.name = object.getString(Master.ORG_NAME);
            this.isChecked = false;
            this.orgabbr = object.getString(Master.ORG_ABBR);

            System.out.println(this.name + " " + this.isChecked + " " + this.orgabbr);
        }
        catch (Exception e)
        {
            Log.e("Organisation cont", "in catch in constructor");
        }
    }

    public String getOrgabbr() {
        return orgabbr;
    }

    public void setOrgabbr(String orgabbr) {
        this.orgabbr = orgabbr;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
