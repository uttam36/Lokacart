package com.mobile.ict.cart;

import android.view.View;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import org.hamcrest.Matcher;

/**
 * Created by uttam on 19/5/16.
 */
public class MyViewAction {

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {

            public Matcher<View> getConstraints() {
                return null;
            }

            public String getDescription() {
                return "Click on a child view with specified id.";
            }


            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }

}
