package worldData;

import gl.CordinateAxis;
import gl.GLCamera;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import system.Container;
import util.EfficientList;
import util.Log;
import util.Vec;

//TODO not the best way to extend ArrayList here..
public class World implements RenderableEntity, Container<RenderableEntity> {

	private static final String LOG_TAG = "World";
	/**
	 * think of this as the position on the screen
	 */
	private Vec myScreenPosition;
	// private Vec myRotation;
	/**
	 * think of this as the scale of the whole world on the screen
	 */
	private Vec myScale;

	EfficientList<RenderableEntity> container;

	/**
	 * the camera which is responsible to display the world correctly
	 */
	private GLCamera myCamera;
	private boolean wasBeenClearedAtLeastOnce;
	private Updateable myParent;

	public World(GLCamera glCamera) {
		myCamera = glCamera;
	}

	@Override
	public boolean add(RenderableEntity x) {
		if (x == null) {
			return false;
		}
		if (container == null)
			container = new EfficientList<RenderableEntity>();
		/*
		 * check if obj already added before adding it to the world!
		 */
		if (container.contains(x) != -1) {
			Log.e(LOG_TAG, "Object " + x + " already contained in this world!");
			return false;
		}
		Log.v(LOG_TAG, "Adding " + x + " to " + this);
		return container.add(x);
	}

	private void glLoadScreenPosition(GL10 gl) {
		if (myScreenPosition != null)
			gl.glTranslatef(myScreenPosition.x, myScreenPosition.y,
					myScreenPosition.z);
	}

	// private void glLoadRotation(GL10 gl) {
	// if (myRotation != null) {
	// // see MeshComponent and GLCamera for more infos why this order is
	// // important:
	// gl.glRotatef(myRotation.z, 0, 0, 1);
	// gl.glRotatef(myRotation.x, 1, 0, 0);
	// gl.glRotatef(myRotation.y, 0, 1, 0);
	// }
	// }

	@Override
	public boolean accept(Visitor v) {
		return v.default_visit((Container) this);
	}

	private void glLoadScale(GL10 gl) {
		if (myScale != null)
			gl.glScalef(myScale.x, myScale.y, myScale.z);
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		// TODO reconstruct why this order is important! or wrong..
		glLoadScreenPosition(gl);
		myCamera.render(gl, this);
		// glLoadRotation(gl);
		glLoadScale(gl);

		// TODO remove the coordinate axes here:

		CordinateAxis.draw(gl);

		drawElements(myCamera, gl);

	}

	public void drawElements(GLCamera camera, GL10 gl) {
		if (container != null) {
			for (int i = 0; i < container.myLength; i++) {
				if (container.get(i) != null)
					container.get(i).render(gl, this);
			}
		}
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
		myCamera.update(timeDelta, this);
		if (container != null) {
			for (int i = 0; i < container.myLength; i++) {
				if (!container.get(i).update(timeDelta, this)) {
					Log.w(LOG_TAG, "Object " + container.get(i)
							+ " was removed from the world on "
							+ "update (because it returned false)!");
					remove(container.get(i));
				}
			}
		}
		return true;
	}

	// private void showArrayPos(final Object[] array, int i) {
	// try {
	// Log.e(LTAG, array.toString() + "[" + i + "]=" + array[i]);
	// } catch (Exception e1) {
	// Log.e(LTAG, array.toString() + "[" + i + "]=ERROR (out of bounds)");
	// }
	// }

	public GLCamera getMyCamera() {
		return myCamera;
	}

	public void setMyScreenPosition(Vec myScreenPosition) {
		this.myScreenPosition = myScreenPosition;
	}

	// public void setMyRotation(Vec myRotation) {
	// this.myRotation = myRotation;
	// }

	public void setMyScale(Vec myScale) {
		this.myScale = myScale;
	}

	public void setMyCamera(GLCamera myCamera) {
		this.myCamera = myCamera;
	}

	@Override
	public void clear() {
		container.clear();
		wasBeenClearedAtLeastOnce = true;
	}

	@Override
	public boolean isCleared() {
		return wasBeenClearedAtLeastOnce;
	}

	@Override
	public int length() {
		return container.myLength;
	}

	@Override
	public boolean remove(RenderableEntity x) {
		return container.remove(x);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removeEmptyItems() {
		for (int i = 0; i < container.myLength; i++) {
			if (container.get(i) instanceof Container) {
				if (((Container) container.get(i)).isCleared())
					container.remove(container.get(i));
			}
		}
	}

	@Override
	public boolean insert(int pos, RenderableEntity item) {
		if (container == null)
			container = new EfficientList<RenderableEntity>();
		return container.insert(pos, item);
	}

	@Override
	public EfficientList<RenderableEntity> getAllItems() {
		if (container == null)
			container = new EfficientList<RenderableEntity>();
		return container;
	}

}
