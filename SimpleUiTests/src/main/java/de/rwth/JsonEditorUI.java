package de.rwth;

import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;
import gui.simpleUI.Theme;
import gui.simpleUI.Theme.ThemeColors;
import gui.simpleUI.modifiers.BoolModifier;
import gui.simpleUI.modifiers.DoubleModifier;
import gui.simpleUI.modifiers.Headline;
import gui.simpleUI.modifiers.TextModifier;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonEditorUI implements EditItem {

	private JSONObject myJsonObj;
	private Theme myTheme;

	public JsonEditorUI(JSONObject jsonObj, Context context) {
		myJsonObj = jsonObj;
		myTheme = Theme.A(context, ThemeColors.initToBlack());
	}

	@Override
	public void customizeScreen(ModifierGroup group, Object message) {
		group.setTheme(myTheme); //TODO remove
		
		createModifiers(group, getJsonObj());

	}

	private JSONObject getJsonObj() {
		return myJsonObj;
	}

	private void createModifiers(ModifierGroup group, JSONObject o) {
		try {

			@SuppressWarnings("rawtypes")
			Iterator i = o.keys();
			while (i.hasNext()) {
				String attrName = (String) i.next();
				Object attr = o.get(attrName);
				if (attr instanceof String)
					addStringModifier(group, o, attrName, (String) attr);
				if (attr instanceof Double)
					addDoubleModifier(group, o, attrName, (Double) attr);
				if (attr instanceof Boolean)
					addBoolModifier(group, o, attrName, (Boolean) attr);
				if (attr instanceof JSONObject) {
					group.addModifier(new Headline(firstToUpper(attrName)));
					ModifierGroup newGroup = new ModifierGroup();
					createModifiers(newGroup, (JSONObject) attr);
					group.addModifier(newGroup);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addBoolModifier(ModifierGroup group, final JSONObject o,
			final String attrName, final Boolean attr) {
		group.addModifier(new BoolModifier() {

			@Override
			public boolean save(boolean newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public boolean loadVar() {
				return attr;
			}

			@Override
			public CharSequence getVarName() {
				return firstToUpper(attrName);
			}
		});

	}

	protected String firstToUpper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private void addDoubleModifier(ModifierGroup group, final JSONObject o,
			final String attrName, final Double attr) {
		group.addModifier(new DoubleModifier() {

			@Override
			public boolean save(double newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public double load() {
				return attr;
			}

			@Override
			public String getVarName() {
				return firstToUpper(attrName);
			}
		});

	}

	private void addStringModifier(ModifierGroup group, final JSONObject o,
			final String attrName, final String attr) {
		group.addModifier(new TextModifier() {

			@Override
			public boolean save(String newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public String load() {
				return attr;
			}

			@Override
			public String getVarName() {
				return firstToUpper(attrName);
			}
		});
	}

	private boolean saveJsonAttr(final JSONObject o, final String attrName,
			final Object attr) {
		try {
			o.put(attrName, attr);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
