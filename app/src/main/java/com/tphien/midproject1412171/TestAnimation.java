package com.tphien.midproject1412171;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class TestAnimation extends AppCompatActivity {
    AnimationView anim_view;
    AnimationView anim_view1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_animation);

        // Get AnimationView reference see animation_main.xml
        anim_view = (AnimationView) this.findViewById(R.id.anim_view);
        anim_view.loadAnimation("spark", 18,0,0);

        // Get AnimationView reference see animation_main.xml
        anim_view1 = (AnimationView) this.findViewById(R.id.anim_view1);

        // You can call this method inside thread
        anim_view1.loadAnimation("spark", 18,0,0);

        Button play_button = (Button) this.findViewById(R.id.play_button);



    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        // After activity close animation will close
        anim_view=null;
        anim_view1=null;

    }
}
