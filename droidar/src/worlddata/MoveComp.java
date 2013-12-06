package worlddata;

import gl.HasPosition;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;

/**
 * This class can be used to move any {@link Entity} which implements the
 * {@link HasPosition} interface (like {@link Obj} or {@link gl.scenegraph.MeshComponent}).
 * 
 * @author Spobo
 * 
 */
public class MoveComp implements RenderableEntity {

	/**
	 * this vector is the new position, where to send the {@link gl.scenegraph.MeshComponent}
	 * of the parent {@link HasPosition} to.
	 */
	public Vec mTargetPos = new Vec();
	private float mSpeedFactor;
	private Updateable mParent;

	/**
	 * @param speedFactor
	 *            try values from 1 to 10. bigger means faster and 20 looks
	 *            nearly like instant placing so values should be < 20!
	 */
	public MoveComp(float speedFactor) {
		this.mSpeedFactor = speedFactor;
	}

	@Override
	public boolean accept(Visitor visitor) {
		// doesn't need visitor processing..
		return false;
	}

	@Override
	public Updateable getMyParent() {
		return mParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		mParent = parent;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);
		Vec pos = null;
		// TODO remove these 2 lines later:
		if (parent instanceof HasPosition) {
			pos = ((HasPosition) parent).getPosition();
		}

		if (pos != null) {
			Vec.morphToNewVec(pos, mTargetPos, timeDelta * mSpeedFactor);

		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

	}
}
