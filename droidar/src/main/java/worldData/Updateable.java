package worldData;

public interface Updateable {

	/**
	 * @param timeDelta
	 *            A value in seconds (normally around 0.020s). Always remember
	 *            that on different phones and depending on how many objects are
	 *            in your update cycle the update method will be called in
	 *            different time-intervals. So don't do movement updates or
	 *            anything like this without taking the passed time into
	 *            account!
	 * @return false if the element does not have to be updated anymore and can
	 *         be removed from the parent
	 */
	public boolean update(float timeDelta, Updateable parent);

}