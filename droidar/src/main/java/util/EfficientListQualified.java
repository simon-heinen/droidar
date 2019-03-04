package util;

/**
 * this {@link EfficientList} subclass should work like the default
 * {@link EfficientList} and when you usse insertWithDefinedQuality a second
 * array with quality indices is created and from then on updated when you do
 * something with the list
 * 
 * @author Spobo
 * 
 * @param <T>
 */
public class EfficientListQualified<T> extends EfficientList<T> {

	private static final float INIT_VALUE = 32;
	private float[] quali = new float[INIT_SIZE];

	public void add(T objToAdd, float quality) {
		if (quali == null)
			initQualiArray();
		for (int i = myLength - 1; i >= 0; i--) {
			if (quali[i] <= quality) {
				insert(i + 1, objToAdd);
				quali[i + 1] = quality;
				return;
			}
		}
		add(objToAdd);
		quali[0] = quality;
	}

	private void initQualiArray() {
		if (myArray != null) {
			quali = new float[myArray.length];
		} else {
			quali = new float[INIT_SIZE];
		}
		for (int i = 0; i < myLength; i++) {
			quali[i] = INIT_VALUE;
		}
	}

	@Override
	protected Object[] resizeArray(int oldSize, Object[] a) {
		if (quali != null) {
			float[] x = new float[oldSize * 2];
			// copy old values:
			for (int i = 0; i < oldSize; i++) {
				x[i] = quali[i];
			}
			quali = x;
		}
		return super.resizeArray(oldSize, a);
	}

	@Override
	public boolean insert(int pos, T item) {
		boolean insterOk = super.insert(pos, item);
		if (insterOk && quali != null) {
			int i;
			for (i = quali.length - 1; i > pos; i--) {
				quali[i] = quali[i - 1];
			}
			quali[i] = INIT_VALUE;
			return insterOk;
		}
		return insterOk;
	}

	@Override
	protected void removeItemFromArray(Object[] a, int pos) {
		if (quali != null) {
			int i;
			for (i = pos; i < quali.length - 1; i++) {
				quali[i] = quali[i + 1];
			}
			quali[i] = 0;
		}
		super.removeItemFromArray(a, pos);
	}

	@Override
	public boolean add(T x) {
		boolean result = super.add(x);
		if (quali != null)
			quali[myLength - 1] = INIT_VALUE;
		return result;
	}

	public void printDebugInfos() {
		Log.d("EfficientList", "myLength=" + myLength);
		for (int i = 0; i < myArray.length; i++) {
			if (myArray[i] != null) {
				Log.d("EfficientList",
						"entry " + i + "=" + myArray[i].getClass());
			} else {
				Log.d("EfficientList", "entry " + i + "=null");
			}
			Log.d("EfficientList", "quali entry " + i + "=" + quali[i]);
		}
	}

	// @Override
	// public EfficientListQualified<T> clone() throws
	// CloneNotSupportedException {
	// EfficientListQualified<T> c = new EfficientListQualified<T>();
	// Log.d("EfficientList", "Cloning "+this);
	// for (int i = 0; i < myLength; i++) {
	// T x = this.get(i);
	// if (x instanceof IsCloneable) {
	// Log.d("EfficientList", "    -> " + x
	// + " is clonable, so cloning it");
	// c.add((T) ((IsCloneable) x).clone(), quali[i]);
	// } else {
	// c.add(x, quali[i]);
	// }
	//
	// }
	// return c;
	// }

}
