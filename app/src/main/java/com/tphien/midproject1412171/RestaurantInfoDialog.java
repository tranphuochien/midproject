package com.tphien.midproject1412171;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.ar.FindPath;
import com.tphien.midproject1412171.databinding.RestaurantProfileBinding;

import java.io.File;
import java.util.Objects;

public class RestaurantInfoDialog extends DialogFragment {
    public Restaurant restaurant;
    private RestaurantProfileBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("dialog", "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.restaurant_profile, container, false);
        View view = binding.getRoot();
        binding.setRestaurant(restaurant);

        ((TextView)view.findViewById(R.id.tvRating)).setText(String.format("Rating: %d", restaurant.getRating()));
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("dialog", "onCreateDialog");
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.restaurant_profile);


        //dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView header = (ImageView) dialog.findViewById(R.id.header_cover_image);
        ImageView favourite = (ImageView) dialog.findViewById(R.id.add_friend);


        Glide.with(getActivity().getApplicationContext())
                .load(restaurant.getStringResourceCurAvatar())
                .crossFade()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(header);

        boolean check = Global.checkFavourite(restaurant);

        if (check){
            favourite.setTag("true");
            Glide.with(getActivity().getApplicationContext())
                    .load("android.resource://com.tphien.midproject1412171/drawable/heart_true")
                    .crossFade()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(favourite);
        }
        else{
            favourite.setTag("false");
            Glide.with(getActivity().getApplicationContext())
                    .load("android.resource://com.tphien.midproject1412171/drawable/heart_false")
                    .crossFade()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(favourite);
        }

        ((ImageView)dialog.findViewById(R.id.btnShare)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("jj");

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,restaurant.getLinkWebsite());
                startActivity(intent);
            }
        });

        ((ImageView)dialog.findViewById(R.id.CallBut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel: " + restaurant.getPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_CALL, number);

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });

        ((ImageView)dialog.findViewById(R.id.cancelBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        ((ImageView)dialog.findViewById(R.id.SmsBut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body", "Hi " + restaurant.getName());
                intent.putExtra("address", restaurant.getPhoneNumber());
                startActivity(intent);
            }
        });

        ((ImageView)dialog.findViewById(R.id.EmailBut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT,"body");

                startActivity(Intent.createChooser(intent, "Send email to " + restaurant.getName()));
            }
        });

        ((ImageView)dialog.findViewById(R.id.add_friend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)v.findViewById(R.id.add_friend);

                String tag = String.valueOf(imageView.getTag());
                if (tag.equals("false")) {
                    imageView.setTag("true");
                    Glide.with(getActivity().getApplicationContext().getApplicationContext())
                            .load("android.resource://com.tphien.midproject1412171/drawable/heart_true")
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((ImageView) v);
                    Global.addNewFavorite(restaurant);

                } else {
                    imageView.setTag("false");
                    Glide.with(getActivity().getApplicationContext().getApplicationContext())
                            .load("android.resource://com.tphien.midproject1412171/drawable/heart_false")
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((ImageView) v);
                    Global.removeFavorite(restaurant);
                }
            }
        });

        ((ImageView)dialog.findViewById(R.id.btnMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = checkUrl(restaurant.getUrl());
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        ((ImageView)dialog.findViewById(R.id.DirectionBut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), FindPath.class);
                intent.putExtra("beginLat",Global.getCurPosition().latitude);
                intent.putExtra("beginLon",Global.getCurPosition().longitude);
                intent.putExtra("endLat",restaurant.getLat());
                intent.putExtra("endLon", restaurant.getLon());
                intent.putExtra("distination",restaurant.getName());
                startActivity(intent);
            }
        });

        return dialog;
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