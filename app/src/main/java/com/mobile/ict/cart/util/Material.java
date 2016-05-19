package com.mobile.ict.cart.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.ProgressDialogPro;
import com.mobile.ict.cart.R;


/**
 * Created by vish on 2/4/16.
 */
public class Material {

    //public static MaterialDialog linearProgressDialog;
    public static ProgressDialogPro circularProgressDialog;


    /*public static void linearProgressDialogFast(Context context, String message, Boolean setCancellable)
    {
        linearProgressDialog = new MaterialDialog(context);
        linearProgressDialog.setCanceledOnTouchOutside(true);
        linearProgressDialog.setTitle(message);
        linearProgressDialog.setCanceledOnTouchOutside(setCancellable);
        linearProgressDialog.setContentView(R.layout.progress_dialog_fast);
        linearProgressDialog.show();
    }

    public static void linearProgressDialogSlow(Context context, String message, Boolean setCancellable)
    {
        linearProgressDialog = new MaterialDialog(context);
        linearProgressDialog.setCanceledOnTouchOutside(true);
        linearProgressDialog.setTitle(message);
        linearProgressDialog.setMessage("Loading...");
        linearProgressDialog.setCanceledOnTouchOutside(setCancellable);
        linearProgressDialog.setContentView(R.layout.progress_dialog_slow);
        linearProgressDialog.show();
    }*/

    public static void circularProgressDialog(Context context, String message, Boolean setCancellable)
    {
        circularProgressDialog = new ProgressDialogPro(context);
        circularProgressDialog.setCancelable(setCancellable);
        circularProgressDialog.setMessage(message);
        circularProgressDialog.show();
    }

    public static void alertDialog(Context context, String message, String button_text) //for showing the messages on an alertbox
    {
        AlertDialogPro.Builder builder= new AlertDialogPro.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(
                button_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialogPro alert11 = builder.create();
        alert11.show();
    }


}
