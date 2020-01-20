package system;

import util.EfficientList;

public interface Container<T> {

	void clear();

	/**
	 * A child which is a {@link Container} can be checked if it is cleared (use
	 * {@link Container#isCleared()}). If this is true it can be removed it can
	 * be removed from the container
	 */
    void removeEmptyItems();

	/**
	 * @return true if this object was cleared at least once and is currently
	 *         empty
	 */
    boolean isCleared();

	int length();

	EfficientList<T> getAllItems();

	boolean add(T newElement);

	boolean remove(T x);

	boolean insert(int pos, T item);

}
