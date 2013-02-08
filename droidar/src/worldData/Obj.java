package worldData;

import gl.Color;
import gl.HasColor;
import gl.HasPosition;
import gl.ObjectPicker;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import util.EfficientList;
import util.Vec;

import commands.Command;

public class Obj extends AbstractObj implements HasPosition, HasColor {

	EfficientList<Entity> myComponents = new EfficientList<Entity>();

	public void setMyComponents(EfficientList<Entity> myComponents) {
		this.myComponents = myComponents;
	}

	private MeshComponent myGraphicsComponent;

	/**
	 * @return the same object as {@link Obj#getGraphicsComponent()}
	 */
	@Deprecated
	public MeshComponent getRenderComp() {
		return getGraphicsComponent();
	}

	/**
	 * @return the same object as {@link Obj#getGraphicsComponent()}
	 */
	public MeshComponent getMeshComp() {
		return getGraphicsComponent();
	}

	public MeshComponent getGraphicsComponent() {
		return myGraphicsComponent;
	}

	// public void updateComponents(Component component) {
	// Log.e("Obj.update()", "update not catched from: " + component);
	// }

	/**
	 * is called from time to time by the {@link World} Thread
	 * 
	 * @param timeDelta
	 *            how many ms have passed since last update
	 */
	@Override
	public boolean update(float timeDelta, Updateable parent) {
		final int lenght = myComponents.myLength;
		for (int i = 0; i < lenght; i++) {
			if (myComponents.get(i) != null)
				if (!myComponents.get(i).update(timeDelta, this)) {
					remove(myComponents.get(i));
				}
		}
		return true;
	}

	/**
	 * @param uniqueCompName
	 *            look into {@link Consts} and there the COMP_.. strings for
	 *            component types
	 * @param comp
	 */
	public void setComp(Entity comp) {
		// TODO rename to add.. and return boolean if could be added
		// TODO put the String info in the comp itself or remove it, its crap
		if (comp instanceof MeshComponent) {
			setMyGraphicsComponent((MeshComponent) comp);
		}
		if (comp != null && myComponents.contains(comp) == -1)
			myComponents.add(comp);
	}

	/**
	 * This will override the grapics component with the passed
	 * {@link MeshComponent}. Normally this method should not be used, instead
	 * use {@link Obj#setComp(Entity)} and pass the {@link MeshComponent} there
	 * as a parameter!
	 * 
	 * @param newGraphicsComponent
	 */
	@Deprecated
	public void setMyGraphicsComponent(MeshComponent newGraphicsComponent) {
		this.myGraphicsComponent = newGraphicsComponent;
	}

	public EfficientList<Entity> getMyComponents() {
		return myComponents;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

		if (myGraphicsComponent == null)
			return;

		/*
		 * nessecary for objects with picking disabled (wich cant be clicked).
		 * this makes sure this objects will be drawn in black so no color key
		 * in the @GlObjectPicker map will match this object
		 * 
		 * its important to do this here and not in the MeshComponent itself,
		 * because if you set a selectionColor to a meshGroup and then clear the
		 * color if a Mesh has no selectionColor all the children of the
		 * meshGroup wont have the correct selection color!
		 */
		if (ObjectPicker.readyToDrawWithColor) {
			gl.glColor4f(0, 0, 0, 1);
		} else {
			/*
			 * before drawing a new object, reset the color to white TODO
			 */
			gl.glColor4f(1, 1, 1, 1);
		}

		myGraphicsComponent.render(gl, this);
	}

	@Override
	public void setOnClickCommand(Command c) {
		super.setOnClickCommand(c);
		MeshComponent m = getComp(MeshComponent.class);
		if (m != null) {
			m.enableMeshPicking(this);
		}
	}

	public boolean remove(Entity compToRemove) {
		if (compToRemove instanceof MeshComponent)
			myGraphicsComponent = null;
		return myComponents.remove(compToRemove);
	}

	// public boolean accept(Visitor visitor) {
	// return visitor.default_visit(this);
	// }

	/**
	 * @param componentSubclass
	 * @return true if any of the {@link Obj} {@link Entity}s is a of the
	 *         specified class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean hasComponent(Class componentSubclass) {
		if (getComp(componentSubclass) != null)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T getComp(Class<T> componentSubclass) {

		if (componentSubclass.isAssignableFrom(MeshComponent.class)) {
			// Log.e(LOG_TAG, "Fast access to obj.meshcomp=" +
			// myGraphicsComponent);
			return (T) getGraphicsComponent();
		}

		for (int i = 0; i < myComponents.myLength; i++) {
			Entity a = myComponents.get(i);
			if (componentSubclass.isAssignableFrom(a.getClass()))
				return (T) a;
		}
		return null;
	}

	@Override
	public Vec getPosition() {
		MeshComponent g = getGraphicsComponent();
		if (g != null)
			return g.getPosition();
		return null;
	}

	@Override
	public void setPosition(Vec position) {
		MeshComponent g = getGraphicsComponent();
		if (g != null)
			g.setPosition(position);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public Color getColor() {
		MeshComponent g = getGraphicsComponent();
		if (g != null) {
			return g.getColor();
		}
		return null;
	}

	@Override
	public void setColor(Color c) {
		MeshComponent g = getGraphicsComponent();
		if (g != null) {
			g.setColor(c);
		}
	}

	// public String getDebugInfos() {
	// return myGraphicsComponent.toString();
	// }

	// public Component getComponent(String compName) {
	// return myComponents.get(compName);
	// }

	// @Override
	// public void setLongDescr(String info) {
	// getMyInfoObject().setLongDescr(info);
	// }

	// @Override
	// public void setShortDescr(String name) {
	// myInfoObj.setShortDescr(name);
	// }

}
