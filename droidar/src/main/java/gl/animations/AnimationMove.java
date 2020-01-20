package gl.animations;

import android.opengl.GLES20;

import gl.Renderable;

//import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Updateable;
import worldData.Visitor;

import static android.opengl.GLES10.glTranslatef;

public class AnimationMove extends GLAnimation {

	private final float MIN_DISTANCE = 0.01f;
	private float timeToMove;
	private Vec relativeTargetPos;
	private Vec pos;
	private boolean done;

	public AnimationMove(float timeToMove, Vec relativeTargetPos) {
		this.timeToMove = timeToMove;
		this.relativeTargetPos = relativeTargetPos;
		pos = new Vec();
	}

	@Override
	public void render(GLES20 unused, Renderable parent) {
		glTranslatef(pos.x, pos.y, pos.z);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (!done) {
			Vec.morphToNewVec(pos, relativeTargetPos, timeDelta / timeToMove);
			if (Vec.distance(pos, relativeTargetPos) < MIN_DISTANCE) {
				done = true;
			}
		}
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}
}
