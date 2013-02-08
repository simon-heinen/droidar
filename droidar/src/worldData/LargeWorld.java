package worldData;

import gl.GLCamera;
import gl.HasPosition;

import javax.microedition.khronos.opengles.GL10;

import util.EfficientList;
import util.QuadTree;
import util.QuadTree.ResultListener;
import util.Vec;

/**
 * Use {@link RenderQuadList} instead!
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class LargeWorld extends World {

	private float myRenderDistance;
	private float myRecalcDistanceMin;
	private float myRecalcDistanceMax;
	private QuadTree<RenderableEntity> tree;

	@SuppressWarnings("rawtypes")
	private ResultListener itemsListener;
	private volatile EfficientList<RenderableEntity> itemsInRange;
	private float oldX;
	private float oldY;

	public LargeWorld(GLCamera glCamera, float renderDistance,
			float recalcDistance) {
		super(glCamera);
		myRenderDistance = renderDistance;
		myRecalcDistanceMin = -recalcDistance;
		myRecalcDistanceMax = recalcDistance;
		tree = new QuadTree<RenderableEntity>();

		itemsListener = tree.new ResultListener() {

			@Override
			public void onResult(RenderableEntity myValue) {
				itemsInRange.add(myValue);
			}
		};
	}

	public EfficientList<RenderableEntity> getItems(Vec position,
			float maxDistance) {

		final EfficientList<RenderableEntity> result = new EfficientList<RenderableEntity>();
		tree.findInArea(tree.new ResultListener() {

			@Override
			public void onResult(RenderableEntity myValue) {
				result.add(myValue);
			}
		}, position.x, position.y, maxDistance);
		return result;
	}

	@Override
	public boolean add(RenderableEntity x) {
		boolean result = super.add(x);
		if (result && x instanceof HasPosition)
			return add((HasPosition) x);
		return result;
	}

	private boolean add(HasPosition x) {
		Vec pos = x.getPosition();
		if (pos != null) {
			if (x instanceof RenderableEntity) {
				tree.add(pos.x, pos.y, (RenderableEntity) x);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean remove(RenderableEntity x) {
		boolean result = super.remove(x);
		if (tree.remove(x))
			return true;
		return result;
	}

	/**
	 * The current internal tree will be deleted and recreated. This is
	 * expensive so do not call this too often!
	 */
	public void rebuildTree() {
		final EfficientList<RenderableEntity> list = new EfficientList<RenderableEntity>();
		tree.getAllItems(tree.new ResultListener() {
			@Override
			public void onResult(RenderableEntity myValue) {
				list.add(myValue);
			}
		});
		tree.clear();
		for (int i = 0; i < list.myLength; i++) {
			this.add(list.get(i));
		}
	}

	@Override
	public void drawElements(GLCamera camera, GL10 gl) {

		EfficientList<RenderableEntity> list = getList(camera.getPosition().x,
				camera.getPosition().y);
		for (int i = 0; i < list.myLength; i++) {
			RenderableEntity obj = list.get(i);
			if (obj != null)
				obj.render(gl, this);
		}
		// super.drawElements(camera, gl, stack);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		EfficientList<RenderableEntity> list = getList(getMyCamera()
				.getPosition().x, getMyCamera().getPosition().y);
		for (int i = 0; i < list.myLength; i++) {
			RenderableEntity obj = list.get(i);
			if (obj != null)
				obj.update(timeDelta, this);
		}
		return true;
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
			tree.findInArea(itemsListener, x, y, myRenderDistance);
			return itemsInRange;
		}
	}

	private boolean needsNoRecalculation(float v, float min, float max) {
		return (min < v) && (v < max);
	}

}
