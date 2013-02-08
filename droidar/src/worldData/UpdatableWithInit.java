package worldData;

public interface UpdatableWithInit extends Updateable {

	/**
	 * this method will only be called once before the regular update mechanism
	 * is started
	 */
	public void init();

}
