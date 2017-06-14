package com.tphien.midproject1412171.tool;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.RestaurantInfoDialog;
import com.tphien.midproject1412171.R;


/**
 * Created by theriddle on 5/7/17.
 */

public class LocationAdapter3d extends ArrayAdapter {
    private Context mContext;
    TextView tvName;
    TextView tvAddress;
    ImageView imgViewCircle;
    ImageButton btnDetail;
    private FragmentManager fragmentManager;

    public LocationAdapter3d(Context context, FragmentManager fragmentManager) {
        super(context, 0);
        this.mContext = context;
        this.fragmentManager = fragmentManager;
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

                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inScaled = true;
                    option.inSampleSize = 8;

                    Bitmap myBmpTemp = BitmapFactory.decodeResource(mContext.getResources(), ((Restaurant) getItem(position)).getCurAvatar(), option);
                    ((ImageView)view).setImageBitmap(myBmpTemp);

                    //((ImageView)view).setImageResource(idAvatar);
                    /*Glide.with(mContext.getApplicationContext()).load(((Restaurant)getItem(position)).getStringResourceCurAvatar())
                            .thumbnail(0.1f)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .placeholder(((Restaurant) getItem(position)).getCurAvatar())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((ImageView) view);*/
                }
            });

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RestaurantInfoDialog fragment1 = new RestaurantInfoDialog();
                    fragment1.restaurant = (Restaurant) getItem(position);
                    fragment1.show(fragmentManager, "");
                }
            });

            tvName.setText(myLocation.getName());
            tvName.setTextColor(Color.parseColor("#446cb3"));
            tvAddress.setText(myLocation.getAddress());
            tvName.setTextColor(Color.parseColor("#6C7A89"));


            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inScaled = true;
            option.inSampleSize = 6 ;

            Bitmap myBmp = BitmapFactory.decodeResource(mContext.getResources(), ((Restaurant) getItem(position)).getCurAvatar(), option);
            imgViewCircle.setImageBitmap(myBmp);


            /*Glide.with(mContext.getApplicationContext()).load(((Restaurant)getItem(position)).getStringResourceCurAvatar())
                    .thumbnail(0.1f)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .placeholder(((Restaurant) getItem(position)).getCurAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgViewCircle);*/
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
