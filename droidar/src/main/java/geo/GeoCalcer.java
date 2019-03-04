package geo;

import system.Container;
import util.Log;
import worldData.LargeWorld;
import worldData.Visitor;

public class GeoCalcer extends Visitor {

	private double nullLatitude;
	private double nullLongitude;
	private double nullAltitude;

	public void setNullPos(double latitude, double longitude, double altitude) {
		nullLatitude = latitude;
		nullLongitude = longitude;
		nullAltitude = altitude;
	}

	@Override
	public boolean visit(Container x) {
		if (x instanceof LargeWorld) {
			((LargeWorld) x).rebuildTree();
		}
		return true;
	}

	@Override
	public boolean visit(GeoObj geoObj) {
		Log.d("visitor.visit()", "Calcing pos for geoObj");
		geoObj.getMySurroundGroup().setPosition(
				geoObj.getVirtualPosition(nullLatitude, nullLongitude,
						nullAltitude));
		return true;
	}

}
