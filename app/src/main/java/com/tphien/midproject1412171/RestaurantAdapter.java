/**
 * Created by tranphuochien on 4/29/2017.
 */

package com.tphien.midproject1412171;

import android.app.FragmentManager;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.tool.CircleTransform;

import java.util.ArrayList;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {
    private final Context context;
    private static int position;
    private FragmentManager fragmentManager;

    public RestaurantAdapter(@NonNull Context context, @NonNull ArrayList<Restaurant> restaurants, FragmentManager fragmentManager) {
        super(context, 0, restaurants);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvAddress;
        ImageView imgViewCircle;
        ImageButton btnDetail;
    }
    public void selectedItem(int position)
    {
        RestaurantAdapter.position = position; //position must be a global variable
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final Restaurant restaurant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder vh; // view lookup cache stored in tag

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            vh = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_restaurants_item, parent, false);

            vh.tvName = (TextView) convertView.findViewById(R.id.tvName);
            vh.tvAddress = (TextView) convertView.findViewById(R.id.tvPhoneNumber);
            vh.imgViewCircle = (ImageView) convertView.findViewById(R.id.imageViewAvatar);
            vh.btnDetail = (ImageButton) convertView.findViewById(R.id.detailBut);

            vh.btnDetail.setBackgroundColor(Color.TRANSPARENT);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(vh);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextColor((TextView)view,"#EF4836", "#446cb3" ,2000,100);
            }
        });

        vh.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextColor((TextView)view,"#EF4836", "#6C7A89" ,2000,100);
            }
        });

        vh.imgViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] idAvatars = getItem(position).getIdAvatars();
                int curPos = getItem(position).getCurPosAvatar();

                Log.d("clickcircle", "pre: " + String.valueOf(curPos));
                int idAvatar;
                int n = idAvatars.length;

                if(curPos == (n-1)) {
                    getItem(position).setCurPosAvatar(0);
                    idAvatar = idAvatars[0];
                } else {
                    curPos += 1;
                    getItem(position).setCurPosAvatar(curPos);
                    idAvatar = idAvatars[curPos];
                }
                Log.d("clickcircle", "last: " + String.valueOf(curPos));

                //((ImageView)view).setImageResource(idAvatar);
                Glide.with(context.getApplicationContext()).load(getItem(position).getStringResourceCurAvatar())
                        .thumbnail(0.1f)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .bitmapTransform(new CircleTransform(context.getApplicationContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into((ImageView) view);
            }
        });

        vh.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantInfoDialog fragment1 = new RestaurantInfoDialog(restaurant);
                fragment1.show(fragmentManager, "");
            }
        });

        vh.tvName.setText(restaurant.getName());
        vh.tvName.setTextColor(Color.parseColor("#446cb3"));
        vh.tvAddress.setText(restaurant.getAddress());
        vh.tvName.setTextColor(Color.parseColor("#6C7A89"));

        Glide.with(context.getApplicationContext()).load(getItem(position).getStringResourceCurAvatar())
                .thumbnail(0.1f)
                .fitCenter()
                .skipMemoryCache(true)
                .bitmapTransform(new CircleTransform(context.getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(vh.imgViewCircle);

        return convertView;
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
