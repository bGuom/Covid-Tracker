package com.mobicom.covidtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobicom.covidtracker.Const.PrefKey;
import com.mobicom.covidtracker.Models.SecretKeys;
import com.mobicom.covidtracker.Models.UserKey;
import com.mobicom.covidtracker.Utils.AES;
import com.mobicom.covidtracker.Utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class JoinActivity extends AppCompatActivity {

    private TextInputLayout contactNumberTextBox;
    private Button joinBtn;
    private ProgressBar pBar;

    private String contactNumber;

    private FirebaseFirestore fireDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        contactNumberTextBox = (TextInputLayout) findViewById(R.id.contactno);
        joinBtn = (Button) findViewById(R.id.join_btn);
        pBar = (ProgressBar) findViewById(R.id.PBar);

        fireDB = FirebaseFirestore.getInstance();

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                joinBtn.setVisibility(View.GONE);
                contactNumber = contactNumberTextBox.getEditText().getText().toString();
                registerDevice(contactNumber);
            }
        });


    }

    private void registerDevice(String contactNumber) {

        SharedPrefManager sharedPrefs = SharedPrefManager.getInstance(JoinActivity.this);

        String uniqueId = UUID.randomUUID().toString();
        sharedPrefs.setString(PrefKey.KEY_DIAGNOSIS_KEY,uniqueId);


        uploadDiagnosisKey(uniqueId);
        getSecrets(sharedPrefs);

    }

    private void getSecrets(final SharedPrefManager sharedPrefs) {
        DocumentReference docRef = fireDB.collection("app_secrets").document("current");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SecretKeys keys  = documentSnapshot.toObject(SecretKeys.class);
                sharedPrefs.setString(PrefKey.KEY_BROADCAST_SECRET_KEY, keys.getBroadcastSecret());
                String[] idKeys = new String[4];
                idKeys[0] = keys.getIdSecret1();
                idKeys[1] = keys.getIdSecret2();
                idKeys[2] = keys.getIdSecret3();
                idKeys[3] = keys.getIdSecret4();

                sharedPrefs.setString(PrefKey.KEY_ID_SECRET_KEYS,idKeys.toString());

                pBar.setVisibility(View.GONE);
                joinBtn.setVisibility(View.VISIBLE);
                if(!contactNumber.equals("")){
                    sharedPrefs.setString(PrefKey.KEY_CONTACT_NUMBER,contactNumber);
                    verifyNumber();
                }else{
                    goDashboard();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!! Please try again", Toast.LENGTH_LONG).show();
                pBar.setVisibility(View.GONE);
                joinBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void uploadDiagnosisKey(String key){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        // Add a new document with a generated ID
        fireDB.collection("users").document(key)
                .set(new UserKey(key,contactNumber, formattedDate))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });


    }





    private void ecrpty() {
        String enc =AES.encrypt(contactNumber,"pattaenc");
        Log.d("secxx","enc_"+enc);
        Log.d("secxx","enc_"+AES.decrypt(enc,"pattaenc"));

    }

    private void verifyNumber(){
        Intent intent = new Intent(JoinActivity.this, PhoneVerificationActivity.class);
        intent.putExtra("contactNumber",contactNumber);
        startActivity(intent);
        finish();
    }

    private void goDashboard(){
        Intent intent = new Intent(JoinActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }



}