package gamelogic;

import gui.simpleUI.ModifierGroup;
import gui.simpleUI.modifiers.InfoText;
import gui.simpleUI.modifiers.PlusMinusModifier;
import android.view.Gravity;
import de.rwth.R;

public class Booster extends GameElement {

	// some examples:
	public static final String MAX_HP_PLUS_15 = "Maximum HP + 15";
	public static final String MAX_HP_MINUS_10 = "Maximum HP - 10";
	public float myValue;
	private String myTargetStat;

	/**
	 * @param uniqueName
	 * @param iconId
	 * @param value
	 *            will be added on the stats value (e.g. +20 or -50)
	 */
	public Booster(String uniqueName, int iconId, float value) {
		super(uniqueName, iconId);
		myValue = value;
	}

	public Booster(String uniqueName, String targetStatName, int iconId,
			float value) {
		this(uniqueName, iconId, value);
		myTargetStat = targetStatName;
	}

	public float getValue(float finalValue, float originalValue) {
		return finalValue + myValue;
	}

	public String getTargetStatName() {
		return myTargetStat;
	}

	@Override
	public void generateEditGUI(ModifierGroup s) {
		s.addModifier(new PlusMinusModifier(R.drawable.minuscirclegray,
				R.drawable.pluscirclegray) {

			@Override
			public boolean save(double currentValue) {
				myValue = (float) currentValue;
				return true;
			}

			@Override
			public double plusEvent(double currentValue) {
				return currentValue + 1;
			}

			@Override
			public double minusEvent(double currentValue) {
				if (currentValue - 1 <= myValue)
					return myValue;
				return currentValue - 1;
			}

			@Override
			public double load() {
				return myValue;
			}

			@Override
			public String getVarName() {
				return myName;
			}
		});
	}

	@Override
	public void generateViewGUI(ModifierGroup s) {
		if (myValue >= 0)
			s.addModifier(new InfoText(" +" + myValue + " by " + myName,
					Gravity.RIGHT));
		else
			s.addModifier(new InfoText(" " + myValue + " by " + myName,
					Gravity.RIGHT));
	}

}
