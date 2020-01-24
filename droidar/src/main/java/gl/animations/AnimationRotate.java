package gl.animations;

import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Updateable;
import worldData.Visitor;

/**
 * This animation rotates a {@link MeshComponent}
 * 
 * @author Spobo
 * 
 */
public class AnimationRotate extends GLAnimation {

	private float angle = 0;
	private final float speed;
	private final Vec rotVec;

	/**
	 * @param speed
	 *            something around 30 to 100
	 * @param rotationVector
	 */
	public AnimationRotate(float speed, Vec rotationVector) {
		this.speed = speed;
		rotVec = rotationVector;
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (angle > 360) {
			angle = 0;
		}
		angle = angle + timeDelta * speed;
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		gl.glRotatef(angle, rotVec.x, rotVec.y, rotVec.z);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

}
