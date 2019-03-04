package gui;

import gl.Color;
import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;
import gui.simpleUI.modifiers.ColorModifier;
import gui.simpleUI.modifiers.Headline;
import gui.simpleUI.modifiers.InfoText;
import gui.simpleUI.modifiers.TextModifier;

import java.util.Arrays;

import listeners.ItemSelectedListener;
import util.EfficientList;
import util.IO;
import util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.view.ViewGroup;
import de.rwth.R;

/**
 * The {@link MetaInfos} object should be used to separate the information an
 * object holds from the system logic part of it. if every object uses an meta
 * infos element to store information about itself, it gets much easier to
 * create UI tools for visualization about different types of objects
 * 
 * @author Spobo
 * 
 */
public class MetaInfos implements EditItem {

	/**
	 * This object should help to separate single facts about the object to get
	 * a better representation
	 * 
	 * @author Spobo
	 * 
	 */
	public static class InfoElement {

		private String key;
		private String value;

		public InfoElement(String value) {
			this.value = value;
		}

		public InfoElement(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			if (key == null)
				return value;
			return key + ": " + value;
		}

		public void generateEditView(ModifierGroup g) {
			g.addModifier(new TextModifier() {

				@Override
				public boolean save(String newValue) {
					value = newValue;
					return true;
				}

				@Override
				public String load() {
					return value;
				}

				@Override
				public String getVarName() {
					if (key == null)
						return "";
					return key;
				}
			});
		}

		public void generateView(ModifierGroup g) {
			g.addModifier(new InfoText(key, value));
		}
	}

	private static final int DEFAULT_ICON = R.drawable.icon;
	private static final Color DEFAULT_COLOR = Color.whiteTransparent();
	/**
	 * this defines the maximum size of an imaged downloaded from an URL
	 */
	protected static final String SHORT_DISCR_NAME = "Name";
	protected static final String LONG_DISCR_NAME = "Desciption";
	protected static final String COLOR_NAME = "ARGB Color";

	/**
	 * the id of the icon (eg R.drawable.icon)
	 */
	private int myIconId;
	/**
	 * if myIconId is 0 and myIconURL isnt null then the icon is loaded from
	 * this url and stored in myIcon
	 */
	private String myIconURL;
	/**
	 * if myIconId is 0 and myIconURL isnt null then the icon is loaded from
	 * this url and stored in myIcon
	 */
	private Bitmap myIcon;

	private EfficientList<InfoElement> longDescr = new EfficientList<InfoElement>();
	private String shortDescr = "";
	private Color myColor;
	/**
	 * this field can be set to temporary change the appereance of the object.
	 * example: the user selects the object inside i ListView, then a
	 * CommandSetSelected will be executed which sets this field in the original
	 * {@link MetaInfos} object to not loose the original state. when the object
	 * later is unseleted the mySelectedInfos only have to be set to null
	 */
	private MetaInfos mySelectedInfos;

	public MetaInfos(Object o) {
		shortDescr = o.getClass().toString();
	}

	public MetaInfos() {
	}

	public String getLongDescrAsString() {
		if (mySelectedInfos != null)
			return mySelectedInfos.getLongDescrAsString();
		String s = "";
		for (int i = 0; i < longDescr.myLength; i++) {
			s = longDescr.get(i).toString() + "\n";
		}

		return s;
	}

	public String getShortDescr() {
		if (mySelectedInfos != null)
			return mySelectedInfos.getShortDescr();

		return shortDescr;
	}

	/**
	 * This class is called by the {@link GeoObjWrapper} and will try to load
	 * the defined {@link Drawable} for this item. It is used for a
	 * {@link CustomItemizedOverlay} on the {@link GMap} view.
	 * 
	 * @return
	 */
	public Drawable getOverlayIcon() {
		if (mySelectedInfos != null)
			return mySelectedInfos.getOverlayIcon();

		return null; // TODO load a specified drawable (myIconId e.g.)
	}

	public void setMyIconId(int myIconId) {
		this.myIconId = myIconId;
	}

