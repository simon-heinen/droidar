package commands.gl;

import gl.GLCamera;
import util.Vec;

import commands.undoable.UndoableCommand;

public class CommandCameraMoveAndLookAt extends UndoableCommand {

	private GLCamera myTargetCamera;
	private Vec myOffset;
	private Vec myPos;
	private Vec myRotation;

	private Vec myOffsetBackup;
	private Vec myPosBackup;
	private Vec myRotationBackup;

	/**
	 * @param targetCamera
	 * @param cameraPos
	 *            the new position of the targetCamera
	 * @param cameraRotation
	 *            the new rotation of the targetCamera. have to be angles (like
	 *            new Vec(-90,0,180) which would be looking at the horizon in
	 *            south direction)
	 * @param cameraOffset
	 *            the new offset of the targetCamera. (0,0,2) would mean the
	 *            camera would stay in constant distance of 2 to the center of
	 *            view. this way you can define the center of a camera and
	 *            rotate around it. for AR view just leave it null
	 */
	public CommandCameraMoveAndLookAt(GLCamera targetCamera, Vec cameraPos,
			Vec cameraRotation, Vec cameraOffset) {
		myTargetCamera = targetCamera;
		myOffset = cameraOffset;
		myPos = cameraPos;
		myRotation = cameraRotation;
	}

	/**
	 * @param targetCamera
	 * @param cameraPos
	 *            the position where to move the camera to
	 * @param targetPos
	 *            the position the camera should look at
	 */
	public CommandCameraMoveAndLookAt(GLCamera targetCamera, Vec cameraPos,
			Vec targetPos) {
		myTargetCamera = targetCamera;
		myOffset = null;
		myPos = cameraPos;
		myRotation = new Vec();
		myRotation.toAngleVec(cameraPos, targetPos);
		myRotation.x *= -1;
	}

	/**
	 * if you want to leave the camera rotation as it was, use this command
	 * 
	 * @param targetCamera
	 * @param cameraPos
	 */
	public CommandCameraMoveAndLookAt(GLCamera targetCamera, Vec cameraPos) {
		myTargetCamera = targetCamera;
		myPos = cameraPos;
		myOffset = null;
		myRotation = null; // TODO make this work with rot=null
	}

	@Override
	public boolean override_do() {
		if (myOffset != null) {
			myOffsetBackup = Vec.copy(myTargetCamera.getNewCameraOffset());
			myTargetCamera.setNewCameraOffset(myOffset);
		}

		if (myPos != null) {
			myPosBackup = Vec.copy(myTargetCamera.getPosition());
			myTargetCamera.setNewPosition(myPos);
		}

		if (myRotation != null) {
			myRotationBackup = Vec.copy(myTargetCamera.getRotation());
			myTargetCamera.setNewRotation(myRotation);
		}
		return true;
	}

	@Override
	public boolean override_undo() {
		if (myOffsetBackup != null)
			myTargetCamera.setNewCameraOffset(myOffsetBackup);
		if (myPosBackup != null)
			myTargetCamera.setNewPosition(myPosBackup);
		if (myRotationBackup != null)
			myTargetCamera.myNewRotationVec = myRotationBackup;
		return true;
	}

}
