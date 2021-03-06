package com.ruby.hindiapp.rkandro;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.Html;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lmsa.cqkv143768.AdCallbackListener;
import com.lmsa.cqkv143768.AdView;
import com.lmsa.cqkv143768.AirPlay;

import com.ruby.hindiapp.rkandro.pojo.RkListItem;
import com.ruby.hindiapp.rkandro.soap.SoapWebServiceUtility;
import com.searchboxsdk.android.StartAppSearch;
import com.startapp.android.publish.StartAppAd;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ruby.hindiapp.rkandro.adapter.RkListAdapter;
import com.ruby.hindiapp.rkandro.soap.SoapWebServiceInfo;


public class RkList extends SherlockActivity implements AdCallbackListener.MraidCallbackListener {

    private ListView lv;
    private ProgressDialog progressDialog;

    private List<RkListItem> rkList = new ArrayList<RkListItem>();
    RkListAdapter rkListAdapter;

    ConnectionDetector cd;

    private StartAppAd startAppAd = new StartAppAd(this);
    AirPlay airPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rk_list);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color='#333333'>"+getString(R.string.app_name)+"</font></b>"));

        StartAppSearch.init(this);
        startAppAd.loadAd();

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        boolean isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            // Internet Connection is Present
            lv = (ListView) findViewById(R.id.lviRkList);
            lv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    RkListItem rkDetail = (RkListItem) parent.getItemAtPosition(position);
                    Intent i = new Intent(RkList.this, RkListDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", rkDetail.getId().toString());
                    bundle.putString("name", rkDetail.getTitle());
                    i.putExtras(bundle);
                    startActivity(i);

                }
            });
            new RkDetail().execute(new Object());

            airPlay=new AirPlay(this, adCallbackListener, true);
            AdView adView=(AdView)findViewById(R.id.myAdView);
            adView.setAdListener(this);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(RkList.this, "No Internet Connection",
                    "You don't have internet connection.");
        }
        AppRater.app_launched(RkList.this);
    }

    @Override
    public void onResume(){
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("refresh").setIcon(R.drawable.ic_refresh_inverse).
                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = (String) item.getTitle();
        if (title.equalsIgnoreCase("refresh")) {
            new RkDetail().execute(new Object());
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private List<RkListItem> convertJsonToList(JSONObject jsonObject)
            throws JSONException {
        List<RkListItem> details = new ArrayList<RkListItem>();
        int total = (Integer) jsonObject.get("Total");
        JSONArray detailArray;
        RkListItem rkListItem;

        for (int i = 0; i < total; i++) {
            detailArray = (JSONArray) jsonObject.get("Record" + i);
            rkListItem = new RkListItem();
            rkListItem.setId((Integer) detailArray.get(0));
            rkListItem.setTitle((i+1) + ". " + detailArray.get(1));

            details.add(rkListItem);
        }
        return details;
    }

    @Override
    public void onAdLoadingListener() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdLoadedListener() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onErrorListener(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCloseListener() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdExpandedListner() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdClickListener() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void noAdAvailableListener() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    class RkDetail extends AsyncTask<Object, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RkList.this, "",
                    "Loading....", true, false);
        }

        protected String doInBackground(Object... parametros) {

            String result = null;
            try {
                String envelop = String.format(SoapWebServiceInfo.GETLIST_ENVELOPE);
                result = SoapWebServiceUtility.callWebService(envelop, SoapWebServiceInfo.GETLIST_SOAP_ACTION, SoapWebServiceInfo.GETLIST_RESULT_TAG);
                if(result != null){
                    JSONObject resJsonObj = new JSONObject(result);
                    rkList = convertJsonToList(resJsonObj);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (rkList.size() > 0) {
                rkListAdapter = new RkListAdapter(RkList.this, rkList);
                lv.setAdapter(rkListAdapter);
                rkListAdapter.notifyDataSetChanged();
            }

            progressDialog.dismiss();
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

    AdCallbackListener adCallbackListener = new AdCallbackListener() {
        @Override
        public void onSmartWallAdShowing() {
            //Toast.makeText(RkList.this, "onAdCached", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSmartWallAdClosed() {
            //Toast.makeText(RkList.this, "onSmartWallAdClosed", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onAdError(String s) {
            //Toast.makeText(RkList.this, "onAdError"+s, Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSDKIntegrationError(String s) {
            //Toast.makeText(RkList.this, "onSDKIntegrationError", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onVideoAdFinished() {
            //Toast.makeText(RkList.this, "onVideoAdFinished", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onVideoAdShowing() {
            //Toast.makeText(RkList.this, "onVideoAdShowing", Toast.LENGTH_SHORT).show();
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onAdCached(AdType adType) {
            //Toast.makeText(RkList.this, "onAdCached", Toast.LENGTH_SHORT).show();
            airPlay.showCachedAd(RkList.this, AdType.interstitial);
        }
    };
}
