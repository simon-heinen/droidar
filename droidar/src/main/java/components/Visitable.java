package components;

import worldData.Visitor;

public interface Visitable {
	/**
	 * Insert point for any {@link Visitor}. Normally just call return
	 * "visitor.default_visit(this);" in this method
	 * 
	 * @param visitor
	 * @return this return value can be processed by the custom {@link Visitor}
	 *         so its up to you as the developer what to return and if you need
	 *         this returned value
	 */
	public boolean accept(Visitor visitor);
}
