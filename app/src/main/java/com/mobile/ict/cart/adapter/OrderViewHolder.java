package com.mobile.ict.cart.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.mobile.ict.cart.Container.Order;
import com.mobile.ict.cart.Container.PlacedOrder;
import com.mobile.ict.cart.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class OrderViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView;
    private TextView orderId,timeStamp,total;

    public OrderViewHolder(View itemView) {
        super(itemView);
        orderId = (TextView) itemView.findViewById(R.id.tvOrderId);
        timeStamp = (TextView) itemView.findViewById(R.id.tvDate);
        total = (TextView) itemView.findViewById(R.id.tvTotal);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_expand_imageview);
    }

    public void bind(PlacedOrder placedOrder) {
        Order order = placedOrder.getOrder();

        System.out.println("orderid-----------"+order.getOrder_id());
        orderId.setText(""+order.getOrder_id());
        Date time = null;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(order.getTimeStamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date = new SimpleDateFormat("EEE, MMM d, yyyy").format(time);
        timeStamp.setText(date);
        total.setText(""+order.getTotalBill());
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                 rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }
    }
}
