package commands.ui;

import geo.GMap;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import commands.undoable.UndoableCommand;

public class CommandMapEnlargeToFullScreen extends UndoableCommand {

	private GMap myMap;
	private LayoutParams myBackupParams;

	public CommandMapEnlargeToFullScreen(GMap map) {
		myMap = map;
	}

	@Override
	public boolean override_do() {
		if (myMap.getParent() instanceof View) {
			myBackupParams = ((View) myMap.getParent()).getLayoutParams();
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			p.height = LayoutParams.FILL_PARENT;
			p.weight = 0;
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
