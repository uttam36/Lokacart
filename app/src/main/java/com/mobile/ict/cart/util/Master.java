package com.mobile.ict.cart.util;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.Container.ProductType;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.database.DBHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vishesh on 16-03-2016.
 */
public class Master {

    public static final String serverURL = "http://ruralict.cse.iitb.ac.in/ruralict/";

    public static String getLoginURL()
    {
        return serverURL + "app/login";
    }
    public static String getForgotPasswordURL()
    {   //" http://ruralict.cse.iitb.ac.in/RuralIvrs/app/forgotpassword";
        return serverURL + "app/forgotpassword";
    }
    public static String getChangePasswordURL()
    {
        return serverURL + "app/changepassword";
    }
    public static String getDetailsFormURL(){
        return serverURL + "app/loginform";
    }
    public static String getEnquiryFormURL ()
    {
        return serverURL + "app/enquiry";
    }
    public static String getEditProfileURL ()
    {
        return serverURL + "api/Test2/manageUsers/editProfile";
    }
    public static String getChangeProfilePasswordURL ()
    {
        return serverURL + "app/changepassword";
    }

    public static String getNumberVerifyURL ()
    {
        return serverURL + "app/numberverify";
    }
    public static String getChangeNumberURL ()
    {
        return serverURL + "app/changenumber";
    }
    public static String getLeaveOrganisationURL()
    {
        return serverURL + "app/delete";
    }
    public static String getSendReferralURL()
    {
        return serverURL + "api/refer";
    }
    public static String getProductsURL(String orgAbbr)
    {
        return serverURL + "api/products/search/byType/map?orgabbr=" + orgAbbr;
    }
    public static String getProductTypesURL(String orgAbbr)
    {
        return serverURL + "api/gettypes?abbr=" + orgAbbr;
    }
    public static String getPlacingOrderURL()
    {
        return serverURL + "api/orders/add";
    }

    public static String getPlacedOrderURL(String orgAbbr,String mobileNumber)
    {
        return serverURL + "api/orders/search/getOrdersForMember?format=binary&status=saved&abbr="+orgAbbr+"&phonenumber=91"+mobileNumber+"&projection=default";
    }

    public static String getCancellingOrderURL(int orderID)
    {
        return serverURL + "api/gettypes?abbr=" + orderID;
    }

    public static String getSendCommentURL()
    {
        return serverURL + "api/feedback";
    }

    public static String getSendReferURL()
    {
        return serverURL + "/api/refer";
    }

    public static String sendGCMTokenUrl()
    {
        return serverURL + "/app/registertoken";
    }

    public static String verifyCartURL()
    {
        return serverURL + "api/cartUpdate";
    }


    //---------------- fragment tags ---------------------------

    public static final String
                        PRODUCT_TAG = "product_fragment",
                        PLACED_ORDER_TAG = "placed_order_fragment",
                        PROCESSED_ORDER_TAG = "processed_order_fragment",
                        PROFILE_TAG = "profile_fragment",
                        REFERRALS_TAG = "referrals_fragment",
                        CHANGE_ORGANISATION_TAG = "organisation_fragment",
                        FEEDBACKS_TAG = "feedbacks_fragment",
                        CONTACT_US_TAG = "contact_us_fragment",
                        TERMS_AND_CONDITIONS_TAG = "terms_and_conditions_fragment",
                        FAQ_TAG = "faq_fragment",
                        ABOUT_US_TAG = "about_us_fragment";
    //----------------------------------------------------------

    //---------------- Navigation drawer textviews -------------

    public static TextView drawerName, drawerEmail, drawerMobileNumber;

    //----------------------------------------------------------

    //---------------- navigation drawer item keys -------------
    public static final int
                        PRODUCT_KEY = 0,
                        PLACED_ORDER_KEY = 1,
                        PROCESSED_ORDER_KEY = 2,
                        PROFILE_KEY = 3,
                        REFERRALS_KEY = 4,
                        CHANGE_ORGANISATION_KEY = 5,
                        LOGOUT_KEY = 6,
                        FEEDBACKS_KEY = 7,
                        CONTACT_US_KEY = 8,
                        TERMS_AND_CONDITIONS_KEY = 9,
                        FAQ_KEY = 10;


    public static int CART_ITEM_COUNT=0;

    //---------------------------------------------------------

    //---------------- SQL DB Keys ------------------------------

    public static final String
                    PRODUCT_NAME = "pname",
                    PRICE = "price",
                    TOTAL = "total",
                    QUANTITY = "quantity",
                    ID = "id",
                    IMAGE_URL = "imageurl",
                    AUDIO_URL ="audiourl";

    public static final String
                    STOCK_QUANTITY = "stockquantity";

    //-----------------------------------------------------------

    //---------------- shared preference keys-------------------

