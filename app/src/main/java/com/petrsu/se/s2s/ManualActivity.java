package com.petrsu.se.s2s;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class ManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        TextView man = (TextView) findViewById(R.id.textManualBody);
        man.setMovementMethod(new ScrollingMovementMethod());
    }

    public void goToMain(View view) {
        Intent intent = new Intent(ManualActivity.this, MainMenu.class);
        startActivity(intent);
    }
}
