package org.stendhalgame.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OpenAppByUrl extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // String action = intent.getAction();
        Uri data = intent.getData();
        Logger.debug("URL: " + data);

     }

}
