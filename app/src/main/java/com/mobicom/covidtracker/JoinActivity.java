package com.mobicom.covidtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class JoinActivity extends AppCompatActivity {

    private TextInputLayout contactNumberTextBox;
    private Button joinBtn;

    private String contactNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        contactNumberTextBox = (TextInputLayout) findViewById(R.id.contactno);
        joinBtn = (Button) findViewById(R.id.join_btn);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactNumber = contactNumberTextBox.getEditText().getText().toString();
                if(!contactNumber.equals("")){
                    Intent intent = new Intent(JoinActivity.this, PhoneVerificationActivity.class);
                    intent.putExtra("contactNumber",contactNumber);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(JoinActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}