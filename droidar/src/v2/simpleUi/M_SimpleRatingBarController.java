package v2.simpleUi;

import java.util.ArrayList;

import v2.simpleUi.customViews.SimpleRatingBar;
import v2.simpleUi.customViews.SimpleRatingBar.RatingItem;
import android.content.Context;
import android.view.View;

public abstract class M_SimpleRatingBarController implements ModifierInterface {

	private int undoIconId;
	private int trashIconId;
	private int badIconId;
	private int goodIconId;
	private ArrayList<RatingItem> questions;

	public M_SimpleRatingBarController(int undoIconId, int trashIconId,
			int badIconId, int goodIconId) {
		this.undoIconId = undoIconId;
		this.trashIconId = trashIconId;
		this.badIconId = badIconId;
		this.goodIconId = goodIconId;
	}

	@Override
	public View getView(Context context) {
		questions = getQuestions();
		return new SimpleRatingBar(context, undoIconId, trashIconId, badIconId,
				goodIconId, questions);
	}

	public abstract ArrayList<RatingItem> getQuestions();

	@Override
	public boolean save() {
		boolean result = true;
		for (RatingItem i : questions) {
			result &= i.save();
		}
		if (result)
			return savedAllChildrenSuccessfully();
		else
			return false;
	}

	public abstract boolean savedAllChildrenSuccessfully();

}
