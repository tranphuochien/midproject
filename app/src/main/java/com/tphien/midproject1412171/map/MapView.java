package com.tphien.midproject1412171.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.tphien.midproject1412171.Global;
import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.RestaurantInfoDialog;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.tool.BitMapHelper;
import com.tphien.midproject1412171.tool.MultiDrawable;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapView extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Restaurant>,
        ClusterManager.OnClusterInfoWindowClickListener<Restaurant>,
        ClusterManager.OnClusterItemClickListener<Restaurant>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Restaurant>{

    private GoogleMap mMap;
    private ClusterManager<Restaurant> mClusterManager;
    private View convertView;
    private OnInterInfoWindowTouchListener lsClick;
    MapWrapperLayout mapWrapperLayout;
    private HashMap<String, Restaurant> markerRestaurantMap= new HashMap<>();
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    public void onClickBtnTypeMap(View view) {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.
                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    private class RestaurantRenderer extends DefaultClusterRenderer<Restaurant> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        //private final ImageView mImageView;
        private ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public RestaurantRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(MapView.this.getApplicationContext());

            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);

            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Restaurant restaurant, MarkerOptions markerOptions) {
            // Draw a single Restaurant.
            // Set the info window to show their name.

            mImageView.setImageResource(restaurant.getCurAvatar());


            /*Glide.with(MapView.this)
                    .load("android.resource://com.tphien.midproject1412171/drawable/ic_sadness")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
            */
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(restaurant.getName());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Restaurant> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Restaurant p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Bitmap bitmapResized =
                        BitMapHelper.decodeSampledBitmapFromResource(getResources(),
                                p.getCurAvatar(), 100,100);
                Drawable drawable =  new BitmapDrawable(getResources(), bitmapResized);

                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

        @Override
        protected void onClusterItemRendered(Restaurant restaurant, Marker marker) {
            super.onClusterItemRendered(restaurant, marker);
            markerRestaurantMap.put(marker.getId(), restaurant);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_wrapper);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.8103330,106.693274), 9.5f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mClusterManager = new ClusterManager<Restaurant>(this, mMap);
        mClusterManager.setRenderer(new RestaurantRenderer());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        MarkerOptions marker = new MarkerOptions().position(new LatLng(10.8483638,106.664746));

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.accountancy));

        mMap.addMarker(marker);


        convertView = LayoutInflater.from(MapView.this).inflate(R.layout.content_location, null);
        Button detailBtn = (Button)convertView.findViewById(R.id.detailBtn);

        lsClick = new OnInterInfoWindowTouchListener(detailBtn) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Restaurant restaurant = markerRestaurantMap.get(marker.getId());
                RestaurantInfoDialog fragment1 = new RestaurantInfoDialog(restaurant);
                fragment1.show(getFragmentManager(), "");
            }
        };
        detailBtn.setOnTouchListener(lsClick);

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {return null; }

            @Override
            public View getInfoContents(Marker marker) {
                Restaurant curRes = markerRestaurantMap.get(marker.getId());
                lsClick.setMarker(marker);
                TextView name= (TextView)convertView.findViewById(R.id.nameLocation);
                TextView location = (TextView)convertView.findViewById(R.id.location);
                TextView email = (TextView)convertView.findViewById(R.id.email);
                ImageView imageView = (ImageView)convertView.findViewById(R.id.imageLocation);

                Glide.with(getApplicationContext()).load("").placeholder(curRes.getCurAvatar()).centerCrop().into(imageView);

                name.setText(curRes.getName());
                location.setText(curRes.getPosition().toString());
                email.setText(curRes.getEmail());



                mapWrapperLayout.setMarkerWithInfoWindow(marker, convertView);
                return convertView;
            }
        });

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        try {
            addItems();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mClusterManager.cluster();
    }


    @Override
    public boolean onClusterClick(Cluster<Restaurant> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getName();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Restaurant> cluster) {
        // Does nothing
    }

    @Override
    public boolean onClusterItemClick(Restaurant restaurant) {
        Toast.makeText(MapView.this, "Hello " + restaurant.getName(),Toast.LENGTH_SHORT ).show();
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Restaurant restaurant) {
    }

    private void addItems() throws JSONException, UnsupportedEncodingException {
        mClusterManager.addItems(Global.getDataBank());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
