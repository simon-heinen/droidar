package worlddata;

import system.Container;
import util.EfficientList;
import util.Log;
/**
 *  Essentially a list class that contains multiple entities. 
 */
public class EntityList implements Entity, Container<Entity> {

	private static final String LOG_TAG = "RenderList";
	private EfficientList<Entity> mItems = new EfficientList<Entity>();
	private boolean mIsClearedAtLeastOnce;
	private Updateable mParent;

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
		for (int i = 0; i < mItems.myLength; i++) {
			if (!mItems.get(i).update(timeDelta, parent)) {
				Log.d(LOG_TAG, "Item " + mItems.get(i)
						+ " will now be removed from RenderList because it "
						+ "is finished (returned false on update())");
				mItems.remove(mItems.get(i));
			}
		}
		if (mItems.myLength == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean add(Entity child) {
		if (child == this) {
			Log.e(LOG_TAG, "Not allowed to add object to itself!");
			return false;
		}
		return mItems.add(child);
	}

	@Override
	public boolean remove(Entity child) {
		return mItems.remove(child);
	}

	@Override
	public void clear() {
		mItems.clear();
		mIsClearedAtLeastOnce = true;
	}

	@Override
	public boolean isCleared() {
		return mIsClearedAtLeastOnce;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void removeEmptyItems() {
		for (int i = 0; i < mItems.myLength; i++) {
			if (((Container) mItems.get(i)).isCleared()) {
				mItems.remove(mItems.get(i));
			}
		}
	}

	@Override
	public int length() {
		return mItems.myLength;
	}

	@Override
	public EfficientList<Entity> getAllItems() {
		return mItems;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit((Container) this);
	}

	@Override
	public boolean insert(int pos, Entity item) {
		return mItems.insert(pos, item);
	}
}
