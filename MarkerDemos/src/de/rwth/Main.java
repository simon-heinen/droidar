package de.rwth;

import gl.GLFactory;
import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;
import gui.simpleUI.SimpleUI;
import gui.simpleUI.modifiers.TextModifier;
import system.ArActivity;
import system.DefaultARSetup;
import system.Setup;
import worldData.World;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.rwth.setups.CollectItemsSetup;
import de.rwth.setups.DebugSetup;
import de.rwth.setups.PlaceObjectsSetup;

public class Main extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button b = generateStartButton(new MultiMarkerSetup());
		setContentView(b);
	}

	private Button generateStartButton(final Setup setup) {
		Button b = new Button(this);
		b.setText("Load " + setup.getClass().getName());
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArActivity.startWithSetup(Main.this, setup);
			}
		});
		return b;
	}
}