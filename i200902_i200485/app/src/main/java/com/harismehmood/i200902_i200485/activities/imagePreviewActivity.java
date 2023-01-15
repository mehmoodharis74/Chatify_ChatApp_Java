package com.harismehmood.i200902_i200485.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.network.ApiService;

public class imagePreviewActivity extends AppCompatActivity {
ImageView imageView;
ImageButton closeImageButton;
EditText message;
ImageButton sendButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imageView = findViewById(R.id.imagePreviewActivityImage);
        closeImageButton = findViewById(R.id.crossButton);
        message = findViewById(R.id.userMainChatActivityTypeMessageInputText);
        sendButton = findViewById(R.id.userMainChatActivitySendMessageButton);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        imageView.setImageURI(Uri.parse(image));

        sendButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",message.getText().toString().trim());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        });

        closeImageButton.setOnClickListener(v -> {
            finish();
        });
    }
}