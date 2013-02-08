package gui;

import util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.rwth.R;

public class DefaultListItemView extends LinearLayout {

	TextView shortDescr;
	TextView longDescr;
	ImageView icon;
	private LinearLayout color2;

	public DefaultListItemView(Context context, MetaInfos metaInfos) {
		super(context);
		View v = View.inflate(context, R.layout.defaultlistitemview, null);
		this.removeAllViews();
		this.addView(v);
		shortDescr = (TextView) v.findViewById(R.id.ShortDescrView);
		longDescr = (TextView) v.findViewById(R.id.LongDescrView);
		icon = (ImageView) v.findViewById(R.id.IconView);
		color2 = (LinearLayout) v.findViewById(R.id.InfoSpacer);

		setToMetaInfo(context, metaInfos);

		Log.d("GUI", "Created new View for '" + metaInfos + "'!");
	}

	public void setToMetaInfo(Context context, MetaInfos metaInfos) {
		shortDescr.setText(metaInfos.getShortDescr());
		longDescr.setText(metaInfos.getLongDescrAsString());

		if (metaInfos.getColor() != null) {
			color2.setBackgroundColor(metaInfos.getColor().toIntRGB());
		}

		Bitmap image = metaInfos.getIcon(context);
		if (image != null) {
			icon.setImageBitmap(image);
		}
	}

	public void setShortDescr(String string) {
		shortDescr.setText(string);
	}

	public void setLongDescr(String string) {
		longDescr.setText(string);
	}

	public void setIcon(ImageView icon) {
		this.icon = icon;
	}

}
