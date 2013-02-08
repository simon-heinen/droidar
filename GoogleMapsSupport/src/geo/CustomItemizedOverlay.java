package geo;

import util.EfficientList;
import util.EfficientListQualified;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	/*
	 * draw custom overlays: http://stackoverflow.com/questions/2176397/want-
	 * to-draw-a-line-or-path-on- google-map-in-hellomapview
	 * 
	 * // more then 150 markers: public void createMarkers(){
	 * 
	 * for(GeoObj x :list){ OverlayItem overlayItem = new
	 * OverlayItem(x.toGeoPoint(),x.toString(), x.toString()); ItemizedOverlay
	 * itemizedOverlay=new ItemizedOverlay(null);
	 * itemizedOverlay.addOverlay(overlayItem); } itemizedOverlay.populateNow();
	 * mapOverlays.add(itemizedOverlay); }
	 */

	/**
	 * A {@link GeoObjWrapper} is a subclass of {@link OverlayItem} which
	 * additionally holds the associated {@link GeoObj} to inform it on
	 * selection etc
	 */
	private EfficientList<GeoObjWrapper> itemList;
	private GeoGraph mygraph;

	public EfficientList<GeoObjWrapper> getItemList() {
		if (itemList == null)
			itemList = new EfficientList<GeoObjWrapper>();
		return itemList;
	}

	/**
	 * @param graph
	 * @param defaultIcon
	 * @throws Exception
	 *             an exeption is thrown if the overlay couldn't be created
	 *             correctly. Don't try to add it to the mapview if this
	 *             exeption is thrown!
	 */
	public CustomItemizedOverlay(GeoGraph graph, Drawable defaultIcon)
			throws Exception {
		super(defaultIcon);
		mygraph = graph;
		setMarkerBottomCenterd(defaultIcon);
		boundCenter(defaultIcon);
		Log.e("", "myItemList=" + getItemList());

		this.populate();

	}

	@Override
	protected boolean onTap(int index) {
		return getItemList().get(index).onTab();
	}

	@Override
	protected OverlayItem createItem(int pos) {
		// this method will be used by the superclass to access the Overlay
		// items and display them
		GeoObjWrapper x = getItemList().get(pos);
		if (x == null) {
			Log.e("Gmaps",
					"CusomizedOverlay error: createItem() tried to get an null item an NULL item and will probably crash now!");
		} else if (x.getGeoObj().isDeleted()) {
			// if the GeoObj was deleted release the wrapper
			getItemList().remove(x);
		}

		return x;
	}

	@Override
	public int size() {

		if (itemList.myLength != mygraph.length()) {
			/*
			 * its important to load all objects of your own datastructure that
			 * you want to display in an prepared List which will be accessed by
			 * the ItemizedOverlay on any map change (movement, zoom,..). so
			 * convert your datastructure here and access the OverlayItems
			 * directly in the createItem method. that way it will work as fast
			 * as possible
			 */
			Log.d("LOG_TAG", "Updating gmap item overlay");
			itemList.clear();
			EfficientListQualified<GeoObj> nodes = mygraph.getAllItems();
			for (int i = 0; i < nodes.myLength; i++) {
				itemList.add(new GeoObjWrapper(nodes.get(i)));
			}
			/*
			 * and then update the overlay
			 */
			populate();
		}

		return itemList.myLength;
	}

	/**
	 * sets the appearance of items like they were pinned on the map (the center
	 * will be the middle bottom)
	 * 
	 * @param myMarker
	 */
	public static void setMarkerBottomCenterd(Drawable myMarker) {
		ItemizedOverlay.boundCenterBottom(myMarker);
	}

	@Override
	public String toString() {
		return "ItemizedOverlay with " + size() + " items in it!";
	}

}
