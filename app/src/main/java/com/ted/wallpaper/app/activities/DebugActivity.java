package com.ted.wallpaper.app.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import com.ted.wallpaper.app.R;

/**
 * Created by Ted on 2015/4/17.
 */
public class DebugActivity extends FragmentActivity implements View.OnClickListener{

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Button button = (Button)findViewById(R.id.imageButton);
        button.setSelected(true);
        button.setOnClickListener(this);
        findViewById(R.id.imageButton2).setOnClickListener(this);
    }
}
