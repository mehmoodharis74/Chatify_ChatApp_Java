package com.harismehmood.i200902_i200485.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    PreferencesManager sharedPreferences;
@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = new PreferencesManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String id = sharedPreferences.getString(Constants.USER_ID);
        documentReference = database.collection(Constants.KEY_USER)
                .document(id);

    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY, 0);
//        sharedPreferences = new PreferencesManager(getApplicationContext());
//        sharedPreferences.putBoolean("available", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_AVAILABILITY, 1);
//

    }
}
