package com.tphien.midproject1412171.ar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.tphien.midproject1412171.R;


public class DetailLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_location);
        double lat = 0.0d;
        double lon = 0.0d;
        String name ="";
        String img = "";

        lat = getIntent().getExtras().getDouble("lat");
        lon = getIntent().getExtras().getDouble("lon");
        name = getIntent().getExtras().getString("name");
        img = getIntent().getExtras().getString("img");


        ((TextView)findViewById(R.id.tvName)).setText(name);
        ((TextView)findViewById(R.id.tvLat)).setText(String.valueOf(lat));
        ((TextView)findViewById(R.id.tvLon)).setText(String.valueOf(lon));
        ((TextView)findViewById(R.id.tvImage)).setText(img);
    }
}
