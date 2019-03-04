package gamelogic;

import gui.ListItem;
import gui.simpleUI.ModifierGroup;
import util.IO;
import worldData.Entity;
import worldData.EntityList;
import worldData.Updateable;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import commands.Command;

public abstract class GameElement implements ListItem, Updateable {

	/**
	 * if this boolean in true a {@link GameElementList#removeEmptyItems()} call
	 * will remove it from the {@link GameElementList}. It is set via
	 * {@link GameElement#setShouldBeRemoved(boolean)}
	 */
	private boolean shouldBeRemoved;
	public String myName;
	public int myIconid;
	private Command myOnClickCommand;
	private Command myListLongClickCommand;
	private EntityList myListeners;

	public GameElement(String uniqueName, int iconId) {
		myName = uniqueName;
		myIconid = iconId;
	}

	/**
	 * Will do the same as {@link GameElement#setOnListClickCommand(Command)} on
	 * default
	 * 
	 * @param myOnClickCommand
	 */
	public void setOnClickCommand(Command myOnClickCommand) {
		this.myOnClickCommand = myOnClickCommand;
	}

	/**
	 * Will do the same as {@link GameElement#setOnClickCommand(Command)} on
	 * default
	 * 
	 * @param myOnClickCommand
	 */
	public void setOnListClickCommand(Command myOnClickCommand) {
		this.myOnClickCommand = myOnClickCommand;
	}

	public Command getOnClickCommand() {
		return myOnClickCommand;
	}

	@Override
	public Command getListClickCommand() {
		return myOnClickCommand;
	}

	@Override
	public Command getListLongClickCommand() {
		return myListLongClickCommand;
	}

	@Override
	public View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView) {
		if (viewToUseIfNotNull instanceof GameElementListItemView) {
			((GameElementListItemView) viewToUseIfNotNull)
					.updateContent(parentView.getContext());
			return viewToUseIfNotNull;
		}
		return new GameElementListItemView(parentView.getContext());
	}

	public boolean shouldBeRemoved() {
		return shouldBeRemoved;
	}

	/**
	 * @param b
	 *            set this to true to remove it from a {@link GameElementList}
	 *            when {@link GameElementList#removeEmptyItems()} is called
	 */
	public void setShouldBeRemoved(boolean b) {
		shouldBeRemoved = b;
	}

	private class GameElementListItemView extends LinearLayout {

		private GameElementView myIconView;
		private TextView myDescriptionView;

		public GameElementListItemView(Context context) {
			super(context);
			myIconView = new GameElementView(context, myIconid);
			myDescriptionView = new TextView(context);
			addView(myIconView);
			addView(myDescriptionView);
			updateContent(context);
		}

		public void updateContent(Context context) {
			myDescriptionView.setText(myName);
			if (myIconid != 0)
				myIconView.setIcon(IO.loadBitmapFromId(context, myIconid));
		}

	}

	public abstract void generateViewGUI(ModifierGroup s);

	public abstract void generateEditGUI(ModifierGroup s);

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (myListeners != null && updateListeners()) {
			myListeners.update(timeDelta, this);
		}
		return true;
	}

	/**
	 * Override this method and return false when the listeners of this subclass
	 * of {@link GameElement} do not have to be updated
	 */
	public boolean updateListeners() {
		return true;
	}

	/**
	 * This will return a {@link View} (on default it is a
	 * {@link GameElementView}) which will be automatically updated
	 * 
	 * Call {@link GameElement#registerNewListener(Entity)} if you are
	 * overriding this message and want to inform the view (is then has to be an
	 * {@link Entity}) on updates of this {@link GameElement}
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public View getNewDefaultView(Context context) {
		GameElementView v = new GameElementView(context, myIconid);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myOnClickCommand != null
						&& isAllowedToExecuteOnClickAction())
					myOnClickCommand.execute(GameElement.this);
			}
		});
		registerNewListener(v);
		return v;
	}

	/**
	 * @return
	 */
	public boolean isAllowedToExecuteOnClickAction() {
		return true;
	}

	public void registerNewListener(Entity v) {
		if (myListeners == null)
			myListeners = new EntityList();
		myListeners.add(v);
	}
}
