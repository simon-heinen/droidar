package util;

/**
 * This structure should be useful for large 2D worlds. It can find objects in a
 * certain area very fast (via
 * {@link QuadTree#findInArea(ResultListener, float, float, float) e.g.})
 * 
 * @param <T>
 */
public class QuadTree<T> {
	private TreeNode myRootNode;
	private int itemCount;

	private class TreeNode {
		float x, y;
		T myValue;
		TreeNode quadrant1, quadrant2, quadrant3, quadrant4;

		private TreeNode(float x, float y, T value) {
			this.x = x;
			this.y = y;
			myValue = value;
		}
	}

	public abstract class ResultListener {
		public abstract void onResult(T myValue);
	}

	public void clear() {
		myRootNode = null;
	}

	public void getAllItems(ResultListener r) {
		getAllItems(r, myRootNode);
	}

	private void getAllItems(ResultListener r, TreeNode node) {
		if (node != null) {
			r.onResult(node.myValue);
			if (node.quadrant1 != null)
				getAllItems(r, node.quadrant1);
			if (node.quadrant2 != null)
				getAllItems(r, node.quadrant2);
			if (node.quadrant3 != null)
				getAllItems(r, node.quadrant3);
			if (node.quadrant4 != null)
				getAllItems(r, node.quadrant4);
		}
	}

	/**
	 * @param newXPos
	 * @param newYPos
	 * @param value
	 * @return true if the position was updated and false if the node could not
	 *         be found
	 */
	public boolean updatePosFor(float newXPos, float newYPos, T value) {
		if (remove(value)) {
			add(newXPos, newYPos, value);
			return true;
		}
		return false;

	}

	/**
	 * @param value
	 * @return true if item was found and removed
	 */
	public boolean remove(T value) {
		return findValueEntry(myRootNode, value, true);
	}

	public boolean contains(T value) {
		return findValueEntry(myRootNode, value, false);
	}

	private boolean findValueEntry(TreeNode node, T value,
			boolean removeWhenFound) {
		if (node != null && node.myValue.equals(value)) {
			if (removeWhenFound)
				node.myValue = null;
			return true;
		} else {
			boolean result = false;
			if (node.quadrant1 != null)
				result |= findValueEntry(node.quadrant1, value, removeWhenFound);
			if (!result && node.quadrant2 != null)
				result |= findValueEntry(node.quadrant2, value, removeWhenFound);
			if (!result && node.quadrant3 != null)
				result |= findValueEntry(node.quadrant3, value, removeWhenFound);
			if (!result && node.quadrant4 != null)
				result |= findValueEntry(node.quadrant4, value, removeWhenFound);
			return result;
		}
	}

	public void add(float x, float y, T value) {
		itemCount++;
		myRootNode = add(myRootNode, x, y, value);
	}

	/**
	 * @return the number of already added elements
	 */
	public int size() {
		return itemCount;
	}

	private TreeNode add(TreeNode node, float x, float y, T value) {
		if (node == null)
			return new TreeNode(x, y, value);
		else if (node.myValue == null && canBeInsertedHere(node, x, y)) {
			node.myValue = value;
			return node;
		}
		// if dublicate objects at the same <x,y> coords should not be allowed,
		// add this line:
		// if (x == node.x && y == node.y) node.myValue = value;
		else if (x < node.x && y < node.y)
			node.quadrant3 = add(node.quadrant3, x, y, value);
		else if (x < node.x && y >= node.y)
			node.quadrant2 = add(node.quadrant2, x, y, value);
		else if (x >= node.x && y < node.y)
			node.quadrant4 = add(node.quadrant4, x, y, value);
		else if (x >= node.x && y >= node.y)
			node.quadrant1 = add(node.quadrant1, x, y, value);
		return node;
	}

	private boolean canBeInsertedHere(TreeNode node, float x, float y) {
		/*
		 * TODO would the following work? the idea is to fill the gaps which are
		 * created by updatePosFor(..). Is it sufficient to only test the direct
		 * sub-nodes of the tree or would a complete sub-node traversal be
		 * necessary?
		 */

		// if ((node.quadrant1 == null)
		// || (node.quadrant1.x > x && node.quadrant1.y > y))
		// if ((node.quadrant2 == null)
		// || (node.quadrant2.x < x && node.quadrant2.y > y))
		// if ((node.quadrant3 == null)
		// || (node.quadrant3.x < x && node.quadrant3.y < y))
		// if ((node.quadrant4 == null)
		// || (node.quadrant4.x > x && node.quadrant4.y < y))
		// return true;
		return false;
	}

	/**
	 * much more efficient then checking for a circular area!
	 * 
	 * @param resultListener
	 *            A resultListener can be created like this:
	 *            "QuadTree< hereTheElementType>.ResultListener l = quadTreeInstance.new ResultListener(){..."
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	public void findInArea(ResultListener resultListener, float xMin,
			float xMax, float yMin, float yMax) {
		findInArea(resultListener, myRootNode, xMin, xMax, yMin, yMax);
	}

	public void findInArea(ResultListener resultListener, float xCenter,
			float yCenter, float squareSize) {
		squareSize /= 2;
		findInArea(resultListener, xCenter - squareSize, xCenter + squareSize,
				yCenter - squareSize, yCenter + squareSize);
	}

	private void findInArea(ResultListener resultListener, TreeNode node,
			float xMin, float xMax, float yMin, float yMax) {
		if (node == null)
			return;

		if (node.x >= xMin && node.x <= xMax && node.y >= yMin
				&& node.y <= yMax) {
			if (node.myValue != null)
				resultListener.onResult(node.myValue);
		}
		if (xMin < node.x && yMin < node.y)
			findInArea(resultListener, node.quadrant3, xMin, xMax, yMin, yMax);
		if (xMin < node.x && yMax >= node.y)
			findInArea(resultListener, node.quadrant2, xMin, xMax, yMin, yMax);
		if (xMax >= node.x && yMin < node.y)
			findInArea(resultListener, node.quadrant4, xMin, xMax, yMin, yMax);
		if (xMax >= node.x && yMax >= node.y)
			findInArea(resultListener, node.quadrant1, xMin, xMax, yMin, yMax);
	}
}
