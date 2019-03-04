package worldData;

import system.Container;
import util.EfficientList;
import util.Log;

public class EntityList implements Entity, Container<Entity> {

	private static final String LOG_TAG = "RenderList";
	EfficientList<Entity> myItems = new EfficientList<Entity>();
	private boolean isClearedAtLeastOnce;
	private Updateable myParent;

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
	public boolean add(Entity child) {
		if (child == this) {
			Log.e(LOG_TAG, "Not allowed to add object to itself!");
			return false;
		}
		return myItems.add(child);
	}

	@Override
	public boolean remove(Entity child) {
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
	public EfficientList<Entity> getAllItems() {
		return myItems;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit((Container) this);
	}

	@Override
	public boolean insert(int pos, Entity item) {
		return myItems.insert(pos, item);
	}
}
