package com.tphien.midproject1412171;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tphien.midproject1412171.Modal.Restaurant;

import java.util.Objects;

public class RestaurantProfile extends AppCompatActivity {

    private Restaurant restaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        Bundle bundle = this.getIntent().getExtras();

        if(bundle !=null)
        {

            restaurant = (Restaurant) bundle.getSerializable("restaurant_info");
            ((TextView)findViewById(R.id.tvRating)).setText(String.format("Rating: %d", restaurant.getRating()));
            ((TextView)findViewById(R.id.user_profile_name)).setText(restaurant.getName());
            ((TextView)findViewById(R.id.tvEmail)).setText(restaurant.getEmail());
            ((TextView)findViewById(R.id.tvNumberPhone)).setText(restaurant.getPhoneNumber());
            ((TextView)findViewById(R.id.tvAddress)).setText(restaurant.getAddress());
            ((TextView)findViewById(R.id.tvWebsite)).setText(checkUrl(restaurant.getLinkWebsite()));
            ((TextView)findViewById(R.id.tvReview)).setText(String.format("Review: %s", restaurant.getReview()));
        }
    }

    public void onClickCallBut(View view) {
        Uri number = Uri.parse("tel: " + restaurant.getPhoneNumber());
        Intent intent = new Intent(Intent.ACTION_CALL, number);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        RestaurantProfile.this.startActivity(intent);
    }

    public void onClickSmsBut(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("sms_body", "Hi " + restaurant.getName());
        intent.putExtra("address", restaurant.getPhoneNumber());

        RestaurantProfile.this.startActivity(intent);
    }

    public void onClickEmailBut(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        intent.putExtra(Intent.EXTRA_TEXT,"body");

        RestaurantProfile.this.startActivity(Intent.createChooser(intent, "Send email to " + restaurant.getName()));
    }

    public void onClickMap(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = checkUrl(restaurant.getUrl());
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private String checkUrl(String url){
        if (Objects.equals(url, "No info"))
            return url;

        if (url.startsWith("http://www.") || url.startsWith("https://www."))
            return url;

        if (url.startsWith("www.")) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
        } else {
            url = "http://www." + url;
        }
        return url;
    }
}
