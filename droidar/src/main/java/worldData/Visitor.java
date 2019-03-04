package worldData;

import geo.Edge;
import geo.GeoGraph;
import geo.GeoObj;
import gl.scenegraph.Shape;
import system.Container;
import util.EfficientList;
import util.EfficientListQualified;
import util.Log;

import components.PhysicsComponent;
import components.ProximitySensor;

/**
 * the concrete visitor should override the visit methods and if the visitor
 * want to change the deeper algorithm behaviour (eg search not complete graph)
 * it has to override default_visit to (and call visit by itself!)
 * 
 * TODO create default log text with hint that the subclases all an own accept
 * method
 * 
 * TODO its important that every class wich ever want to be used with the
 * visitor pattern implements its own accept method. its not enough to implement
 * it in the superclass!
 * 
 * @author Spobo
 * 
 */
public abstract class Visitor {

	private static final String LOG_TAG = "Visitor.visit()";

	public boolean default_visit(Container<RenderableEntity> container) {
		EfficientList<RenderableEntity> list = container.getAllItems();
		if (list != null) {
			final int lenght = list.myLength;
			for (int i = 0; i < lenght; i++) {
				list.get(i).accept(this);
			}
		}
		return visit(container);
	}

	public boolean visit(Container<RenderableEntity> x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "World: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

	public boolean default_visit(Obj obj) {
		EfficientList<Entity> x = obj.myComponents;
		final int lenght = obj.myComponents.myLength;
		for (int i = 0; i < lenght; i++) {
			x.get(i).accept(this);
		}
		return visit(obj);
	}

	public boolean visit(Obj x) {
		Log.w(LOG_TAG, this.getClass().toString()
				+ "Obj: no visit action defined for classtype " + x.getClass());
		return false;
	}

	public boolean default_visit(Shape shape) {
		return visit(shape);
	}

	public boolean visit(Shape x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "Shape: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

	public boolean default_visit(Entity entity) {
		return visit(entity);
	}

	public boolean visit(Entity x) {
		Log.w(LOG_TAG,
				this.getClass().toString() + "Entity: no visit action defined "
						+ "for classtype " + x.getClass());
		return false;
	}

	public boolean default_visit(RenderableEntity renderEntity) {
		return visit(renderEntity);
	}

	public boolean visit(RenderableEntity x) {
		Log.w(LOG_TAG, this.getClass().toString()
				+ "RenderableEntity: no visit action defined for classtype "
				+ x.getClass());
		return false;
	}

	public boolean default_visit(PhysicsComponent physicsComponent) {
		return visit(physicsComponent);
	}

	public boolean visit(PhysicsComponent x) {
		Log.w(LOG_TAG, this.getClass().toString()
				+ "PhysicsComponent: no visit action defined for classtype "
				+ x.getClass());
		return false;
	}

	// TODO remove this method here, just methods with special behaviour like
	// groups need a seperate method here! remember to update uml diagramm and
	// to do this with all the default objects here
	public boolean default_visit(GeoObj geoObj) {
		return visit(geoObj);
	}

	// TODO remove too, see above
	public boolean visit(GeoObj x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "GeoObj: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

	public boolean default_visit(GeoGraph geoGraph) {
		{
			EfficientListQualified<GeoObj> geoObj = geoGraph.getAllItems();
			final int l = geoGraph.getAllItems().myLength;
			for (int i = 0; i < l; i++) {
				geoObj.get(i).accept(this);
			}
		}
		{
			if (geoGraph.hasEdges()) {
				EfficientList<Edge> e = geoGraph.getEdges();
				final int l = geoGraph.getEdges().myLength;
				for (int i = 0; i < l; i++) {
					e.get(i).accept(this);
				}
			}
		}
		return visit(geoGraph);
	}

	public boolean visit(GeoGraph x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "GeoGraph: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

	@Deprecated
	public boolean visit(AbstractObj x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "AbstractObj: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

	public boolean default_visit(ProximitySensor x) {
		return visit(x);
	}

	public boolean visit(ProximitySensor x) {
		Log.w(LOG_TAG,
				this.getClass().toString()
						+ "ProximitySensor: no visit action defined for classtype "
						+ x.getClass());
		return false;
	}

}
