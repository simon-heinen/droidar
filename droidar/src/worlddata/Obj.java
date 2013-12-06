package worlddata;

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
/**
 * Base class for storing {@link worlddata.MeshComponent}.
 */
public class Obj extends AbstractObj implements HasPosition, HasColor {

	EfficientList<Entity> mComponents = new EfficientList<Entity>();

	public void setMyComponents(EfficientList<Entity> myComponents) {
		this.mComponents = myComponents;
	}

	private MeshComponent mGraphicsComponent;

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

	/**
	 * @return - {@link gl.scenegraph.MeshComponent}
	 */
	public MeshComponent getGraphicsComponent() {
		return mGraphicsComponent;
	}

	/**
	 * is called from time to time by the {@link World} Thread.
	 * 
	 * @param timeDelta
	 *            how many ms have passed since last update
	 * @param parent - {@link worlddata.Updateable}
	 * @return - true if process successful
	 */
	@Override
	public boolean update(float timeDelta, Updateable parent) {
		final int lenght = mComponents.myLength;
		for (int i = 0; i < lenght; i++) {
			if (mComponents.get(i) != null) {
				if (!mComponents.get(i).update(timeDelta, this)) {
					remove(mComponents.get(i));
				}
			}
		}
		return true;
	}

	/**
	 * @param comp - set the component
	 */
	public void setComp(Entity comp) {
		// TODO rename to add.. and return boolean if could be added
		// TODO put the String info in the comp itself or remove it, its crap
		if (comp instanceof MeshComponent) {
			setMyGraphicsComponent((MeshComponent) comp);
		}
		if (comp != null && mComponents.contains(comp) == -1) {
			mComponents.add(comp);
		}
	}

	/**
	 * This will override the grapics component with the passed
	 * {@link MeshComponent}. Normally this method should not be used, instead
	 * use {@link Obj#setComp(Entity)} and pass the {@link MeshComponent} there
	 * as a parameter!
	 * 
	 * @param newGraphicsComponent -  {@link gl.scenegraph.MeshComponent}
	 */
	@Deprecated
	public void setMyGraphicsComponent(MeshComponent newGraphicsComponent) {
		this.mGraphicsComponent = newGraphicsComponent;
	}

	/**
	 * @return - return list of Entity
	 */
	public EfficientList<Entity> getMyComponents() {
		return mComponents;
	}

	@Override
	public void render(GL10 gl, Renderable parent) {

		if (mGraphicsComponent == null) {
			return;
		}

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

		mGraphicsComponent.render(gl, this);
	}

	@Override
	public void setOnClickCommand(Command c) {
		super.setOnClickCommand(c);
		MeshComponent m = getComp(MeshComponent.class);
		if (m != null) {
			m.enableMeshPicking(this);
		}
	}
	
	/**
	 * @param compToRemove - {@link worlddata.Entity} to remove
	 * @return - true if successful
	 */
	public boolean remove(Entity compToRemove) {
		if (compToRemove instanceof MeshComponent) {
			mGraphicsComponent = null;
		}
		return mComponents.remove(compToRemove);
	}

	/**
	 * @param componentSubclass - not sure
	 * @return true if any of the {@link Obj} {@link Entity}s is a of the
	 *         specified class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean hasComponent(Class componentSubclass) {
		if (getComp(componentSubclass) != null) {
			return true;
		}
		return false;
	}

	/**
	 * @param componentSubclass - class
	 * @return - T
	 */
	@SuppressWarnings("unchecked")
	public <T> T getComp(Class<T> componentSubclass) {

		if (componentSubclass.isAssignableFrom(MeshComponent.class)) {
			// Log.e(LOG_TAG, "Fast access to obj.meshcomp=" +
			// mGraphicsComponent);
			return (T) getGraphicsComponent();
		}

		for (int i = 0; i < mComponents.myLength; i++) {
			Entity a = mComponents.get(i);
			if (componentSubclass.isAssignableFrom(a.getClass())) {
				return (T) a;
			}
		}
		return null;
	}

	@Override
	public Vec getPosition() {
		MeshComponent g = getGraphicsComponent();
		if (g != null) {
			return g.getPosition();
		}
		return null;
	}

	@Override
	public void setPosition(Vec position) {
		MeshComponent g = getGraphicsComponent();
		if (g != null) {
			g.setPosition(position);
		}
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
}
