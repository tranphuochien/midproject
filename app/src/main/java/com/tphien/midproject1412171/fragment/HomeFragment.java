package com.tphien.midproject1412171.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.tphien.midproject1412171.Global;
import com.tphien.midproject1412171.MainView.onRadiusChangeListener;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.RestaurantAdapter;
import com.tphien.midproject1412171.tool.MyReaderJson;
import com.tphien.midproject1412171.tool.ServiceControler;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements onRadiusChangeListener {
    private ArrayList<Restaurant> nearbyDataBank;
    private ArrayList<Restaurant> bufferData;
    private RestaurantAdapter restaurantAdapter;
    private int postLast;
    private static int MAX = 100;
    private static final int BUFFER = 10;
    private boolean isLoadedData = false; // mutex
    private static Context context;
    private TextView tvCurPos;
    private TextView tvResult;
    private ImageView curLocation;
    private FragmentManager fragmentManager;
    static final String LINK_REQUEST = "http://group9cntn.me/data_restaurants.json";

    public HomeFragment() {}

    public HomeFragment(Context context, FragmentManager fragmentManager) {
        // Required empty public constructor
        HomeFragment.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadData() {
        LoadDataTask loadDataTask = new LoadDataTask(context);
        loadDataTask.execute(LINK_REQUEST);
    }

    private boolean updateBufferData() {
        if (postLast > MAX)
            return false;
        //not enough entries to load
        if ( (MAX - postLast) < BUFFER ) {
            for (int i = postLast; i < MAX; i++) {
                bufferData.add(nearbyDataBank.get(i));
            }
            postLast += BUFFER;
            return true;
        }
        int n = nearbyDataBank.size();
        //enough entries to load
        if (postLast < n) {
            for (int i = postLast; i < postLast + BUFFER; i++) {
                bufferData.add(nearbyDataBank.get(i));
            }
            postLast += BUFFER;

            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nearbyDataBank = new ArrayList<>();
        bufferData = new ArrayList<>();

        //Load data into data bank and load first buffer data
        loadData();

        restaurantAdapter = new RestaurantAdapter(context, bufferData, fragmentManager);

        //Process listView
        final ListView listView =(ListView) view.findViewById(R.id.lvRestaurants);
        listView.setAdapter(restaurantAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Toast.makeText(context, "row click", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        });

        //Process titlle
        curLocation = (ImageView) view.findViewById(R.id.CurLocationlBut);
        tvCurPos = (TextView) view.findViewById(R.id.tvCurPos);
        tvResult = (TextView) view.findViewById(R.id.tvResult);
        tvCurPos.setText("Your position: ");
        tvResult.setText("There are 0 locals near your");
        curLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = "http://maps.google.com/?q=" + Global.getCurPosition().latitude + ","
                        + Global.getCurPosition().longitude;
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.get(context).clearMemory();
        isLoadedData = false;
    }

    private ArrayList<Restaurant> getRestaurantsNearby() {
        ArrayList<Restaurant> result = new ArrayList<>();
        float distance = 0f;
        int radius = Global.radius; // radius searching
        int nLocations = Global.getDataBank().size(); // total locations in data bank
        LatLng pos = Global.getCurPosition();
        Location curPos = new Location("current position");
        Location targetPos = new Location("current position");
        Restaurant tmp;

        curPos.setLatitude(pos.latitude);
        curPos.setLongitude(pos.longitude);

        for (int i = 0; i < nLocations; i++) {
            tmp = Global.getDataByIndex(i);
            targetPos.setLatitude(tmp.getLat());
            targetPos.setLongitude(tmp.getLon());
            distance = curPos.distanceTo(targetPos);
            if (distance <= radius)
                result.add(tmp);
        }

        //If not found restaurant, add dummy location
        if (result.size() == 0)
            result.add(new Restaurant());

        return result;
    }

    private class LoadDataTask extends AsyncTask<String, Void, ArrayList<Restaurant>> {
        private Context context;

        LoadDataTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Restaurant> doInBackground(String... params) {
            ArrayList<Restaurant> items = new ArrayList<>();

            //If the internet is not available. load data from local
            if (!ServiceControler.isNetworkAvailable(context)) {
                InputStream inputStream = getResources().openRawResource(R.raw.data_restaurants);

                try {
                    items = new MyReaderJson().read(inputStream);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //Canot read data from json file -> create dummy data
                    items.add(new Restaurant());
                    return items;
                }
            } else {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    final URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    InputStream inputStream = new ByteArrayInputStream((buffer.toString()).getBytes(StandardCharsets.UTF_8));
                    items = new MyReaderJson().read(inputStream);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    items.add(new Restaurant());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return items;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            Global.setDataBank(restaurants);

            reloadData();
        }
    }

    //When radius or GPS signal change
    public void reloadData() {
        //down mutext
        isLoadedData = false;
        nearbyDataBank = getRestaurantsNearby();
        MAX = nearbyDataBank.size();
        postLast = 0;
        bufferData.clear();

        updateBufferData();
        restaurantAdapter.notifyDataSetChanged();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        if (ServiceControler.isNetworkAvailable(context)) {
            try {
                addresses = geocoder.getFromLocation(Global.getCurPosition().latitude, Global.getCurPosition().longitude, 1);
                tvCurPos.setText("Your position: " + addresses.get(0).getAddressLine(0)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tvCurPos.setText("Your position: " + Global.getCurPosition().latitude + ", " +
                    Global.getCurPosition().longitude);
        }

        tvResult.setText("There are "+ MAX +" locals near your");

        //up mutex
        isLoadedData = true;
    }


}
