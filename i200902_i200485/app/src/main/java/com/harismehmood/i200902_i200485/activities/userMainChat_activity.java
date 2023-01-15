package com.harismehmood.i200902_i200485.activities;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.ChatModel;
import com.harismehmood.i200902_i200485.User.UserModel;
import com.harismehmood.i200902_i200485.adapters.ChatAdapter;
import com.harismehmood.i200902_i200485.network.ApiClient;
import com.harismehmood.i200902_i200485.network.ApiService;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userMainChat_activity extends BaseActivity{
ImageButton backArrowButton;
UserModel receiverUser;
List<ChatModel> chatMessageList;
FirebaseFirestore database;
PreferencesManager preferences;
ChatAdapter chatAdapter;
Bitmap receiveUserImage;
ImageButton sendMessageButton ,addImageButton;
String encodedImage = null;
RecyclerView chatRecyclerView;
private String conversationId = null;
private Boolean isReceiverAvailable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_chat);
        backArrowButton = findViewById(R.id.userMainChatActivityBackArrowBtn);
        preferences = new PreferencesManager(this);
        backArrowButton.setOnClickListener(v -> onBackPressed());
        addImageButton = findViewById(R.id.userMainChatAddImageBtn);
        EditText messageEditText = findViewById(R.id.userMainChatActivityTypeMessageInputText);

        loadReceiverDetails();
        init();
        listenMessages();

        //send button click listener
        sendMessageButton = findViewById(R.id.userMainChatActivitySendMessageButton);
        sendMessageButton.setOnClickListener
                (v -> {sendMessage(encodedImage,messageEditText.getText().toString());
                            messageEditText.setText(null);});

        //add image button click listener
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }



    private void init(){
        chatMessageList = new ArrayList<>();
        //setting chatAdapter
        chatAdapter = new ChatAdapter(this, chatMessageList,receiveUserImage, preferences.getString(Constants.USER_ID));
        RecyclerView chatsRecyclerView= findViewById(R.id.userMainChatActivityRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chatsRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        chatsRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

    }
    private  void sendMessage(String encodedImage, String messageEditText){
      if(messageEditText.trim().isEmpty())
          return;
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferences.getString(Constants.USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.userId);
        message.put(Constants.KEY_CHAT_ROOM_MESSAGE, messageEditText);
        if(encodedImage != null){
            message.put(Constants.KEY_CHAT_ROOM_MESSAGE_IMAGE, encodedImage);
        }
        else{
            message.put(Constants.KEY_CHAT_ROOM_MESSAGE_IMAGE, null);
        }
        message.put(Constants.KEY_TIMESTAMP, new Date());

        database.collection(Constants.KEY_CHAT_ROOMS).add(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            } else {
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
            }
        });
        if(conversationId != null){
            if(encodedImage != null){
                //update conversation and send string named sentImageMessage
                updateConversation("Image");
            }
            else{
                updateConversation(messageEditText.trim());
            }
        }
        else{
            HashMap<String, Object> conversation = new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID, preferences.getString(Constants.USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME, preferences.getString(Constants.USER_NAME));
            conversation.put(Constants.KEY_SENDER_IMAGE, preferences.getString(Constants.USER_IMG));
            conversation.put(Constants.KEY_RECEIVER_ID, receiverUser.userId);
            conversation.put(Constants.KEY_RECEIVER_NAME, receiverUser.userName);
            conversation.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.userImage);
            if(encodedImage != null){
                conversation.put(Constants.KEY_LAST_MESSAGE, "Image");
            }
            else{
                conversation.put(Constants.KEY_LAST_MESSAGE, messageEditText.trim());
            }
           // conversation.put(Constants.KEY_LAST_MESSAGE, messageEditText.getText().toString().trim());
            conversation.put(Constants.KEY_TIMESTAMP, new Date());
            addConversation(conversation);
        }
        if(!isReceiverAvailable){
            try{
             JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);
                JSONObject data = new JSONObject();
                data.put(Constants.USER_ID, preferences.getString(Constants.USER_ID));
                data.put(Constants.USER_NAME, preferences.getString(Constants.USER_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferences.getString(Constants.KEY_FCM_TOKEN));
                if(!messageEditText.trim().equals("")){
                    data.put(Constants.KEY_CHAT_ROOM_MESSAGE, messageEditText.trim());
                }
                else if(encodedImage != null){
                    data.put(Constants.KEY_CHAT_ROOM_MESSAGE, "Image");
                }


                JSONObject body = new JSONObject();
                body.put(Constants.KEY_REMOTE_MSG_DATA, data);
                body.put(Constants.KEY_REMOTE_MSG_REGISTRATION_IDS, tokens);
                sendNotification(body.toString());
            }
            catch (Exception e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }



    }
    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if(response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray resultsArray = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) resultsArray.get(0);
                                Toast.makeText(userMainChat_activity.this, "Error: " + error.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(userMainChat_activity.this,"Error: " + response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(userMainChat_activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void listenAvailabilityOfReceiver(){

        database.collection(Constants.USERS_KEY_COLLECTIONS).document(
                receiverUser.userId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        return;
                    }
                    if(value != null){
                        if(value.getLong(Constants.KEY_AVAILABILITY) != null){
                            int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)).intValue();
                            isReceiverAvailable = availability == 1;
                        }
                        receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                        if(receiveUserImage == null){
                            receiverUser.userImage = value.getString(Constants.USER_IMG);
                            chatAdapter.setReceivedProfileImage(getUserBitmapImage(receiverUser.userImage));
                            chatAdapter.notifyItemRangeChanged(0, chatMessageList.size());
                            loadReceiverDetails();
                        }
                    }
                    if(isReceiverAvailable){
                        //receiver is available
                        TextView onlineStatus = findViewById(R.id.userMainChatActivityOnlineStatus);
                        onlineStatus.setText("online");
                    }
                    else{
                        //receiver is not available
                        TextView onlineStatus = findViewById(R.id.userMainChatActivityOnlineStatus);
                        onlineStatus.setText("offline");
                    }

                });
    }
    private void listenMessages(){
        database.collection(Constants.KEY_CHAT_ROOMS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferences.getString(Constants.USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.userId)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_CHAT_ROOMS)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.userId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferences.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        chatRecyclerView = findViewById(R.id.userMainChatActivityRecyclerView);
        if (error != null) {
            return;
        } else if (value != null) {
            int size = chatMessageList.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatModel chatModel = new ChatModel();

                    chatModel.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatModel.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatModel.message = documentChange.getDocument().getString(Constants.KEY_CHAT_ROOM_MESSAGE);
                    chatModel.messageTime = getReadableDate(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatModel.timestamp = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatModel.imageMessage = documentChange.getDocument().getString(Constants.KEY_CHAT_ROOM_MESSAGE_IMAGE);
                    chatMessageList.add(chatModel);
                }
            }
            Collections.sort(chatMessageList, (o1, o2) -> o1.timestamp.compareTo(o2.timestamp));
            if (size == 0)
                chatAdapter.notifyDataSetChanged();
            else {
                chatAdapter.notifyItemRangeInserted(size, chatMessageList.size());
                chatRecyclerView.smoothScrollToPosition(chatMessageList.size());
            }
        }
       // database = FirebaseFirestore.getInstance();
        if(conversationId == null ){
            checkForConversation();
        }

    };
    private void loadReceiverDetails(){
        TextView receiverName = findViewById(R.id.userMainChatActivityProfileName);
        ImageView receiverProfileImage = findViewById(R.id.userMainChatActivityProfileImage);
        receiverUser = (UserModel) getIntent().getSerializableExtra("users");
        receiverName.setText(receiverUser.userName);
        receiveUserImage = getUserBitmapImage(receiverUser.userImage);
        receiverProfileImage.setImageBitmap(receiveUserImage);
    }
    private Bitmap getUserBitmapImage(String encodedImage){
        if(encodedImage != null){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }else
            return null;

    }
    private String getReadableDate(Date date){
            return  new SimpleDateFormat("MM/dd/yy - hh:mm a", Locale.getDefault()).format(date);
    }
    private void addConversation(HashMap<String , Object> conversation){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId()

                );
    }
    private void updateConversation(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());
    }
    private void checkForConversation(){
        if(chatMessageList.size()!= 0){
            checkOnConversationRemotely(preferences.getString(Constants.USER_ID), receiverUser.userId);
            checkOnConversationRemotely(receiverUser.userId, preferences.getString(Constants.USER_ID));
        }
        else{
            //delete that
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkOnConversationRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);

    }
    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult()!= null && task.getResult().getDocuments().size() > 0 ) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
    public String setEncodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){

            // imageView.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //imageView.setImageBitmap(bitmap);
                encodedImage = setEncodedImage(bitmap);
                Intent intent = new Intent(this, imagePreviewActivity.class);
                intent.putExtra("image", uri.toString());
                //start activity for result and get the image back
                startActivityForResult(intent, 2);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                encodedImage = null;
            }
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                sendMessage(encodedImage,result);
            }

        }
        else
            encodedImage = null;
    }
    @Override
    protected void onPause() {
        super.onPause();
        listenAvailabilityOfReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
       listenAvailabilityOfReceiver();
    }
}