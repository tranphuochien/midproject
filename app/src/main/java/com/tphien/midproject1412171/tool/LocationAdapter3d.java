package com.tphien.midproject1412171.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.RestaurantProfile;

import java.io.Serializable;


/**
 * Created by theriddle on 5/7/17.
 */

public class LocationAdapter3d extends ArrayAdapter {
    private Context mContext;
    TextView tvName;
    TextView tvAddress;
    ImageView imgViewCircle;
    ImageButton btnDetail;

    public LocationAdapter3d(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
            view = layoutInflater.inflate(R.layout.list_restaurants_item_3d, null);
        }
        final Restaurant myLocation = (Restaurant) getItem(position);
        if (myLocation != null){
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvAddress = (TextView) view.findViewById(R.id.tvPhoneNumber);
            imgViewCircle = (ImageView) view.findViewById(R.id.imageViewAvatar);
            btnDetail = (ImageButton) view.findViewById(R.id.detailBut);

            btnDetail.setBackgroundColor(Color.TRANSPARENT);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeTextColor((TextView)view,"#EF4836", "#446cb3" ,2000,100);
                }
            });

            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeTextColor((TextView)view,"#EF4836", "#6C7A89" ,2000,100);
                }
            });

            imgViewCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int[] idAvatars = ((Restaurant)getItem(position)).getIdAvatars();
                    int curPos = ((Restaurant)getItem(position)).getCurPosAvatar();

                    Log.d("clickcircle", "pre: " + String.valueOf(curPos));
                    int idAvatar;
                    int n = idAvatars.length;

                    if(curPos == (n-1)) {
                        ((Restaurant)getItem(position)).setCurPosAvatar(0);
                        idAvatar = idAvatars[0];
                    } else {
                        curPos += 1;
                        ((Restaurant)getItem(position)).setCurPosAvatar(curPos);
                        idAvatar = idAvatars[curPos];
                    }
                    Log.d("clickcircle", "last: " + String.valueOf(curPos));

                    //((ImageView)view).setImageResource(idAvatar);
                    Glide.with(mContext.getApplicationContext()).load(((Restaurant)getItem(position)).getStringResourceCurAvatar())
                            .thumbnail(0.5f)
                            .centerCrop()
                            .placeholder(((Restaurant) getItem(position)).getCurAvatar())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((ImageView) view);
                }
            });

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RestaurantProfile.class );
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("restaurant_info", (Serializable) getItem(position));
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            tvName.setText(myLocation.getName());
            tvName.setTextColor(Color.parseColor("#446cb3"));
            tvAddress.setText(myLocation.getAddress());
            tvName.setTextColor(Color.parseColor("#6C7A89"));

            Glide.with(mContext.getApplicationContext()).load(((Restaurant)getItem(position)).getStringResourceCurAvatar())
                    .thumbnail(0.5f)
                    .centerCrop()
                    .placeholder(((Restaurant) getItem(position)).getCurAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgViewCircle);
        }


        return view;
    }

    private static void changeTextColor(final TextView textView, String sColor, String eColor,
                                        final long animDuration, final long animUnit){
        if (textView == null) return;

        int startColor = Color.parseColor(sColor);
        int endColor = Color.parseColor(eColor);

        final int startRed = Color.red(startColor);
        final int startBlue = Color.blue(startColor);
        final int startGreen = Color.green(startColor);

        final int endRed = Color.red(endColor);
        final int endBlue = Color.blue(endColor);
        final int endGreen = Color.green(endColor);

        new CountDownTimer(animDuration, animUnit){
            //animDuration is the time in ms over which to run the animation
            //animUnit is the time unit in ms, update color after each animUnit
            @Override
            public void onTick(long l) {
                int red = (int) (endRed + (l * (startRed - endRed) / animDuration));
                int blue = (int) (endBlue + (l * (startBlue - endBlue) / animDuration));
                int green = (int) (endGreen + (l * (startGreen - endGreen) / animDuration));

                //Log.d("Changing color", "Changing color to RGB" + red + ", " + green + ", " + blue);
                textView.setTextColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onFinish() {
                textView.setTextColor(Color.rgb(endRed, endGreen, endBlue));
            }
        }.start();
    }
}
