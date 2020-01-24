package gl.scenegraph;

import gl.Color;
import gl.Renderable;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Visitor;

public class Shape extends MeshComponent {

	private ArrayList<Vec> myShapeArray;
	protected RenderData myRenderData;
	private boolean singeSide = false;

	public Shape() {
		this(null);
	}

	public Shape(Color color) {
		super(color);
	}

	public Shape(Color color, Vec pos) {
		this(color);
		myPosition = pos;
	}

	public ArrayList<Vec> getMyShapeArray() {
		if (myShapeArray == null)
			myShapeArray = new ArrayList<Vec>();
		return myShapeArray;
	}

	public void add(Vec v) {
		if (myShapeArray == null)
			myShapeArray = new ArrayList<Vec>();
		myShapeArray.add(v.copy());

		if (myRenderData == null)
			myRenderData = new RenderData();
		myRenderData.updateShape(myShapeArray);
	}

	/**
	 * use this to add multiple vectors at once and call
	 * {@link Shape#updateRenderDataManually()} after all vectors are added!
	 * 
	 * @param v
	 */
	public void addFast(Vec v) {
		if (myShapeArray == null)
			myShapeArray = new ArrayList<Vec>();
		myShapeArray.add(v.copy());
	}

	/**
	 * Use this in combination with {@link Shape#addFast(Vec)}
	 */
	public void updateRenderDataManually() {
		if (myShapeArray != null) {
			if (myRenderData == null)
				myRenderData = new RenderData();
			myRenderData.updateShape(myShapeArray);
		}
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {
		if (myRenderData != null) {
			if (singeSide) {
				// which is the front? the one which is drawn counter clockwise
				gl.glFrontFace(GL10.GL_CCW);
				// enable the differentiation of which side may be visible
				gl.glEnable(GL10.GL_CULL_FACE);
				// which one should NOT be drawn
				gl.glCullFace(GL10.GL_BACK);
				gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0);
				myRenderData.draw(gl);

				// Disable face culling.
				gl.glDisable(GL10.GL_CULL_FACE);
			} else {
				/*
				 * The GL_LIGHT_MODEL_TWO_SIDE can be used to use the same
				 * normal vector and light for both sides of the mesh
				 */
				gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1);
				myRenderData.draw(gl);
			}
		}
	}

	public void setMyRenderData(RenderData myRenderData) {
		this.myRenderData = myRenderData;
	}

	public void setTriangleDrawing() {
		if (myRenderData == null)
			myRenderData = new RenderData();
		myRenderData.drawMode = GL10.GL_TRIANGLES;
	}

	public void setLineDrawing() {
		if (myRenderData == null)
			myRenderData = new RenderData();
		myRenderData.drawMode = GL10.GL_LINES;
	}

	/*
	 * also possible: GL_POINTS GL_LINE_STRIP GL_TRIANGLE_STRIP GL_TRIANGLE_FAN
	 */

	public void setLineLoopDrawing() {
		myRenderData.drawMode = GL10.GL_LINE_LOOP;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public String toString() {
		return "Shape " + super.toString();
	}

	public void clearShape() {
		setMyRenderData(null);
	}

	public RenderData getMyRenderData() {
		return myRenderData;
	}

}
