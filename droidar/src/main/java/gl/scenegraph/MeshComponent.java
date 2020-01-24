package gl.scenegraph;

import geo.GeoObj;
import gl.Color;
import gl.HasColor;
import gl.HasPosition;
import gl.HasRotation;
import gl.HasScale;
import gl.LightSource;
import gl.ObjectPicker;
import gl.Renderable;
import gl.animations.GLAnimation;

import javax.microedition.khronos.opengles.GL10;

import listeners.SelectionListener;
import system.Container;
import util.EfficientList;
import util.Log;
import util.Vec;
import util.Wrapper;
import worldData.Obj;
import worldData.RenderableEntity;
import worldData.Updateable;
import android.opengl.Matrix;

import commands.Command;
import commands.undoable.UndoableCommand;

/**
 * This is a subclass of {@link RenderableEntity} and it can be used for any
 * type of World Object which has a position ( {@link HasPosition} ), a
 * {@link Color}, a rotation ( {@link HasRotation} ) and a scale (
 * {@link HasScale} ). <br>
 * It can have children {@link MeshComponent#addChild(RenderableEntity)} so also
 * a {@link Shape} or {@link LightSource} can have direct children if required.
 * A special type of child is the {@link GLAnimation} (which is a
 * {@link RenderableEntity} as well).
 * 
 * @author Spobo
 * 
 */
