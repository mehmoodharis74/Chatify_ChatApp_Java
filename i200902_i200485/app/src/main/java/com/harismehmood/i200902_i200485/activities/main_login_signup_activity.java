package com.harismehmood.i200902_i200485.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.harismehmood.i200902_i200485.R;
import com.hbb20.CountryCodePicker;

public class main_login_signup_activity extends AppCompatActivity {
CountryCodePicker countryCodeInput;
String final_phoneNo_after_ccp;
Button getOTP;
EditText phone_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_signup);

        countryCodeInput = findViewById(R.id.phoneCountrySpinner);
        getOTP = findViewById(R.id.mainRegisterGetOtpBtn);
        phone_number = findViewById(R.id.mainRegisterPhoneInput);


        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_phoneNo_after_ccp=countryCodeInput.getSelectedCountryCodeWithPlus()+phone_number.getText().toString();
                Intent intent = new Intent(main_login_signup_activity.this, otp_verification_activity.class);
               // Intent intent = new Intent(main_login_signup_activity.this, firstTime_InputData_activity.class);
                //push phone number to next activities
                intent.putExtra("phone", final_phoneNo_after_ccp);
                startActivity(intent);
            }
        });
    }
}