package com.tphien.midproject1412171.ar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.map.DirectionFinder;
import com.tphien.midproject1412171.map.DirectionFinderListener;
import com.tphien.midproject1412171.map.Route;
import com.tphien.midproject1412171.tool.ServiceControler;

import java.util.ArrayList;
import java.util.List;

public class FindPath extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private PlaceAutocompleteFragment fragmentOrigin;
    private PlaceAutocompleteFragment fragmentDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String origin = "";
    String destination = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fragmentOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_origin);
        fragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_distination);


        fragmentOrigin.getView().setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
        ((EditText)fragmentOrigin.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(12.0f);
        fragmentDestination.getView().setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
        ((EditText)fragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(12.0f);

        fragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                origin = (String) place.getAddress();
            }

            @Override
            public void onError(Status status) {
            }
        });
        fragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = (String) place.getAddress();
            }

            @Override
            public void onError(Status status) {
            }
        });

        double beginLat, beginLon, endLat, endLon;
        beginLat = getIntent().getExtras().getDouble("beginLat");
        beginLon = getIntent().getExtras().getDouble("beginLon");
        endLat = getIntent().getExtras().getDouble("endLat");
        endLon = getIntent().getExtras().getDouble("endLon");

        fragmentOrigin.setText("My position");
        fragmentDestination.setText(getIntent().getExtras().getString("distination"));

        if (ServiceControler.isNetworkAvailable(FindPath.this)) {
            try {
                new DirectionFinder(this, beginLat, beginLon, endLat, endLon, true).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ServiceControler.buildAlertMessageNoNetwork(this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    private void sendRequest() {
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ServiceControler.isNetworkAvailable(FindPath.this)) {
            try {
                new DirectionFinder(this, origin, destination, false).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ServiceControler.buildAlertMessageNoNetwork(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding direction...", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView)findViewById(R.id.text_view_distance)).setText(route.distance.text);
            ((TextView)findViewById(R.id.text_view_time)).setText(route.duration.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void onClickBtnFindPath(View view) {
        sendRequest();
    }

    public void onClickBtnSwitch(View view) {
        String tmp = "";
        if (origin.isEmpty() || destination.isEmpty()) {
            return;
        }

        tmp = origin;
        origin = destination;
        destination = tmp;

        fragmentOrigin.setText(origin);
        fragmentDestination.setText(destination);
    }
}
