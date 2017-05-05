package com.tphien.midproject1412171.ar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ARView extends Activity implements SensorEventListener {
    PowerManager.WakeLock mWakeLock;
    CameraView cameraView;
    RadarMarkerView radarMarkerView;
    DisplayMetrics displayMetrics;
    android.hardware.Camera camera;
    ProgressDialog pd;
    boolean isInited = false;

    public static float azimuth;
    public static float pitch;
    public static float roll;
    public static Location mCurrent;
    public static CustomLocation locations;

    static PaintUtils paintScreen;
    static DataView dataView;
    static final float ALPHA = 0.25f;
    static final String LINK_REQUEST = "http://group9cntn.me/radar.txt";
    private static Context _context;

    public int screenWidth;
    public int screenHeight;
    public RelativeLayout upperLayerLayout;

    private LocationManager mLocationManager;
    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];
    private float results[] = new float[3];
    private SensorManager sensorMgr;
    private List<Sensor> sensors;
    private Sensor sensorGrav, sensorMag;

    protected float[] gravSensorVals;
    protected float[] magSensorVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get data from server
        new JsonTask().execute(LINK_REQUEST);

        //Keep screen don't sleep
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //Layer draw location and radar locations
        upperLayerLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams upperLayerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        upperLayerLayout.setLayoutParams(upperLayerLayoutParams);
        upperLayerLayout.setBackgroundColor(Color.TRANSPARENT);

        _context = this;
        cameraView = new CameraView(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout headerFrameLayout = new FrameLayout(this);
        RelativeLayout headerRelativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relaLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        headerRelativeLayout.setBackgroundColor(Color.BLACK);
        headerRelativeLayout.setLayoutParams(relaLayoutParams);
        Button button = new Button(this);
        RelativeLayout.LayoutParams buttonparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        button.setLayoutParams(buttonparams);
        button.setText("Cancel");
        button.setPadding(15, 0, 15, 0);

        TextView titleTextView = new TextView(this);
        RelativeLayout.LayoutParams textparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        textparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        titleTextView.setLayoutParams(textparams);
        titleTextView.setText("Augmented Reality View");

        headerRelativeLayout.addView(button);
        headerRelativeLayout.addView(titleTextView);
        headerFrameLayout.addView(headerRelativeLayout);

        setContentView(cameraView);

        addContentView(headerFrameLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 60,
                Gravity.TOP));
        addContentView(upperLayerLayout, upperLayerLayoutParams);


        upperLayerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(_context, "RELATIVE LAYOUT CLICKED", Toast.LENGTH_SHORT).show();
            }
        });

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {

                for (int i = 0; i < dataView.coordinateArray.length; i++) {
                    if ((int) event.getX() < dataView.coordinateArray[i][0] && ((int) event.getX() + 100) > dataView.coordinateArray[i][0]) {
                        if ((int) event.getY() <= dataView.coordinateArray[i][1] && ((int) event.getY() + 100) > dataView.coordinateArray[i][1]) {
                            //Toast.makeText(_context, "match Found its " + dataView, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
                return true;
            }
        });


    }

    protected void registerGPS() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(ARView.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ARView.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener);
        mCurrent = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public static Context getContext() {
        return _context;
    }

    public int convertToPix(int val) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, _context.getResources().getDisplayMetrics());
        return (int) px;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mWakeLock.release();

        sensorMgr.unregisterListener(this, sensorGrav);
        sensorMgr.unregisterListener(this, sensorMag);
        sensorMgr = null;

        if (mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    protected void onResume() {

        super.onResume();
        this.mWakeLock.acquire();


        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensorGrav = sensors.get(0);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) {
            sensorMag = sensors.get(0);
        }

        sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

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
        if (mLocationManager != null)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, mLocationListener);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent evt) {


        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(evt.values.clone(), gravSensorVals);
            grav[0] = evt.values[0];
            grav[1] = evt.values[1];
            grav[2] = evt.values[2];

        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magSensorVals = lowPass(evt.values.clone(), magSensorVals);
            mag[0] = evt.values[0];
            mag[1] = evt.values[1];
            mag[2] = evt.values[2];

        }

        if (gravSensorVals != null && magSensorVals != null) {
            SensorManager.getRotationMatrix(RTmp, I, gravSensorVals, magSensorVals);

            int rotation = Compatibility.getRotation(this);

            if (rotation == 1) {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
            } else {
                SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
            }

            SensorManager.getOrientation(Rot, results);

            ARView.azimuth = (convertRadian2Degree((float) (results[0] + Math.PI / 2)) +180) % 360;
            Log.d("azimuth", String.valueOf(azimuth));
            ARView.pitch = convertRadian2Degree(results[1])+90f;
            ARView.roll = convertRadian2Degree(results[2]);

            if (radarMarkerView != null)
                radarMarkerView.postInvalidate();
        }
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private float convertRadian2Degree(float radian){
        return (float) ((radian * 180) / Math.PI);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mCurrent = location;
            Log.d("mylocation", String.valueOf(location.getLatitude() + " " + location.getLongitude()));
            dataView.updateLocation(mCurrent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ARView.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            if (result == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ARView.this);
                // khởi tạo dialog
                alertDialogBuilder.setMessage("No Internet. Use local data");
                // thiết lập nội dung cho dialog
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Data[] dummy = new Data[6];

                        dummy[0] = new Data("Chùa Minh Kiến Đài", 10.8420836d, 106.66506d, "accountancy");
                        dummy[1] = new Data("Ngã tư Quang Trung", 10.8341279d, 106.665937d, "mydefault");
                        dummy[2] = new Data("Trường THCS Nguyễn Du", 10.8444073d, 106.662778d, "schools");
                        dummy[3] = new Data("HCMUS",10.7620684d ,106.68283d , "schools");
                        dummy[4] = new Data("Nowzone", 10.7637943d,106.683551d  , "movies");
                        dummy[5] = new Data("<3",10.8665766d ,106.608887d , "astrology");

                        locations = new CustomLocation(dummy);

                        //Load radar and locations
                        loadUpperLayout();
                        //Register GPS
                        registerGPS();
                        return;
                    }
                });

                alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                // tạo dialog
                alertDialog.show();
                // hiển thị dialog
                return;
            }

            //Parse data from json
            Gson gson = new GsonBuilder().create();
            locations = gson.fromJson(result,CustomLocation.class);

            //Load radar and locations
            loadUpperLayout();
            //Register GPS
            registerGPS();
        }
    }

    private void loadUpperLayout() {
        radarMarkerView = new RadarMarkerView(ARView.this, displayMetrics, upperLayerLayout);
        LinearLayout.LayoutParams radarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        radarMarkerView.setLayoutParams(radarParams);

        addContentView(radarMarkerView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (!isInited) {
            dataView = new DataView(ARView.this);
            paintScreen = new PaintUtils();
            isInited = true;
        }
    }
}
class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    ARView arView;
    SurfaceHolder holder;
    android.hardware.Camera camera;

    public CameraView(Context context) {
        super(context);
        arView = (ARView) context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            //Fix error rotate camera preview when initial
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            Display display = ((WindowManager) arView.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            if(display.getRotation() == Surface.ROTATION_0)
            {
                parameters.setPreviewSize(h, w);
                camera.setDisplayOrientation(90);
            }

            if(display.getRotation() == Surface.ROTATION_90)
            {
                parameters.setPreviewSize(w, h);
            }

            if(display.getRotation() == Surface.ROTATION_180)
            {
                parameters.setPreviewSize(h, w);
            }

            if(display.getRotation() == Surface.ROTATION_270)
            {
                parameters.setPreviewSize(w, h);
                camera.setDisplayOrientation(180);
            }

            camera.setParameters(parameters);
            //camera.startPreview();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            }
            catch(Exception e) {}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }

            camera = android.hardware.Camera.open();
            arView.camera = camera;
            camera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            try {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                    } catch (Exception ignore) {
                    }
                    try {
                        camera.release();
                    } catch (Exception ignore) {
                    }
                    camera = null;
                }
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

class RadarMarkerView extends View{

    ARView arView;
    DisplayMetrics displayMetrics;
    RelativeLayout upperLayoutView = null;
    GeomagneticField geoField;
    public RadarMarkerView(Context context, DisplayMetrics displayMetrics, RelativeLayout rel) {
        super(context);

        arView = (ARView) context;
        this.displayMetrics = displayMetrics;
        upperLayoutView = rel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ARView.paintScreen.setWidth(canvas.getWidth());
        ARView.paintScreen.setHeight(canvas.getHeight());
        ARView.paintScreen.setCanvas(canvas);



        if (!ARView.dataView.isInited()) {
            ARView.dataView.init(ARView.paintScreen.getWidth(), ARView.paintScreen.getHeight(),
                    arView.camera, displayMetrics,upperLayoutView, ARView.mCurrent, ARView.locations);
        }

        ARView.dataView.draw(ARView.paintScreen, ARView.azimuth, ARView.pitch, ARView.roll);
    }
}