package com.mobile.ict.cart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

/**
 * Created by Vishesh on 11-03-2016.
 */

public class SplashScreenActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
       /* Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //startActivity(new Intent(SplashScreenActivity.this, StepperActivity.class));

                    if(SharedPreferenceConnector.readBoolean(getApplicationContext(), Master.STEPPER, Master.DEFAULT_STEPPER))
                    {
                        SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.STEPPER, false);
                        startActivity(new Intent(SplashScreenActivity.this, StepperActivity.class));
                    }
                    else
                    {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }
        };
        timerThread.start();*/


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                /*// This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();*/

                if(SharedPreferenceConnector.readBoolean(getApplicationContext(), Master.STEPPER, Master.DEFAULT_STEPPER))
                {
                    SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.STEPPER, false);
                    startActivity(new Intent(SplashScreenActivity.this, StepperActivity.class));
                }
                else
                {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}