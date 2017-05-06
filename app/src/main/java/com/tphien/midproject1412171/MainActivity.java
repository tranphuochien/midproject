package com.tphien.midproject1412171;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tphien.midproject1412171.ar.ARView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickGetStarted(View view) {
        startActivity(new Intent(MainActivity.this, MainView.class));
    }
}
