package gamelogic;

import gui.simpleUI.ModifierGroup;
import gui.simpleUI.modifiers.InfoText;
import gui.simpleUI.modifiers.PlusMinusModifier;
import worldData.Updateable;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.rwth.R;

public class Stat extends GameElement {

	// just some examples:
	public static final String MAX_HP = "Max. HP";
	public static final String HP = "HP";
	public static final String MAX_MANA = "Max. Mana";
	public static final String MANA = "Mana";
	public static final String GOLD = "Gold";
	public static final String STRENGTH = "Strength";
	public static final String AGILITY = "Agility";
	public static final String INTELLIGENCE = "Intelligence";
	public static final String MIN_DAMAGE = "Min. Damage";
	public static final String MAX_DAMAGE = "Max. Damage";
	public static final String FIRE_RESISTANCE = "Fire resistance";

	private float myValue;
	private BoosterList myBoosterList;

	public Stat(String uniqueName, int iconId, float value) {
		super(uniqueName, iconId);
		myValue = value;
	}

	public BoosterList getMyBoosterList() {
		return myBoosterList;
	}

	@Override
	public View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView) {
		if (viewToUseIfNotNull instanceof StatListItemView) {
			((StatListItemView) viewToUseIfNotNull).updateContent();
			return viewToUseIfNotNull;
		}
		return new StatListItemView(parentView.getContext());
	}

	public float getValue() {
		if (myBoosterList != null)
			return myBoosterList.getValue(myValue);
		return myValue;
	}

	private class StatListItemView extends LinearLayout {

		private ImageView myIconView;
		private TextView myDescriptionView;
		private TextView myValueView;

		public StatListItemView(Context context) {
			super(context);
			myIconView = new ImageView(context);
			myDescriptionView = new TextView(context);
			addView(myIconView);
			LinearLayout l2 = new LinearLayout(context);
			l2.setOrientation(LinearLayout.VERTICAL);
			l2.addView(myDescriptionView);
			l2.addView(myValueView);
			addView(l2);
			updateContent();
		}

		public void updateContent() {
			myDescriptionView.setText(myName);
			myValueView.setText("" + myValue);
			if (myIconid != 0)
				myIconView.setBackgroundResource(myIconid);
		}

	}

	public boolean addBooster(Booster booster) {
		if (myBoosterList == null)
			myBoosterList = new BoosterList();
		return myBoosterList.add(booster);
	}

	public void setValue(float newValue) {
		if (myBoosterList != null) {
			float boostValue = myBoosterList.getValue(0);
			myValue = newValue - boostValue;
			// TODO redesign this, too risky this way
		} else {
			myValue = newValue;
		}
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
			s.addModifier(new InfoText(myName + ":", "+" + myValue));
		else
			s.addModifier(new InfoText(myName + ":", "" + myValue));
	}

	/**
	 * if you want to decrease the stat by -10.5 just pass -10.5f here
	 * 
	 * @param incrValue
	 */
	public void incValue(float incrValue) {
		setValue(getValue() + incrValue);
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		super.update(timeDelta, parent);
		if (myBoosterList != null)
			myBoosterList.update(timeDelta, parent);
		return true;
	}
}
