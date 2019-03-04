package components;

import gl.GLCamera;
import gl.HasPosition;
import gl.scenegraph.MeshComponent;
import util.Vec;
import worldData.Entity;
import worldData.Obj;
import worldData.UpdateTimer;
import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

public abstract class TooFarAwayComp implements Entity {

	private static final float DEFAULT_GRAYZONE_SIZE = 30; // TODO
	private static final float DEFAULT_UPDATE_SPEED = 0.4f;

	private static final String LOG_TAG = "TooFarAwayComp";

	private static final int IS_TO_FAR_AWAY = 2;
	private static final int IS_CLOSE = 1;

	private float myMaxDistance;
	private GLCamera myCamera;
	private UpdateTimer timer;

	private float myGrayZoneDist;
	private int currentState;

	/**
	 * 
	 * obj->|--------|max dist.-------|gray.dist--------|measured dist.-----
	 * 
	 * @param maxDistance
	 * @param grayZoneDist
	 *            has to be larger then the max. distance!
	 * @param camera
	 * @param updateSpeed
	 */
	public TooFarAwayComp(float maxDistance, float grayZoneDist,
			GLCamera camera, float updateSpeed) {
		myMaxDistance = maxDistance;
		myCamera = camera;
		myGrayZoneDist = grayZoneDist;
		timer = new UpdateTimer(updateSpeed, null);
	}

	public TooFarAwayComp(float maxDistance, GLCamera camera) {
		this(maxDistance, maxDistance + DEFAULT_GRAYZONE_SIZE, camera,
				DEFAULT_UPDATE_SPEED);
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

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		/*
		 * as long as the parent is not a HasPosition subclass object the timer
		 * wont be updated
		 */
		if (parent instanceof HasPosition) {
			if (timer.update(timeDelta, parent)) {
				Vec pos = ((HasPosition) parent).getPosition();
				if (pos != null) {
					return usePosition(parent, pos);
				}
			}
			return true;
		}
		Log.e(LOG_TAG, "Could not extract position from parent! " + parent
				+ "(Class=" + parent.getClass() + "). "
				+ "The comp cant be used in this context.");
		return false;
	}

	private boolean usePosition(Updateable parent, Vec pos) {

		MeshComponent parentsMesh = tryToGetTheParentsMesh(parent);

		Vec direction = pos.copy().sub(myCamera.getPosition());
		float length = direction.getLength();
		direction.mult(-1);

		if (length > myMaxDistance) {

			/*
			 * the object is too faar away so inform the component
			 */
			if (currentState != IS_TO_FAR_AWAY) {
				currentState = IS_TO_FAR_AWAY;
				isNowToFarAway(parent, parentsMesh, direction);
			}
			onFarAwayEvent(parent, parentsMesh, direction);

			/*
			 * if the distance is in the grayzone additionially fire the
			 * onGrayZoneEvent event
			 */
			if (length < myGrayZoneDist) {
				float grayZonePercent = (length
						/ (myGrayZoneDist - myMaxDistance) - 1) * 100;
				onGrayZoneEvent(parent, parentsMesh, direction, grayZonePercent);
			}

		} else if (currentState != IS_CLOSE) {
			currentState = IS_CLOSE;
			isNowCloseEnough(parent, parentsMesh, direction);

		}
		return true;
	}

	private MeshComponent tryToGetTheParentsMesh(Updateable parent) {
		if (parent instanceof Obj)
			return ((Obj) parent).getGraphicsComponent();
		return null;
	}

	/**
	 * This will be called ONCE when the user gets close enough to the object
	 * (use this method to hide the arrow pointing towards the object e.g.)
	 * 
	 * @param parent
	 *            Normally this will be the {@link MeshComponent} of the target
	 *            {@link Obj} and if the object does not have one it will be the
	 *            object itself
	 * @param direction
	 *            The direction points from the object TO the user! If a
	 *            {@link MeshComponent} is added to the parentMesh with
	 *            subMesh.setPosition(direction) it will be at the location of
	 *            the camera. Because of this reduce the length of the direction
	 *            {@link Vec} to move it away from the users position and
	 *            towards the target object
	 */
	public abstract void isNowCloseEnough(Updateable parent,
			MeshComponent parentsMesh, Vec direction);

	/**
	 * This will be called when ONCE when the target object is getting to faar
	 * away from the user (Use this method to display an arrow in the object
	 * direction e.g.)
	 * 
	 * @param parent
	 *            the parent where the {@link TooFarAwayComp} was added to. Will
	 *            also be a subclass of {@link HasPosition}.
	 * @param parentsMesh
	 *            the {@link MeshComponent} of the parent. If it is null the
	 *            parent does not have a meshComp jet.
	 * @param direction
	 *            The direction points from the object TO the user! If a
	 *            {@link MeshComponent} is added to the parentMesh with
	 *            subMesh.setPosition(direction) it will be at the location of
	 *            the camera. Because of this reduce the length of the direction
	 *            {@link Vec} to move it away from the users position and
	 *            towards the target object
	 */
	public abstract void isNowToFarAway(Updateable parent,
			MeshComponent parentsMesh, Vec direction);

	/**
	 * This method is called frequently with the updated position of the target
	 * (e.g. to update the position of the arrow pointing to the target)
	 * 
	 * @param parent
	 *            the parent where the {@link TooFarAwayComp} was added to. Will
	 *            also be a subclass of {@link HasPosition}.
	 * @param parentsMesh
	 *            the {@link MeshComponent} of the parent. If it is null the
	 *            parent does not have a meshComp jet.
	 * @param direction
	 *            The direction points from the object TO the user! If a
	 *            {@link MeshComponent} is added to the parentMesh with
	 *            subMesh.setPosition(direction) it will be at the location of
	 *            the camera. Because of this reduce the length of the direction
	 *            {@link Vec} to move it away from the users position and
	 *            towards the target object
	 */
	public abstract void onFarAwayEvent(Updateable parent,
			MeshComponent parentsMesh, Vec direction);

	/**
	 * This method will be called frequently when the user is in the defined
	 * gray zone (so nearly too far away to still see the object)
	 * 
	 * @param parent
	 *            the parent where the {@link TooFarAwayComp} was added to. Will
	 *            also be a subclass of {@link HasPosition}.
	 * @param parentsMesh
	 *            the {@link MeshComponent} of the parent. If it is null the
	 *            parent does not have a meshComp jet.
	 * @param direction
	 *            The direction points from the object TO the user! If a
	 *            {@link MeshComponent} is added to the parentMesh with
	 *            subMesh.setPosition(direction) it will be at the location of
	 *            the camera. Because of this reduce the length of the direction
	 *            {@link Vec} to move it away from the users position and
	 *            towards the target object
	 * @param grayZonePercent
	 *            from 0 to 100. 0 means entered grayzone but still far away and
	 *            100 means that the user is getting close enough to the object
	 *            so that this {@link TooFarAwayComp} is not needed anymore
	 * 
	 */
	public abstract void onGrayZoneEvent(Updateable parent,
			MeshComponent parentsMesh, Vec direction, float grayZonePercent);

	@Override
	public boolean accept(Visitor visitor) {
		// TODO Auto-generated method stub
		return false;
	}

	public GLCamera getMyCamera() {
		return myCamera;
	}

}
