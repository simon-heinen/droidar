package gui;

import system.ActivityConnector;
import util.EfficientList;
import util.Log;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;

import commands.Command;
import commands.CommandGroup;

/**
 * will probably be replaced by a smartui class
 * 
 * @author Spobo
 * 
 */
@Deprecated
public class CustomListActivity extends ListActivity {

	ListSettings mySettings;
	// TODO why is this id static??:
	private String activityId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO write this in onresume??
		activityId = getIntent().getExtras().getString(
				ActivityConnector.KEY_IDENTIFIER);
		Object x = ActivityConnector.getInstance()
				.loadObjFromNewlyCreatedActivity(this);
		if (x instanceof ListSettings) {
			mySettings = (ListSettings) x;
			Log.d("ListActivity", "Setting adapter=" + mySettings.adapter
					+ " to CommandListActivity");
			setListAdapter(mySettings.adapter);
			this.setTitle(mySettings.getActivityTitle());
		} else {
			Log.e("ListActivity",
					"Passing CustomBasAdapter from Activity A to B failed!");
		}

		registerForContextMenu(getListView());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("ListActivity", "Creating optionsmenu");
		if (createOptionsMenu(menu)) {
			return true;
		}
		Log.d("ListActivity", "No Optionsmenu defined");
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * every time you longpress an item in the list is done. so get the
	 * commandgroup or command of the item and show all possibilities
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		if (mySettings.adapter.getItem(position) instanceof ListItem) {
			ListItem item = (ListItem) mySettings.adapter.getItem(position);
			if (!createLongClickMenu(menu, item)) {
				Log.e("ListActivity",
						"Long click menu wasn't created correctly! :(");
				menu.close();
			}
			Log.e("ListActivity", "Long click menu created correctly! :)");
		} else {
			Log.e("ListActivity",
					"Long click menu wasn't created correctly! :(");
			menu.close();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Log.d("ListActivity", "info.position=" + info.position);
		if (mySettings.adapter.getItem(info.position) instanceof ListItem) {
			ListItem listitem = (ListItem) mySettings.adapter
					.getItem(info.position);
			if (clickLongOnListItem(listitem, item.getItemId())) {
				executeCorrectLongClickCommand(listitem);
				refreshList();
				return true;
			} else {
				Log.w("ListActivity",
						"long click on item in menu wasn't executed correctly");
			}
		}
		Log.w("ListActivity", "LongCLick action wasn't executed correctly!");
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("ListActivity", "Item in list was clicked: pos=" + position
				+ " id=" + id);
		Log.d("ListActivity",
				"   -> Informing " + mySettings.adapter.getItem(position));
		if (mySettings.adapter.getItem(position) instanceof ListItem) {
			ListItem item = (ListItem) mySettings.adapter.getItem(position);
			if (clickOnListItem(item)) {
				executeCorrectClickCommand(item);
				refreshList();
				if (mySettings.closeOnCorrectClick) {
					this.finish();
				}
			} else {
				Log.w("ListActivity",
						"on click command wasnt executed correctly!");
			}
		} else {
			Log.w("ListActivity",
					"Item " + mySettings.adapter.getItem(position)
							+ " was clicked in list, but wasn't ListItem "
							+ "so nothing is done!");
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		Log.d("ListActivity", "Item in options menu clicked(featureId="
				+ featureId + ", item=" + item + ")");

		if (featureId == Window.FEATURE_CONTEXT_MENU) {
			return onContextItemSelected(item);
		}
		if (featureId == Window.FEATURE_OPTIONS_PANEL) {
			if (mySettings.myMenuCommands != null) {
				if (mySettings.myMenuCommands instanceof CommandGroup) {
					boolean b = ((CommandGroup) mySettings.myMenuCommands).myList
							.get(item.getItemId()).execute();
					refreshList();
					return b;
				}
				boolean b = mySettings.myMenuCommands.execute();
				refreshList();
				return b;
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void executeCorrectClickCommand(ListItem item) {
		if (mySettings.myCommandOnCorrectClick != null) {
			mySettings.myCommandOnCorrectClick.execute(item);
		}
	}

	private void executeCorrectLongClickCommand(ListItem item) {
		if (mySettings.myCommandOnCorrectLongClick != null) {
			mySettings.myCommandOnCorrectLongClick.execute(item);
		}
	}

	private void refreshList() {
		Log.d("ListActivity", "Trying to refresh list");
		if (mySettings.adapter instanceof BaseAdapter) {
			((BaseAdapter) mySettings.adapter).notifyDataSetChanged();
			// ((BaseAdapter) mySettings.adapter).notifyDataSetInvalidated();
			Log.d("ListActivity", "    -> List refreshed :)");
		}
	}

	public boolean clickOnListItem(ListItem i) {

		if (mySettings.myDefaultClickCommand != null) {
			return mySettings.myDefaultClickCommand.execute(i);
		}
		Command c = i.getListClickCommand();
		if (c != null) {
			return c.execute(i);
		}
		Log.w("ListActivity",
				"Item has no click command and defaultClickCommand was null too!");
		return false;
	}

	public boolean createOptionsMenu(Menu menu) {
		// check settings and load if necessary:
		if (mySettings == null) {
			Log.w("ListActivity", "mySetup was null, trying to reload it..");
			if (activityId != "") {
				Object x = ActivityConnector.getInstance().getObj(activityId);
				if (x instanceof ListSettings) {
					mySettings = (ListSettings) x;
					Log.d("ListActivity", "Setting adapter="
							+ mySettings.adapter + " to CommandListActivity");
					setListAdapter(mySettings.adapter);
				}
			}
		}
		// recheck mySettings
		if (mySettings == null) {
			Log.e("ListActivity", "mySetup could not be loaded");
			// TODO exit app?
			return false;
		}
		if (mySettings.myMenuCommands instanceof CommandGroup) {
			Log.d("ListActivity", "Menu commands loaded");
			return fillMenuWithCommandsFromCommandgroup(menu,
					(CommandGroup) mySettings.myMenuCommands);
		}
		if (mySettings.myMenuCommands != null) {
			Log.d("ListActivity", "Menu command was loaded");
			menu.add(mySettings.myMenuCommands.getInfoObject().getShortDescr());
			return true;
		}
		Log.w("ListActivity", "No menu commands were set");
		return false;

	}

	public boolean createLongClickMenu(ContextMenu menu, ListItem item) {

		if (mySettings.myDefaultLongClickCommand instanceof CommandGroup) {
			return fillMenuWithCommandsFromCommandgroup(menu,
					(CommandGroup) mySettings.myDefaultLongClickCommand);
		}
		if (mySettings.myDefaultLongClickCommand != null) {
			menu.add(mySettings.myDefaultLongClickCommand.getInfoObject()
					.getShortDescr());
			return true;
		}
		if (item.getListLongClickCommand() instanceof CommandGroup) {
			return fillMenuWithCommandsFromCommandgroup(menu,
					(CommandGroup) item.getListLongClickCommand());
		}
		if (item.getListLongClickCommand() instanceof Command) {
			// TODO maybe let ListItem implement MetaInfoInterface ??
			menu.add((item.getListLongClickCommand()).getInfoObject()
					.getShortDescr());
			return true;
		}
		return false;

	}

	private boolean fillMenuWithCommandsFromCommandgroup(Menu menu,
			CommandGroup g) {
		EfficientList<Command> a = g.myList;
		final int l = g.myList.myLength;
		for (int i = 0; i < l; i++) {
			menu.add(Menu.NONE, i, Menu.NONE, a.get(i).getInfoObject()
					.getShortDescr());
		}
		return true;
	}

	public boolean clickLongOnListItem(ListItem item, int menuId) {
		final Command defaultC = mySettings.myDefaultLongClickCommand;
		if (defaultC != null) {
			Log.d("ListActivity", "Executing default long press command: "
					+ defaultC);
			if (defaultC instanceof CommandGroup) {
				return ((CommandGroup) defaultC).myList.get(menuId).execute(
						item);
			}
			return defaultC.execute(item);
		}

		Command c = item.getListLongClickCommand();
		Log.d("ListActivity", "Executing long press command: " + c);
		if (c instanceof CommandGroup) {
			return ((CommandGroup) c).myList.get(menuId).execute(item);
		}
		if (c instanceof Command) {

			return c.execute(item);
		}
		Log.d("ListActivity",
				"Item has no long click command and defaultLongClickCommand was null too!");
		return false;
	}

	public void setCloseOnCorrectClick(boolean closeOnCorrectClick) {
		mySettings.closeOnCorrectClick = closeOnCorrectClick;
	}

}
