package com.tphien.midproject1412171.ar;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tphien.midproject1412171.Modal.Restaurant;
import com.tphien.midproject1412171.MyCustomDialog;
import com.tphien.midproject1412171.R;

import java.util.ArrayList;


/**
 * 
 * Currently the markers are plotted with reference to line parallel to the earth surface.
 * We are working to include the elevation and height factors.
 * 
 * */


public class DataView {

	private RelativeLayout[] locationMarkerView;
	private ImageView[] subjectImageView;
	private RelativeLayout.LayoutParams[] layoutParams;
	RelativeLayout.LayoutParams[] subjectImageViewParams;
	private RelativeLayout.LayoutParams[] subjectTextViewParams;
	private TextView[] locationTextView;
	private ArrayList<Restaurant> locations;
    private int nLocation =0;
	private int[] nextXofText ;
	ArrayList<Integer> 	nextYofText = new ArrayList<Integer>();
	private double[] bearings;
	private float angleToShift;
	private float yPosition;
	private Location currentLocation = new Location("provider");
	private Location destinedLocation = new Location("provider");

	/** is the view Inited? */
    private boolean isInit = false;
	private boolean isDrawing = true;
	boolean isFirstEntry;
	private Context _context;
	/** width and height of the view*/
	private int width, height;
	private android.hardware.Camera camera;
    private static Context context;
	private float yawPrevious;
	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;

	private DisplayMetrics displayMetrics;
	private static RadarView radarPoints;

	private RadarLines lrl = new RadarLines();
	private RadarLines rrl = new RadarLines();
	float rx = 10, ry = 20;
	public float addX = 0, addY = 0;
	public float degreetopixelWidth;
	public float degreetopixelHeight;
	public float pixelstodp;
	public float bearing;
	public int[][] coordinateArray = new int[20][2];
    private Location mCurrent;
	private FragmentManager fragmentManager;


	public DataView(Context ctx, FragmentManager fragmentManager) {
		this._context = ctx;
		this.fragmentManager =fragmentManager;
	}

	boolean isInited() {
		return isInit;
	}

	void updateLocation(Location curPos){
		if (curPos == null) {
			mCurrent = new Location("provider");
			mCurrent.setLatitude(10.8428107);
			mCurrent.setLongitude(106.663896);
		} else {
			mCurrent = curPos;
		}

        Toast.makeText(_context, String.valueOf("curPos: " + mCurrent.getLatitude()), Toast.LENGTH_SHORT).show();

        bearings = new double[nLocation];
        currentLocation.setLatitude( mCurrent.getLatitude());
        currentLocation.setLongitude( mCurrent.getLongitude());


        if(bearing < 0)
            bearing  = 360 + bearing;

        for(int i = 0; i <nLocation;i++){
            destinedLocation.setLatitude(locations.get(i).getLat());
            destinedLocation.setLongitude(locations.get(i).getLon());
            bearing = currentLocation.bearingTo(destinedLocation);

            if(bearing < 0){
                bearing  = 360 + bearing;
            }
            bearings[i] = bearing;

        }
        radarPoints = new RadarView(this, bearings, mCurrent, locations);
    }

