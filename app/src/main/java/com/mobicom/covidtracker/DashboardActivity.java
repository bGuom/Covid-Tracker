package com.mobicom.covidtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import com.mobicom.covidtracker.Const.PrefKey;
import com.mobicom.covidtracker.DB.DBHelper;
import com.mobicom.covidtracker.Models.BluetoothPayload;
import com.mobicom.covidtracker.Models.ContactData;
import com.mobicom.covidtracker.Models.ModelInput;
import com.mobicom.covidtracker.Models.PositiveKeys;
import com.mobicom.covidtracker.Models.SecretKeys;
import com.mobicom.covidtracker.Services.BluetoothTrackerService;
import com.mobicom.covidtracker.Utils.AES;
import com.mobicom.covidtracker.Utils.DistanceMeter;
import com.mobicom.covidtracker.Utils.KeyHandler;
import com.mobicom.covidtracker.Utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class DashboardActivity extends AppCompatActivity {

    /** Bluetooth interface provided by Android SDK */
    public static BluetoothAdapter mBluetoothAdapter;
    /** Broadcast Bluetooth signal to discover new Bluetooth devices */
    public BroadcastReceiver mBroadcastReceiver ;

    private FirebaseCustomLocalModel localModel;

    private DBHelper dbHelper;
    private FirebaseFirestore fireDB;

    private Switch alertSwitch;
    private SharedPrefManager sharedPrefManager;

    private TextView tvHigh, tvMed, tvLow, tvTotal,tvActive,tvNew, warningTitle,warningMessage;

    private ImageView imgGetQR, imgKeyQR;

    private CardView warningView;

    private Button warningDismiss;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("model.tflite")
                .build();


        dbHelper = new DBHelper(DashboardActivity.this);
        dbHelper.deleteOldRecords();
        fireDB = FirebaseFirestore.getInstance();

        alertSwitch = (Switch) findViewById(R.id.alertSwitch);

        tvHigh = (TextView) findViewById(R.id.tvHighRisk);
        tvMed = (TextView) findViewById(R.id.tvMedRisk);
        tvLow = (TextView) findViewById(R.id.tvLowRisk);

        tvTotal = (TextView) findViewById(R.id.tvtotalCases);
        tvActive = (TextView) findViewById(R.id.tvActiveCases);
        tvNew = (TextView) findViewById(R.id.tvNewCases);

        imgKeyQR = (ImageView)findViewById(R.id.keyQR);
        imgKeyQR.setVisibility(View.GONE);
        imgGetQR = (ImageView)findViewById(R.id.imgGetKey);

        warningView = (CardView) findViewById(R.id.warningView);
        warningView.setVisibility(View.GONE);
        warningTitle = (TextView) findViewById(R.id.warningViewTitle);
        warningMessage = (TextView) findViewById(R.id.warningViewBody);
        warningDismiss = (Button) findViewById(R.id.warningViewButton);

        sharedPrefManager = SharedPrefManager.getInstance(DashboardActivity.this);

        boolean isAlertEnabled = sharedPrefManager.getBoolean(PrefKey.KEY_IS_ALERT_ENABLED,false);
        alertSwitch.setChecked(isAlertEnabled);

        getCovidStats();
        getContactStatus();

        alertSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("bluel","clicked");
                if(isChecked){
                    sharedPrefManager.setBoolean(PrefKey.KEY_IS_ALERT_ENABLED,true);
                    ensureBluetoothIsEnable();
                    requestAccessFineLocation();
                    Log.d("bluel","start service");
                    //startService();
                    initBluetoothService();
                }
                else{
                    Log.d("bluel","stop service");
                    sharedPrefManager.setBoolean(PrefKey.KEY_IS_ALERT_ENABLED,false);
                    //stopService();
                }
            }
        });



        imgGetQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bluel","qrread");

                QRGEncoder qrgEncoder = new QRGEncoder(KeyHandler.getInstance(DashboardActivity.this).getDeviceTrackingID(), null, QRGContents.Type.TEXT,512);
                try {
                    // Getting QR-Code as Bitmap
                    Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                    // Setting Bitmap to ImageView
                    imgKeyQR.setImageBitmap(bitmap);
                    imgKeyQR.setVisibility(View.VISIBLE);
                } catch (WriterException e) {

                }
            }
        });

        imgKeyQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgKeyQR.setVisibility(View.GONE);
            }
        });

        warningDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningView.setVisibility(View.GONE);
            }
        });
    }

    private void getContactStatus() {

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1; // Month are starting from 0
        int date = cal.get(Calendar.DAY_OF_MONTH);

        String yy = Integer.toString(year);
        String mm = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        String dd = (date < 10) ? "0" + Integer.toString(date) : Integer.toString(date);


        String today = dd+"-"+mm+"-"+yy;

        Log.d("bluel",today);

        DocumentReference docRef = fireDB.collection("positive_cases").document(today);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Toast.makeText(getApplicationContext(), "Latest Data Received!", Toast.LENGTH_LONG).show();
                PositiveKeys keys  = documentSnapshot.toObject(PositiveKeys.class);
                if(keys!=null) {
                    checkContactStatus(keys.keys);
                }else{
                    Toast.makeText(getApplicationContext(), "Latest Data not Available Yet!!! Please try again", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting latest data!!! Please try again", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void checkContactStatus(ArrayList<String> keys) {
        ArrayList<ContactData> contactData = (ArrayList<ContactData>) dbHelper.getAllContactData();
        int totalContacts = contactData.size();
        int highRisk =0;
        int medRisk =0;
        int lowRisk =totalContacts;
        for (ContactData contact : contactData)
        {
            if(keys.contains(contact.getKey())){
                if(contact.getLevel()==1){
                    highRisk+=1;
                    lowRisk-=1;
                }else{
                    medRisk+=1;
                    lowRisk-=1;
                }
            }

        }
        tvHigh.setText(Integer.toString(highRisk));
        tvMed.setText(Integer.toString(medRisk));
        tvLow.setText(Integer.toString(lowRisk));

        if(highRisk>0){
            warningTitle.setText("You had close contact with " + Integer.toString(highRisk) + " COVID 19 positive patient.");
            warningMessage.setText("Please seek immediate medical attention and get into self quarantine as soon as possible ");
            warningView.setVisibility(View.VISIBLE);
        }
        if(highRisk == 0 && medRisk >0){
            warningTitle.setText("You have encountered " + Integer.toString(medRisk) + " COVID 19 positive patient(s).");
            warningMessage.setText("Please get into self quarantine as soon as possible!");
            warningView.setVisibility(View.VISIBLE);
        }



    }

    public void startService() {
        Intent serviceIntent = new Intent(this, BluetoothTrackerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, BluetoothTrackerService.class);
        stopService(serviceIntent);
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



    //--------------

    private void initBluetoothService() {



        // Initialize bluetooth adapter
        mBluetoothAdapter = getDefaultAdapter();


        // Register BroadcastReceiver
        mBroadcastReceiver = new BroadcastReceiver() {

            /**
             * Once a new Bluetooth device is discovered, onReceive is called and the device can be
             * processed.
             * @param context Application Context.
             * @param intent Intent.
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d("Bluel", "Receive");

                if (ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    Log.d("Bluel", Integer.toString(rssi));
                    addDevice(device,rssi);
                } else if (ACTION_DISCOVERY_FINISHED.equals(action)) {

                }
            }
        };

        registerBroadcastReceiver(new String[]{ACTION_FOUND, ACTION_DISCOVERY_FINISHED});

    }





    private void scanArea(){
        ensureBluetoothIsEnable();
        mBluetoothAdapter.startDiscovery();
        Log.d("Bluel", "scan");
    }

    private void stopScan() {
        unregisterReceiver(mBroadcastReceiver);
    }


    private void addDevice(BluetoothDevice device, int dbm) {
        String deviceName = device.getName();
        String deviceMacAddress = device.getAddress();
        String signalDbm= Integer.toString(dbm);

        String receivedBluetoothPayload = device.getName();
        if(receivedBluetoothPayload!=null && receivedBluetoothPayload.length()>20){
            String broadcastSecret = SharedPrefManager.getInstance(DashboardActivity.this).getBroadcastSecretKey();
            String decryptedPayload = AES.decrypt(receivedBluetoothPayload.replaceAll(" ",""),broadcastSecret);

            if(decryptedPayload!=null) {
                BluetoothPayload bluetoothPayload = getBluetoothPayload(decryptedPayload);
                Float senderBatteryCap = Float.parseFloat(bluetoothPayload.getBatteryCapacity()) *1000;
                Float senderBatteryLevel = Float.parseFloat(bluetoothPayload.getBatteryPercentage());
                Float receiverBatteryLevel = Float.parseFloat(deviceBattery());
                Float senderBTVersion = Float.parseFloat(bluetoothPayload.getBtVersion());
                Float senderTemp = Float.parseFloat(bluetoothPayload.getCpuTemp());
                Float receiverTemp = getCpuTemperature();
                Float receiverBTVersion = Float.parseFloat(getBtVersion());
                Float rssi = Float.parseFloat(signalDbm);

                String key = KeyHandler.getInstance(DashboardActivity.this).getReversedDiagnosisKey(bluetoothPayload.getBroadcastID(),bluetoothPayload.getKey());

                ModelInput modelInput = new ModelInput(senderBatteryCap,senderBatteryLevel,receiverBatteryLevel,senderBTVersion,senderTemp,receiverTemp,receiverBTVersion,rssi,key);
                predictDistance(modelInput);
                Log.d("Bluel", bluetoothPayload.toString());
            }else{
                if(dbm>-70){
                    vibrate();
                }else {
                    
                }

            }

        }








    }
    private BluetoothPayload getBluetoothPayload(String payload){
        ArrayList<String> fields = new ArrayList<String>();
        fields.addAll(Arrays.asList(payload.split(",")));

        //broadcastID+","+key+","+ btVersion +","+batteryPercentage+","+cpuTemp+","+batteryCapacity;

        BluetoothPayload bluetoothPayload = new BluetoothPayload();
        bluetoothPayload.setBroadcastID(fields.get(0));
        bluetoothPayload.setKey(Integer.parseInt(fields.get(1)));
        bluetoothPayload.setBtVersion(fields.get(2));
        bluetoothPayload.setBatteryPercentage(fields.get(3));
        bluetoothPayload.setCpuTemp(fields.get(4));
        bluetoothPayload.setBatteryCapacity(fields.get(5));

        return bluetoothPayload;

    }

    private void runBroadcastUpdater(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        ensureBluetoothIsEnable();
                        String payload = getBroadcastPayload();
                        String secret = SharedPrefManager.getInstance(DashboardActivity.this).getBroadcastSecretKey();
                        String encryptedPayload = AES.encrypt(payload,secret);
                        mBluetoothAdapter.setName(encryptedPayload);
                        scanArea();


                    }
                }, 0, 15, TimeUnit.SECONDS);
    }

    private String getBroadcastPayload(){

        Log.d("Bluel", "broadcast");
        BluetoothPayload bluetoothPayload = new BluetoothPayload();

        bluetoothPayload.setBroadcastID(KeyHandler.getInstance(DashboardActivity.this).getRandomBroadcastID().getBroadcastKey());
        bluetoothPayload.setKey(KeyHandler.getInstance(DashboardActivity.this).getRandomBroadcastID().getSecretId());

        bluetoothPayload.setBtVersion(getBtVersion());
        bluetoothPayload.setCpuTemp(Float.toString(getCpuTemperature()));
        bluetoothPayload.setBatteryPercentage(deviceBattery());
        bluetoothPayload.setBatteryCapacity(getBatteryCapacity(DashboardActivity.this));
        return bluetoothPayload.getCommaSeparated();

    }


    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public String getBtVersion() {
        List<String> deviceModel = Arrays.asList("Xiaomi Redmi Note 8", "Huawei GR5 mini", "Xiaomi Redmi 4A", "Xiaomi Redmi 5 Plus");
        List<String> deviceBle = Arrays.asList("5", "4.1", "4.1", "4.2");
        int index = deviceModel.indexOf(getDeviceName());
        if(index!=-1) {
            return deviceBle.get(index);
        }else{
            return "4";
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public String deviceBattery(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = level * 100 /scale;
        return Integer.toString(batteryPct);
    }
    public String getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Double.toString(batteryCapacity/1000);

    }
    public float getCpuTemperature()
    {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line!=null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            }else{
                return 51.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }




    private void registerBroadcastReceiver(@NonNull String[] actions) {
        IntentFilter intentFilter = new IntentFilter();

        for (String action : actions)
            intentFilter.addAction(action);

        Log.d("bluel","Registered");

        registerReceiver(mBroadcastReceiver, intentFilter);

        runBroadcastUpdater();
    }

    //-----------

    public void predictDistance(final ModelInput mInput){
        FirebaseModelInterpreter interpreter;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            try{
                FirebaseModelInputOutputOptions inputOutputOptions =
                        new FirebaseModelInputOutputOptions.Builder()
                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,8})
                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,1})
                                .build();
                float[][] input = new float[1][8];
                input[0][0]=mInput.getSenderBatteryCap();
                input[0][1]=mInput.getSenderBatteryLevel();
                input[0][2]=mInput.getReceiverBatteryLevel();
                input[0][3]=mInput.getSenderBTVersion();
                input[0][4]=mInput.getSenderTemp();
                input[0][5]=mInput.getReceiverTemp();//
                input[0][6]=mInput.getReceiverBTVersion();
                input[0][7]=-mInput.getRssi();
                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your model requires
                        .build();
                interpreter.run(inputs, inputOutputOptions)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseModelOutputs>() {
                                    @Override
                                    public void onSuccess(FirebaseModelOutputs result) {
                                        float[][] output = result.getOutput(0);
                                        float probability = output[0][0];
                                        Log.d("Bluel",Float.toString(probability));
                                        if(probability>50f){
                                            Log.d("Bluel","close");
                                            vibrate();
                                            ContactData contactData = new ContactData(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),new SimpleDateFormat("HH:mm:ss").format(new Date()),1,mInput.getSenderKey());
                                            if(!dbHelper.checkKey(contactData.getKey(),contactData.getDate())) {
                                                dbHelper.recordContact(contactData);
                                            }
                                        }else {
                                            Log.d("Bluel","long");
                                            ContactData contactData = new ContactData(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),new SimpleDateFormat("HH:mm:ss").format(new Date()),0,mInput.getSenderKey());
                                            if(!dbHelper.checkKey(contactData.getKey(),contactData.getDate())) {
                                            dbHelper.recordContact(contactData);
                                            }
                                        }


                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Bluel",e.getLocalizedMessage());
                                    }
                                });

            }catch (FirebaseMLException er){
                Log.d("Bluel",er.getLocalizedMessage());
            }
        } catch (FirebaseMLException e) {
            Log.d("Bluel",e.getLocalizedMessage());

        }
    }

    private void vibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("COVID TRACKER ALERT !")
                .setContentText("Keep Your Distance")
                .setVibrate(new long[]{1000, 1000})
                .setChannelId("CHANNEL_ID");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("CHANNEL_ID", getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        Random r = new Random();
        //int notif_id = (int)System.currentTimeMillis();
        notificationManager.notify( 1, notificationBuilder.build());
    }






}