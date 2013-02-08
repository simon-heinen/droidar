package system;

import util.EfficientList;

public interface Container<T> {

	public void clear();

	/**
	 * A child which is a {@link Container} can be checked if it is cleared (use
	 * {@link Container#isCleared()}). If this is true it can be removed it can
	 * be removed from the container
	 */
	public void removeEmptyItems();

	/**
	 * @return true if this object was cleared at least once and is currently
	 *         empty
	 */
	public boolean isCleared();

	public int length();

	public EfficientList<T> getAllItems();

	public boolean add(T newElement);

	public boolean remove(T x);

	public boolean insert(int pos, T item);

}
