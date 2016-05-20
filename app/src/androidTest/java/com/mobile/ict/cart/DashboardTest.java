package com.mobile.ict.cart;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.*;
import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.internal.AlertController;
import com.mikepenz.materialdrawer.Drawer;
import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.activity.DashboardActivity;
import com.mobile.ict.cart.fragment.ProfileFragment;
import com.mobile.ict.cart.activity.LoginActivity;
import com.mobile.ict.cart.adapter.ProductAdapter;
import com.mobile.ict.cart.fragment.ChangeOrganisationFragment;
import com.mobile.ict.cart.fragment.ProductFragment;
import com.mobile.ict.cart.fragment.PlacedOrderFragment;
import com.mobile.ict.cart.util.Master;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.widget.Toolbar;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.util.regex.Pattern.matches;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by uttam on 15/5/16.
 */
@RunWith(AndroidJUnit4.class)
public class DashboardTest
{

    @Rule
    public ActivityTestRule<DashboardActivity> mActivityRule =
            new ActivityTestRule<>(DashboardActivity.class);





    /* Test 1-> Add to cart */
    @Test
    public void AddToCart()
    {

        int pos = -1;
        for(int i=0;i<Master.productList.size();i++)
        {
            if(Master.productList.get(i).getStockQuantity()>0)
            {
               // onView(withId(R.id.ivBuy)).perform(click());
                pos = i;
                onView(withId(R.id.rvProduct)).perform(
                        RecyclerViewActions.actionOnItemAtPosition(i, MyViewAction.clickChildViewWithId(R.id.ivBuy)));
                onView(withId(R.id.rvProduct)).perform(
                        RecyclerViewActions.actionOnItemAtPosition(i, MyViewAction.clickChildViewWithId(R.id.ivBuy)));
               // break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    /* Test 2-> Remove From Cart */
    @Test
    public void RemoveFromCart()
    {
        if(Master.cartList.isEmpty())
            AddToCart();
        onView(withId(R.id.action_cart)).perform(click());
        for(int i=0;i<Master.cartList.size();i++)
        {
            onView(withId(R.id.rvCart)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.bDelete)));
            onView(withText("Ok")).perform(click());
        }

    }




    /* Test 3->   */
    @Test
    public void ChangeOrganisation()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));


       // onView(withId(R.id.icon)).perform(click());
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Organisation")).perform(click());

        Organisations.organisationList.get(0).setIsChecked(true);

        onView(withId(R.id.bChangeOrganisation)).perform(click());
        onView(withId(R.id.bChangeOrganisation)).perform(swipeRight());
        onView(withText("Products")).perform(click());

        /* Changing product Type */
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(1,swipeLeft()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Fruits (Dozen)")).perform(click());

        /*  back to normal */



        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));

        try {
            TimeUnit.MILLISECONDS.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Organisation")).perform(click(),closeSoftKeyboard());

        Organisations.organisationList.get(0).setIsChecked(false);
        Organisations.organisationList.get(1).setIsChecked(true);
        onView(withId(R.id.bChangeOrganisation)).perform(click());
        onView(withId(R.id.bChangeOrganisation)).perform(swipeRight());
        onView(withText("Products")).perform(click());

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }





    @Test
    public void PlacedOrdersTest()
    {

        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        onView(withText("Orders")).perform(click());
        onView(withText("Placed orders")).perform(click());

        // Need for loop
            onView(withId(R.id.rvPlacedOrder)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.rvPlacedOrder)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.bDelete)));

            onView(withText("Ok")).perform(click());


    }

    @Test
    public void ProcessedOrdersTest()
    {

       // onView(withId(R.id.ivBuy)).perform(swipeRight());
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        onView(withText("Orders")).perform(click());
        onView(withText("Processed orders")).perform(click());
    }

    @Test
    public void ProfileTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Profile")).perform(click());

        /* Changing Name */
        onView(allOf(withId(R.id.ivEdit),isDisplayed())).perform(click());
        onView(withId(R.id.eEditProfileFName)).perform(clearText(),typeText("Uttam"),closeSoftKeyboard());
        onView(withId(R.id.eEditProfileLName)).perform(clearText(),typeText("Chaudhary"),closeSoftKeyboard());
        onView(withId(R.id.eEditProfilePincode)).perform(clearText(),typeText("384001"),closeSoftKeyboard());
        onView(withId(R.id.ivDone)).perform(click());
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.eEditProfileFName)).perform(clearText(),typeText("Sid"),closeSoftKeyboard());
        onView(withId(R.id.eEditProfileLName)).perform(clearText(),typeText("Ramachandran"),closeSoftKeyboard());
        onView(withId(R.id.ivDone)).perform(click());
    }

    @Test
    public void ReferralsTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Referrals")).perform(click());

        /* Reffer to predefine Mobile number and Email Address */

        onView(withId(R.id.referralRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withId(R.id.eReferralEmail)).perform(typeText("chaudharyuttam36@gmail.com"));
       // onView(withId(R.id.bSendReferral)).perform(click());
        onView(withId(R.id.eReferralMobileNumber)).perform(typeText("8140453507"));

        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.bSendReferral)).perform(click());
        onView(withText(R.string.button_confirm)).perform(click());

    }

    @Test
    public void AboutUsTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        onView(withText("About us")).perform(click());
    }

    @Test
    public void FeedbackTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Help")).perform(click());
        onView(withText("Feedbacks")).perform(click());

        /* Submit a feedback */
        // Need to choose organisation !!
        onView(withId(R.id.feedback_edittext)).perform(typeText("Just Testing...Don't worry!!"),closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());


    }
   /* @Test
    public void CallUsTest()
    {
       // onView(withId(R.id.ivBuy)).perform(swipeRight());
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Help")).perform(click());
        onView(withText("Contact us")).perform(click());
        onView(withId(R.id.call_us_iv)).perform(click());
    } */
    @Test
    public void FbPageTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Help")).perform(click());
        onView(withText("Contact us")).perform(click());
        onView(withId(R.id.facebook_iv)).perform(click());
    }
    @Test
    public void MailUsTest()
    {
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Help")).perform(click());
        onView(withText("Contact us")).perform(click());
        onView(withId(R.id.mail_us_iv)).perform(click());
    }
    @Test
    public void WebPageTest()
    {

        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Help")).perform(click());
        onView(withText("Contact us")).perform(click());
        onView(withId(R.id.website_iv)).perform(click());
    }
    @Test
    public void TandCTest()
    {

        //onView(withId(R.id.ivBuy)).perform(swipeRight());
        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(0,swipeRight()));
        onView(withText("Help")).perform(click(),swipeUp());
        onView(withText("Terms and conditions")).perform(click());
    }
    @Test
    public void CartTest()
    {
        if(Master.cartList.isEmpty())
            AddToCart();

        onView(withId(R.id.action_cart)).perform(click());
        for(int i=0;i<Master.cartList.size();i++) {
            onView(withId(R.id.rvCart)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(i, MyViewAction.clickChildViewWithId(R.id.bPlus)));

            onView(withId(R.id.rvCart)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(i,MyViewAction.clickChildViewWithId(R.id.bPlus)));
            onView(withId(R.id.rvCart)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(i, MyViewAction.clickChildViewWithId(R.id.bMinus)));



            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onView(withText(startsWith("CheckOut"))).perform(click());
        onView(withText("Done")).perform(click());

        // Delete "OK" need to be added

    }

    /*@Test
    public void AddLargeProducts()
    {
        onView(withId(R.id.action_cart)).perform(click());

        for(int j=0;j<=1000;j++)
            onView(withId(R.id.rvCart)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(0,MyViewAction.clickChildViewWithId(R.id.bPlus)));


    } */
    @Test
    public void ProductTest()
    {
        //for finding availabel item
       int i=0;

        for(i=0;i<Master.productList.size();i++)
        {
            if(Master.productList.get(i).getStockQuantity()>0)
                break;
        }

        onView(withId(R.id.rvProduct)).perform(RecyclerViewActions.actionOnItemAtPosition(i,click()));
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.ivPlay)).perform(click());
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.ivPause)).perform(click());
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.ivPlay)).perform(click());
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.ivStop)).perform(click());


    }
    @After
    public void logout()
    {
        //  onView(withId(R.id.ivBuy)).perform(swipeRight(),closeSoftKeyboard());
        // onView(withText("Logout")).perform(click(),closeSoftKeyboard());
        //onView(withId(R.id.eEnquiryName)).perform(typeText("Uttam"),closeSoftKeyboard());
    }



}
