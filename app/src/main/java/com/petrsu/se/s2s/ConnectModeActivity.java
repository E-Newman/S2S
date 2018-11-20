package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectModeActivity extends AppCompatActivity implements View.OnClickListener {
    private IntentIntegrator qrScan;
    private Button buttonGetIPQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_mode);

        qrScan = new IntentIntegrator(this);
        buttonGetIPQR = (Button) findViewById(R.id.buttonGetIPQR);

        //attaching onclick listener
        buttonGetIPQR.setOnClickListener(this);
    }

    public void goToMain(View view) {
        Intent intent = new Intent(ConnectModeActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void goToIP(View view) {
        Intent intent = new Intent(ConnectModeActivity.this, EnterIP_Activity.class);
        startActivity(intent);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show(); // #TODO: resolve IP and send it to StartTransmission
            } else {
                //if qr contains data
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void onClick (View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
