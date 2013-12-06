package actions;

import geo.GeoCalcer;
import gl.GLCamera;
import logger.ARLogger;
import system.EventManager;
import worlddata.World;
import android.location.Location;

/**
 * This action is the basic action for virtual camera movement in relation to
 * the physical device movement. The GPS input is used to calculate the virtual
 * position. If the distance to the center of the virtual world gets to big, the
 * virtual zero position is reseted and the virtual positions are recalculated
 * 
 * <br>
 * latutude is north(+)/south(-)<br>
 * longitude is east(+)/west(-)<br>
 * 
 * TODO combine this with the moveCamera action? good idea or not?
 * 
 * @author Spobo
 * 
 */
public class ActionCalcRelativePos extends Action {

	/**
	 * On default this is false, because the altitude values received via GPS
	 * are very inaccurate.
	 * 
	 * set this to true if your scenario need to take altitude values into
	 * account
	 */
	public static boolean USE_ALTITUDE_VALUES = false;

	/**
	 * set this to false if you want to position objects at the real 0 altitude,
	 * because otherwise if you set altitude to 0 the current device altitude
	 * will be used.
	 */
	public static final boolean USE_DEVICE_ALTI_FOR_ZERO = true;

	private static final double MAX_METER_DISTANCE = 1000; // 500 meter
	private static final String LOG_TAG = "ActionCalcRelativePos";

	private static final boolean LOG_SHOW_POSITION = false;

	/**
	 * this could be replaces by the
	 * {@link EventManager#getZeroPositionLocationObject()} values. Should store
	 * the same information. where is the better place to store the data TODO
	 */
	private double mNullLongitude;
	private double mNullLatitude;
	private double mNullAltitude;

	private World mWorld;
	private GLCamera mCamera;
	private GeoCalcer mGeoCalcer;

	/**
	 * Constructor.
	 * @param world - {@link worlddata.World}
	 * @param camera - {@link gl.GLCamera}
	 */
	public ActionCalcRelativePos(World world, GLCamera camera) {
		mWorld = world;
		mCamera = camera;
	}

	@Override
	public boolean onLocationChanged(Location location) {
		if (mNullLatitude == 0 || mNullLongitude == 0) {
			/*
			 * if the mNullLat or mNullLong are 0 this method was probably never
			 * called before (TODO problem when living in greenwhich e.g.?)
			 */
			resetWorldZeroPositions(location);
		} else {
			/*
			 * the following calculations were extracted from
			 * GeoObj.calcVirtualPosition() for further explanation how they
			 * work read the javadoc there. the two calculations were extracted
			 * to increase performance because this method will be called every
			 * time a new GPS-position arrives
			 */
			final double latitudeDistInMeters = (location.getLatitude() - mNullLatitude) * 111133.3333;
			final double longitudeDistInMeters = (location.getLongitude() - mNullLongitude)
					* 111319.4917 * Math.cos(mNullLatitude * 0.0174532925);

			if (LOG_SHOW_POSITION) {
				ARLogger.verbose(LOG_TAG, "latutude dist (north(+)/south(-))="
						+ latitudeDistInMeters);
				ARLogger.verbose(LOG_TAG, "longitude dist (east(+)/west(-))="
						+ longitudeDistInMeters);
			}

			if (worldShouldBeRecalced(latitudeDistInMeters,
					longitudeDistInMeters)) {
				resetWorldZeroPositions(location);
			} else {
				if (USE_ALTITUDE_VALUES) {
					/*
					 * if the altitude values should be used calculate the
					 * correct height
					 */
					final double relativeHeight = location.getAltitude()
							- mNullAltitude;
					mCamera.setNewPosition((float) longitudeDistInMeters,
							(float) latitudeDistInMeters,
							(float) relativeHeight);
				} else {
					// else dont change the z value
					mCamera.setNewPosition((float) longitudeDistInMeters,
							(float) latitudeDistInMeters);
				}

			}
		}

		return true;
	}

	private void resetCameraTomNullPosition() {
		mCamera.resetPosition(false);
	}

	private boolean worldShouldBeRecalced(double latDistMet, double longDistMet) {
		if (Math.abs(latDistMet) > MAX_METER_DISTANCE) {
			return true;
		} else if (Math.abs(longDistMet) > MAX_METER_DISTANCE) {
			return true;
		}
		return false;
	}

	/**
	 * Reset the world to (0,0,0) position. 
	 * @param location {@link Location}
	 */
	public void resetWorldZeroPositions(Location location) {
		ARLogger.debug(LOG_TAG, "Reseting virtual world positions");
		setNewNullValues(location);
		resetCameraTomNullPosition();
		calcNewWorldPositions();
	}

	private void setNewNullValues(Location location) {
		mNullLatitude = location.getLatitude();
		mNullLongitude = location.getLongitude();
		mNullAltitude = location.getAltitude();
		EventManager.getInstance().setZeroLocation(location);
	}

	private void calcNewWorldPositions() {
		if (mGeoCalcer == null) {
			mGeoCalcer = new GeoCalcer();
		}
		mGeoCalcer.setNullPos(mNullLatitude, mNullLongitude, mNullAltitude);
		mWorld.accept(mGeoCalcer);
	}

}
