package gamelogic;

import gui.simpleUI.ModifierGroup;
import gui.simpleUI.modifiers.InfoText;
import gui.simpleUI.modifiers.PlusMinusModifier;
import de.rwth.R;

public class ActionThrowFireball extends GameAction {

	private static int myIconId = R.drawable.spaceship;

	public static final String FIREBALL_ACTION = "Throw fireball";

	public static final String LEVEL = "Level";

	public ActionThrowFireball() {
		this(FIREBALL_ACTION);
	}

	public ActionThrowFireball(String uniqueName) {
		super(uniqueName, 1, myIconId);
		addStat(new Stat(LEVEL, R.id.button1, 1));
	}

	@Override
	public ActionFeedback onActionStart(GameParticipant initiator,
			GameParticipant target) {
		ActionFeedback feedback = new ActionFeedback("Fireball");
		if (target == null) {
			feedback.addInfo("Can't attack, no enemy selected!");
			return feedback;
		}

		Stat i = initiator.getStatList().get(Stat.INTELLIGENCE);
		float damage = 0;
		if (i != null)
			damage = getStatValue(LEVEL) * i.getValue();
		feedback.addInfo("damage", damage);

		float defence = 0;
		Stat f = target.getStatList().get(Stat.FIRE_RESISTANCE);
		if (f != null)
			defence = f.getValue();
		feedback.addInfo("fire resistance of target", defence);

		damage -= defence;
		if (damage < 0)
			damage = 0;
		feedback.addInfo("final damage", damage);
		Stat hp = target.getStatList().get(Stat.HP);
		if (hp != null) {
			float newHp = hp.getValue();
			feedback.addInfo("Target HP before damage", newHp);
			newHp -= damage;
			feedback.addInfo("Target HP after damage", newHp);
			hp.setValue(newHp);
			feedback.setActionCorrectExecuted(true);
		}

		return feedback;
	}

	@Override
	public void generateViewGUI(ModifierGroup s) {
		s.addModifier(new InfoText("Fireball Level", "" + getStatValue(LEVEL)));
	}

	@Override
	public void generateEditGUI(ModifierGroup s) {
		s.addModifier(new PlusMinusModifier(R.drawable.minuscirclegray,
				R.drawable.pluscirclegray) {

			@Override
			public boolean save(double currentValue) {
				getStatList().get(LEVEL).setValue((int) currentValue);
				return true;
			}

			@Override
			public double plusEvent(double currentValue) {
				return currentValue + 1;
			}

			@Override
			public double minusEvent(double currentValue) {
				float level = getStatValue(LEVEL);
				if (currentValue - 1 < level)
					return level;
				return currentValue - 1;
			}

			@Override
			public double load() {
				return getStatValue(LEVEL);
			}

			@Override
			public String getVarName() {
				return myName;
			}
		});
	}

}
