package gamelogic;

import gui.simpleUI.ModifierGroup;

import java.util.HashMap;

import system.Container;
import util.EfficientList;
import worldData.Updateable;
import android.util.Log;

public abstract class GameElementList<T extends GameElement> implements
		Updateable, Container<T> {

	private static final String LOG_TAG = "GameElementList";
	private EfficientList<T> myList = new EfficientList<T>();
	/**
	 * an additional structure for fast searching for special GameElements
	 */
	private HashMap<String, T> mySearchIndex = new HashMap<String, T>();

	@Override
	public void clear() {
		myList.clear();
		mySearchIndex.clear();
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		for (int i = 0; i < myList.myLength; i++) {
			if (!myList.get(i).update(timeDelta, this)) {
				Log.w(LOG_TAG, "Removing " + myList.get(i)
						+ " from list because it returned"
						+ " false on update()");
				myList.remove(myList.get(i));
			}
		}
		return true;
	}

	@Override
	public int length() {
		return myList.myLength;
	}

	@Override
	public EfficientList<T> getAllItems() {
		return myList;
	}

	@Override
	public boolean isCleared() {
		return myList.myLength == 0;
	}

	@Override
	public void removeEmptyItems() {
		for (int i = 0; i < myList.myLength; i++) {
			GameElement e = myList.get(i);
			if (e.shouldBeRemoved())
				myList.remove(e);

		}
	}

	@Override
	public boolean add(T item) {
		mySearchIndex.put(item.myName, item);
		return myList.add(item);
	}

	/**
	 * @param uniqueName
	 * @return null if the elemtent can't be found
	 */
	public T get(String uniqueName) {
		return mySearchIndex.get(uniqueName);
	}

	public boolean remove(String uniqueName) {
		GameElement itemToDelete = mySearchIndex.get(uniqueName);
		mySearchIndex.remove(uniqueName);
		return myList.remove(itemToDelete);
	}

	@Override
	public boolean remove(T item) {
		mySearchIndex.remove(item.myName);
		return myList.remove(item);
	}

	public void generateViewGUI(ModifierGroup s) {
		for (int i = 0; i < myList.myLength; i++) {
			myList.get(i).generateViewGUI(s);
		}
	}

	@Override
	public boolean insert(int pos, T item) {
		return myList.insert(pos, item);
	};

	public void generateEditGUI(ModifierGroup s) {
		for (int i = 0; i < myList.myLength; i++) {
			myList.get(i).generateEditGUI(s);
		}
	}

}
