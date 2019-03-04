package gl.animations;

import gl.GLCamera;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Updateable;
import worldData.Visitor;

public class AnimationFaceToCamera extends GLAnimation {

	private GLCamera myTargetCamera;
	private float lastUpdateAway = 0;
	private float myUpdateDelay;
	private Vec rotationVec = new Vec();
	private Vec newRotationVec = new Vec();

	private Vec adjustmentVec;
	private Vec myTargetCameraPosition;
	private boolean dontChangeXRotation;

	/**
	 * @param targetCamera
	 * @param targetMesh
	 * @param updateDelay
	 *            around 0.5f s
	 * @param dontChangeXRotation
	 *            if this is false, the mesh will also change the rotation x
	 *            value, otherwise only the z value to face to the camera
	 */
	public AnimationFaceToCamera(GLCamera targetCamera, float updateDelay,
			boolean dontChangeXRotation) {
		myTargetCamera = targetCamera;

		myUpdateDelay = updateDelay;
		myTargetCameraPosition = myTargetCamera.getPosition();
		this.dontChangeXRotation = dontChangeXRotation;
		// Log.d("face camera animation", "created. camera=" + myTargetCamera
		// + " targetMesh class=" + myTargetMesh.getClass()
		// + " update delay=" + myUpdateDelay);
	}

	public AnimationFaceToCamera(GLCamera targetCamera, float updateDelay) {
		this(targetCamera, updateDelay, true);
	}

	public AnimationFaceToCamera(GLCamera targetCamera) {
		this(targetCamera, 0.5f, true);
	}

	/**
	 * @param targetCamera
	 * @param targetMesh
	 * @param updateDelay
	 *            0.5f
	 * @param adjustmentVec
	 */
	public AnimationFaceToCamera(GLCamera targetCamera, float updateDelay,
			Vec adjustmentVec) {
		this(targetCamera, updateDelay);
		this.adjustmentVec = adjustmentVec;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {

		/*
		 * TODO use mesh instead of assigning a mesh while creating this
		 * animation!
		 */
		timeDelta = Math.abs(timeDelta);
		lastUpdateAway += timeDelta;
		if (lastUpdateAway > myUpdateDelay) {
			updateRotation(parent);
			// Log.d("face camera animation", "new rotation vec calculated:");
			// Log.d("face camera animation",
			// "x="+newRotationVec.x+" , z="+newRotationVec.z);
			lastUpdateAway = 0;
		}
		if (dontChangeXRotation) {
			Vec.morphToNewAngleVec(rotationVec, 0, 0, newRotationVec.z,
					timeDelta);
		} else {
			Vec.morphToNewAngleVec(rotationVec, newRotationVec.x,
					newRotationVec.y, newRotationVec.z, timeDelta);
		}
		return true;
	}

	Vec absolutePosition = new Vec();

	private void updateRotation(Updateable parent) {
		if (parent instanceof MeshComponent) {
			absolutePosition.setToZero();
			((MeshComponent) parent).getAbsoluteMeshPosition(absolutePosition);
			// Log.d("face camera animation", "mesh position: "+pos);
			newRotationVec.toAngleVec(absolutePosition, myTargetCameraPosition);
			/*
			 * substract 90 from the x value becaute calcanglevec returns 90 if
			 * the rotation should be the horizon (which would mean no object
			 * rotation)
			 */
			newRotationVec.x -= 90;
			newRotationVec.z *= -1;
		}
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

		gl.glRotatef(rotationVec.z, 0, 0, 1);
		gl.glRotatef(rotationVec.x, 1, 0, 0);
		gl.glRotatef(rotationVec.y, 0, 1, 0);

		if (adjustmentVec != null) {
			/*
			 * if an adjustment vector is set this adjustment has to be done
			 * AFTER the rotation to be easy to use, see constructor for infos
			 * about adjustment
			 */
			gl.glRotatef(adjustmentVec.x, 1, 0, 0); // TODO find correct order
			gl.glRotatef(adjustmentVec.z, 0, 0, 1);
			gl.glRotatef(adjustmentVec.y, 0, 1, 0);
		}
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

}
