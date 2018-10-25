/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.pvv22.test3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tw1 = (TextView) findViewById(R.id.textView2);
        netcode nc = new netcode();
        tw1.setText(nc.getAddr());
    }

    public void netButtonClick(View view) throws Exception {
        netcode nc = new netcode();
        nc.connect();
        TextView tw = (TextView) findViewById(R.id.textView);
        while (true){
            if (nc.mesg != null)
                tw.setText(nc.mesg);
        }

    }
}
