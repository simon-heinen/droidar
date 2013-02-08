package gui;

import system.Container;
import android.view.View;
import android.view.ViewGroup;

import commands.Command;

/**
 * Every object which has to be displayed in a {@link CustomListActivity} has to
 * implement this interface. Also see {@link Container}.
 * 
 * @author Spobo
 * 
 */
public interface ListItem {

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	View getMyListItemView(View viewToUseIfNotNull, ViewGroup parentView);

	/**
	 * @return normally this should return the default onClick command if the
	 *         class already has one
	 */
	Command getListClickCommand();

	Command getListLongClickCommand();

}