	public Bitmap getIcon(Context optionalContext) {
		if (mySelectedInfos != null)
			return mySelectedInfos.getIcon(optionalContext);

		if (myIcon != null)
			return myIcon;

		if (myIconId != 0) {
			myIcon = IO.loadBitmapFromId(optionalContext, myIconId);
			return myIcon;
		}

		if (myIconURL != null) {
			myIcon = IO.loadBitmapFromURL(myIconURL);
			return myIcon;
		}

		return loadDefaultIcon(optionalContext);
	}

	public Bitmap loadDefaultIcon(Context context) {
		return IO.loadBitmapFromId(context, DEFAULT_ICON);
	}

	public Color getColor() {
		if (mySelectedInfos != null)
			return mySelectedInfos.getColor();
		if (myColor == null) {
			Log.w("",
					"MetaInfos.getColor(): myColor was null, so set do DEFAULT_COLOR");
			myColor = DEFAULT_COLOR;
		}
		return myColor;
	}

	/**
	 * @param searchTerm
	 * @return -1 if searchTerm not found
	 */
	public int matchesSearchTerm(String searchTerm) {
		String s = getShortDescr();
		if (s != null && s.toLowerCase().contains(searchTerm.toLowerCase()))
			return 1;
		s = getLongDescrAsString();
		if (s != null && s.toLowerCase().contains(searchTerm.toLowerCase()))
			return 2;
		return -1;
	}

	public void addTextToLongDescr(String info) {
		if (info != "")
			longDescr.add(new InfoElement(info));
	}

	public void addDataToLongDescr(String key, String value) {
		if (key != "" && value != "")
			longDescr.add(new InfoElement(key, value));
	}

	public String getDataFromLongDescr(String key) {
		if (longDescr == null)
			return null;
		for (int i = 0; i < longDescr.myLength; i++) {
			InfoElement item = longDescr.get(i);
			if (item.key.equals(key))
				return item.value;
		}
		return null;
	}

	public void setShortDescr(String name) {
		if (name != "")
			shortDescr = name;
	}

	public void extractInfos(Address a) {
		String s = "";
		if (a.getFeatureName() != null)
			setShortDescr(a.getFeatureName());
		else {
			setShortDescr(a.getAddressLine(0));
		}
		int i = 0;
		for (i = 0; i < a.getMaxAddressLineIndex(); i++) {
			s += a.getAddressLine(i) + "\n";
		}
		s += a.getAddressLine(i);
		if (a.getPostalCode() != null)
			s += a.getPostalCode() + " ";
		if (a.getSubAdminArea() != null)
			s += a.getSubAdminArea() + "\n";
		if (a.getCountryName() != null)
			s += a.getCountryName();
		addTextToLongDescr(s);
	}

	public void setColor(Color color) {
		myColor = color;
	}

	public void setTo(MetaInfos i) {
		setShortDescr(i.shortDescr);
		longDescr = i.longDescr;
		if (i.myColor != null)
			setColor(i.myColor.copy());
		setMyIconId(i.myIconId);
		setMyIconURL(i.myIconURL);
	}

	public int getIconId() {
		return myIconId;
	}

	public String getIconURL() {
		return myIconURL;
	}

	public android.view.View getDefaultListItemView(
			android.view.View viewToUseIfNotNull, ViewGroup parentView) {
		DefaultListItemView v = null;
		if (viewToUseIfNotNull instanceof DefaultListItemView) {
			v = (DefaultListItemView) viewToUseIfNotNull;
			v.setToMetaInfo(v.getContext(), this);
			Log.d("GUI", "    -> Text set to '" + "'");
			return v;
		}
		v = new DefaultListItemView(parentView.getContext(), this);

		return v;

	}

	public MetaInfos getSelectedInfos() {
		return mySelectedInfos;
	}

	public boolean isSelected() {
		if (mySelectedInfos != null)
			return true;
		return false;
	}

	public boolean setDeselected() {
		if (mySelectedInfos == null)
			return false;
		mySelectedInfos = null;
		return true;
	}

