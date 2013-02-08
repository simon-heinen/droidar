package gui.simpleUI.modifiers;

import gui.simpleUI.AbstractModifier;
import gui.simpleUI.SimpleUIv1;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ImageModifier extends AbstractModifier {

	private int selectedItemNr = -1;
	private ArrayList<Integer> r;

	@Override
	public View getView(final Context context) {
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		LinearLayout l = new LinearLayout(context);
		l.setPadding(SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING,
				SimpleUIv1.DEFAULT_PADDING, SimpleUIv1.DEFAULT_PADDING);
		l.setOrientation(LinearLayout.VERTICAL);

		TextView t = new TextView(context);
		t.setGravity(Gravity.CENTER_HORIZONTAL);
		t.setText(getVarName());

		l.addView(t);
		final Gallery g = new Gallery(context);
		g.setLayoutParams(p);
		g.setSpacing(10);
		// g.setUnselectedAlpha(0.1f);

		r = getImageIds();

		// TypedArray a = obtainStyledAttributes(android.R.style.Theme);
		// mGalleryItemBackground = a.getResourceId(
		// android.R.styleable.Theme_galleryItemBackground, 0);
		// a.recycle();

		g.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView iv;
				if (convertView instanceof ImageView) {
					iv = (ImageView) convertView;
				} else {
					iv = new ImageView(context);
					iv.setLayoutParams(new Gallery.LayoutParams(64, 64));
					iv.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				iv.setImageResource(r.get(position));
				return iv;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public int getCount() {
				return r.size();
			}
		});

		g.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View item,
					int position, long id) {
				selectedItemNr = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		l.addView(g);

		if (getTheme() != null) {
			getTheme().applyOuter1(l);
			getTheme().applyNormal1(g);
			getTheme().applyOuter2(t);
		}

		return l;
	}

	@Override
	public boolean save() {
		if (selectedItemNr == -1)
			return true;
		return save(r.get(selectedItemNr));
	}

	public abstract boolean save(int selectedItemId);

	public abstract ArrayList<Integer> getImageIds();

	public abstract String getVarName();

}