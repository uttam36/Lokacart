package com.mobile.ict.cart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.CartIconDrawable;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;

import java.io.IOException;

public class ProductDetailActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    ImageView ivProduct, ivPlay, ivPause, ivStop;
    TextView tDescription, tPrice, tCurrentDuration, tTotalDuration, tProductName, tAvailable;
    CoordinatorLayout coordinatorLayout;
    AppCompatSeekBar audioProgressBar;
    FloatingActionButton fab;
    int position, pauseLength;
    String imgUrl, audioUrl;
    MediaPlayer mediaPlayer;
    Handler mHandler = new Handler();
    Utilities utils;
    Boolean isPause = false;
    DBHelper dbHelper;
    LayerDrawable icon;
    Menu menu;
    MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setTitle("");
        position = getIntent().getIntExtra("position", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper= new DBHelper(this);

        ivProduct = (ImageView) findViewById(R.id.ivProduct);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPause = (ImageView) findViewById(R.id.ivPause);
        ivStop = (ImageView) findViewById(R.id.ivStop);

        tDescription = (TextView) findViewById(R.id.tProductDescription);
        tPrice = (TextView) findViewById(R.id.tProductPrice);
        tCurrentDuration = (TextView) findViewById(R.id.tCurrentDuration);
        tTotalDuration = (TextView) findViewById(R.id.tTotalDuration);
        tProductName = (TextView) findViewById(R.id.tProductName);
        tAvailable = (TextView) findViewById(R.id.tAvailable);

        audioProgressBar = (AppCompatSeekBar) findViewById(R.id.sbProduct);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.detailsCoordinatorLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        tDescription.setText(R.string.desc_apple);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        init();

    }

    void init()
    {
        imgUrl = Master.productList.get(position).getImageUrl();
        audioUrl = Master.productList.get(position).getAudioUrl();

        if(audioUrl == "null")
        {
            /*ivPlay.setBackgroundResource(R.mipmap.play_disabled);

            ivPause.setBackgroundResource(R.mipmap.pause);

            ivStop.setBackgroundResource(R.mipmap.stop);
            */

            ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);
            ivPlay.setEnabled(false);
            ivPause.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);
            ivPause.setEnabled(false);
            ivStop.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);
            ivStop.setEnabled(false);


            audioProgressBar.setEnabled(false);

            Toast.makeText(this, R.string.toast_audio_not_available_for_this_product, Toast.LENGTH_LONG).show();
        }
        else
        {
            utils = new Utilities();

            ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);
            ivPlay.setEnabled(true);
            ivPause.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);
            ivPause.setEnabled(true);
            ivStop.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);
            ivStop.setEnabled(true);

            ivPlay.setOnClickListener(this);
            ivStop.setOnClickListener(this);
            ivPause.setOnClickListener(this);

            audioProgressBar.setOnSeekBarChangeListener(this);
            mediaPlayer = new MediaPlayer();
        }

        if(Master.productList.get(position).getStockEnabledStatus().equals("true") && Master.productList.get(position).getStockQuantity() == 0)
        {
            fab.setVisibility(View.GONE);
            tAvailable.setText("Out of Stock");
            tAvailable.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            fab.setVisibility(View.VISIBLE);
            tAvailable.setText("In Stock");
            tAvailable.setTextColor(getResources().getColor(R.color.green));
        }

        /*if(imgUrl == "null")
        {
            Glide.with(this)
                    .load(imgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_products)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivProduct);
        }
        else*/
        {
            Glide.with(this)
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder_products)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivProduct);
        }


        tPrice.setText("\u20B9" + Master.productList.get(position).getUnitPrice());
        //tDescription.setText("No description available");
        tProductName.setText(Master.productList.get(position).getName().toUpperCase());

    }





        public void updateProgressBar() {
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }

        Runnable mUpdateTimeTask = new Runnable() {
            public void run() {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                tTotalDuration.setText("" + utils.milliSecondsToTimer(totalDuration));

                tCurrentDuration.setText("" + utils.milliSecondsToTimer(currentDuration));

                int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

                audioProgressBar.setProgress(progress);

                if(!(mediaPlayer.isPlaying())){
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    return ;
                }

                mHandler.postDelayed(this, 100);
            }
        };


        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if(b)
                mediaPlayer.seekTo(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(mUpdateTimeTask);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

            mediaPlayer.seekTo(currentPosition);

            updateProgressBar();
        }


    @Override
    public boolean onSupportNavigateUp() {

        if(mediaPlayer!=null)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
        }
        finish();
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mediaPlayer!=null)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_item_count_white,menu);

        MenuItem  item = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable)item.getIcon();

        Master.setBadgeCount(this, icon, Master.CART_ITEM_COUNT);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_cart:
                startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivPlay:

                try {

                    if(!isPause)
                    {
                        mediaPlayer.reset();
                        Uri uri = Uri.parse(audioUrl);
                        mediaPlayer.setDataSource(ProductDetailActivity.this, uri);
                        mediaPlayer.prepare();
                        audioProgressBar.setProgress(0);
                        audioProgressBar.setMax(100);
                    }
                    else
                    {

                        mediaPlayer.seekTo(pauseLength);

                    }

                    isPause = false;
                    mediaPlayer.start();
                    updateProgressBar();

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                ivPlay.setEnabled(false);
                ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                ivStop.setEnabled(true);
                ivStop.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);

                ivPause.setEnabled(true);
                ivPause.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);

                audioProgressBar.setEnabled(true);

                tCurrentDuration.setVisibility(View.VISIBLE);
                tTotalDuration.setVisibility(View.VISIBLE);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        ivPlay.setEnabled(true);
                        ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);

                        ivStop.setEnabled(false);
                        ivStop.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                        ivPause.setEnabled(false);
                        ivPause.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                        audioProgressBar.setEnabled(false);
                        tCurrentDuration.setVisibility(View.INVISIBLE);
                        tTotalDuration.setVisibility(View.INVISIBLE);
                    }
                });
                break;

            case R.id.ivPause:

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPause = true;

                    ivPlay.setEnabled(true);
                    ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);

                    ivPause.setEnabled(false);
                    ivPause.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                    pauseLength = mediaPlayer.getCurrentPosition();
                }
                break;

            case R.id.ivStop:

                isPause = false;

                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    audioProgressBar.setProgress(0);

                }

                ivPlay.setEnabled(true);
                ivPlay.setColorFilter(getResources().getColor(R.color.mediaButtons), PorterDuff.Mode.SRC_IN);

                ivPause.setEnabled(false);
                ivPause.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                ivStop.setEnabled(false);
                ivStop.setColorFilter(getResources().getColor(R.color.mediaButtonsGrayed), PorterDuff.Mode.SRC_IN);

                audioProgressBar.setEnabled(false);
                audioProgressBar.setProgress(0);
                tCurrentDuration.setVisibility(View.INVISIBLE);
                tTotalDuration.setVisibility(View.INVISIBLE);

                mHandler.removeCallbacks(mUpdateTimeTask);
                break;


            case R.id.fab:

                if(Master.productList.get(position).getStockEnabledStatus().equals("true")
                        && Master.productList.get(position).getQuantity() >= Master.productList.get(position).getStockQuantity())
                {
                    Material.alertDialog(this, getString(R.string.alert_no_more_stock_available), "OK");
                    /*tAvailable.setText(R.string.textview_out_of_stock);
                    tAvailable.setTextColor(getResources().getColor(R.color.red));*/

                }
                else
                {
                    dbHelper = new DBHelper(getApplicationContext());
                    int qty = dbHelper.addProduct(
                            Master.productList.get(position).getUnitPrice() + "",
                            Master.productList.get(position).getUnitPrice() + "",
                            Master.productList.get(position).getName(),
                            MemberDetails.getMobileNumber(),
                            MemberDetails.getSelectedOrgAbbr(),
                            Master.productList.get(position).getID(),
                            Master.productList.get(position).getImageUrl(),
                            Master.productList.get(position).getStockQuantity() + "",
                            Master.productList.get(position).getStockEnabledStatus());

                    Master.productList.get(position).setQuantity(qty);



                    invalidateOptionsMenu();

                    // ((Activity)context).invalidateOptionsMenu();

                    Snackbar.make(coordinatorLayout,
                            Master.productList.get(position).getName() + " " + getString(R.string.toast_product_added_to_cart),
                            Snackbar.LENGTH_LONG).show();
                }

                break;
        }
    }


    public class Utilities {

        /**
         * Function to convert milliseconds time to
         * Timer Format
         * Hours:Minutes:Seconds
         * */
        public String milliSecondsToTimer(long milliseconds){
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int)( milliseconds / (1000*60*60));
            int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
            int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
            // Add hours if there
            if(hours > 0){
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if(seconds < 10){
                secondsString = "0" + seconds;
            }else{
                secondsString = "" + seconds;}

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }

        /**
         * Function to get Progress percentage
         * @param currentDuration
         * @param totalDuration
         * */
        public int getProgressPercentage(long currentDuration, long totalDuration){
            Double percentage = (double) 0;

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage =(((double)currentSeconds)/totalSeconds)*100;

            // return percentage
            return percentage.intValue();
        }

        /**
         * Function to change progress to timer
         * @param progress -
         * @param totalDuration
         * returns current duration in milliseconds
         * */
        public int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double)progress) / 100) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }
    }

}
