package de.rwth;

import system.ArActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class DemoMain extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout l = new LinearLayout(this);
		l.addView(newButton("jaiqua-mesh.xml iii", "nskingr.jpg"));
		l.addView(newButton("robot-mesh.xml", "r2skin.jpg"));
		l.addView(newButton("boy_low.g3dt", "boy_lowpoly_color.png"));
		l.addView(newButton("blobbie_world_test.dae",
				"world_blobbie_blocks.png"));

		l.addView(newButton("knight.md2", "knight.jpg"));

		l.addView(newButton("redWhiteHouse.dae", "redWhiteHouse/texture0.jpg"));

		l.addView(newButton("qbob/world_blobbie_brushes.g3dt",
				"qbob/world_blobbie_blocks.png"));
		// l.addView(newButton("multipleuvs.g3dt", "multipleuvs_1.png",
		// "multipleuvs_2.png"));
		l.addView(newButton("head.obj", null));

		l.setOrientation(LinearLayout.VERTICAL);
		setContentView(l);
	}

	private View newButton(final String fileName, final String textureName) {
		Button b = new Button(this);
		b.setText("Load " + fileName);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArActivity.startWithSetup(DemoMain.this, new ModelLoaderSetup(
						fileName, textureName));
			}
		});
		return b;
	}

}