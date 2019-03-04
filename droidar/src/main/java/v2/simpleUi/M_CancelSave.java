package v2.simpleUi;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;

public class M_CancelSave extends M_HalfHalf {

	public M_CancelSave(final String cancelText, final String saveText,
			final ModifierInterface modifierToSave) {

		super(new M_Button(cancelText) {
			@Override
			public void onClick(Context context, Button clickedButton) {
				if (context instanceof Activity)
					((Activity) context).finish();
			}
		}, new M_Button(saveText) {
			@Override
			public void onClick(Context context, Button clickedButton) {
				modifierToSave.save();
				if (context instanceof Activity)
					((Activity) context).finish();
			}
		});

	}
}
