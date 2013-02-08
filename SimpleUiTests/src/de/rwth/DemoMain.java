package de.rwth;

import java.io.IOException;

import gui.simpleUI.ModifierGroup;

import org.json.JSONException;
import org.json.JSONObject;

import util.IO;
import android.app.Activity;
import android.os.Bundle;

public class DemoMain extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			JSONObject jsonObj = new JSONObject(
					IO.convertInputStreamToString(this.getAssets().open(
							"vorlage")));
			ModifierGroup group = new ModifierGroup();
			new JsonEditorUI(jsonObj, this).customizeScreen(group, null);
			setContentView(group.getView(this));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SimpleUI.showEditScreen(this, new JsonEditorUI(jsonObj),
		// null);

	}
}