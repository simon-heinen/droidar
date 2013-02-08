package gamelogic;

public class BoosterList extends GameElementList<Booster> {

	public float getValue(float originalValue) {
		int length = length();
		float finalValue = originalValue;
		for (int i = 0; i < length; i++) {
			finalValue = getAllItems().get(i).getValue(finalValue,
					originalValue);
		}
		return finalValue;
	}

}
