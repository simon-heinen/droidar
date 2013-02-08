package commands.ui;

import geo.GMap;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import commands.undoable.UndoableCommand;

public class CommandMinimizeMap extends UndoableCommand {

	private GMap myMap;
	private float myWeight;
	private int myHeight;
	private LayoutParams myBackupParams;

	public CommandMinimizeMap(GMap map, float weight, int height) {
		myMap = map;
		myWeight = weight;
		myHeight = height;
	}

	@Override
	public boolean override_do() {
		if (myMap.getParent() instanceof View) {
			myBackupParams = ((View) myMap.getParent()).getLayoutParams();
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, myHeight);
			p.weight = myWeight;
			((View) myMap.getParent()).setLayoutParams(p);
			return true;
		}
		return false;
	}

	@Override
	public boolean override_undo() {
		if (myBackupParams != null && myMap.getParent() instanceof View) {
			((View) myMap.getParent()).setLayoutParams(myBackupParams);
			return true;
		}
		return false;
	}

}
