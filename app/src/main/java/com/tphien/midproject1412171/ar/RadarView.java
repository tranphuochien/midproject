package com.tphien.midproject1412171.ar;

import android.graphics.Color;
import android.location.Location;

import com.tphien.midproject1412171.Modal.Restaurant;

import java.util.ArrayList;


public class RadarView{
	/** The screen */
	public DataView view;
	/** The radar's range */
	float range;
	/** Radius in pixel on screen */
	public static float RADIUS = 100;
	/** Position on screen */
	static float originX = 0 , originY = 0;
	
	/**
	 * You can change the radar color from here.
	 *   */
	static int radarColor = Color.argb(100, 220, 0, 0);
	Location currentLocation = new Location("provider");
	Location destinedLocation = new Location("provider");
	private Location mCurrent;
	
	public float[][] coordinateArray;

	float angleToShift;
	public float degreetopixel;
	public float bearing;
	public float circleOriginX;
	public float circleOriginY;
	private float mscale;
    private ArrayList<Restaurant> locations;
	private int nLocation =0;

	public float x = 0;
	public float y = 0;
	public float z = 0;

	float  yaw = 0;
	double[] bearings;
	ARView arView = new ARView();

	public RadarView(DataView dataView, double[] bearings, Location curPos, ArrayList<Restaurant> locations){
        this.locations = locations;
        nLocation = locations.size();
		this.bearings = bearings;
		mCurrent = curPos;
		calculateMetrics();
	}

	public void calculateMetrics(){
		circleOriginX = originX + RADIUS;
		circleOriginY = originY + RADIUS;

		range = (float)arView.convertToPix(10) * 50;
		mscale = range / arView.convertToPix((int)RADIUS);
	}

	public void paint(PaintUtils dw, float yaw) {

//		circleOriginX = originX + RADIUS;
//		circleOriginY = originY + RADIUS;
		this.yaw = yaw;
//		range = arView.convertToPix(10) * 1000;		/** Draw the radar */
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

		/** put the markers in it */
//		float scale = range / arView.convertToPix((int)RADIUS);
		/**
		 *  Your current location coordinate here.
		 * */

        currentLocation.setLatitude(mCurrent.getLatitude());
        currentLocation.setLongitude(mCurrent.getLongitude());


		for(int i = 0; i <nLocation;i++){
			destinedLocation.setLatitude(locations.get(i).getLat());
			destinedLocation.setLongitude(locations.get(i).getLon());
			convLocToVec(currentLocation, destinedLocation);
			float x = this.x / mscale;
			float y = this.z / mscale;


			if (x * x + y * y < RADIUS * RADIUS) {
				dw.setFill(true);
				dw.setColor(Color.rgb(255, 255, 255));
				dw.paintRect(x + RADIUS, y + RADIUS, 2, 2);
			}
		}
	}

	public void calculateDistances(PaintUtils dw, float yaw){
		currentLocation.setLatitude(10.8428107);
		currentLocation.setLongitude(106.663896);
        coordinateArray = new float[nLocation][2];
		for(int i = 0; i <nLocation;i++){
			if(bearings[i]<0){
				bearings[i] = 360 - bearings[i];
			}
			if(Math.abs(coordinateArray[i][0] - yaw) > 3){
				angleToShift = (float)bearings[i] - this.yaw;
				coordinateArray[i][0] = this.yaw;
			}else{
				angleToShift = (float)bearings[i] - coordinateArray[i][0] ;
				
			}
			destinedLocation.setLatitude(locations.get(i).getLat());
			destinedLocation.setLongitude(locations.get(i).getLon());
			float[] z = new float[1];
			z[0] = 0;
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destinedLocation.getLatitude(), destinedLocation.getLongitude(), z);
			bearing = currentLocation.bearingTo(destinedLocation);

			this.x = (float) (circleOriginX + 40 * (Math.cos(angleToShift)));
			this.y = (float) (circleOriginY + 40 * (Math.sin(angleToShift)));


			if (x * x + y * y < RADIUS * RADIUS) {
				dw.setFill(true);
				dw.setColor(Color.rgb(255, 255, 255));
				dw.paintRect(x + RADIUS - 1, y + RADIUS - 1, 2, 2);
			}
		}
	}
	
	/** Width on screen */
	public float getWidth() {
		return RADIUS * 2;
	}

	/** Height on screen */
	public float getHeight() {
		return RADIUS * 2;
	}
	
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void convLocToVec(Location source, Location destination) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(source.getLatitude(), source.getLongitude(), destination
				.getLatitude(), source.getLongitude(), z);
		float[] x = new float[1];
		Location.distanceBetween(source.getLatitude(), source.getLongitude(), source
				.getLatitude(), destination.getLongitude(), x);
		if (source.getLatitude() < destination.getLatitude())
			z[0] *= -1;
		if (source.getLongitude() > destination.getLongitude())
			x[0] *= -1;

		set(x[0], (float) 0, z[0]);
	}
}