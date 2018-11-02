package com.petrsu.se.s2s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class EnterIP_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ip_);

        /* Prohibit to enter non-int values */
        int[] fields = {R.id.EditIP1, R.id.EditIP2, R.id.EditIP3, R.id.EditIP4};

        for (int i = 0; i < fields.length; i++)
        {
            final EditText curField = (EditText) findViewById(fields[i]);
            curField.setTransformationMethod(null);
        }
    }

    public void startT(View view) {
        String addr = "";

        /* First check the proper field filling */
        int[] fields = {R.id.EditIP1, R.id.EditIP2, R.id.EditIP3, R.id.EditIP4};
        final EditText warn = (EditText) findViewById(R.id.textWarning);
        warn.setVisibility(View.INVISIBLE);

        for (int i = 0; i < fields.length; i++) {
            final EditText curField = (EditText) findViewById(fields[i]);
            if (curField.getText().toString().isEmpty()) {
                curField.requestFocus();
                warn.setVisibility(View.VISIBLE);
                warn.setText("Пожалуйста, укажите IP-адрес полностью");
                return;
            }

            int constraint = Integer.parseInt(curField.getText().toString()); // simpler?

            if (constraint > 255) {
                curField.selectAll();
                curField.requestFocus();
                warn.setVisibility(View.VISIBLE);
                warn.setText("Значение части IP-адреса не может быть больше 255");
                return;
            }
            if (i != fields.length - 1)
                addr += curField.getText().toString() + ".";
            else
                addr += curField.getText().toString();
        }

        Intent intent = new Intent(EnterIP_Activity.this, StartTransmissionActivity.class);
        intent.putExtra("addr", addr);
        startActivity(intent);
    }

    public void goBack(View view) {
        Intent intent = new Intent(EnterIP_Activity.this, ConnectModeActivity.class);
        startActivity(intent);
    }

    public void goToMainMenu(View view) {
        Intent intent = new Intent(EnterIP_Activity.this, MainMenu.class);
        startActivity(intent);
    }
}
