package gl.animations;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worlddata.Updateable;
import worlddata.Visitor;

/**
 * This animation rotates a {@link gl.scenegraph.MeshComponent}.
 * 
 * @author Spobo
 * 
 */
public class AnimationRotate extends GLAnimation {

	private static final int FULL_CIRCLE_IN_DEG = 360;
	private float mAngle = 0;
	private float mSpeed;
	private Vec mRotVec;

	/**
	 * @param speed
	 *            something around 30 to 100
	 * @param rotationVector
	 *            - The rotation vector for this animation.
	 */
	public AnimationRotate(float speed, Vec rotationVector) {
		this.mSpeed = speed;
		mRotVec = rotationVector;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (mAngle > FULL_CIRCLE_IN_DEG) {
			mAngle = 0;
		}
		mAngle = mAngle + (timeDelta * mSpeed);
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glRotatef(mAngle, mRotVec.x, mRotVec.y, mRotVec.z);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
