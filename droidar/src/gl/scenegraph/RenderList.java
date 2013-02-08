package gl.scenegraph;

import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import system.Container;
import util.EfficientList;
import util.Log;
import worldData.RenderableEntity;
import worldData.Updateable;
import worldData.Visitor;

public class RenderList implements RenderableEntity,
		Container<RenderableEntity> {

	private static final String LOG_TAG = "RenderList";
	EfficientList<RenderableEntity> myItems = new EfficientList<RenderableEntity>();
	private boolean isClearedAtLeastOnce;
	private Updateable myParent;

	@Override
	public void render(GL10 gl, Renderable parent) {
		for (int i = 0; i < myItems.myLength; i++) {
			myItems.get(i).render(gl, parent);
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
		for (int i = 0; i < myItems.myLength; i++) {
			if (!myItems.get(i).update(timeDelta, parent)) {
				Log.d(LOG_TAG, "Item " + myItems.get(i)
						+ " will now be removed from RenderList because it "
						+ "is finished (returned false on update())");
				myItems.remove(myItems.get(i));
			}
		}
		if (myItems.myLength == 0)
			return false;
		return true;
	}

	@Override
	public boolean add(RenderableEntity child) {
		if (child == this) {
			Log.e(LOG_TAG, "Not allowed to add object to itself!");
			return false;
		}
		return myItems.add(child);
	}

	@Override
	public boolean remove(RenderableEntity child) {
		return myItems.remove(child);
	}

	@Override
	public void clear() {
		myItems.clear();
		isClearedAtLeastOnce = true;
	}

	@Override
	public boolean isCleared() {
		return isClearedAtLeastOnce;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removeEmptyItems() {
		for (int i = 0; i < myItems.myLength; i++) {
			if (((Container) myItems.get(i)).isCleared())
				myItems.remove(myItems.get(i));
		}
	}

	@Override
	public int length() {
		return myItems.myLength;
	}

	@Override
	public EfficientList<RenderableEntity> getAllItems() {
		return myItems;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit((Container) this);
	}

	@Override
	public String toString() {
		return LOG_TAG + " (" + myItems.myLength + " items)";
	}

	@Override
	public boolean insert(int pos, RenderableEntity item) {
		return myItems.insert(pos, item);
	}
}
