package com.mobile.ict.cart.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.fragment.AboutUsFragment;
import com.mobile.ict.cart.fragment.ChangeOrganisationFragment;
import com.mobile.ict.cart.fragment.ContactUsFragment;
import com.mobile.ict.cart.fragment.FAQFragment;
import com.mobile.ict.cart.fragment.FeedbackFragment;
import com.mobile.ict.cart.fragment.PlacedOrderFragment;
import com.mobile.ict.cart.fragment.ProcessedOrderFragment;
import com.mobile.ict.cart.fragment.ProductFragment;
import com.mobile.ict.cart.fragment.ProfileFragment;
import com.mobile.ict.cart.fragment.ReferralFragment;
import com.mobile.ict.cart.fragment.TermsAndConditionsFragment;
import com.mobile.ict.cart.gcm.MyGcmListenerService;
import com.mobile.ict.cart.gcm.RegistrationIntentService;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.Container.MemberDetails;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    NavigationView navigationView;
    static TextView drawerName;
    static TextView drawerEmail;
    static TextView drawerMobileNumber;
    ImageView ivEdit;
    View headerView;
    public static Boolean backpress = false;
    RelativeLayout relativeLayout;
    Drawer result;
    AccountHeaderBuilder accountHeaderBuilder;
    Toolbar toolbar;
    DBHelper dbHelper;
    static LayerDrawable icon;
    MenuItem item;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);

        backpress = false;
        Master.initialise(getApplicationContext());

        Master.productList = new ArrayList<>();
        Master.getJSON = new GetJSON();

        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relativeLayout = (RelativeLayout) findViewById(R.id.dashboardRelativeLayout);

        fragmentManager = getSupportFragmentManager();

        headerView = getLayoutInflater().inflate(R.layout.nav_header_dashboard, null);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withHeader(headerView)
                /*.inflateMenu(R.menu.activity_dashboard_drawer)*/
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_products).withIcon(R.drawable.products).withIdentifier(0),
                        new ExpandableDrawerItem().withName(R.string.menu_orders).withIcon(R.drawable.orders).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.menu_placed_orders).withLevel(5).withIdentifier(1),
                                new SecondaryDrawerItem().withName(R.string.menu_processed_orders).withLevel(5).withIdentifier(2)
                        ),
                        new PrimaryDrawerItem().withName(R.string.menu_profile).withIcon(R.drawable.profile).withIdentifier(11),
                        new PrimaryDrawerItem().withName(R.string.menu_change_organisation).withIcon(R.drawable.organisation).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.menu_referrals).withIcon(R.drawable.referrals).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.menu_about_us).withIcon(R.drawable.about_us).withIdentifier(5),
                        new ExpandableDrawerItem().withName(R.string.menu_help).withIcon(R.drawable.help).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.menu_feedback).withLevel(5).withIdentifier(6),
                                new SecondaryDrawerItem().withName(R.string.menu_contact_us).withLevel(5).withIdentifier(7),
                                new SecondaryDrawerItem().withName(R.string.menu_terms_and_conditions).withLevel(5).withIdentifier(8)
                                /*new SecondaryDrawerItem().withName(R.string.menu_faqs).withLevel(2).withIdentifier(9)*/
                        ),
                        new PrimaryDrawerItem().withName(R.string.menu_logout).withIdentifier(10)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        Log.e("DashboardAct", "Position: " + drawerItem.getIdentifier());
                        dashBoardClickListener(drawerItem.getIdentifier());

                        backpress = false;

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        drawerName = (TextView) headerView.findViewById(R.id.tDashboardName);
        drawerName.setText(MemberDetails.getFname() + " " + MemberDetails.getLname());

        drawerEmail = (TextView) headerView.findViewById(R.id.tDashboardEmail);
        drawerEmail.setText(MemberDetails.getEmail());

        drawerMobileNumber = (TextView) headerView.findViewById(R.id.tDashboardMobileNumber);
        drawerMobileNumber.setText(MemberDetails.getMobileNumber());

        ivEdit = (ImageView) headerView.findViewById(R.id.ivEdit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment(), Master.PROFILE_TAG).commit();
                result.setSelection(11);
                result.closeDrawer();
            }
        });

        try
        {
            if(getIntent().getStringExtra("notification").equals("processed"))
            {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProcessedOrderFragment(), Master.PROCESSED_ORDER_TAG).commit();
                result.setSelection(2);
            }
            else
            {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProductFragment(), Master.PRODUCT_TAG).commit();
            }
        }
        catch (Exception e)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProductFragment(), Master.PRODUCT_TAG).commit();
        }



        Intent intent = new Intent(this, RegistrationIntentService.class);
        if (checkPlayServices())
        {
            startService(intent);
        }
    }



    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    void dashBoardClickListener(long position)
    {
        if (position == 0)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProductFragment(), Master.PRODUCT_TAG).commit();
        }
        else if (position == 1)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new PlacedOrderFragment(), Master.PLACED_ORDER_TAG).commit();
        }
        else if (position == 2)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProcessedOrderFragment(), Master.PROCESSED_ORDER_TAG).commit();
        }
        else if (position == 3)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChangeOrganisationFragment(), Master.CHANGE_ORGANISATION_TAG).commit();
        }
        else if (position == 4)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ReferralFragment(), Master.REFERRALS_TAG).commit();
        }
        else if(position == 5)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AboutUsFragment(), Master.ABOUT_US_TAG).commit();
        }
        else if (position == 6)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FeedbackFragment(), Master.FEEDBACKS_TAG).commit();
        }
        else if (position == 7)
        {


            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            FragmentManager fm = this.getSupportFragmentManager();
            // Create and show the dialog.
            ContactUsFragment newFragment = new ContactUsFragment();
            newFragment.show(fm, "dialog");

        }
        else if (position == 8)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TermsAndConditionsFragment(), Master.TERMS_AND_CONDITIONS_TAG).commit();
        }
        else if (position == 9)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FAQFragment(), Master.FAQ_TAG).commit();
        }
        else if(position == 10)
        {
            dbHelper.clearLogin();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
        else if(position == 11)
        {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(Master.PROFILE_TAG);
            if(profileFragment == null)
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment(), Master.PROFILE_TAG).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_item_count_white, menu);

        item = menu.findItem(R.id.action_cart);
        icon = (LayerDrawable)item.getIcon();
        Master.setBadgeCount(this, icon, Master.CART_ITEM_COUNT);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_cart:
                startActivity(new Intent(DashboardActivity.this, CartActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

   /* public static void updateCart()
    {
        Master.setBadgeCount(, icon, Master.CART_ITEM_COUNT);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyGcmListenerService.ACTION));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor;
        if(sharedPrefs.contains("orderNotifs"))
        {
            editor = sharedPrefs.edit();
            editor.remove("orderNotifs");
            editor.apply();
        }
        if(sharedPrefs.contains("memberNotifs"))
        {
            editor = sharedPrefs.edit();
            editor.remove("memberNotifs");
            editor.apply();
        }

        Master.CART_ITEM_COUNT=  dbHelper.getCartItemsCount(MemberDetails.getMobileNumber(),MemberDetails.getSelectedOrgAbbr());
        System.out.println("--------ON START-------------" + Master.CART_ITEM_COUNT);
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {

        if (result.isDrawerOpen()) {
            result.closeDrawer();
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            if(backpress)
                finish();
            else
            {
                backpress = true;
                Snackbar.make(relativeLayout, R.string.snackbar_press_back_once_more_to_exit, Snackbar.LENGTH_LONG).show();
            }
        }
        else
            getSupportFragmentManager().popBackStack();

    }

    public static void updateNavHeader()
    {
        drawerName.setText(MemberDetails.getFname() + " " + MemberDetails.getLname());
        drawerEmail.setText(MemberDetails.getEmail());
        drawerMobileNumber.setText(MemberDetails.getMobileNumber());
    }


}
