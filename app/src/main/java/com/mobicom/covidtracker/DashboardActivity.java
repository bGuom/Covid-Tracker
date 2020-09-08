package com.mobicom.covidtracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.mobicom.covidtracker.Const.PrefKey;
import com.mobicom.covidtracker.Services.BluetoothTrackerService;
import com.mobicom.covidtracker.Utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class DashboardActivity extends AppCompatActivity {

    private Switch alertSwitch;
    private SharedPrefManager sharedPrefManager;

    private TextView tvTotal,tvActive,tvNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);



        alertSwitch = (Switch) findViewById(R.id.alertSwitch);

        tvTotal = (TextView) findViewById(R.id.tvtotalCases);
        tvActive = (TextView) findViewById(R.id.tvActiveCases);
        tvNew = (TextView) findViewById(R.id.tvNewCases);

        sharedPrefManager = SharedPrefManager.getInstance(DashboardActivity.this);

        boolean isAlertEnabled = sharedPrefManager.getBoolean(PrefKey.KEY_IS_ALERT_ENABLED,false);
        alertSwitch.setChecked(isAlertEnabled);

        getCovidStats();

        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("bluel","clicked");
                if(isChecked){
                    sharedPrefManager.setBoolean(PrefKey.KEY_IS_ALERT_ENABLED,true);
                    ensureBluetoothIsEnable();
                    requestAccessFineLocation();
                    Log.d("bluel","start service");
                    startService(new Intent(DashboardActivity.this,BluetoothTrackerService.class));
                }
                else{
                    Log.d("bluel","stop service");
                    sharedPrefManager.setBoolean(PrefKey.KEY_IS_ALERT_ENABLED,false);
                    stopService(new Intent(DashboardActivity.this,BluetoothTrackerService.class));
                }
            }
        });
    }



    private void ensureBluetoothIsEnable() {
        final int REQUEST_ENABLE_BLUETOOTH = 1;
        startActivityForResult(new Intent(ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Request Access Fine Location permission.
     */
    private void requestAccessFineLocation() {
        final int REQUEST_ACCESS_FINE_LOCATION = 1;

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            // ACCESS_FINE_LOCATION Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getCovidStats(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.hpb.health.gov.lk/api/get-current-statistical";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.getJSONObject("data");

                            String total = data.getString("local_total_cases");
                            String active  = data.getString("local_active_cases");
                            String newcases = data.getString("local_new_cases");
                            tvTotal.setText(total);
                            tvActive.setText(active);
                            tvNew.setText("+" + newcases);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvTotal.setText("-");
                            tvActive.setText("-");
                            tvNew.setText("-");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvTotal.setText("-");
                tvActive.setText("-");
                tvNew.setText("-");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }



}