package worldData;

import gl.HasPosition;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;

/**
 * This class can be used to move any {@link Entity} which implements the
 * {@link HasPosition} interface (like {@link Obj} or {@link MeshComponent})
 * 
 * @author Spobo
 * 
 */
public class MoveComp implements RenderableEntity {

	/**
	 * this vector is the new position, where to send the {@link MeshComponent}
	 * of the parent {@link HasPosition} to
	 */
	public Vec myTargetPos = new Vec();
	private float mySpeedFactor;
	private Updateable myParent;

	/**
	 * @param speedFactor
	 *            try values from 1 to 10. bigger means faster and 20 looks
	 *            nearly like instant placing so values should be < 20!
	 */
	public MoveComp(float speedFactor) {
		this.mySpeedFactor = speedFactor;
	}

	@Override
	public boolean accept(Visitor visitor) {
		// doesn't need visitor processing..
		return false;
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);
		Vec pos = null;
		// TODO remove these 2 lines later:
		if (parent instanceof HasPosition)
			pos = ((HasPosition) parent).getPosition();

		if (pos != null) {
			Vec.morphToNewVec(pos, myTargetPos, timeDelta * mySpeedFactor);

		}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

	}
}
