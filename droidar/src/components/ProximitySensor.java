package components;

import geo.GeoObj;
import gl.GLCamera;
import gl.scenegraph.MeshComponent;
import util.Log;
import util.Vec;
import worldData.Entity;
import worldData.Obj;
import worldData.UpdateTimer;
import worldData.Updateable;
import worldData.Visitor;

public abstract class ProximitySensor implements Entity {

	private static final float DEFAULT_UPDATE_TIME = 1;
	private static final String LOG_TAG = "ProximitySensor";
	private GLCamera myCamera;
	private float myDistance;
	private UpdateTimer myTimer;

	public ProximitySensor(GLCamera camera, float distance) {
		myCamera = camera;
		myDistance = distance;
		myTimer = new UpdateTimer(DEFAULT_UPDATE_TIME, null);
	}

	public void setMyCamera(GLCamera myCamera) {
		this.myCamera = myCamera;
	}

	public void setMyDistance(float myDistance) {
		this.myDistance = myDistance;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {

		if (myTimer.update(timeDelta, this)) {
			if (parent instanceof Obj) {
				Obj obj = (Obj) parent;
				Vec position = obj.getPosition();
				if (position != null) {
					float currentDistance = Vec.distance(position,
							myCamera.getPosition());
					checkCurrentDistance(obj, obj.getMeshComp(),
							currentDistance);
					return true;
				} else {
					Log.w(LOG_TAG, "MeshComp of target Obj was null!");
				}
			}
			if (parent instanceof GeoObj) {
				GeoObj obj = (GeoObj) parent;
				float currentDistance = obj.getVirtualPosition(
						myCamera.getGPSPositionAsGeoObj()).getLength();
				checkCurrentDistance(obj, null, currentDistance);
			} else {
				Log.w(LOG_TAG, "Sensor parent " + parent
						+ " has no position, cant be used!");
			}
		}
		return true;
	}

	@Override
	public Updateable getMyParent() {
		Log.e(LOG_TAG, "Get parent called which is not "
				+ "implemented for this component!");
		return null;
	}

	@Override
	public void setMyParent(Updateable parent) {
		// can't have children so the parent does not have to be stored
		Log.e(LOG_TAG, "Set parent called which is not "
				+ "implemented for this component!");
	}

	private void checkCurrentDistance(Obj obj, MeshComponent m,
			float currentDistance) {
		if (0 <= currentDistance && currentDistance < myDistance) {
			onObjectIsCloseToCamera(myCamera, obj, m, currentDistance);
		}
	}

	/**
	 * @param glCamera
	 *            the camera (which should be the users position)
	 * @param obj
	 *            the obj where the {@link ProximitySensor} is contained in
	 * @param meshComp
	 *            the {@link MeshComponent} of the obj
	 * @param currentDistance
	 *            the distance of the camera to the obj
	 */
	public abstract void onObjectIsCloseToCamera(GLCamera glCamera, Obj obj,
			MeshComponent meshComp, float currentDistance);

}
