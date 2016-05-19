package com.mobile.ict.cart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.Master;

/**
 * Created by Siddharthsingh on 09-05-2016.
 */
public class ContactUsFragment extends DialogFragment implements View.OnClickListener {


    ImageView facebookIV;
    ImageView callUsIV;
    ImageView websiteIV;
    ImageView mailUsIV;

    private long mLastClickTime = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_us,container,false);
        TextView faqTV = (TextView)view.findViewById(R.id.faq_string_TV);
        facebookIV = (ImageView) view.findViewById(R.id.facebook_iv);
        callUsIV = (ImageView)view.findViewById(R.id.call_us_iv);
        websiteIV = (ImageView)view.findViewById(R.id.website_iv);
        mailUsIV = (ImageView)view.findViewById(R.id.mail_us_iv);

        facebookIV.setOnClickListener(this);
        callUsIV.setOnClickListener(this);
        websiteIV.setOnClickListener(this);
        mailUsIV.setOnClickListener(this);

        faqTV.setOnClickListener(this);
        Button closeDialogFragmentTV = (Button) view.findViewById(R.id.close_dailof_fragment_tv);

        closeDialogFragmentTV.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }




    @Override
    public void onClick(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 600) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch(view.getId()){

            case R.id.call_us_iv:{
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:912225767728"));
                startActivity(intent);
                break;

            }
            case R.id.mail_us_iv:{
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","lokacart@cse.iitb.ac.in", null));

                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;

            }
            case R.id.website_iv:{
                String url = "http://ruralict.cse.iitb.ac.in/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            }
            case R.id.facebook_iv:{
                String url = "https://www.facebook.com/RuralICT.iitb/";
                Intent i = getOpenFacebookIntent(getActivity());
                //i.setData(Uri.parse(url));
                startActivity(i);
                break;

            }

            case R.id.close_dailof_fragment_tv:{
                getDialog().dismiss();
                break;

            }

            case R.id.faq_string_TV:{
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FAQFragment(), Master.FAQ_TAG).commit();
                getDialog().dismiss();
                break;

            }
        }
    }


    /*this function returns the intent which should be opened .
    If facebook app is installed the intent will open facebook
    otherwise it will be opened in browser
    */
    public static Intent getOpenFacebookIntent(Context context) {

        try {
            //querying packagemanager to check if facebook is installed
            //package name of facebook app is com.facebook.katana
            //if it is not installed this line will throw an exception and the code in catch block will be executed
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=RuralICT.iitb"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/RuralICT.iitb/"));
        }
    }


}
