package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectModeActivity extends AppCompatActivity implements View.OnClickListener {
    private IntentIntegrator qrScan;
    private Button buttonGetIPQR;
    String IPADDRESS_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

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
                Toast.makeText(this, "QR-соде пустой", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                Pattern ippattern = Pattern.compile(IPADDRESS_PATTERN);
                Matcher ipm = ippattern.matcher(result.getContents());
                if (ipm.matches()) {
                    Toast.makeText(this, "Просканирован IP-адрес: " + result.getContents(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ConnectModeActivity.this, StartTransmissionActivity.class);
                    intent.putExtra("addr", result.getContents());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Некорректный QR-код. Он содержит: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
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
