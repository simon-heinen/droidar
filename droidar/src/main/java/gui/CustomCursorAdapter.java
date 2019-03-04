package gui;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

@Deprecated
public class CustomCursorAdapter extends SimpleCursorAdapter implements
		Filterable {

	private Context context;

	private int layout;

	public CustomCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {

		super(context, layout, c, from, to);

		this.context = context;

		this.layout = layout;

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		Cursor c = getCursor();

		final LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(layout, parent, false);

		int nameCol = c.getColumnIndex(PeopleColumns.NAME);

		String name = c.getString(nameCol);

		/**
		 * 
		 * Next set the name of the entry.
		 */

		TextView name_text = new TextView(context);

		if (name_text != null) {

			name_text.setText(name);

		}

		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {

		int nameCol = c.getColumnIndex(PeopleColumns.NAME);

		String name = c.getString(nameCol);

		/**
		 * 
		 * Next set the name of the entry.
		 */

		TextView name_text = new TextView(context);

		if (name_text != null) {

			name_text.setText(name);

		}

	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {

		/*
		 * used by the textfilter interface
		 */

		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}

		StringBuilder buffer = null;

		String[] args = null;

		if (constraint != null) {

			buffer = new StringBuilder();

			buffer.append("UPPER(");

			buffer.append(PeopleColumns.NAME);

			buffer.append(") GLOB ?");

			args = new String[] { constraint.toString().toUpperCase() + "*" };

		}

		return context.getContentResolver().query(People.CONTENT_URI, null,

		buffer == null ? null : buffer.toString(), args,
				PeopleColumns.NAME + " ASC");

	}

}
