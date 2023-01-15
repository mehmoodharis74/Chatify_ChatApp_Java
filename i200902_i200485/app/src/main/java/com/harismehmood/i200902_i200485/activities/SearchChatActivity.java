package com.harismehmood.i200902_i200485.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.User.UserModel;
import com.harismehmood.i200902_i200485.adapters.SearchChatRecyclerAdapter;
import com.harismehmood.i200902_i200485.listeners.UserListener;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class SearchChatActivity extends BaseActivity implements UserListener {
PreferencesManager sharedPreferences;
ImageButton bachArrowButton;
ProgressBar progressBar;
//DocumentReference availabilityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chat);
        sharedPreferences = new PreferencesManager(this);
      //  availabilityRef = FirebaseFirestore.getInstance().collection(Constants.KEY_USER).document(sharedPreferences.getString(Constants.USER_ID));

        bachArrowButton = findViewById(R.id.mainAppBarSearchChatBackArrowButton);
        bachArrowButton.setOnClickListener(v -> {
            onBackPressed();
        });

        getUserData();


    }
    protected void getUserData(){
        isLoading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USERS_KEY_COLLECTIONS)
                .get()
                .addOnCompleteListener(task -> {
                    String curr_userId = sharedPreferences.getString(Constants.USER_ID);
                  if(task.isSuccessful() && task!= null){
                      List<UserModel> users = new ArrayList<>();
                      for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                          if(curr_userId.equals(queryDocumentSnapshot.getId()))
                              continue;

                              UserModel contact= new UserModel();
                              contact.userName=(queryDocumentSnapshot.getString(Constants.USER_NAME));
                              contact.userImage=(queryDocumentSnapshot.getString(Constants.USER_IMG));
                              contact.token=(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                              contact.userId=(queryDocumentSnapshot.getId());
                              users.add(contact);

                      }
                      if(users.size()>0){
                          RecyclerView chatsRecyclerView= findViewById(R.id.searchChatRecyclerView);
                          chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                          SearchChatRecyclerAdapter adapter= new SearchChatRecyclerAdapter(this,users);
                          chatsRecyclerView.setAdapter(adapter);
                            isLoading(false);
                      }
                      else {
                          Toast.makeText(this, "No User Found", Toast.LENGTH_SHORT).show();
                        isLoading(false);
                      }
                  }
                  else {
                      Toast.makeText(this, "Unable To Fetch Data From Server", Toast.LENGTH_SHORT).show();
                      isLoading(false);
                  }});
    }
    public void isLoading(Boolean isLoading){
        LottieAnimationView progressBar = findViewById(R.id.loader);
        if(isLoading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onUserClicked(UserModel userModel) {
        Intent intent = new Intent(getApplicationContext(), userMainChat_activity.class);
        intent.putExtra(Constants.KEY_USER, userModel);
        startActivity(intent);
        finish();
    }
}