	public void init( int widthInit, int heightInit, android.hardware.Camera camera,
					 DisplayMetrics displayMetrics, RelativeLayout rel, Location curPos,
					  ArrayList<Restaurant> locations) {
		if (curPos == null) {
            mCurrent = new Location("provider");
            mCurrent.setLatitude(10.8428107);
            mCurrent.setLongitude(106.663896);
        } else {
            mCurrent = curPos;
        }
        this.locations = locations;
        nLocation = locations.size();

		try {
			locationMarkerView = new RelativeLayout[nLocation];
            subjectImageView = new ImageView[nLocation];
            locationTextView = new TextView[nLocation];

			layoutParams = new RelativeLayout.LayoutParams[nLocation];
			subjectImageViewParams = new RelativeLayout.LayoutParams[nLocation];
			subjectTextViewParams = new RelativeLayout.LayoutParams[nLocation];

			nextXofText = new int[nLocation];
			
			for(int i=0;i<nLocation;i++){
				layoutParams[i] = new RelativeLayout.LayoutParams(300, 300);

				subjectTextViewParams[i] = new RelativeLayout.LayoutParams(225, 225);

				subjectImageView[i] = new ImageView(_context);
                subjectImageView[i].setBackgroundResource(R.drawable.accountancy);
                subjectImageView[i].setId(i);

                locationTextView[i] = new TextView(_context);
                locationTextView[i].setText(checkTextToDisplay(locations.get(i).getName()));
                locationTextView[i].setTextColor(Color.BLACK);
                locationTextView[i].setBackgroundResource(R.drawable.rounded_rectangle_button);
                locationTextView[i].setId(i);
                locationTextView[i].setPadding(30,30,30,30);

                locationMarkerView[i] = new RelativeLayout(_context);
                locationMarkerView[i].setId(i);


                //Setup params
                layoutParams[i].setMargins(displayMetrics.widthPixels/2, displayMetrics.heightPixels/2, 0, 0);

                subjectTextViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                subjectTextViewParams[i].topMargin = 15;

                subjectImageViewParams[i] = new  RelativeLayout.LayoutParams(75, 75);
                subjectImageViewParams[i].topMargin = 15;
                subjectImageViewParams[i].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

                //Add params to view
                locationMarkerView[i].setLayoutParams(layoutParams[i]);
                subjectImageView[i].setLayoutParams(subjectImageViewParams[i]);
                locationTextView[i].setLayoutParams(subjectTextViewParams[i]);

                locationMarkerView[i].addView(subjectImageView[i]);
                locationMarkerView[i].addView(locationTextView[i]);

				rel.addView(locationMarkerView[i]);

				subjectImageView[i].setClickable(false);
				locationTextView[i].setClickable(false);

				subjectImageView[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
                        processOnClickLocation(v);
					}
				});


				locationTextView[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
                        processOnClickLocation(v);
					}
				});

				locationMarkerView[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
                        processOnClickLocation(v);
					}
				});
			}


			this.displayMetrics = displayMetrics;
			this.degreetopixelWidth = this.displayMetrics.widthPixels / camera.getParameters().getHorizontalViewAngle();
			this.degreetopixelHeight = this.displayMetrics.heightPixels / camera.getParameters().getVerticalViewAngle();
			System.out.println("camera.getParameters().getHorizontalViewAngle()=="+camera.getParameters().getHorizontalViewAngle());

			bearings = new double[nLocation];

            currentLocation.setLatitude( mCurrent.getLatitude());
            currentLocation.setLongitude( mCurrent.getLongitude());


			if(bearing < 0)
				bearing  = 360 + bearing;

			for(int i = 0; i <nLocation;i++){
				destinedLocation.setLatitude(locations.get(i).getLat());
				destinedLocation.setLongitude(locations.get(i).getLon());
				bearing = currentLocation.bearingTo(destinedLocation);

				if(bearing < 0){
					bearing  = 360 + bearing;
				}
				bearings[i] = bearing;

			}
			radarPoints = new RadarView(this, bearings, mCurrent, locations);
			this.camera = camera;
			width = widthInit;
			height = heightInit;
			
			lrl.set(0, -RadarView.RADIUS);
			lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2);
			lrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
			rrl.set(0, -RadarView.RADIUS);
			rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
			rrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		/*
		 * initialization is done, so dont call init() again.
		 * */
		isInit = true;
	}

    private void processOnClickLocation(final View v) {
        if (v.getId() != -1) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationMarkerView[v.getId()].getLayoutParams();
            Rect rect = new Rect(params.leftMargin, params.topMargin, params.leftMargin + params.width, params.topMargin + params.height);
            ArrayList<Integer> matchIDs = new ArrayList<Integer>();
            Rect compRect = new Rect();
            int count = 0;
            int index = 0;
            for (RelativeLayout.LayoutParams layoutparams : layoutParams) {
                compRect.set(layoutparams.leftMargin, layoutparams.topMargin,
                        layoutparams.leftMargin + layoutparams.width, layoutparams.topMargin + layoutparams.height);
                if (compRect.intersect(rect)) {
                    matchIDs.add(index);
                    count+=1;
                }
                index++;
            }

            final int id = v.getId();
            if (count > 1) {
                Toast.makeText(_context, "Number of places here = "+count, Toast.LENGTH_SHORT).show();
            } else {
                final CharSequence[] items = {
                        "Find path to this location", "View more info"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                builder.setTitle("Choose action")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(_context, FindPath.class);
                                    intent.putExtra("beginLat",mCurrent.getLatitude());
                                    intent.putExtra("beginLon",mCurrent.getLongitude());
                                    intent.putExtra("endLat",locations.get(id).getLat());
                                    intent.putExtra("endLon", locations.get(id).getLon());
                                    intent.putExtra("distination",locations.get(id).getName());

                                    _context.startActivity(intent);
                                }
                                else {
									MyCustomDialog fragment1 = new MyCustomDialog();
									fragment1.restaurant = locations.get(id);
									fragment1.show(fragmentManager, "");

                                }
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
            //locationMarkerView[v.getId()].bringToFront();
            //Toast.makeText(_context, " LOCATION NO : "+v.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    public void draw(PaintUtils dw, float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;

		// Draw Radar
		String	dirTxt = "";
		int bearing = (int) this.yaw; 
		int range = (int) (this.yaw / (360f / 16f));
		if (range == 15 || range == 0) dirTxt = "N"; 
		else if (range == 1 || range == 2) dirTxt = "NE"; 
		else if (range == 3 || range == 4) dirTxt = "E"; 
		else if (range == 5 || range == 6) dirTxt = "SE";
		else if (range == 7 || range == 8) dirTxt= "S"; 
		else if (range == 9 || range == 10) dirTxt = "SW"; 
		else if (range == 11 || range == 12) dirTxt = "W"; 
		else if (range == 13 || range == 14) dirTxt = "NW";


		radarPoints.view = this;

		dw.paintObj(radarPoints, rx+PaintUtils.XPADDING, ry+PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
		dw.setFill(false);
		dw.setColor(Color.argb(100,220,0,0));
		dw.paintLine( lrl.x, lrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS); 
		dw.paintLine( rrl.x, rrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS);
		dw.setColor(Color.rgb(255,255,255));
		dw.setFontSize(12);
		radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx + RadarView.RADIUS, ry - 5, true, false, -1);


		drawTextBlock(dw);
	}

	void drawPOI(PaintUtils dw, float yaw){
		if(isDrawing){
			dw.paintObj(radarPoints, rx+PaintUtils.XPADDING, ry+PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
			isDrawing = false;
		}
	}

	void radarText(PaintUtils dw, String txt, float x, float y, boolean bg, boolean isLocationBlock, int count) {

		float padw = 4, padh = 2;
		float w = dw.getTextWidth(txt) + padw * 2;
		float h;
		if(isLocationBlock){
			h = dw.getTextAsc() + dw.getTextDesc() + padh * 2+10;
		}else{
			h = dw.getTextAsc() + dw.getTextDesc() + padh * 2;
		}
		if (bg) {

			if(isLocationBlock){
				layoutParams[count].setMargins((int)(x - w / 2 - 10), (int)(y - h / 2 - 10), 0, 0);
				layoutParams[count].height = 300;
				layoutParams[count].width = 300;
				locationMarkerView[count].setLayoutParams(layoutParams[count]);

			}else{
				dw.setColor(Color.rgb(0, 0, 0));
				dw.setFill(true);
				dw.paintRect((x - w / 2) + PaintUtils.XPADDING , (y - h / 2) + PaintUtils.YPADDING, w, h);
				pixelstodp = (padw + x - w / 2)/((displayMetrics.density)/160);
				dw.setColor(Color.rgb(255, 255, 255));
				dw.setFill(false);
				dw.paintText((padw + x -w/2)+PaintUtils.XPADDING, ((padh + dw.getTextAsc() + y - h / 2)) + PaintUtils.YPADDING,txt);
			}
		}

	}

	String checkTextToDisplay(String str){

		if(str.length()>15){
			str = str.substring(0, 15)+"...";
		}
		return str;

	}

	void drawTextBlock(PaintUtils dw){

		for(int i = 0; i<bearings.length;i++){
			if(bearings[i]<0){

				if(this.pitch != 90){
					yPosition = (this.pitch - 90) * this.degreetopixelHeight+200;
				}else{
					yPosition = (float)this.height/2;
				}

				bearings[i] = 360 - bearings[i];
				angleToShift = (float)bearings[i] - this.yaw;
				nextXofText[i] = (int)(angleToShift*degreetopixelWidth);
				yawPrevious = this.yaw;
				isDrawing = true;
				radarText(dw, locations.get(i).getName(), nextXofText[i], yPosition, true, true, i);
				coordinateArray[i][0] =  nextXofText[i];
				coordinateArray[i][1] =   (int)yPosition;

			}else{
				angleToShift = (float)bearings[i] - this.yaw;

				if(this.pitch != 90){
					yPosition = (this.pitch - 90) * this.degreetopixelHeight+200;
				}else{
					yPosition = (float)this.height/2;
				}


				nextXofText[i] = (int)((displayMetrics.widthPixels/2)+(angleToShift*degreetopixelWidth));
				if(Math.abs(coordinateArray[i][0] - nextXofText[i]) > 50){
					radarText(dw, locations.get(i).getName(), (nextXofText[i]), yPosition, true, true, i);
					coordinateArray[i][0] =  (int)((displayMetrics.widthPixels/2)+(angleToShift*degreetopixelWidth));
					coordinateArray[i][1] =  (int)yPosition;

					isDrawing = true;
				}else{
					radarText(dw, locations.get(i).getName(),coordinateArray[i][0],yPosition, true, true, i);
					isDrawing = false;
				}
			}
		}
	}
	
	public class NearbyPlacesList extends BaseAdapter{

		ArrayList<Integer> matchIDs = new ArrayList<Integer>();
		public NearbyPlacesList(ArrayList<Integer> matchID){
			matchIDs = matchID;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return matchIDs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}