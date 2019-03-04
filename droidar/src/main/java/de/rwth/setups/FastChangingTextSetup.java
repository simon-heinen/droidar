package de.rwth.setups;

import gl.GL1Renderer;
import gl.GLFactory;
import gl.GLText;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;

import java.util.HashMap;

import system.DefaultARSetup;
import worldData.Obj;
import worldData.World;
import android.app.Activity;

import commands.Command;

public class FastChangingTextSetup extends DefaultARSetup {

	HashMap<String, MeshComponent> textMap = new HashMap<String, MeshComponent>();
	private GLText text;

	@Override
	public void addObjectsTo(GL1Renderer renderer, World world,
			GLFactory objectFactory) {

		text = new GLText("11223344swrvgweln@@@@", myTargetActivity, textMap,
				getCamera());

		Obj o = new Obj();
		o.setComp(text);
		world.add(o);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		super._e2_addElementsToGuiSetup(guiSetup, activity);
		guiSetup.addSearchbarToView(guiSetup.getBottomView(), new Command() {

			@Override
			public boolean execute() {
				return false;
			}

			@Override
			public boolean execute(Object transfairObject) {
				if (transfairObject instanceof String) {
					String s = (String) transfairObject;
					if (text != null)
						text.changeTextTo(s);
				}
				return true;
			}
		}, "Enter text");
	}

}