	/**
	 * sets the selected values (but keeps the normal values). So if the
	 * selected infos are set to null the normal values will be still available.
	 * This allows temporary style customization
	 * 
	 * @param selectedInfos
	 *            {@link MetaInfos} with selected details or null to remove the
	 *            selection
	 */
	public void setSelected(MetaInfos selectedInfos) {
		mySelectedInfos = selectedInfos;
	}

	/**
	 * example: normaly the item has the text 'test text'. when selected, it
	 * should display '< TEST TEXT >' in red
	 * 
	 * @param textPrefix
	 *            < in the example
	 * @param textPostfix
	 *            > in the example
	 * @param color
	 *            red in the example
	 * @param textInfixUppercase
	 *            true if everything should be uppercase
	 */
	public void setSelected(String textPrefix, String textPostfix, Color color,
			boolean textInfixUppercase) {

		if (textPrefix == null)
			textPrefix = "";
		if (textPostfix == null)
			textPostfix = "";

		MetaInfos m = new MetaInfos();
		if (textInfixUppercase) {
			m.setShortDescr(textPrefix + this.getShortDescr().toUpperCase()
					+ textPostfix);
		} else {
			m.setShortDescr(textPrefix + this.getShortDescr() + textPostfix);
		}
		m.setColor(color);
		m.addTextToLongDescr(this.getLongDescrAsString());
		setSelected(m);
	}

	public boolean setSelected(ItemSelectedListener myListener) {
		return myListener.onItemSelected(this);
	}

	public void setMyIconURL(String url) {
		if (url != "")
			myIconURL = url;
	}

	@Override
	public void customizeScreen(ModifierGroup group, Object message) {

		if (message instanceof String) {
			String m = (String) message;

			String[] keywords = { "Edit", "edit", "editscreen", "edit mode",
					"editmode", "Editmode" }; // TODO

			if (Arrays.asList(keywords).contains(m)) {
				getEditUI(group);
				return;
			}
		}
		getUI(group);

	}

	private void getUI(ModifierGroup group) {
		ModifierGroup infosGroup = new ModifierGroup();
		group.addModifier(infosGroup);
		try {
			infosGroup
					.addModifier(new Headline(shortDescr, myColor.toIntARGB()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModifierGroup g2 = new ModifierGroup();
		infosGroup.addModifier(g2);
		for (int i = 0; i < longDescr.myLength; i++) {
			longDescr.get(i).generateView(g2);
		}

	}

	private void getEditUI(ModifierGroup group) {
		ModifierGroup infosGroup = new ModifierGroup();
		group.addModifier(infosGroup);
		infosGroup.addModifier(new TextModifier() {
			@Override
			public boolean save(String newValue) {
				shortDescr = newValue;
				return true;
			}

			@Override
			public String load() {
				return shortDescr;
			}

			@Override
			public String getVarName() {
				return SHORT_DISCR_NAME;
			}
		});
		ModifierGroup g2 = new ModifierGroup();
		infosGroup.addModifier(g2);
		for (int i = 0; i < longDescr.myLength; i++) {
			longDescr.get(i).generateEditView(g2);
		}

		if (myColor != null) {
			infosGroup.addModifier(new ColorModifier() {

				@Override
				public String getVarName() {
					return COLOR_NAME;
				}

				@Override
				public boolean save(int a, int r, int g, int b) {
					if (myColor != null) {
						myColor.alpha = a / 255f;
						myColor.red = r / 255f;
						myColor.green = g / 255f;
						myColor.blue = b / 255f;
					}
					return true;
				}

				@Override
				public String getAlpha() {
					if (myColor != null)
						return "" + (int) (myColor.alpha * 255);
					return "0";
				}

				@Override
				public String getBlue() {
					if (myColor != null)
						return "" + (int) (myColor.blue * 255);
					return "0";
				}

				@Override
				public String getGreen() {
					if (myColor != null)
						return "" + (int) (myColor.green * 255);
					return "0";
				}

				@Override
				public String getRed() {
					if (myColor != null)
						return "" + (int) (myColor.red * 255);
					return "0";
				}
			});
		}
	}

	public EfficientList<InfoElement> getLongDescr() {
		if (mySelectedInfos != null)
			return mySelectedInfos.getLongDescr();
		return longDescr;
	}
}
