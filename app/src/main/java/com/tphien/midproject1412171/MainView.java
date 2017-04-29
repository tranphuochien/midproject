package com.tphien.midproject1412171;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tphien.midproject1412171.Modal.Restaurant;

import java.util.ArrayList;

public class MainView extends AppCompatActivity {
    private ArrayList<Restaurant> dataBank;
    private ArrayList<Restaurant> bufferData;
    private RestaurantAdapter restaurantAdapter;
    private int postLast;
    private static final int MAX = 100;
    private static final int BUFFER = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        dataBank = new ArrayList<>();
        bufferData = new ArrayList<>();

        //Load data into data bank
        loadData();

        updateBufferData();
        restaurantAdapter = new RestaurantAdapter(this, bufferData);

        final ListView listView =(ListView) findViewById(R.id.lvRestaurants);
        listView.setAdapter(restaurantAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Toast.makeText(MainView.this, "row click", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1
                        && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                    Toast.makeText(MainView.this, "end", Toast.LENGTH_SHORT).show();
                    updateBufferData();
                    restaurantAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadData() {
        ArrayList<Restaurant> list = new ArrayList<>();
        int []idAvatars = {R.drawable.avatar, R.drawable.avatar2, R.drawable.avatar3};

        for (int i = 0; i < MAX; i++) {
            dataBank.add(new Restaurant("Nhà hàng " + i, "cống quỳnh", idAvatars));
        }
    }

    private void updateBufferData() {
        //Nothing to load
        if (postLast == (MAX -1))
            return;

        //not enough entries to load
        if ( (MAX - postLast) < BUFFER ) {
            for (int i = postLast; i < MAX; i++) {
                bufferData.add(dataBank.get(i));
            }
            postLast = MAX - 1;
            return;
        }

        //enough entries to load
        for (int i = postLast; i < postLast + BUFFER; i++) {
            bufferData.add(dataBank.get(i));
        }
        postLast += BUFFER;
    }
}
