package com.mobile.ict.cart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.ict.cart.R;


public class OrderSubmitActivity extends Activity implements View.OnClickListener{

    Button b1;
    TextView orderid;
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);

        orderId= getIntent().getExtras().getString("orderid");
        orderid=(TextView)findViewById(R.id.message2);
        orderid.setText(getResources().getString(R.string.label_activity_ordersubmit_orderid)+": "+orderId);
        b1 = (Button) findViewById(R.id.button_order_submit_done);
        b1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==b1){
            Intent i = new Intent(this,DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finishOrderSubmitActivity();
                    }
    }

    public void finishOrderSubmitActivity() {
        OrderSubmitActivity.this.finish();
    }

}
