package worldData;

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

public abstract class AbstractObj implements HasInfosInterface, ListItem,
		SelectionListener, EditItem, RenderableEntity {

	private MetaInfos myInfoObj;
	private Command myListClickCommand;
	private Command myListLongClickCommand;
	private ItemGuiListener myItemGuiListener;
	private Command myOnClickCommand;
	private Command myOnDoubleClickCommand;
	private Command myOnLongClickCommand;
	private Command myOnMapClickCommand;
	private Updateable myParent;

	@Override
	public MetaInfos getInfoObject() {
		if (myInfoObj == null)
			myInfoObj = new MetaInfos(this);
		return myInfoObj;
	}

	@Override
	public boolean HasInfoObject() {
		if (myInfoObj != null)
			return true;
		return false;
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;
	}

	@Override
	public Command getListClickCommand() {
		return myListClickCommand;
	}

	@Override
	public Command getListLongClickCommand() {
		return myListLongClickCommand;
	}

	@Override
	public Command getOnClickCommand() {
		return myOnClickCommand;
	}

	@Override
	public Command getOnDoubleClickCommand() {
		return myOnDoubleClickCommand;
	}

	@Override
	public Command getOnLongClickCommand() {
		return myOnLongClickCommand;
	}

	@Override
	public Command getOnMapClickCommand() {
		return myOnMapClickCommand;
	}

	@Override
	public void setOnClickCommand(Command c) {
		myOnClickCommand = c;
	}

	@Override
	public void setOnDoubleClickCommand(Command c) {
		myOnDoubleClickCommand = c;
	}

	@Override
	public void setOnLongClickCommand(Command c) {
		myOnLongClickCommand = c;
	}

	@Override
	public void setOnMapClickCommand(Command c) {
		myOnMapClickCommand = c;
	}

	public void setListClickCommand(Command clickCommand) {
		myListClickCommand = clickCommand;
	}

	public void setListLongClickCommand(Command longClickCommand) {
		myListLongClickCommand = longClickCommand;
	}

	@Override
	public View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView) {
		if (myItemGuiListener != null)
			return myItemGuiListener.requestItemView(viewToUseIfNotNull,
					parentView);
		Log.d("GUI", "    -> Loading default view for " + this.getClass());
		return getInfoObject().getDefaultListItemView(viewToUseIfNotNull,
				parentView);
	}

	public void setMyItemGuiListener(ItemGuiListener myItemGuiListener) {
		this.myItemGuiListener = myItemGuiListener;
	}

	@Override
	public void customizeScreen(ModifierGroup g, Object message) {
		getInfoObject().customizeScreen(g, message);
	}

	@Override
	public String toString() {
		if (HasInfoObject())
			return getInfoObject().getShortDescr();
		return super.toString();
	}

}
