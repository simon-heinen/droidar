package commands.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

import commands.Command;

public class CommandAnimateZoom extends Command {

	private Command myEndCommand;
	private View myTargetView;
	private ScaleAnimation a;

	public CommandAnimateZoom(View target, Command endCommand) {
		myEndCommand = endCommand;
		myTargetView = target;

		int from = 1;
		int to = 3;
		a = new ScaleAnimation(from, to, from, to, Animation.RELATIVE_TO_SELF,
				1, Animation.RELATIVE_TO_SELF, 1);
		a.setDuration(500);
		a.setAnimationListener(new AnimListener());

	}

	@Override
	public boolean execute() {
		myTargetView.startAnimation(a);
		return true;
	}

	private class AnimListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			if (myEndCommand != null)
				myEndCommand.execute();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

}
