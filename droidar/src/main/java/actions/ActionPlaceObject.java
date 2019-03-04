package actions;

import gl.GLCamera;
import util.Vec;
import util.Wrapper;
import worldData.MoveComp;
import worldData.Obj;

import components.ViewPosCalcerComp;

import de.rwth.setups.PlaceObjectsSetupTwo;

/**
 * Don't use this {@link Action} anymore, instead use a
 * {@link ViewPosCalcerComp} like in the {@link PlaceObjectsSetupTwo} !
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class ActionPlaceObject extends ActionUseCameraAngles {

	private GLCamera myCamera;
	private Wrapper myObjWrapper;
	private float maxDistance;
	private float myAzimuth;
	private MoveComp myMoveObjComp;
	private Obj compareObj;

	/**
	 * @param targetCamera
	 * @param objToPlace
	 *            the Wrapper should contain a {@link Obj}
	 * @param maxDistance
	 *            maximum distance in meters how far away from the camera the
	 *            elements can be places
	 */
	@Deprecated
	public ActionPlaceObject(GLCamera targetCamera, Wrapper objToPlace,
			float maxDistance) {
		super(targetCamera);
		myCamera = targetCamera;
		myObjWrapper = objToPlace;
		this.maxDistance = maxDistance;
		myMoveObjComp = new MoveComp(4);
	}

	@Override
	public void updateCompassAzimuth(float azimuth) {
		myAzimuth = azimuth;
	}

	@Override
	public void updatePitch(float pitch) {
		// not needed for movement, maybe for rotation?
	}

	@Override
	public void updateRoll(float rollAngle) {
		/*
		 * if the element in the wrapper changes, flip the component to the new
		 * element if its an Obj:
		 */
		final Object o = myObjWrapper.getObject();
		if (compareObj != o) {
			System.out.println(o.getClass().toString());
			if (o instanceof Obj) {
				if (compareObj != null)
					compareObj.remove(myMoveObjComp);
				((Obj) o).setComp(myMoveObjComp);
				System.out.println("myMoveCom was set");
				compareObj = (Obj) o;
				calcPosOnFloor(rollAngle);
			}
		} else {
			calcPosOnFloor(rollAngle);
		}
	}

	private void calcPosOnFloor(float rollAngle) {
		final Vec camPos = myCamera.getPosition();
		if (camPos != null) {
			Vec targetPos = myMoveObjComp.myTargetPos;
			/*
			 * the following formula calculates the opposite side of the
			 * right-angled triangle where the adjacent side is the height of
			 * the camera and the alpha angle the roll angle of the device
			 * 
			 * this way the distance can be calculated by the angle
			 */
			float distance = (float) (Math.tan(Math.toRadians(rollAngle)) * camPos.z);
			if (distance > maxDistance)
				distance = maxDistance;
			targetPos.x = 0;
			targetPos.y = distance;
			targetPos.z = 0;
			if (myAzimuth != 0) {
				// now calc the real position according to the cam rotation:
				targetPos.rotateAroundZAxis(360 - myAzimuth);
			}

			// dont forget to mention that the camera doesnt have to be a
			// the zero point:
			targetPos.x += camPos.x;
			targetPos.y += camPos.y;
		}
	}
}