    public static final String
                        FNAME = "firstname",
                        LNAME = "lastname",
                        EMAIL = "email",
                        ADDRESS = "address",
                        MOBILENUMBER = "phonenumber",
                        PASSWORD = "password",
                        PINCODE = "pincode",
                        STEPPER = "stepper",
                        SIGNCHK = "sign_check",
                        RESPONSE = "response",
                        STATUS =  "status",
                        ORGANISATIONS = "organizations",
                        ORG_NAME = "name",
                        ORG_ABBR = "abbr",
                        SELECTED_ORG_NAME = "selectedOrgName",
                        SELECTED_ORG_ABBR = "selectedOrgAbbr",
                        REFER_ORG_ABBR = "reforgabbr",
            STOCK_MANAGEMENT_STATUS = "stockManagement",
                        REFER_EMAIL = "refemail";

    public static final String feedbackText = "content";


    public static final String
            DEFAULT_FNAME = "",
            DEFAULT_LNAME = "",
            DEFAULT_EMAIL = "",
            DEFAULT_ADDRESS = "",
            DEFAULT_MOBILENUMBER = "",
            DEFAULT_PASSWORD = "",
            DEFAULT_PINCODE = "",
            DEFAULT_LOGIN_JSON = "",
            DEFAULT_ORG_NAME = "null",
            DEFAULT_ORG_ABBR = "abbr";

    public static Boolean
            DEFAULT_SIGNCHK = false,
            DEFAULT_STEPPER = true;

    public static final String
            LOGIN = "login",
            LOGIN_JSON = "loginJSON";



    public static void initialise(Context context)
    {
        Master.getJSON = new GetJSON();
        Master.responseObject = new JSONObject();
        Master.getMemberDetails(context);
    }
    //----------------------------------------------------------

    //------- GCM ---------------------

    public static String token;

    public static String getToken()
    {
        return token;
    }
    public static void setToken(String tok)
    {
        token = tok;
    }

    //--------------------------------

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    //-----------------------------------------------------------

    //------------public objects---------------------------------

    public static boolean isProductClicked; // to handle multiple clicks in recycler view
    public static GetJSON getJSON;
    public static String response; //for storing JSON in AsyncTasks
    public static JSONObject responseObject; //for making JSONObject from response;
   // public static DBHelper dbHelper;

    public static ArrayList<Product> productList;
    public static ArrayList<Product> cartList;
    public static ArrayList<ProductType> productTypeList;

    //-----------------------------------------------------------

    public static void getMemberDetails(Context context)
    {

        DBHelper dbHelper = new DBHelper(context);
        dbHelper.getSignedInProfile();
        /*
        MemberDetails.setEmail(SharedPreferenceConnector.readString(context, Master.EMAIL, Master.DEFAULT_EMAIL));
        MemberDetails.setMobileNumber(SharedPreferenceConnector.readString(context, Master.MOBILENUMBER, Master.DEFAULT_MOBILENUMBER));
        MemberDetails.setPassword(SharedPreferenceConnector.readString(context, Master.PASSWORD, Master.DEFAULT_PASSWORD));
        MemberDetails.setFname(SharedPreferenceConnector.readString(context, Master.FNAME, Master.DEFAULT_FNAME));
        MemberDetails.setLname(SharedPreferenceConnector.readString(context, Master.LNAME, Master.DEFAULT_LNAME));
        MemberDetails.setAddress(SharedPreferenceConnector.readString(context, Master.ADDRESS, Master.DEFAULT_ADDRESS));
        MemberDetails.setPincode(SharedPreferenceConnector.readString(context, Master.PINCODE, Master.DEFAULT_PINCODE));*/
    }


    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        if (Build.VERSION.SDK_INT <= 15) {
            return;
        }

        CartIconDrawable badge;

        //LayerDrawable d = (LayerDrawable)icon.getDrawable(0);
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof CartIconDrawable) {
            badge = (CartIconDrawable) reuse;
        } else {
            badge = new CartIconDrawable(context);
        }
      //  badge = new CartIconDrawable(context);
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
       // icon.invalidateSelf();
    }

   /* public static  MenuItem cartItem;
    public static  LayerDrawable localLayerDrawable;
    public static  Drawable cartBadgeDrawable;
    public static CartIconDrawable badgeDrawable ;

    public static void createCartBadge(Context context,Menu menu,int paramInt) {
       *//* if (Build.VERSION.SDK_INT <= 15) {
            return;
        }*//*
        cartItem = menu.findItem(R.id.action_cart);
       // if(cartItem.getIcon()!=null)
       // {
            localLayerDrawable = (LayerDrawable) cartItem.getIcon();
            cartBadgeDrawable = localLayerDrawable.findDrawableByLayerId(R.id.ic_badge);
       // }
        badgeDrawable = new CartIconDrawable(context);
      //  badgeDrawable = (CartIconDrawable) cartBadgeDrawable;

       *//* if ((cartBadgeDrawable != null)
                && ((cartBadgeDrawable instanceof CartIconDrawable))
               ) {
            badgeDrawable = (CartIconDrawable) cartBadgeDrawable;
        } else {
            badgeDrawable = new CartIconDrawable(context);
        }*//*
        badgeDrawable.setCount(paramInt);
        localLayerDrawable.mutate();
        localLayerDrawable.setDrawableByLayerId(R.id.ic_badge, badgeDrawable);
        //cartItem.setIcon(localLayerDrawable);
    }*/

}
