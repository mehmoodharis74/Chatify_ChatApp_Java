package com.harismehmood.i200902_i200485.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class firstTime_InputData_activity extends AppCompatActivity {
ImageView imageView;
EditText inputUserName;
Button inputLoginButton;
ProgressBar progressBar;
TextView addPhotoTextView;
String encodedImage;
DocumentSnapshot documentSnapshot;
boolean imageSet;
boolean userExists;
String checkUserName;
String checkUserId;

PreferencesManager sharedPreferences;

public String setEncodedImage(Bitmap bitmap) {
    int previewWidth = 150;
    int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
    Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
    byte[] bytes = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
}

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_input_data);

        //fetching already user data for display
        signIn(); //sign in to check if user already exits or not in database if it is then fetch its name and pic and display it

        //fetching ids of all the variables
        sharedPreferences = new PreferencesManager(this);
        imageView = findViewById(R.id.firstTimeInputDataProfileImage);
        inputUserName = findViewById(R.id.firstTimeInputDataName);
        inputLoginButton = findViewById(R.id.firstTimeInputDataLoginButton);
        addPhotoTextView = findViewById(R.id.firstTimeInputDataProfileImageText);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




        //on click of image view open gallery
        imageView.setOnClickListener(v -> {
            //open gallery
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);

        });

        inputLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     if(inputUserName.getText().toString().trim().length() <= 0){
                        Toast.makeText(firstTime_InputData_activity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
                        return;
                     }
                     else if(!imageSet){
                         Toast.makeText(firstTime_InputData_activity.this, "Select Image", Toast.LENGTH_SHORT).show();
                         return;
                     }
                     else
                         addDataToFirebase();

//                //open main activities
//                Intent intent = new Intent(firstTime_InputData_activity.this,MainActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
           // imageView.setImageURI(data.getData());
            Uri uri = data.getData();
            addPhotoTextView.setVisibility(View.GONE);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                encodedImage = setEncodedImage(bitmap);
                imageSet = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                imageSet = false;
            }
        }
        else
        {
            addPhotoTextView.setVisibility(View.VISIBLE);
        }
    }
    public void signIn(){

    Intent intent = getIntent();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection(Constants.USERS_KEY_COLLECTIONS)
            .whereEqualTo(Constants.USER_PHONE, intent.getStringExtra("phone"))
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()&&task.getResult().size() > 0&&task.getResult()!=null) {
                    //user already exists
                    documentSnapshot = task.getResult().getDocuments().get(0);
                    checkUserId = documentSnapshot.getString(Constants.USER_ID);
                    checkUserName = documentSnapshot.getString(Constants.USER_NAME);
                    inputUserName.setText(documentSnapshot.getString(Constants.USER_NAME));
                    if(documentSnapshot.getString(Constants.USER_IMG) != null){
                        encodedImage = documentSnapshot.getString(Constants.USER_IMG);
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageView.setImageBitmap(decodedByte);
                        addPhotoTextView.setVisibility(View.GONE);
                        imageSet = true;
                        userExists = true;
                    }
                    else{
                        addPhotoTextView.setVisibility(View.VISIBLE);
                        imageSet = false;
                        userExists = false;
                    }
                }
                else{
                    //user does not exists
                    userExists = false;
                    return;
                }

            });

    }
    public void updateConversationData(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USERS_KEY_COLLECTIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0 && task.getResult() != null) {
                        //user already exists
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            db.collection(Constants.USERS_KEY_COLLECTIONS)
                                    .document(documentSnapshot.getId())
                                    .update(Constants.KEY_SENDER_NAME, inputUserName.getText().toString().trim(),
                                            Constants.KEY_SENDER_IMAGE, encodedImage);
                        }
                    }

                });
        db.collection(Constants.USERS_KEY_COLLECTIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0 && task.getResult() != null) {
                        //user already exists
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            db.collection(Constants.USERS_KEY_COLLECTIONS)
                                    .document(documentSnapshot.getId())
                                    .update(Constants.KEY_RECEIVER_NAME, inputUserName.getText().toString().trim(),
                                            Constants.KEY_SENDER_IMAGE, encodedImage);
                        }
                    }

                });
    }
    public void addDataToFirebase() {
        isLoading(true);
        Intent intent = getIntent();
        //add data to firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.USER_NAME, inputUserName.getText().toString());
        user.put(Constants.USER_IMG, encodedImage);

        //this condition is for if user already exists then update its data
        if (userExists) {
            //user already exists
            if (!checkUserName.equals(inputUserName.getText().toString().trim())) {
                //user name is changed
                updateConversationData(checkUserId);

            }
                db.collection(Constants.USERS_KEY_COLLECTIONS)
                        .document(documentSnapshot.getId())
                        .update(user)
                        .addOnCompleteListener(documentReference -> {
                            //data added successfully
                            isLoading(false);
                            sharedPreferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            sharedPreferences.putString(Constants.USER_ID, documentSnapshot.getId());
                            sharedPreferences.putString(Constants.USER_NAME, inputUserName.getText().toString());
                            sharedPreferences.putString(Constants.USER_IMG, encodedImage);
                            sharedPreferences.putString(Constants.USER_PHONE, intent.getStringExtra("phone"));
                            //   Toast.makeText(firstTime_InputData_activity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            //open main activities
                            Intent intent1 = new Intent(firstTime_InputData_activity.this, MainActivity.class);
                            startActivity(intent1);
                        })
                        .addOnFailureListener(e -> {
                            //data not added
                            isLoading(false);
                            Toast.makeText(firstTime_InputData_activity.this, "Unable to Update data", Toast.LENGTH_SHORT).show();
                        });
            }
            //this condition of if user does not exits then add it into database
            else {
                //user does not exists
                user.put(Constants.USER_PHONE, intent.getStringExtra("phone"));
                db.collection(Constants.USERS_KEY_COLLECTIONS)
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            isLoading(false);
                            sharedPreferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            sharedPreferences.putString(Constants.USER_ID, documentReference.getId());
                            sharedPreferences.putString(Constants.USER_NAME, inputUserName.getText().toString());
                            sharedPreferences.putString(Constants.USER_IMG, encodedImage);
                            sharedPreferences.putString(Constants.USER_PHONE, intent.getStringExtra("phone"));
                            Intent intent1 = new Intent(firstTime_InputData_activity.this, MainActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                            finish();
                            // Toast.makeText(firstTime_InputData_activity.this, "Data Added", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            isLoading(false);
                            Toast.makeText(firstTime_InputData_activity.this, "Unable to add data", Toast.LENGTH_SHORT).show();
                        });
            }


        }

    public void isLoading(Boolean isLoading){
        LottieAnimationView progressBar = findViewById(R.id.loader);
        if(isLoading){

            inputLoginButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            inputLoginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}