package v3;

import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class M_Url implements ModifierInterface {

	private String url;

	public M_Url(String url) {
		this.url = url;
	}

	@Override
	public View getView(Context context) {
		TextView t = new TextView(context);
		t.setText(Html.fromHtml(url));
		t.setLinksClickable(true);
		Linkify.addLinks(t, Linkify.ALL);
		return t;
	}

	@Override
	public boolean save() {
		return true;
	}

}
