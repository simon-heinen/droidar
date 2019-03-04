package util;

/**
 * collections are realy expensive so dont use them if performance is important!
 * use this class instead.
 * 
 * the biggest problem is that every time you work with data of a a collection,
 * an interator object will be created wich is realy expensive and will cause
 * the gc to collect those iterators very often
 * 
 * TODO write testcases to be shure everything works here!
 * 
 * @author Spobo
 * 
 * @param <T>
 */
public class EfficientList<T> {

	private static final String LOG_TAG = "Efficient List";

	protected final int INIT_SIZE = 2;

	/**
	 * is accessible to increase performance in loops
	 */
	protected Object[] myArray;

	/**
	 * the lenght of the {@link EfficientList}, should be used in any kind of
	 * loop
	 */
	public int myLength = 0;

	public EfficientList() {
		myArray = new Object[INIT_SIZE];
	}

	private EfficientList(int initSize) {
		myArray = new Object[initSize];
	}

	public synchronized boolean add(T x) {
		if (x == null) {
			Log.e(LOG_TAG, "null-object not allowed to be added to " + this);
			return false;
		}
		if (myArray == null)
			myArray = new Object[INIT_SIZE];
		resizeArrayIfNessecary();
		try {
			myArray[myLength] = x;
			myLength++;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Tryed to add " + x + " at pos " + myLength
					+ " but myArray.length is only " + myArray.length);
			Log.e(LOG_TAG, "The thread which caused this was "
					+ Thread.currentThread().toString());
			Log.e(LOG_TAG, "The error appeared here:");
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * there is a add request so check if there is a free slot in the array
	 */
	private void resizeArrayIfNessecary() {
		if (myArray.length - 1 < myLength) {
			// Log.i(LOG_TAG, "Resizing array " + myArray.toString());
			myArray = resizeArray(myArray.length, myArray);
			// Log.i(LOG_TAG, "Resized to" + myArray.toString());
		}
	}

	protected Object[] resizeArray(int oldSize, Object[] a) {
		Object[] x = new Object[oldSize * 2];
		// copy old values:
		for (int i = 0; i < oldSize; i++) {
			x[i] = a[i];
		}
		return x;
	}

	/**
	 * removes the first appearance of x from the list. (x is handled as an
	 * object to improve performance)
	 * 
	 * @param x
	 * @return
	 */
	public boolean remove(Object x) {
		if (x == null)
			return true;

		for (int i = 0; i < myArray.length; i++) {
			if (myArray[i] == x) {
				myLength--;
				removeItemFromArray(myArray, i);
				return true;
			}
		}
		return false;
	}

	protected void removeItemFromArray(Object[] a, int pos) {
		int i;
		for (i = pos; i < a.length - 1; i++) {
			a[i] = a[i + 1];
		}
		a[i] = null;
	}

	/**
	 * @param pos
	 *            should be from 0 to this.myLength-1
	 * @param item
	 * @return true if the item was inserted correctly
	 */
	public boolean insert(int pos, T item) {
		if (pos > myLength)
			return false;
		resizeArrayIfNessecary();
		int i;
		for (i = myArray.length - 1; i > pos; i--) {
			myArray[i] = myArray[i - 1];
		}
		myArray[i] = item;
		myLength++;
		return true;
	}

	/**
	 * @param x
	 *            the object to search for
	 * @return -1 if object not found and the position if its found
	 */
	public int contains(T x) {
		if (x == null) {
			return -1;
		}
		if (myArray == null) {
			return -1;
		}
		for (int i = 0; i < myArray.length; i++) {
			if (myArray[i] == x) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public T get(int pos) {
		if (pos >= myLength)
			return null;
		return (T) myArray[pos];
	}

	// TODO is this method tested??
	public void clear() {
		myLength = 0;
		myArray = new Object[INIT_SIZE];
	}

	// @Override
	// public EfficientList<T> clone() throws CloneNotSupportedException {
	// EfficientList<T> c = new EfficientList<T>();
	// Log.d("EfficientList", "Cloning " + this);
	// for (int i = 0; i < myLength; i++) {
	// T x = get(i);
	// if (x instanceof IsCloneable) {
	// Log.d("EfficientList", "    -> " + x
	// + " is clonable, so cloning it");
	// c.add((T) ((IsCloneable) x).clone());
	// } else {
	// c.add(x);
	// }
	// }
	// return c;
	// }

	public static String arrayToString(Object[] o, int size) {
		String s = "array(size=" + size + ", realSize=" + o.length + ") [";
		for (int i = 0; i < o.length; i++) {
			s += o[i] + ", ";
		}
		return s + "]";
	}

	@Override
	public String toString() {
		return arrayToString(myArray, myLength);
	}

	@SuppressWarnings("unchecked")
	public void addFrom(EfficientList<T> listToCopyEveryElementFrom) {
		if (listToCopyEveryElementFrom == null) {
			Log.e(LOG_TAG, "List to get items from was null!");
			return;
		}
		Object[] a = listToCopyEveryElementFrom.myArray;
		for (int i = 0; i < listToCopyEveryElementFrom.myLength; i++) {
			this.add((T) a[i]);
		}
	}

	public boolean isEmpty() {
		return myLength == 0;
	}

	/**
	 * this method should only be used for testing and will be removed so never
	 * use it
	 * 
	 * @return
	 */
	@Deprecated
	public Object[] getArrayCopy() {
		return myArray;
	}

	public static String arrayToString(Object[] o) {
		// TODO Auto-generated method stub
		return arrayToString(o, o.length);
	}

	public EfficientList<T> copy() {
		EfficientList<T> result = new EfficientList<T>(myLength);
		for (int i = 0; i < this.myLength; i++) {
			result.add(this.get(i));
		}
		return result;
	}
}
