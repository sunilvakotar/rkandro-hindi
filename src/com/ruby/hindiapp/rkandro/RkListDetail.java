package com.ruby.hindiapp.rkandro;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lmsa.cqkv143768.AdCallbackListener;
import com.lmsa.cqkv143768.AdView;
import com.lmsa.cqkv143768.AirPlay;
import com.ruby.hindiapp.rkandro.soap.SoapWebServiceInfo;
import com.ruby.hindiapp.rkandro.soap.SoapWebServiceUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class RkListDetail extends SherlockActivity implements AdCallbackListener.MraidCallbackListener{

    public static final long NOTIFY_INTERVAL = 240 * 1000; // 10 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private ProgressDialog progressDialog;

	private String description;
    private String id;
    private String name;
	
	TextView textDescription;
    //ImageView closeButton;
    //RelativeLayout adsLayout;
    AirPlay airPlay;
    ConnectionDetector cd;

    AdCallbackListener adCallbackListener = new AdCallbackListener() {
        @Override
        public void onSmartWallAdShowing() {
            //Toast.makeText(RkListDetail.this, "onAdCached", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSmartWallAdClosed() {
            //Toast.makeText(RkListDetail.this, "onSmartWallAdClosed", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onAdError(String s) {
            //Toast.makeText(RkListDetail.this, "onAdError:"+s, Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSDKIntegrationError(String s) {
            //Toast.makeText(RkListDetail.this, "onSDKIntegrationError", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onVideoAdFinished() {
            //Toast.makeText(RkListDetail.this, "onVideoAdFinished", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onVideoAdShowing() {
            //Toast.makeText(RkListDetail.this, "onVideoAdShowing", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onAdCached(AdType adType) {
            //Toast.makeText(RkListDetail.this, "onAdCached", Toast.LENGTH_SHORT).show();
            //airPlay.showCachedAd(RkListDetail.this, AdType.appwall);
        }
    };

	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rk_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        boolean isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {

            Bundle extra = getIntent().getExtras();

            id = extra != null ? extra.getString("ID") : " ";
            name = extra != null ? extra.getString("name") : " ";

            if(!" ".equals(name)){
                if(name.contains(".")){
                    name = name.substring(name.indexOf(".")+1);
                }
                getSupportActionBar().setTitle(name);
            }

            textDescription = (TextView) findViewById(R.id.TextDescription);
            // Look up the AdView as a resource and load a request.
            new RkDescription().execute(new Object());

            airPlay=new AirPlay(this, adCallbackListener, true);
            airPlay.startSmartWallAd();
            airPlay.showRichMediaInterstitialAd();

            /*AdView adView=new AdView(this, AdView.BANNER_TYPE_IN_APP_AD, AdView.PLACEMENT_TYPE_INTERSTITIAL, false, false,
                    AdView.ANIMATION_TYPE_LEFT_TO_RIGHT);
            adView.setAdListener(this);
            AdView adView=(AdView)findViewById(R.id.adviewpopup);
            adView.setAdListener(this); */


            /*adsLayout = (RelativeLayout) findViewById(R.id.popupWithCross);
            closeButton = (ImageView) findViewById(R.id.closeBtn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adsLayout.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Close ads", Toast.LENGTH_SHORT).show();
                }
            });*/

            if (mTimer != null) {
                mTimer.cancel();
            } else {
                // recreate new
                mTimer = new Timer();
            }
            // schedule task
            mTimer.scheduleAtFixedRate(new PopupDisplayTimerTask(), NOTIFY_INTERVAL, NOTIFY_INTERVAL);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(RkListDetail.this, "No Internet Connection",
                    "You don't have internet connection.");
        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent i = new Intent(RkListDetail.this, RkList.class);
        //startActivity(i);
        this.finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            //Toast.makeText(getApplicationContext(), "Stop timer",Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public void onAdLoadingListener() {
        //Toast.makeText(RkListDetail.this, "onAdLoadingListener", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdLoadedListener() {
        //Toast.makeText(RkListDetail.this, "onAdLoadedListener", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onErrorListener(String s) {
        //Toast.makeText(RkListDetail.this, "onErrorListener:"+s, Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCloseListener() {
        //Toast.makeText(RkListDetail.this, "onCloseListener", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdExpandedListner() {
        //Toast.makeText(RkListDetail.this, "onAdExpandedListner", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdClickListener() {
        //Toast.makeText(RkListDetail.this, "onAdClickListener", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void noAdAvailableListener() {
        //Toast.makeText(RkListDetail.this, "noAdAvailableListener", Toast.LENGTH_SHORT).show();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean smartAd = false;
    class PopupDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if(smartAd){
                        airPlay.showRichMediaInterstitialAd();
                        airPlay.showCachedAd(RkListDetail.this, AdCallbackListener.AdType.smartwall);
                        smartAd = false;
                    }else {
                        airPlay.startSmartWallAd();
                        airPlay.showCachedAd(RkListDetail.this, AdCallbackListener.AdType.interstitial);
                        smartAd = true;
                    }
                    // Make popup ad visible
                    /*if(adsLayout.getVisibility() == View.GONE){
                        adsLayout.setVisibility(View.VISIBLE);
                        // display toast
                        //Toast.makeText(getApplicationContext(), "Popup show", Toast.LENGTH_SHORT).show();
                    }*/
                }

            });
        }

    }

    class RkDescription extends AsyncTask<Object, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RkListDetail.this, "",
                    "Loading....", true, false);
        }

        protected String doInBackground(Object... parametros) {

            String result = null;
            try {
                String envelop = String.format(SoapWebServiceInfo.GETDETAIL_ENVELOPE, id);
                result = SoapWebServiceUtility.callWebService(envelop, SoapWebServiceInfo.GETDETAIL_SOAP_ACTION, SoapWebServiceInfo.GETDETAIL_RESULT_TAG);
                if(result != null){
                    JSONObject resJsonObj = new JSONObject(result);
                    description = convertJsonToString(resJsonObj);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (description != null) {
                textDescription.setText(Html.fromHtml(description));
            }
            progressDialog.dismiss();
        }
    }

    private String convertJsonToString(JSONObject jsonObject)
            throws JSONException {
        int total = (Integer) jsonObject.get("Total");
        JSONArray detailArray;
        String desc = "";
        for (int i = 0; i < total; i++) {
            detailArray = (JSONArray) jsonObject.get("Record" + i);
            desc =  (String) detailArray.get(0);
        }
        return desc;
    }

}
