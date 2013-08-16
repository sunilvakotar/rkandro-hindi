package com.ruby.hindi.rkandro;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.ruby.hindi.rkandro.adapter.RkListAdapter;
import com.ruby.hindi.rkandro.pojo.RkListItem;
import com.ruby.hindi.rkandro.soap.SoapWebServiceInfo;
import com.ruby.hindi.rkandro.soap.SoapWebServiceUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RkListDetail extends SherlockActivity {

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
    ImageView closeButton;
    RelativeLayout adsLayout;

    ConnectionDetector cd;
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

            AdView adViewPopup = (AdView) this.findViewById(R.id.adviewpopup);
            adViewPopup.loadAd(new AdRequest());

            adsLayout = (RelativeLayout) findViewById(R.id.popupWithCross);
            closeButton = (ImageView) findViewById(R.id.closeBtn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adsLayout.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Close ads", Toast.LENGTH_SHORT).show();
                }
            });

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

    class PopupDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // Make popup ad visible
                    if(adsLayout.getVisibility() == View.GONE){
                        adsLayout.setVisibility(View.VISIBLE);
                        // display toast
                        //Toast.makeText(getApplicationContext(), "Popup show", Toast.LENGTH_SHORT).show();
                    }
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
