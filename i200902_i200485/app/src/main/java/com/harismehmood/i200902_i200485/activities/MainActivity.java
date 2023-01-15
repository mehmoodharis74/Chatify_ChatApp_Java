package com.harismehmood.i200902_i200485.activities;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.ChatModel;
import com.harismehmood.i200902_i200485.User.UserModel;
import com.harismehmood.i200902_i200485.adapters.MainChatRecyclerAdapter;
import com.harismehmood.i200902_i200485.listeners.MainConversionListener;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements MainConversionListener {
    RecyclerView MainChatsRecyclerView ;
    MainChatRecyclerAdapter MainChatAdapter;
    List<ChatModel> conversation;
    PreferencesManager sharedPreferences;
    ImageButton signOutButton, addChatButton;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = new PreferencesManager(this);

        init();
        loadUserDetails();
        getToken();
        listenConversation();

        signOutButton = findViewById(R.id.signOutAppBarButton);
        addChatButton = findViewById(R.id.mainSearchChatButton);



        //add chat button
        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchChatActivity.class);
                startActivity(intent);
            }
        });
        //exit app
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutFunction();
            }
        });
    }
    private void init(){
        //createRecyclerView();
        MainChatsRecyclerView = findViewById(R.id.mainChatsRecyclerView);
        conversation = new ArrayList<>();
        MainChatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainChatAdapter = new MainChatRecyclerAdapter(this,conversation);
        MainChatsRecyclerView.setAdapter(MainChatAdapter);
        database = FirebaseFirestore.getInstance();

    }
    private void listenConversation(){
        //sharedPreferences = new PreferencesManager(this);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,sharedPreferences.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,sharedPreferences.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
    }


    private final EventListener<QuerySnapshot> eventListener= ((value, error) -> {

        if (error != null) {
            Toast.makeText(this, "Error while loading!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED ) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatModel chatModel = new ChatModel();
                    chatModel.senderId = senderId;
                    chatModel.receiverId = receiverId;
                    if (sharedPreferences.getString(Constants.USER_ID).equals(senderId)) {
                        chatModel.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        chatModel.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatModel.conversationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                    } else {
                        chatModel.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        chatModel.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatModel.conversationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                    }
                    chatModel.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatModel.timestamp = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversation.add(chatModel);
                }
                else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for(int i=0; i<conversation.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversation.get(i).senderId.equals(senderId) && conversation.get(i).receiverId.equals(receiverId)){
                            conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversation.get(i).timestamp = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
                // arrayContacts.add(chatModel);
            }
            Collections.sort(conversation, (o1, o2) -> o2.timestamp.compareTo(o1.timestamp));
            MainChatAdapter.notifyDataSetChanged();
            MainChatsRecyclerView.smoothScrollToPosition(0);

        }
        // adapter.notifyDataSetChanged();
    });

    protected void loadUserDetails(){
        RoundedImageView profileImage = findViewById(R.id.mainAppBarProfileImage);
        //load user details
        sharedPreferences = new PreferencesManager(this);
        profileImage.setImageBitmap(getUserBitmapImage(sharedPreferences.getString(Constants.USER_IMG)));


    }
    protected void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    protected  void updateToken(String token){
       sharedPreferences.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.USERS_KEY_COLLECTIONS).document(sharedPreferences.getString(Constants.USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(aVoid -> {

                  //  Toast.makeText(this, "Update Token Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update Token Failed", Toast.LENGTH_SHORT).show();
                });
    }
    protected void signOutFunction(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.USERS_KEY_COLLECTIONS).document(sharedPreferences.getString(Constants.USER_ID));
        HashMap <String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    sharedPreferences.clear();
                    startActivity(new Intent(this, main_login_signup_activity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update Token Failed", Toast.LENGTH_SHORT).show();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onConversionClicked(UserModel user) {
        Intent intent = new Intent(getApplicationContext(), userMainChat_activity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
    private Bitmap getUserBitmapImage(String encodedImage){
        byte [] encodeByte= Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}