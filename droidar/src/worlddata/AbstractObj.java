package worlddata;

import gui.ListItem;
import gui.MetaInfos;
import gui.simpleUI.EditItem;
import gui.simpleUI.ModifierGroup;
import listeners.ItemGuiListener;
import listeners.SelectionListener;
import util.Log;
import android.view.View;
import android.view.ViewGroup;

import commands.Command;

/**
 * Default implementation for Objs. 
 */
public abstract class AbstractObj implements HasInfosInterface, ListItem,
		SelectionListener, EditItem, RenderableEntity {

	private MetaInfos mInfoObj;
	private Command mListClickCommand;
	private Command mListLongClickCommand;
	private ItemGuiListener mItemGuiListener;
	private Command mOnClickCommand;
	private Command mOnDoubleClickCommand;
	private Command mOnLongClickCommand;
	private Command mOnMapClickCommand;
	private Updateable mParent;

	@Override
	public MetaInfos getInfoObject() {
		if (mInfoObj == null) {
			mInfoObj = new MetaInfos(this);
		}
		return mInfoObj;
	}

	@Override
	public boolean hasInfoObject() {
		if (mInfoObj != null) {
			return true;
		}
		return false;
	}

	@Override
	public Updateable getMyParent() {
		return mParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		mParent = parent;
	}

	@Override
	public Command getListClickCommand() {
		return mListClickCommand;
	}

	@Override
	public Command getListLongClickCommand() {
		return mListLongClickCommand;
	}

	@Override
	public Command getOnClickCommand() {
		return mOnClickCommand;
	}

	@Override
	public Command getOnDoubleClickCommand() {
		return mOnDoubleClickCommand;
	}

	@Override
	public Command getOnLongClickCommand() {
		return mOnLongClickCommand;
	}

	@Override
	public Command getOnMapClickCommand() {
		return mOnMapClickCommand;
	}

	@Override
	public void setOnClickCommand(Command c) {
		mOnClickCommand = c;
	}

	@Override
	public void setOnDoubleClickCommand(Command c) {
		mOnDoubleClickCommand = c;
	}

	@Override
	public void setOnLongClickCommand(Command c) {
		mOnLongClickCommand = c;
	}

	@Override
	public void setOnMapClickCommand(Command c) {
		mOnMapClickCommand = c;
	}

	/**
	 * @param clickCommand - {@link commands.Command}
	 */
	public void setListClickCommand(Command clickCommand) {
		mListClickCommand = clickCommand;
	}

	/**
	 * @param longClickCommand - {@link commands.Command}
	 */
	public void setListLongClickCommand(Command longClickCommand) {
		mListLongClickCommand = longClickCommand;
	}

	@Override
	public View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView) {
		if (mItemGuiListener != null) {
			return mItemGuiListener.requestItemView(viewToUseIfNotNull,
					parentView);
		}
		Log.d("GUI", "    -> Loading default view for " + this.getClass());
		return getInfoObject().getDefaultListItemView(viewToUseIfNotNull,
				parentView);
	}

	/**
	 * @param pItemGuiListener - {@link listeners.ItemGuiListener}
	 */
	public void setmItemGuiListener(ItemGuiListener pItemGuiListener) {
		mItemGuiListener = pItemGuiListener;
	}

	@Override
	public void customizeScreen(ModifierGroup g, Object message) {
		getInfoObject().customizeScreen(g, message);
	}

	@Override
	public String toString() {
		if (hasInfoObject()) {
			return getInfoObject().getShortDescr();
		}
		return super.toString();
	}

}
