package com.mobile.ict.cart.util;

import android.content.Context;
import android.widget.EditText;

import com.mobile.ict.cart.R;

/**
 * Created by vish on 6/4/16.
 */
public class Validation {

    public final static boolean isValidEmail(String target) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidMobileNumber(String target) {
        return target.length() == 10;
    }

    public static boolean hasText(Context ctx,EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError(ctx.getString(R.string.label_validation_required));
            return false;
        }

        return true;
    }

}
