package gl.scenegraph;

import android.opengl.GLES10;
import android.opengl.GLES20;

import gl.Color;
import gl.Renderable;

import java.util.ArrayList;

//import javax.microedition.khronos.opengles.GL10;

import util.Vec;
import worldData.Visitor;

import static android.opengl.GLES10.glLightModelf;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;

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
		if (myShapeArray == null) myShapeArray = new ArrayList<>();
		return myShapeArray;
	}

	public void add(Vec v) {
		if (myShapeArray == null) myShapeArray = new ArrayList<>();
		myShapeArray.add(v.copy());

		if (myRenderData == null) myRenderData = new RenderData();
		myRenderData.updateShape(myShapeArray);
	}

	/**
	 * use this to add multiple vectors at once and call
	 * {@link Shape#updateRenderDataManually()} after all vectors are added!
	 * 
	 * @param v
	 */
	public void addFast(Vec v) {
		if (myShapeArray == null) myShapeArray = new ArrayList<>();
		myShapeArray.add(v.copy());
	}

	/**
	 * Use this in combination with {@link Shape#addFast(Vec)}
	 */
	public void updateRenderDataManually() {
		if (myShapeArray != null) {
			if (myRenderData == null) myRenderData = new RenderData();
			myRenderData.updateShape(myShapeArray);
		}
	}

	@Override
	public void draw(GLES20 unused, Renderable parent) {
		if (myRenderData != null) {
			if (singeSide) {
				// which is the front? the one which is drawn counter clockwise
				glFrontFace(GLES10.GL_CCW);
				// enable the differentiation of which side may be visible
				glEnable(GLES10.GL_CULL_FACE);
				// which one should NOT be drawn
				glCullFace(GLES10.GL_BACK);
				glLightModelf(GLES10.GL_LIGHT_MODEL_TWO_SIDE, 0);
				myRenderData.draw(unused);

				// Disable face culling.
				glDisable(GLES10.GL_CULL_FACE);
			} else {
				/*
				 * The GL_LIGHT_MODEL_TWO_SIDE can be used to use the same
				 * normal vector and light for both sides of the mesh
				 */
				glLightModelf(GLES10.GL_LIGHT_MODEL_TWO_SIDE, 1);
				myRenderData.draw(unused);
			}
		}
	}

	public void setMyRenderData(RenderData myRenderData) {
		this.myRenderData = myRenderData;
	}

	public void setTriangleDrawing() {
		if (myRenderData == null) myRenderData = new RenderData();
		myRenderData.drawMode = GLES10.GL_TRIANGLES;
	}

	public void setLineDrawing() {
		if (myRenderData == null) myRenderData = new RenderData();
		myRenderData.drawMode = GLES10.GL_LINES;
	}

	/*
	 * also possible: GL_POINTS GL_LINE_STRIP GL_TRIANGLE_STRIP GL_TRIANGLE_FAN
	 */

	public void setLineLoopDrawing() {
		myRenderData.drawMode = GLES10.GL_LINE_LOOP;
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