public abstract class MeshComponent implements RenderableEntity,
		SelectionListener, HasPosition, HasColor, HasRotation, HasScale {

	private static final String LOG_TAG = "MeshComp";
	/**
	 * positive x value is in east direction (along red axis) positive y value
	 * is i north direction (along green axis) positive z value is in sky
	 * direction
	 */
	protected Vec myPosition;
	/**
	 * a vector that describes how the MeshComp is rotated. For example:
	 * Vec(90,0,0) would rotate it 90 degree around the x axis
	 */
	private Vec myRotation;
	private Vec myScale;
	private Color myColor;

	private Color myPickColor;

	@Deprecated
	private boolean graficAnimationActive = true;
	private RenderableEntity myChildren;

	private Updateable myParent;

	private Command myOnClickCommand;
	private Command myOnLongClickCommand;
	private Command myOnMapClickCommand;
	private Command myOnDoubleClickCommand;

	/**
	 * how to extract euler angles from a rotation matrix
	 * http://paulbourke.net/geometry/eulerangle/ TODO provide a method for this
	 * extraction
	 */
	private float[] markerRotationMatrix;

	/**
	 * for now only used for marker detection
	 */
	public void setRotationMatrix(float[] rotationMatrix) {
		this.markerRotationMatrix = rotationMatrix;
	}

	@Override
	public Vec getRotation() {
		return myRotation;
	}

	@Override
	public Vec getScale() {
		return myScale;
	}

	@Override
	public void setScale(Vec scale) {
		if (myScale == null)
			myScale = scale.copy();
		else
			myScale.setToVec(scale);
	}

	@Override
	public void setRotation(Vec rotation) {
		if (myRotation == null)
			myRotation = rotation.copy();
		else
			myRotation.setToVec(rotation);
	}

	@Override
	public void setColor(Color c) {
		if (myColor == null)
			myColor = c.copy();
		else
			myColor.setTo(c);
	}

	@Override
	public Color getColor() {
		return myColor;
	}

	/**
	 * Example. An object at position 5,5,5 is rotated by an rotation matrix
	 * (set via {@link MeshComponent#setRotationMatrix(float[])} and we want to
	 * know there the point 0,0,1 (which normaly without rotation would be at
	 * 5,5,6 ) is now. then we can use this method and pass 0,0,1 and we will
	 * get the correct world coordinates
	 * 
	 * @param modelSpaceCoords
	 * @return the coordinates in the world system
	 */
	public Vec getWorldCoordsFromModelSpacePosition(Vec modelSpaceCoords) {
		float[] resultVec = new float[3];
		float[] modelSpaceCoordsVec = { modelSpaceCoords.x, modelSpaceCoords.y,
				modelSpaceCoords.z };
		Matrix.multiplyMV(resultVec, 0, markerRotationMatrix, 0,
				modelSpaceCoordsVec, 0);
		return new Vec(resultVec[0] + myPosition.x,
				resultVec[1] + myPosition.y, resultVec[2] + myPosition.z);
	}

	@Override
	public Vec getPosition() {
		if (myPosition == null)
			myPosition = new Vec();
		return myPosition;
	}

	@Override
	public void setPosition(Vec position) {
		if (myPosition == null)
			myPosition = position.copy();
		else
			myPosition.setToVec(position);
	}

	protected MeshComponent(Color canBeNull) {
		this.myColor = canBeNull;
	}

	/**
	 * resize the Mesh equally in all 3 dimensions
	 * 
	 * @param scaleRate
	 */
	public void scaleEqual(float scaleRate) {
		this.myScale = new Vec(scaleRate, scaleRate, scaleRate);
	}

	private void loadPosition(GL10 gl) {
		if (myPosition != null)
			gl.glTranslatef(myPosition.x, myPosition.y, myPosition.z);
	}

	private void loadRotation(GL10 gl) {

		if (markerRotationMatrix != null) {
			gl.glMultMatrixf(markerRotationMatrix, 0);
		}

		if (myRotation != null) {
			/*
			 * this order is important. first rotate around the blue-z-axis
			 * (like a compass) then the the green-y-axis and red-x-axis. the
			 * order of the x and y axis rotations normaly is not important but
			 * first x and then y is better in this case because of
			 * Vec.calcRotationVec which may be extendet to add also a y
			 * rotation which then would have to be rotated last to not make the
			 * x-axis rotation wrong. so z x y is the best rotation order but
			 * normaly z y x would work too:
			 */
			gl.glRotatef(myRotation.z, 0, 0, 1);
			gl.glRotatef(myRotation.x, 1, 0, 0);
			gl.glRotatef(myRotation.y, 0, 1, 0);
		}

	}

	private void setScale(GL10 gl) {
		if (myScale != null)
			gl.glScalef(myScale.x, myScale.y, myScale.z);
	}

	@Override
	public synchronized void render(GL10 gl, Renderable parent) {

		// store current matrix and then modify it:
		gl.glPushMatrix();
		loadPosition(gl);
		setScale(gl);
		loadRotation(gl);

		if (ObjectPicker.readyToDrawWithColor) {
			if (myPickColor != null) {
				gl.glColor4f(myPickColor.red, myPickColor.green,
						myPickColor.blue, myPickColor.alpha);
			} else {
				Log.d("Object Picker", "Object " + this
						+ " had no picking color");
			}
		} else if (myColor != null) {
			gl.glColor4f(myColor.red, myColor.green, myColor.blue,
					myColor.alpha);
		}

		if (myChildren != null) {
			myChildren.render(gl, this);
		}

		draw(gl, parent);
		// restore old matrix:
		gl.glPopMatrix();
	}

	/**
	 * Don't override the {@link Renderable#render(GL10, Renderable)} method if
	 * you are creating a subclass of {@link MeshComponent}. Instead implement
	 * this method and all the translation and rotation abilities of the
	 * {@link MeshComponent} will be applied automatically
	 * 
	 * @param gl
	 * @param parent
	 * @param stack
	 */
	public abstract void draw(GL10 gl, Renderable parent);

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		setMyParent(parent);

		if ((myChildren != null) && (graficAnimationActive)) {

			// if the animation does not need to be animated anymore..
			if (!myChildren.update(timeDelta, this)) {
				// ..remove it:
				Log.d(LOG_TAG, myChildren
						+ " will now be removed from mesh because it "
						+ "is finished (returned false on update())");
				myChildren = null;
			}
		}
		return true;
	}

	/**
	 * when this is called the mesh can be selected and the onClick,
	 * onLongCLick.. {@link UndoableCommand}s set for this mesh will be executed
	 * if it is clicked
	 */
	public void enableMeshPicking() {
		enableMeshPicking(this);
	}

	/**
	 * @param selectionInterface
	 *            can be the MeshComponent itself or another
	 *            {@link SelectionListener} to inform that instead (eg the
	 *            parent {@link Obj} or {@link GeoObj})
	 */
	public void enableMeshPicking(SelectionListener selectionInterface) {
		Log.d(LOG_TAG, "Enabling picking for: " + this);
		// create a random picking color:
		Color c = Color.getRandomRGBColor();
		if (myColor != null) {
			// if the mesh has a color, use this to avoid screen-flashing;
			c.copyValues(myColor);
		}
		Log.v(LOG_TAG, "   > Sending " + c + " to ColorPicker");

		Wrapper selectionsWrapper = new Wrapper(selectionInterface);

		myPickColor = ObjectPicker.getInstance().registerMesh(
				selectionsWrapper, c);
		Log.v(LOG_TAG, "   > myPickColor=" + myPickColor);
	}

	@Override
	public Updateable getMyParent() {
		return myParent;
	}

	@Override
	public void setMyParent(Updateable parent) {
		myParent = parent;
	}

	public void getAbsoluteMeshPosition(Vec pos) {
		if (myPosition != null) {
			pos.add(myPosition);
		}

		Updateable p = getMyParent();
		if (p instanceof MeshComponent) {
			((MeshComponent) p).getAbsoluteMeshPosition(pos);
		}

	}

	@Override
	public Command getOnClickCommand() {

		// if (myOnClickCommand == null)
		// return getMyParentObj().getOnClickCommand();
		return myOnClickCommand;
	}

	@Override
	public Command getOnLongClickCommand() {
		// if (myOnLongClickCommand == null)
		// return getMyParentObj().getOnLongClickCommand();
		return myOnLongClickCommand;
	}

	@Override
	public Command getOnMapClickCommand() {
		// if (myOnMapClickCommand == null)
		// return getMyParentObj().getOnMapClickCommand();
		return myOnMapClickCommand;
	}

	@Override
	public Command getOnDoubleClickCommand() {
		// if (myOnDoubleClickCommand == null)
		// return getMyParentObj().getOnDoubleClickCommand();
		return myOnDoubleClickCommand;
	}

	@Override
	public void setOnClickCommand(Command c) {
		enableMeshPicking(this);
		myOnClickCommand = c;
	}

	@Override
	public void setOnDoubleClickCommand(Command c) {
		enableMeshPicking(this);
		myOnDoubleClickCommand = c;
	}

	@Override
	public void setOnLongClickCommand(Command c) {
		enableMeshPicking(this);
		myOnLongClickCommand = c;
	}

	/**
	 * @param c
	 * @param objToInform
	 *            set the {@link SelectionListener} manually (eg the parent
	 *            {@link Obj}
	 */
	public void setOnClickCommand(Command c, SelectionListener objToInform) {
		enableMeshPicking(objToInform);
		myOnClickCommand = c;
	}

	public void setOnDoubleClickCommand(Command c, SelectionListener objToInform) {
		enableMeshPicking(objToInform);
		myOnDoubleClickCommand = c;
	}

	public void setOnLongClickCommand(Command c, SelectionListener objToInform) {
		enableMeshPicking(objToInform);
		myOnLongClickCommand = c;
	}

	@Override
	public void setOnMapClickCommand(Command c) {
		myOnMapClickCommand = c;
	}

	@Override
	public MeshComponent clone() throws CloneNotSupportedException {
		Log.e("", "MeshComponent.clone() subclass missed, add it there");
		return null;
	}

	/**
	 * @param child
	 */
	public void addChild(RenderableEntity child) {
		addChildToTargetsChildGroup(this, child, false);
	}

	public static void addChildToTargetsChildGroup(MeshComponent target,
			RenderableEntity a, boolean insertAtBeginnung) {
		if (a == null) {
			Log.e(LOG_TAG, "Request to add NULL object as a child to " + target
					+ " was denied!");
			return;
		}

		if (target.myChildren == null) {
			target.myChildren = a;
			return;
		}

		if (!(target.myChildren instanceof RenderList)) {
			RenderList childrenGroup = new RenderList();
			// keep the old animation:
			if (target.myChildren != null) {
				childrenGroup.add(target.myChildren);
			}
			// and change myChildren to the created group:
			target.myChildren = childrenGroup;
		}

		if (insertAtBeginnung) {
			((RenderList) target.myChildren).insert(0, a);
		} else {
			((RenderList) target.myChildren).add(a);
		}
	}

	/**
	 * An animation will be inserted at the BEGINNING of the children list. So
	 * the last animation added will be executed first by the renderer!
	 * 
	 * @param animation
	 */
	public void addAnimation(GLAnimation animation) {
		addChildToTargetsChildGroup(this, animation, true);
	}

	/**
	 * use {@link MeshComponent#removeAllChildren()} instead
	 */
	@Deprecated
	public void clearChildren() {
		myChildren = null;
	}

	/**
	 * Removes all children from this {@link MeshComponent}, also all
	 * {@link GLAnimation}s which might have been added via
	 * {@link MeshComponent#addAnimation(GLAnimation)}
	 */
	// @Override TODO
	public void removeAllChildren() {
		myChildren = null;
	}

	// @Override TODO
	/**
	 * Removes a child (this migth also be an {@link GLAnimation}) from the
	 * {@link MeshComponent}
	 * 
	 * @param meshToRemove
	 * @return
	 */
	public boolean remove(RenderableEntity meshToRemove) {
		return find(meshToRemove, true);
	}

	// @Override TODO
	public boolean contains(RenderableEntity meshToFind) {
		return find(meshToFind, false);
	}

	// @Override TODO
	private boolean find(RenderableEntity entity, boolean andRemove) {
		if (myChildren == entity) {
			if (andRemove)
				clearChildren();
			return true;
		}
		if (myChildren instanceof Container) {
			if (andRemove)
				return ((Container) myChildren).remove(entity);
			else
				return ((Container) myChildren).getAllItems().contains(entity) >= 0;
		}
		return false;
	}

	public void removeAllAnimations() {
		if (myChildren instanceof GLAnimation)
			this.clearChildren();
		if (myChildren instanceof Container)
			removeAllElementsOfType(((Container) myChildren), GLAnimation.class);
	}

	/**
	 * @param c
	 *            the collection to run through. Can be
	 *            {@link MeshComponent#getChildren()} for example if the child
	 *            is a collection (check via instanceof!)
	 * @param classTypeToRemove
	 *            The class-type (like {@link GLAnimation}.class e.g.) If an
	 *            object of the specified class-type is found in the passed
	 *            {@link Container} it will be removes
	 */
	public static void removeAllElementsOfType(Container c,
			Class classTypeToRemove) {
		EfficientList list = c.getAllItems();
		for (int i = 0; i < list.myLength; i++) {
			if (classTypeToRemove.isAssignableFrom(list.get(i).getClass())) {
				c.remove(list.get(i));
			}
		}
	}

	public RenderableEntity getChildren() {
		return myChildren;
	}

}
