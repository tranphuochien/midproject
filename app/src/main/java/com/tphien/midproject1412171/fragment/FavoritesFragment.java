package com.tphien.midproject1412171.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tphien.midproject1412171.Global;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.tool.ListView3d;
import com.tphien.midproject1412171.tool.LocationAdapter3d;
import com.tphien.midproject1412171.tool.SimpleDynamics;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private static Context context;
    private ArrayList<Restaurant> favoriteDataBank;
    private ArrayList<Restaurant> bufferData;
    private ArrayList<Restaurant> list;
    private LocationAdapter3d restaurantAdapter;
    private int postLast = 0;
    private static int MAX = 100;
    private static final int BUFFER = 10;
    private boolean isLoadedData = false;
    private ListView3d listView;
    private FragmentManager fragmentManager;

    public FavoritesFragment() {}
    public FavoritesFragment(Context context, FragmentManager fragmentManager) {

        FavoritesFragment.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            FavoritesFragment.context = Global.tmpContext;
    }

    /*private boolean updateBufferData() {
        if (postLast > MAX)
            return false;

        //not enough entries to load
        if ( (MAX - postLast) < BUFFER ) {
            for (int i = postLast; i < MAX; i++) {
                bufferData.add(favoriteDataBank.get(i));
            }
            postLast += BUFFER;
            return true;
        }

        //enough entries to load
            if (postLast < MAX) {
                for (int i = postLast; i < postLast + BUFFER; i++) {
                    bufferData.add(favoriteDataBank.get(i));
                }
                postLast += BUFFER;

            return true;
        }
        return false;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);


        //Load data into data bank and load first buffer data
        //loadData();
        list = Global.getDataFavorites();

        restaurantAdapter = new LocationAdapter3d(context, fragmentManager);
        restaurantAdapter.addAll(list);


        //Process listView
        listView =(ListView3d) view.findViewById(R.id.lvRestaurants);
        listView.setAdapter(restaurantAdapter);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Toast.makeText(context, "row click", Toast.LENGTH_SHORT).show();
            }
        });*/

        listView.setDynamics(new SimpleDynamics(0.9f, 0.6f));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adaptẻ, View view, int position, long id) {
                Toast.makeText(context, "row click", Toast.LENGTH_SHORT).show();
            }
        });

        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (isLoadedData) {
                    if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1
                            && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                        if (updateBufferData())
                            restaurantAdapter.notifyDataSetChanged();
                    }
                }
            }
        });*/

        return view;
    }

    /*private void loadData() {
        bufferData = new ArrayList<>();
        favoriteDataBank = Global.getDataFavorites();
        MAX = favoriteDataBank.size();
        if (MAX > 0)
            isLoadedData = true;

        updateBufferData();
    }*/



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Glide.get(context).clearMemory();
        Global.tmpContext = context;
    }
}
