package gamelogic;

import gui.simpleUI.ModifierGroup;
import util.EfficientList;

public class StatList extends GameElementList<Stat> {

	public boolean applyBooster(Booster booster) {
		String targetname = booster.getTargetStatName();
		Stat target = get(targetname);
		if (target != null) {
			return target.addBooster(booster);
		}
		return false;
	}

	/**
	 * @param boosterList
	 * @return true if all booster were applied correctly
	 */
	public boolean applyBoosterList(BoosterList boosterList) {
		int size = boosterList.length();
		EfficientList<Booster> items = boosterList.getAllItems();
		boolean result = true;
		for (int i = 0; i < size; i++) {
			result &= applyBooster(items.get(i));
		}
		return result;
	}

	@Override
	public void generateEditGUI(ModifierGroup s) {
		EfficientList<Stat> statList = getAllItems();
		int l = getAllItems().myLength;
		for (int i = 0; i < l; i++) {
			Stat o = statList.get(i);
			o.generateEditGUI(s);
			if (o.getMyBoosterList() != null)
				o.getMyBoosterList().generateViewGUI(s);
		}
	}

}
