package com.mobile.ict.cart.Container;

/**
 * Created by Vishesh on 16-03-2016.
 */
public class MemberDetails {

    public static String fname, lname, password, address, pincode, email, mobileNumber, selectedOrgAbbr, selectedOrgName;

    public static String getSelectedOrgAbbr() {
        return selectedOrgAbbr;
    }

    public static void setSelectedOrgAbbr(String selectedOrgAbbr) {
        MemberDetails.selectedOrgAbbr = selectedOrgAbbr;
    }

    public static String getSelectedOrgName() {
        return selectedOrgName;
    }

    public static void setSelectedOrgName(String selectedOrgName) {
        MemberDetails.selectedOrgName = selectedOrgName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MemberDetails.password = password;
    }

    public static String getFname() {
        return fname;
    }

    public static void setFname(String fname) {
        MemberDetails.fname = fname;
    }

    public static String getLname() {
        return lname;
    }

    public static void setLname(String lname) {
        MemberDetails.lname = lname;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        MemberDetails.address = address;
    }

    public static String getPincode() {
        return pincode;
    }

    public static void setPincode(String pincode) {
        MemberDetails.pincode = pincode;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        MemberDetails.email = email;
    }

    public static String getMobileNumber() {
        return mobileNumber;
    }

    public static void setMobileNumber(String mobileNumber) {
        MemberDetails.mobileNumber = mobileNumber;
    }
}
