package gamelogic;

import gui.simpleUI.ModifierGroup;
import gui.simpleUI.modifiers.InfoText;
import gui.simpleUI.modifiers.PlusMinusModifier;
import android.view.Gravity;
import de.rwth.R;

public class FactorBooster extends Booster {

	/**
	 * this allows bonus values like -10% (90%) or originalValue*3 (300%) for
	 * any stat
	 * 
	 * @param uniqueName
	 * @param iconId
	 * @param percentValue
	 *            will change the stats value to a specific percentlevel.
	 *            Example: statValue=50 percentValue=80 -> resultValue=40
	 */
	public FactorBooster(String uniqueName, int iconId, float percentValue) {
		super(uniqueName, iconId, percentValue);
	}

	@Override
	public float getValue(float finalValue, float originalValue) {
		/*
		 * the original manipulations of the originalValue have to be permanent.
		 * 
		 * finalValue=110
		 * 
		 * originalValue=100
		 * 
		 * percentValue=10%
		 * 
		 * so normaly return 10 but final value is 110 so return
		 * (110-100)+10%*100=20 correct
		 * 
		 * 200% -> return (110-100)+200%*100=210 also correct
		 * 
		 * 
		 * finalValue=50 originalValue=50 50% 50-100 + 50 = 0
		 */

		return (finalValue - originalValue) + originalValue * myValue / 100;
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
				return currentValue + 10;
			}

			@Override
			public double minusEvent(double currentValue) {
				if (currentValue - 1 <= myValue)
					return myValue;
				return currentValue - 10;
			}

			@Override
			public double load() {
				return myValue;
			}

			@Override
			public String getVarName() {
				return myName + " (in %)";
			}
		});
	}

	@Override
	public void generateViewGUI(ModifierGroup s) {
		if (myValue >= 0)
			s.addModifier(new InfoText(" +" + myValue + "% by " + myName,
					Gravity.RIGHT));
		else
			s.addModifier(new InfoText(" " + myValue + "% by " + myName,
					Gravity.RIGHT));
	}

}
