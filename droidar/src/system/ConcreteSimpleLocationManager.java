package system;

import java.util.ArrayList;

import util.LimitedQueue;
import util.Log;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

public class ConcreteSimpleLocationManager extends SimpleLocationManager {

	/**
	 * <1 accuracy less important
	 * 
	 * >1 accuracy more important
	 */
	private static final float ACCURACY_WEIGHTING = 1f;

	private static final int MAX_NUMBR_OF_LOCATIONS = 15;

	private static final double SQRT2PII = Math.sqrt(2 * Math.PI);

	private static final String LOG_TAG = "ConcreteSimpleLocationManager";

	private Location currentPosition;
	private LimitedQueue<Location> lastPositions;

	private final float mimProb = 0.5f; // TODO

	private Location lastStepPos;

	public ConcreteSimpleLocationManager(Context context) {
		super(context);
	}

	public LimitedQueue<Location> getLastPositions() {
		return lastPositions;
	}

	@Override
	public Location getCurrentBUfferedLocation() {
		return currentPosition;
	}

	@Override
	public void onLocationEventFromGPS(Location location,
			ArrayList<LocationListener> listenersToInform) {
		if (currentPosition == null) {
			currentPosition = new Location("AveragePosition");
		}
		calcFromLastPositions(currentPosition, location);

		for (int i = 0; i < listenersToInform.size(); i++) {
			listenersToInform.get(i).onLocationChanged(currentPosition);
		}
	}

	@Override
	public void onLocationEventFromSteps(Location location,
			ArrayList<LocationListener> listenersToInform) {
		lastStepPos = location;
		onLocationEventFromGPS(location, listenersToInform);
	}

	public Location getLastStepPos() {
		return lastStepPos;
	}

	private boolean calcFromLastPositions(Location target, Location newLocation) {

		addToLastLocationsList(newLocation);

		float accuracySum = 0;
		float inverseAccuracySum = 0;
		double meanLat = 0;
		double meanAlti = 0;
		double meanLong = 0;

		/*
		 * weights via accuracy
		 * 
		 * example: a and b weights (e.g. 1/10m and 1/20m), x and y are latitude
		 * values from the 2 measurements:
		 * 
		 * 1/(a+b)*(a*x+b*y)
		 * 
		 * TODO could also add a simple time control parameter via gaus
		 * anzI=i*(i+1)/2
		 */
		int numberOfLocations = lastPositions.size();
		Log.d(LOG_TAG, "Calculating average of " + numberOfLocations
				+ " locations");
		for (int i = 0; i < numberOfLocations; i++) {
			Location l = lastPositions.get(i);
			float acc = ACCURACY_WEIGHTING / l.getAccuracy();
			accuracySum += l.getAccuracy();
			inverseAccuracySum += acc;
			meanLat += acc * l.getLatitude();
			meanLong += acc * l.getLongitude();
			meanAlti += l.getAltitude();
		}

		target.setAccuracy(accuracySum / numberOfLocations);
		target.setAltitude(meanAlti / numberOfLocations);
		target.setLatitude(meanLat / inverseAccuracySum);
		target.setLongitude(meanLong / inverseAccuracySum);

		Log.d(LOG_TAG, "Average is: " + target);

		/*
		 * check with gausian distr if value is ok to add (>a certaion
		 * probability) TODO then add to list of location measurements
		 */

		// double variance=0;
		// variance/=numberOfLocations-1;
		//
		// double nLat = 1
		// / (meanLat / inverseAccuracySum * SQRT2PII)
		// * Math.exp(-1
		// / 2
		// * ((t.getLatitude() - meanLat / inverseAccuracySum) / variance)
		// * ((t.getLatitude() - meanLat / inverseAccuracySum) / variance));
		// System.out.println("nLat=" + nLat);

		return true;
	}

	@Override
	public void setMaxNrOfBufferedLocations(int maxNrOfBufferedLocations) {
		if (lastPositions == null) {
			lastPositions = new LimitedQueue<Location>(maxNrOfBufferedLocations);
		} else {
			lastPositions.setLimit(maxNrOfBufferedLocations);
		}
	}

	private void addToLastLocationsList(Location location) {
		if (lastPositions == null) {
			lastPositions = new LimitedQueue<Location>(MAX_NUMBR_OF_LOCATIONS);
		}
		lastPositions.add(location);
	}
}
