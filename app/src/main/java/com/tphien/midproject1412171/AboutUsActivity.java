package com.tphien.midproject1412171;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tphien.midproject1412171.tool.CircleTransform;

public class AboutUsActivity extends AppCompatActivity {
    private static final String authorAvatar = "android.resource://com.tphien.midproject1412171/drawable/author";
    private static final String authorAvatar2 = "android.resource://com.tphien.midproject1412171/drawable/author_han";
    private ImageView imageView;
    private ImageView imageView2;
    private RelativeLayout relativeLayout;
    private TextView tvAuthor;
    private TextView tvDesc;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeAvatar);
        imageView = (ImageView)findViewById(R.id.imgViewAboutUs);
        imageView2 = (ImageView)findViewById(R.id.imgViewAboutUs2);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvDesc = (TextView)findViewById(R.id.tvDescription);
        webView = (WebView)findViewById(R.id.webviewSkill);

        Glide.with(this).load(authorAvatar)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        Glide.with(this).load(authorAvatar2)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView2);

        tvAuthor.setText(R.string.author_Info);
        tvDesc.setText(R.string.aboutus_description);

        String summary = "<html><body>Our project's skills: <br/>" +
                "<ul><li>Using ListView, custom UI listview, load more data when scroll</li>" +
                "<li>Using Google map, clustering marker, custom UI marker</li>" +
                "<li>Using VR</li>" +
                "<li>Using API of Microsoft ProjectOxford</li>" +
                "<li>Signing with Google Account, auth with firebase</li>" +
                "<li>Skill 5</li>" +
                "<li>Skill 6</li>" +
                "<li>Skill 7</li>" +
                "<li>Skill 8</li>" +
                "<li>Skill 9</li>" +
                "<li>Skill 10</li>" +
                "<li>Skill 11</li>" +
                "<li>Skill 12</li></ul>"+
                "</body></html>";
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(summary, "text/html", null);
    }
    private void setViewByOrientation() {
        int orientation = getScreenOrientation();

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            changeUILandscape();
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            changeUIPortrait();
    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setViewByOrientation();
    }

    private void changeUIPortrait() {

        GridLayout.LayoutParams paramsAvatar =  new GridLayout.LayoutParams();
        paramsAvatar.columnSpec = GridLayout.spec(0,2);
        paramsAvatar.rowSpec = GridLayout.spec(0);
        paramsAvatar.setGravity(Gravity.CENTER);
        relativeLayout.setLayoutParams(paramsAvatar);

        GridLayout.LayoutParams paramsAuthor =  new GridLayout.LayoutParams();
        paramsAuthor.columnSpec = GridLayout.spec(0,2);
        paramsAuthor.rowSpec = GridLayout.spec(1);
        tvAuthor.setLayoutParams(paramsAuthor);

        GridLayout.LayoutParams paramsDesc =  new GridLayout.LayoutParams();
        paramsDesc.columnSpec = GridLayout.spec(0,2);
        paramsDesc.rowSpec = GridLayout.spec(2);
        paramsDesc.setGravity(Gravity.FILL);
        paramsDesc.width = this.getWindowManager().getDefaultDisplay().getWidth() >> 1;
        tvDesc.setLayoutParams(paramsDesc);

        GridLayout.LayoutParams paramsSkills =  new GridLayout.LayoutParams();
        paramsSkills.columnSpec = GridLayout.spec(0,2);
        paramsSkills.rowSpec = GridLayout.spec(3);
        webView.setLayoutParams(paramsSkills);
    }

    private void changeUILandscape() {
        GridLayout.LayoutParams paramsAvatar =  new GridLayout.LayoutParams();
        paramsAvatar.columnSpec = GridLayout.spec(0);
        paramsAvatar.rowSpec = GridLayout.spec(0);
        paramsAvatar.setGravity(Gravity.CENTER);
        relativeLayout.setLayoutParams(paramsAvatar);

        GridLayout.LayoutParams paramsAuthor =  new GridLayout.LayoutParams();
        paramsAuthor.columnSpec = GridLayout.spec(0);
        paramsAuthor.rowSpec = GridLayout.spec(1);
        tvAuthor.setLayoutParams(paramsAuthor);

        GridLayout.LayoutParams paramsDesc =  new GridLayout.LayoutParams();
        paramsDesc.columnSpec = GridLayout.spec(0);
        paramsDesc.rowSpec = GridLayout.spec(2);
        paramsDesc.setGravity(Gravity.FILL);
        paramsDesc.width = this.getWindowManager().getDefaultDisplay().getWidth() >> 1;
        tvDesc.setLayoutParams(paramsDesc);

        GridLayout.LayoutParams paramsSkills =  new GridLayout.LayoutParams();
        paramsSkills.columnSpec = GridLayout.spec(1);
        paramsSkills.rowSpec = GridLayout.spec(0,4);
        webView.setLayoutParams(paramsSkills);
    }
}
