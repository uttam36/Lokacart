package com.mobile.ict.cart.database;

/**
 * Created by vish on 20/4/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "lokacart.db";
    public static final String PROFILE_TABLE = "profile";
    public static final String CART_TABLE = "cart";

    public final class ProfileDataEntry implements BaseColumns{

        public static final String COLUMN_MOBILE_NUMBER = Master.MOBILENUMBER;;
        public static final String COLUMN_FNAME = Master.FNAME;
        public static final String COLUMN_LNAME = Master.LNAME;
        public static final String COLUMN_EMAIL = Master.EMAIL;
        public static final String COLUMN_ADDRESS = Master.ADDRESS;
        public static final String COLUMN_PINCODE = Master.PINCODE;
        public static final String COLUMN_SELECTED_ORG_ABBR = Master.SELECTED_ORG_ABBR;
        public static final String COLUMN_SELECTED_ORG_NAME = Master.SELECTED_ORG_NAME;
        public static final String COLUMN_PASSWORD = Master.PASSWORD;
        public static final String COLUMN_LOGIN = Master.LOGIN;

    }

    public final class CartDataEntry implements BaseColumns{

        public static final String COLUMN_ORGABBR = Master.ORG_ABBR;
        public static final String COLUMN_PRODUCT_ID = Master.ID;
        public static final String COLUMN_PRODUCT_NAME = Master.PRODUCT_NAME;
        public static final String COLUMN_MOBILE_NUMBER = Master.MOBILENUMBER;
        public static final String COLUMN_PRICE = Master.PRICE;
        public static final String COLUMN_ITEM_TOTAL = Master.TOTAL;
        public static final String COLUMN_QUANTITY = Master.QUANTITY;
        public static final String COLUMN_IMAGE_URL = Master.IMAGE_URL;
        public static final String COLUMN_STOCK_QUANTITY = Master.STOCK_QUANTITY;
        public static final String COLUMN_STOCK_MANAGEMENT_STATUS = Master.STOCK_MANAGEMENT_STATUS;

    }


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PROFILE_TABLE + " (" +
                ProfileDataEntry.COLUMN_MOBILE_NUMBER + " TEXT NOT NULL," +
                ProfileDataEntry.COLUMN_FNAME + " TEXT NOT NULL, " +
                ProfileDataEntry.COLUMN_LNAME + " TEXT NOT NULL," +
                ProfileDataEntry.COLUMN_EMAIL + " TEXT NOT NULL , " +
                ProfileDataEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                ProfileDataEntry.COLUMN_PINCODE + " TEXT NOT NULL," +
                ProfileDataEntry.COLUMN_SELECTED_ORG_ABBR + " TEXT NOT NULL," +
                ProfileDataEntry.COLUMN_SELECTED_ORG_NAME + " TEXT NOT NULL, " +
                ProfileDataEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                ProfileDataEntry.COLUMN_LOGIN + " TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + CART_TABLE + " (" +
                CartDataEntry.COLUMN_MOBILE_NUMBER + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_ORGABBR + " TEXT NOT NULL, " +
                CartDataEntry.COLUMN_PRODUCT_ID + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_PRICE + " TEXT NOT NULL , " +
                CartDataEntry.COLUMN_ITEM_TOTAL + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_QUANTITY + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_STOCK_QUANTITY + " TEXT NOT NULL," +
                CartDataEntry.COLUMN_STOCK_MANAGEMENT_STATUS + " TEXT NOT NULL" +
                ")");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Tasks to perform On Upgrde
        db.execSQL(" DROP TABLE IF EXISTS " + PROFILE_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + CART_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public int addProduct(String unitPrice, String itemTotal,String productName, String mobileNumber,
                          String orgAbbr, String id, String imageURL, String stockQuantity, String stockManagementStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + CART_TABLE + " where " + CartDataEntry.COLUMN_PRODUCT_ID + "=" +"\""+ id +"\""
                + " and " + CartDataEntry.COLUMN_MOBILE_NUMBER + " = " +"\""+ mobileNumber +"\""
                + " and " + CartDataEntry.COLUMN_ORGABBR + " = " +"\""+ orgAbbr +"\"", null);

        if(id_cursor.getCount() == 0)
        {
            ContentValues values = new ContentValues();

            values.put(CartDataEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            values.put(CartDataEntry.COLUMN_PRODUCT_ID, id);
            values.put(CartDataEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(CartDataEntry.COLUMN_PRICE, unitPrice);
            values.put(CartDataEntry.COLUMN_ITEM_TOTAL, itemTotal);
            values.put(CartDataEntry.COLUMN_ORGABBR, orgAbbr);
            values.put(CartDataEntry.COLUMN_QUANTITY, "1");
            values.put(CartDataEntry.COLUMN_IMAGE_URL, imageURL);
            values.put(CartDataEntry.COLUMN_STOCK_QUANTITY, stockQuantity);
            values.put(CartDataEntry.COLUMN_STOCK_MANAGEMENT_STATUS, stockManagementStatus);

            db.insert(CART_TABLE, null, values);
            Master.CART_ITEM_COUNT++;
            id_cursor.close();

            if (db != null && db.isOpen())
                db.close();

            return 1;
        }
        else
        {
            id_cursor.moveToNext();

            String temp = id_cursor.getString(6);
            int qty = Integer.parseInt(temp);
            qty += 1;

            int total = (int) (qty * Double.parseDouble(unitPrice));

            String WHERE =  CartDataEntry.COLUMN_ORGABBR + "= ?" +  " and "
                    + CartDataEntry.COLUMN_PRODUCT_ID + "= ?" + " and "
                    + CartDataEntry.COLUMN_MOBILE_NUMBER + "= ?" ;

            ContentValues values = new ContentValues();
            values.put(CartDataEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            values.put(CartDataEntry.COLUMN_PRODUCT_ID, id);
            values.put(CartDataEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(CartDataEntry.COLUMN_PRICE, unitPrice);
            values.put(CartDataEntry.COLUMN_ITEM_TOTAL, total + "");
            values.put(CartDataEntry.COLUMN_ORGABBR, orgAbbr);
            values.put(CartDataEntry.COLUMN_QUANTITY, qty + "");
            values.put(CartDataEntry.COLUMN_IMAGE_URL, imageURL);
            values.put(CartDataEntry.COLUMN_STOCK_QUANTITY, stockQuantity);
            values.put(CartDataEntry.COLUMN_STOCK_MANAGEMENT_STATUS, stockManagementStatus);

            db.update(CART_TABLE, values, WHERE, new String[]{orgAbbr, id, mobileNumber});
            id_cursor.close();

            if (db != null && db.isOpen())
               db.close();

            return qty;
        }
    }

    public void updateProduct(String unitPrice,String quantity,String itemTotal, String productName, String mobileNumber,
                              String orgAbbr, String id, String imageURL, String stockQuantity, String stockManagementStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("---------updattinggggggg------------");
        Cursor id_cursor =
                db.rawQuery("select * from " + CART_TABLE + " where "
                        + CartDataEntry.COLUMN_PRODUCT_ID + "=" + "\"" + id + "\""
                        + " and " + CartDataEntry.COLUMN_ORGABBR + " = " + "\"" + orgAbbr + "\"", null);

        if (id_cursor.getCount() != 0) {


            System.out.println("---------------------");
            System.out.println("Name: " + productName);
            System.out.println("Qty: " + quantity);
            System.out.println("price: " + unitPrice);
            System.out.println("total: " + itemTotal);
            System.out.println("id: " + id);
            System.out.println("mobilenumber: " + mobileNumber);
            System.out.println("orgabbr: " + orgAbbr);
            System.out.println("---------------------");

            ContentValues values = new ContentValues();

            values.put(CartDataEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            values.put(CartDataEntry.COLUMN_PRODUCT_ID, id);
            values.put(CartDataEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(CartDataEntry.COLUMN_PRICE, unitPrice);
            values.put(CartDataEntry.COLUMN_QUANTITY,quantity);
            values.put(CartDataEntry.COLUMN_ITEM_TOTAL, itemTotal);
            values.put(CartDataEntry.COLUMN_ORGABBR, orgAbbr);
            values.put(CartDataEntry.COLUMN_IMAGE_URL, imageURL);
            values.put(CartDataEntry.COLUMN_STOCK_QUANTITY, stockQuantity);
            values.put(CartDataEntry.COLUMN_STOCK_MANAGEMENT_STATUS, stockManagementStatus);

            String WHERE =  CartDataEntry.COLUMN_ORGABBR + "= ?" +  " and "
                    + CartDataEntry.COLUMN_PRODUCT_ID + "= ?"+  " and " + CartDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

            db.update(CART_TABLE, values, WHERE, new String[]{orgAbbr, id, mobileNumber});

            id_cursor.close();

           if (db != null && db.isOpen())                 db.close();
        }
    }

    public boolean isMobileNumberPresent(String number)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + number + "\"", null);

        if(id_cursor.getCount() == 0)
        {
            System.out.println("------number not present------");
            id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return false;
        }
        else
        {
            System.out.println("------number present------");
            id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return true;
        }
    }


    public long addProfile() {

        System.out.println("------adding profile------");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ProfileDataEntry.COLUMN_MOBILE_NUMBER, MemberDetails.getMobileNumber());
        values.put(ProfileDataEntry.COLUMN_FNAME, MemberDetails.getFname());
        values.put(ProfileDataEntry.COLUMN_LNAME, MemberDetails.getLname());
        values.put(ProfileDataEntry.COLUMN_EMAIL, MemberDetails.getEmail());
        values.put(ProfileDataEntry.COLUMN_ADDRESS, MemberDetails.getAddress());
        values.put(ProfileDataEntry.COLUMN_PINCODE, MemberDetails.getPincode());
        values.put(ProfileDataEntry.COLUMN_SELECTED_ORG_ABBR, MemberDetails.getSelectedOrgAbbr());
        values.put(ProfileDataEntry.COLUMN_SELECTED_ORG_NAME, MemberDetails.getSelectedOrgName());
        values.put(ProfileDataEntry.COLUMN_PASSWORD, MemberDetails.getPassword());
        values.put(ProfileDataEntry.COLUMN_LOGIN, "true");

        long newRowID = db.insert(PROFILE_TABLE, null, values);

       if (db != null && db.isOpen())                 db.close();

        return newRowID;
    }

    public void setLogin(String mobileNumber)
    {
        System.out.println("------setting login true------");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProfileDataEntry.COLUMN_LOGIN, "true");

        String WHERE = ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

        db.update(PROFILE_TABLE, values, WHERE, new String[]{mobileNumber});

       if (db != null && db.isOpen())                 db.close();
    }


    public void deleteProduct( String mobileNumber, String ID) {
        

        System.out.println("deleting product-------" + mobileNumber + "---------------" + ID);

        SQLiteDatabase db = this.getWritableDatabase();

        long numOfRows = db.delete(CART_TABLE,
                CartDataEntry.COLUMN_MOBILE_NUMBER + "= ?" + " AND " + CartDataEntry.COLUMN_PRODUCT_ID + "=  ?",
                new String[]{mobileNumber, ID});

       if (db != null && db.isOpen())                 db.close();
    }

    public void deleteCart(String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        long numOfRows = db.delete(CART_TABLE,
                CartDataEntry.COLUMN_MOBILE_NUMBER + "= ?",
                new String[]{mobileNumber});

        System.out.println("-------number of rows deleted------------" + numOfRows);

       if (db != null && db.isOpen())                 db.close();
    }

    public void deleteProfile(String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        long numOfRows = db.delete(PROFILE_TABLE, ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?", new String[]{mobileNumber});

       if (db != null && db.isOpen())                 db.close();
    }


    public boolean getSignedInProfile()
    {
        String profile[] = new String[2];
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_LOGIN + "=" + "\"" + "true" + "\"", null);

        if (id_cursor.moveToNext()) {
            MemberDetails.setMobileNumber(id_cursor.getString(0));
            MemberDetails.setEmail(id_cursor.getString(3));
            MemberDetails.setPassword(id_cursor.getString(8));
            MemberDetails.setFname(id_cursor.getString(1));
            MemberDetails.setLname(id_cursor.getString(2));
            MemberDetails.setAddress(id_cursor.getString(4));
            MemberDetails.setPincode(id_cursor.getString(5));
            MemberDetails.setSelectedOrgAbbr(id_cursor.getString(6));
            MemberDetails.setSelectedOrgName(id_cursor.getString(7));
            id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return true;
        }
        else
        {id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return false;
        }


    }



    public void updateProfile(String mobileNumber) {

        System.out.println("------updating profile------");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(ProfileDataEntry.COLUMN_FNAME, MemberDetails.getFname());
        values.put(ProfileDataEntry.COLUMN_LNAME, MemberDetails.getLname());
        values.put(ProfileDataEntry.COLUMN_ADDRESS, MemberDetails.getAddress());
        values.put(ProfileDataEntry.COLUMN_PINCODE, MemberDetails.getPincode());
        values.put(ProfileDataEntry.COLUMN_LOGIN, "true");


        String WHERE = ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

        db.update(PROFILE_TABLE, values, WHERE, new String[]{mobileNumber});

       if (db != null && db.isOpen())                 db.close();


    }

    public void setSelectedOrg (String mobileNumber, String orgName, String orgAbbr)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + mobileNumber + "\"", null);

        if (id_cursor.getCount() != 0) {
            ContentValues values = new ContentValues();

            values.put(ProfileDataEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            values.put(ProfileDataEntry.COLUMN_SELECTED_ORG_ABBR, orgAbbr);
            values.put(ProfileDataEntry.COLUMN_SELECTED_ORG_NAME, orgName);

            String WHERE = ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

            db.update(PROFILE_TABLE, values, WHERE, new String[]{mobileNumber});



        }
        id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
    }

    public void clearLogin()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ProfileDataEntry.COLUMN_LOGIN, "false");

        db.update(PROFILE_TABLE, values, null, null);
       if (db != null && db.isOpen())                 db.close();
    }

    public String[] getSelectedOrg(String mobileNumber)
    {
        String orgName[] = new String[2];

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + mobileNumber + "\"", null);

        if(db == null)
            Log.e("DBHelper", "DB is null");

        if (id_cursor.getCount() != 0) {
            id_cursor.moveToNext();
            orgName[0] = id_cursor.getString(6);
            orgName[1] = id_cursor.getString(7);
        }
        else
        {
            orgName[0] = orgName[1] = "null";
        }

        id_cursor.close();
       if (db != null && db.isOpen())                 db.close();
        return orgName;
    }


    public boolean changeMobileNumber(String oldNumber, String newNumber)
    {
        String orgName[] = new String[2];

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + oldNumber + "\"", null);

        if (id_cursor.getCount() != 0) {
            ContentValues values = new ContentValues();

            values.put(ProfileDataEntry.COLUMN_MOBILE_NUMBER, newNumber);

            String WHERE = ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

            db.update(PROFILE_TABLE, values, WHERE, new String[]{oldNumber});

            Cursor temp = db.rawQuery("select * from " + CART_TABLE + " where "
                    + CartDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + oldNumber + "\"", null);

            if(temp.getCount() != 0)
            {
                ContentValues values1 = new ContentValues();

                values1.put(CartDataEntry.COLUMN_MOBILE_NUMBER, newNumber);

                String WHERE1 = CartDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

                db.update(CART_TABLE, values1, WHERE1, new String[]{oldNumber});
            }
            id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return true;
        }
        else
        {id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return false;
        }
    }

    public boolean changePassword(String mobileNumber, String password)
    {
        String orgName[] = new String[2];

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor =
                db.rawQuery("select * from " + PROFILE_TABLE + " where "
                        + ProfileDataEntry.COLUMN_MOBILE_NUMBER + "=" + "\"" + mobileNumber + "\"", null);

        if (id_cursor.getCount() != 0) {
            ContentValues values = new ContentValues();

            values.put(ProfileDataEntry.COLUMN_PASSWORD, password);

            String WHERE = ProfileDataEntry.COLUMN_MOBILE_NUMBER + "= ?";

            db.update(PROFILE_TABLE, values, WHERE, new String[]{password});

            id_cursor.close();
           if (db != null && db.isOpen())                 db.close();
            return true;
        }
        id_cursor.close();
       if (db != null && db.isOpen())                 db.close();
        return false;
    }


    public ArrayList<Product> getCartDetails(String mobileNumber, String orgAbbr)
    {

        ArrayList<Product> product_list= new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + CART_TABLE + " where " + CartDataEntry.COLUMN_ORGABBR + " = "
                + "\""+orgAbbr+"\"" + " and " + CartDataEntry.COLUMN_MOBILE_NUMBER + " = " + "\""+mobileNumber+"\"", null);

        while (cursor.moveToNext())
        {
            Product temp = new Product();

            System.out.println(cursor.getString(1)+"--------"+cursor.getString(2)+"----------"+cursor.getString(3)+"-------"+cursor.getString(4)+"--------"+cursor.getString(5)+"---------"+cursor.getString(6)+"---------"+cursor.getString(7));

            temp.setOrgAbbr(cursor.getString(1));
            temp.setID(cursor.getString(2));
            temp.setName(cursor.getString(3));
            temp.setUnitPrice(Double.parseDouble(cursor.getString(4)));
            temp.setTotal(Double.parseDouble(cursor.getString(5)));
            temp.setQuantity(Integer.parseInt(cursor.getString(6)));
            temp.setImageUrl(cursor.getString(7));
            temp.setStockQuantity(Integer.parseInt(cursor.getString(8)));
            temp.setStockEnabledStatus(cursor.getString(9));

            product_list.add(temp);
        }
        cursor.close();

        if (db != null && db.isOpen())
            db.close();

        return product_list;
    }


    public int getCartItemsCount(String mobileNumber, String orgAbbr)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor id_cursor = db.rawQuery("select * from " + CART_TABLE + " where " + CartDataEntry.COLUMN_ORGABBR + " = "
                + "\""+orgAbbr+"\"" + " and " + CartDataEntry.COLUMN_MOBILE_NUMBER + " = " + "\""+mobileNumber+"\"", null);

        if(id_cursor.getCount()!=0)
        {
            int count = id_cursor.getCount();
            System.out.println("----cart item count---------" + id_cursor.getCount());

            id_cursor.close();

            if (db != null && db.isOpen())
               db.close();

            return count;
        }

        id_cursor.close();

        if (db != null && db.isOpen())
           db.close();

        return 0;

    }

}

