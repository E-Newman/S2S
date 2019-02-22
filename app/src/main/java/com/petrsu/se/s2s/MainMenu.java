package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class MainMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        startService(new Intent(this, ScreenRecorder.class));
    }

    public void goToConnectMode(View view) {
        Intent intent = new Intent(MainMenu.this, ConnectModeActivity.class);
        startActivity(intent);
    }

    public void goToAuthors(View view) {
        Intent intent = new Intent(MainMenu.this, AuthorsActivity.class);
        startActivity(intent);
    }

    public void goToManual(View view) {
        Intent intent = new Intent(MainMenu.this, ManualActivity.class);
        startActivity(intent);
    }
}
