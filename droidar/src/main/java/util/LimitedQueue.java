package util;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {

	private int limit;

	public LimitedQueue(int limit) {
		this.limit = limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
		while (size() > limit) {
			remove();
		}
	}

	@Override
	public boolean add(E o) {
		super.add(o);
		while (size() > limit) {
			super.remove();
		}
		return true;
	}
}
