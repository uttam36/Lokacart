package com.mobile.ict.cart.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.fragment.SampleSlide;

/**
 * Created by vish on 11/4/16.
 */
public class StepperActivity extends AppIntro2 {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
       // addSlide(first_fragment);
       // addSlide(second_fragment);
      //  addSlide(third_fragment);
     //   addSlide(fourth_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
      //  addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

        addSlide(SampleSlide.newInstance(R.layout.stepper_1));
        addSlide(SampleSlide.newInstance(R.layout.stepper_2));
        addSlide(SampleSlide.newInstance(R.layout.stepper_3));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        //showSkipButton(false);
        setProgressButtonEnabled(true);


        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(false);
        setVibrateIntensity(30);
    }

/*    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }*/

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        startActivity(new Intent(StepperActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}
