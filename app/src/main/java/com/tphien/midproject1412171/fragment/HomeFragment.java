package com.tphien.midproject1412171.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.tphien.midproject1412171.ar.ARView;
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


public class HomeFragment extends Fragment implements onRadiusChangeListener {
    private ArrayList<Restaurant> nearbyDataBank;
    private ArrayList<Restaurant> bufferData;
    private RestaurantAdapter restaurantAdapter;
    private int postLast;
    private static int MAX = 100;
    private static final int BUFFER = 10;
    private boolean isLoadedData = false;
    private static Context context;
    private TextView tvCurPos;
    private TextView tvResult;
    static final String LINK_REQUEST = "http://group9cntn.me/data_restaurants.json";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(Context context) {
        // Required empty public constructor
        HomeFragment.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void loadData() {
        LoadDataTask loadDataTask = new LoadDataTask(context);
        loadDataTask.execute(LINK_REQUEST);
    }

    private boolean updateBufferData() {
        //Nothing to load
        if (postLast == (MAX -1))
            return false;

        //not enough entries to load
        if ( (MAX - postLast) < BUFFER ) {
            for (int i = postLast; i < MAX; i++) {
                bufferData.add(nearbyDataBank.get(i));
            }
            postLast = MAX - 1;
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


        restaurantAdapter = new RestaurantAdapter(context, bufferData);

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
        tvCurPos = (TextView) view.findViewById(R.id.tvCurPos);
        tvCurPos.setText("Your position: ");
        tvResult = (TextView) view.findViewById(R.id.tvResult);
        tvResult.setText("There are 0 locals near your");
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



    private boolean buildAlertMessageNoGps() {
        //final boolean[] mode = new boolean[0];
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //mode[0] = true;
                    }
                })
                .setNegativeButton("No, use fake GPS", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        //mode[0] = false;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

        return true;
    }

    private ArrayList<Restaurant> getRestaurantsNearby() {
        ArrayList<Restaurant> result = new ArrayList<>();
        int radius = Global.radius;
        boolean mode = false;
        int nLocations = Global.getDataBank().size();

        final LocationManager manager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            mode = buildAlertMessageNoGps();
        }

        LatLng pos = Global.getCurPosition(mode);

        Location curPos = new Location("current position");
        Restaurant tmp;
        curPos.setLatitude(pos.latitude);
        curPos.setLongitude(pos.longitude);
        Location targetPos = new Location("current position");
        for (int i = 0; i < nLocations; i++) {
            tmp = Global.getDataByIndex(i);
            targetPos.setLatitude(tmp.getLat());
            targetPos.setLongitude(tmp.getLon());
            float k = 0;
            k = curPos.distanceTo(targetPos);
            if (k <= radius)
                result.add(tmp);
        }
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
        isLoadedData = false;
        nearbyDataBank = getRestaurantsNearby();
        MAX = nearbyDataBank.size();
        postLast = 0;
        bufferData.clear();

        updateBufferData();
        restaurantAdapter.notifyDataSetChanged();

        tvCurPos.setText("Your position: " + Global.getCurPosition(false).latitude+ "," +
                Global.getCurPosition(false).longitude);
        tvResult.setText("There are "+ MAX +" locals near your");

        isLoadedData = true;
    }


}
