package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class StartTransmissionActivity extends AppCompatActivity {
    public byte [] addr = new byte[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transmission);
        Intent intent = getIntent();
        addr = intent.getByteArrayExtra("addr");
    }

    public void goBack(View view) { // TODO: check the mode and load the proper screen
        Intent intent = new Intent(StartTransmissionActivity.this, EnterIP_Activity.class);
        startActivity(intent);
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(StartTransmissionActivity.this, MainMenu.class);
        startActivity(intent);
    }
}
