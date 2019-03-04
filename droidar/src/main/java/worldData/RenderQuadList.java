package worldData;

import gl.GLCamera;
import gl.HasPosition;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import system.Container;
import util.EfficientList;
import util.QuadTree;
import util.QuadTree.ResultListener;
import util.Vec;
import android.util.Log;

/**
 * This Container structure uses a Quadtree (
 * http://en.wikipedia.org/wiki/Quadtree )
 * 
 * @author Spobo
 * 
 */
public class RenderQuadList implements RenderableEntity,
		Container<RenderableEntity> {

	private static final String LOG_TAG = "RenderQuadList";
	private float myRenderDistance;
	private float myRecalcDistanceMin;
	private float myRecalcDistanceMax;

	private EfficientList<RenderableEntity> allItems;
	private QuadTree<RenderableEntity> tree;

	@SuppressWarnings("rawtypes")
	private ResultListener itemsListener;
	private volatile EfficientList<RenderableEntity> itemsInRange;
	private float oldX;
	private float oldY;

	private GLCamera myGlCamera;
	private boolean wasClearedAtLeastOnce = false;
	private Updateable myParent;

	/**
	 * @param glCamera
	 * @param renderDistance
	 * @param recalcDistance
	 *            If you pass 10 here then the list of objects currently updated
	 *            will be refreshed every 10 meters when the user moves around
	 */
	public RenderQuadList(GLCamera glCamera, float renderDistance,
			float recalcDistance) {
		myGlCamera = glCamera;
		myRecalcDistanceMax = recalcDistance;
		myRecalcDistanceMin = -recalcDistance;
		myRenderDistance = renderDistance;

		itemsListener = new QuadTree<RenderableEntity>().new ResultListener() {
			@Override
			public void onResult(RenderableEntity myValue) {
				itemsInRange.add(myValue);
			}
		};
	}

	public EfficientList<RenderableEntity> getItems(Vec position,
			float maxDistance) {
		final EfficientList<RenderableEntity> result = new EfficientList<RenderableEntity>();
		if (tree != null) {
			tree.findInArea(tree.new ResultListener() {

				@Override
				public void onResult(RenderableEntity myValue) {
					result.add(myValue);
				}
			}, position.x, position.y, maxDistance);
		}
		return result;
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
		Vec p = myGlCamera.getPosition();
		EfficientList<RenderableEntity> list = getList(p.x, p.y);
		for (int i = 0; i < list.myLength; i++) {
			RenderableEntity obj = list.get(i);
			if (obj != null)
				obj.update(timeDelta, this);
		}
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (allItems != null)
			for (int i = 0; i < allItems.myLength; i++) {
				allItems.get(i).accept(visitor);
			}
		return true;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		Vec p = myGlCamera.getPosition();
		EfficientList<RenderableEntity> list = getList(p.x, p.y);
		for (int i = 0; i < list.myLength; i++) {
			RenderableEntity obj = list.get(i);
			if (obj != null)
				obj.render(gl, this);
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized EfficientList<RenderableEntity> getList(float x,
			float y) {
		if (itemsInRange != null
				&& needsNoRecalculation(x - oldX, myRecalcDistanceMin,
						myRecalcDistanceMax)
				&& needsNoRecalculation(y - oldY, myRecalcDistanceMin,
						myRecalcDistanceMax)) {
			return itemsInRange;
		} else {
			if (itemsInRange == null)
				itemsInRange = new EfficientList<RenderableEntity>();
			else
				itemsInRange.clear();
			oldX = x;
			oldY = y;
			refreshItemsInRangeList();
			return itemsInRange;
		}
	}

	/**
	 * Call this method to update the list of objects that are rendered and
	 * updated after you modified the quad tree
	 */
	public void refreshList() {
		if (itemsInRange == null)
			itemsInRange = new EfficientList<RenderableEntity>();
		else
			itemsInRange.clear();
		refreshItemsInRangeList();
	}

	private void refreshItemsInRangeList() {
		if (tree != null && itemsInRange != null)
			tree.findInArea(itemsListener, oldX, oldY, myRenderDistance);
	}

	private boolean needsNoRecalculation(float v, float min, float max) {
		return (min < v) && (v < max);
	}

	@Override
	public void clear() {
		if (tree != null) {
			allItems.clear();
			tree.clear();
			wasClearedAtLeastOnce = true;
			refreshItemsInRangeList();
		}
	}

	@Override
	public void removeEmptyItems() {
		if (allItems != null) {
			for (int i = 0; i < allItems.myLength; i++) {
				if (allItems.get(i) instanceof Container) {
					Container c = (Container) allItems.get(i);
					if (c.isCleared())
						remove((RenderableEntity) c);
				}
			}
		}
	}

	@Override
	public boolean isCleared() {
		if (allItems != null) {
			return allItems.isEmpty() && wasClearedAtLeastOnce;
		}
		return false;
	}

	@Override
	public int length() {
		if (allItems != null) {
			return allItems.myLength;
		}
		return 0;
	}

	@Override
	public EfficientList<RenderableEntity> getAllItems() {
		return allItems;
	}

	@Override
	public boolean add(RenderableEntity newElement) {
		if (newElement instanceof HasPosition)
			return add((HasPosition) newElement);
		Log.w(LOG_TAG, "Object was not added to the RenderQuadList "
				+ "because it had no HasPosition interface!");
		return false;
	}

	private boolean add(HasPosition x) {
		Vec pos = x.getPosition();
		if (pos != null) {
			addToTree(x, pos);
			addToAllItemsList(x);
			// refreshList(); //TODO?
			return true;
		}
		return false;
	}

	private void addToAllItemsList(HasPosition x) {
		if (allItems == null)
			allItems = new EfficientList<RenderableEntity>();
		allItems.add((RenderableEntity) x);
	}

	private boolean insertInAllItemsList(int pos, RenderableEntity item) {
		if (allItems == null)
			allItems = new EfficientList<RenderableEntity>();
		return allItems.insert(pos, item);
	}

	private void addToTree(HasPosition x, Vec pos) {
		if (tree == null)
			tree = new QuadTree<RenderableEntity>();
		tree.add(pos.x, pos.y, (RenderableEntity) x);
		refreshItemsInRangeList();
	}

	@Override
	public boolean remove(RenderableEntity x) {
		if (tree != null) {
			boolean rt = tree.remove(x);
			boolean rl = allItems.remove(x);
			refreshItemsInRangeList();
			if ((rt && !rl) || (rl && !rt))
				Log.e(LOG_TAG,
						"Inconsistency in tree und allItems-list while removing!");
			if (rt && rl)
				return true;
		}
		return false;
	}

	/**
	 * The current internal tree will be deleted and recreated. This is
	 * expensive so do not call this too often!
	 */
	public void rebuildTree() {
		if (tree != null) {
			tree.clear();
			for (int i = 0; i < allItems.myLength; i++) {
				this.add(allItems.get(i));
			}
		}
	}

	@Override
	public boolean insert(int pos, RenderableEntity item) {
		if (item instanceof HasPosition) {
			boolean result = insertInAllItemsList(pos, item);
			if (result)
				addToTree((HasPosition) item,
						((HasPosition) item).getPosition());
			return result;
		}
		Log.w(LOG_TAG, "Object was not inserted into the RenderQuadList "
				+ "because it had no HasPosition interface!");
		return false;
	}

}
