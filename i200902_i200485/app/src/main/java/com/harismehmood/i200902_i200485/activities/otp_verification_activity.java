package com.harismehmood.i200902_i200485.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.harismehmood.i200902_i200485.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp_verification_activity extends AppCompatActivity {
TextView phone_no_textView, resend_otp_textView;
Button verifyOTPBtn;
PinView pinView;
FirebaseAuth mAuth;
String verificationId;
String enteredOTP_Btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_otp_verification);
        phone_no_textView = findViewById(R.id.textToHintPhoneNo);
        verifyOTPBtn = findViewById(R.id.BtnVerifyOTP);
        pinView = findViewById(R.id.pinView);
        resend_otp_textView = findViewById(R.id.resendOtp);

        //get phone number from previous activities

        String phone_no = getIntent().getStringExtra("phone");  //get phone number from previous activities
        Log.d("phone", phone_no);
        System.out.println("phone no is: "+phone_no);
        phone_no_textView.setText(phone_no_textView.getText().toString() +" ****"+ phone_no.charAt(phone_no.length()-3)+ phone_no.charAt(phone_no.length()-2)+ phone_no.charAt(phone_no.length()-1));
        // entered otp by user
        mAuth = FirebaseAuth.getInstance();
      

        //reset otp text view
resend_otp_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoading(true);
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phone_no)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(otp_verification_activity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isLoading(true);
            if(pinView.getText().toString().isEmpty()){
                Toast.makeText(otp_verification_activity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                return;
            }
                enteredOTP_Btn = pinView.getText().toString();
                verifyCode(enteredOTP_Btn);

            }
        });

        sendVerificationCode(phone_no);

    }
    private void sendVerificationCode(String phone) {
        isLoading(true);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
              //  otpCode.setText(code);
                verifyCode(code);
                pinView.setText(code);
                isLoading(false);

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otp_verification_activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            onBackPressed();
            isLoading(false);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            isLoading(false);
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
        isLoading(false);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //create a builder
                            isLoading(false);
                            Dialog builder = new Dialog(otp_verification_activity.this);
                            builder.setContentView(R.layout.signup_success_dialog);
                            builder.show();
                            //dismiss dialog builder after 3 seconds
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {

                                            Intent intent = new Intent(otp_verification_activity.this, firstTime_InputData_activity.class);
                                            intent.putExtra("phone", getIntent().getStringExtra("phone"));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    },
                                    3000);

                        } else {
                            Toast.makeText(otp_verification_activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(otp_verification_activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void isLoading(Boolean isLoading){
        LottieAnimationView progressBar = findViewById(R.id.loader);
        if(isLoading){

            verifyOTPBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            verifyOTPBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
