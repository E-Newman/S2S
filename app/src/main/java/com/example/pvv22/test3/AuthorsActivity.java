package com.example.pvv22.test3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class AuthorsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
    }
    public void toMainActivity(View view){
        startActivity(new Intent(AuthorsActivity.this, MainActivity.class));
    }
}
