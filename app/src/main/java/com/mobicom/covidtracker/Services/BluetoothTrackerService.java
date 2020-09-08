package com.mobicom.covidtracker.Services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.mobicom.covidtracker.DashboardActivity;
import com.mobicom.covidtracker.Models.BluetoothPayload;
import com.mobicom.covidtracker.R;
import com.mobicom.covidtracker.Utils.AES;
import com.mobicom.covidtracker.Utils.KeyHandler;
import com.mobicom.covidtracker.Utils.SharedPrefManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;


public class BluetoothTrackerService extends Service {

    /** Bluetooth interface provided by Android SDK */
    public static android.bluetooth.BluetoothAdapter mBluetoothAdapter;

    /** List of bluetooth devices */
    private ArrayList<String> mBluetoothDevices;



    /** Broadcast Bluetooth signal to discover new Bluetooth devices */
    public BroadcastReceiver mBroadcastReceiver ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("bluel","service Started");


        Intent activityIntent = new Intent(this, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "com.mobicom.covidtracker";
        String channelName = "TrackerService";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }


        // This always shows up in the notifications area when this Service is running.
        // TODO: String localization
        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            not = new Notification.Builder(this,NOTIFICATION_CHANNEL_ID).
                    setContentTitle(getString(R.string.tracker_active))
                    .setSmallIcon(R.drawable.ic_notif).
                    setContentIntent(pendingIntent).build();
        }else{
            not = new Notification.Builder(this).
                    setContentTitle(getString(R.string.tracker_active))
                    .setSmallIcon(R.drawable.ic_notif).
                    setContentIntent(pendingIntent).build();
        }
        startForeground(101, not);

        initBluetoothService();

        //we have some options for service
        //start sticky means service will be explicit started and stopped
        return START_STICKY;
    }

    private void initBluetoothService() {



        // Initialize bluetooth adapter
        mBluetoothAdapter = getDefaultAdapter();
        runBroadcastUpdater();
        // Setup list view
        mBluetoothDevices = new ArrayList<>();
        // Ensure bluetooth is enabled
        ensureBluetoothIsEnable();
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
                    addDevice(device,rssi);
                } else if (ACTION_DISCOVERY_FINISHED.equals(action)) {

                }
            }
        };

        registerBroadcastReceiver(new String[]{ACTION_FOUND, ACTION_DISCOVERY_FINISHED});
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
        //broadcastUpdater.stop();
        stopForeground(true);
        stopSelf();
    }




    private void scanArea(){
        ensureBluetoothIsEnable();
        mBluetoothAdapter.startDiscovery();
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
            String broadcastSecret = SharedPrefManager.getInstance(BluetoothTrackerService.this).getBroadcastSecretKey();
            String decryptedPayload = AES.decrypt(receivedBluetoothPayload.replaceAll(" ",""),broadcastSecret);

            if(decryptedPayload!=null) {
                BluetoothPayload bluetoothPayload = getBluetoothPayload(decryptedPayload);
                Log.d("Bluel", bluetoothPayload.toString());
            }else{

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
                        String secret = SharedPrefManager.getInstance(BluetoothTrackerService.this).getBroadcastSecretKey();
                        String encryptedPayload = AES.encrypt(payload,secret);
                        mBluetoothAdapter.setName(encryptedPayload);
                        scanArea();
                        Log.d("Bluel", "scan");

                    }
                }, 0, 10, TimeUnit.SECONDS);
    }

    private String getBroadcastPayload(){

        Log.d("Bluel", "broadcast");
        BluetoothPayload bluetoothPayload = new BluetoothPayload();

        bluetoothPayload.setBroadcastID(KeyHandler.getInstance(BluetoothTrackerService.this).getRandomBroadcastID().getBroadcastKey());
        bluetoothPayload.setKey(KeyHandler.getInstance(BluetoothTrackerService.this).getRandomBroadcastID().getSecretId());

        bluetoothPayload.setBtVersion(getBtVersion());
        bluetoothPayload.setCpuTemp(Float.toString(getCpuTemperature()));
        bluetoothPayload.setBatteryPercentage(deviceBattery());
        bluetoothPayload.setBatteryCapacity(getBatteryCapacity(BluetoothTrackerService.this));
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

        registerReceiver(mBroadcastReceiver, intentFilter);
    }


    private void ensureBluetoothIsEnable() {

        if (!mBluetoothAdapter.isEnabled())
            stopScan();
            stopForeground(true);
            stopSelf();
    }




}