package gl;

import geo.GeoObj;
import gl.animations.AnimationFaceToCamera;
import gl.animations.AnimationRotate;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.MultiColoredShape;
import gl.scenegraph.Shape;
import gl.textures.TextureManager;
import gl.textures.Textured2dShape;
import gl.textures.TexturedRenderData;
import gl.textures.TexturedShape;

import javax.microedition.khronos.opengles.GL10;

import util.IO;
import util.Log;
import util.Vec;
import worldData.Obj;
import worldData.Visitor;
import worldData.World;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Use this factory to understand how to create 3D objects with {@link Shape}s
 * and {@link RenderGroup}s. Often it is more efficient to create the objects
 * you need manually and not combine objects created with this factory. The
 * benefits of algorithmic objects are that they are much more flexible and
 * random {@link Vec}tors can be used to add a unique touch to each object.
 * 
 * Loading object from externmal files like md3 is the alternative to this
 * approach.
 * 
 * 
 * @author Spobo
 * 
 */
public class GLFactory {

	private static final String LOG_TAG = "GLFactory";

	private static GLFactory myInstance = new GLFactory();

	private GLFactory() {
	}

	public static GLFactory getInstance() {
		return myInstance;
	}

	public Shape newSquare(Color canBeNull) {
		Shape s = new Shape(canBeNull);
		s.add(new Vec(-1, 1, 0));
		s.add(new Vec(-1, -1, 0));
		s.add(new Vec(1, -1, 0));

		s.add(new Vec(1, -1, 0));
		s.add(new Vec(1, 1, 0));
		s.add(new Vec(-1, 1, 0));

		return s;
	}

	public MeshComponent newCube(Color canBeNull) {
		MeshComponent g = new Shape();
		Shape s1 = newSquare(canBeNull);
		g.addChild(s1);

		Shape s2 = newSquare(canBeNull);
		s2.setPosition(new Vec(0, 0, 2));
		g.addChild(s2);

		Shape s3 = newSquare(canBeNull);
		s3.setPosition(new Vec(0, 1, 1));
		s3.setRotation(new Vec(90, 0, 0));
		g.addChild(s3);

		Shape s4 = newSquare(canBeNull);
		s4.setPosition(new Vec(0, -1, 1));
		s4.setRotation(new Vec(90, 0, 0));
		g.addChild(s4);

		return g;
	}

	public Shape newTreangle(Color canBeNull) {
		Shape s = new Shape(canBeNull);
		s.add(new Vec(0, 0, 0.8f));
		s.add(new Vec(0, 0.8f, 0));
		s.add(new Vec(0.8f, 0, 0));
		return s;
	}

	public Shape newHexagon(Color canBeNull) {
		Shape s = new Shape(canBeNull);
		s.add(new Vec(0, -1, 0));
		s.add(new Vec(0, 1, 0));
		s.add(new Vec(1, 0.5f, 0));

		s.add(new Vec(0, -1, 0));
		s.add(new Vec(0, 1, 0));
		s.add(new Vec(-1, 0.5f, 0));

		s.add(new Vec(0, -1, 0));
		s.add(new Vec(1, 0.5f, 0));
		s.add(new Vec(1, -0.5f, 0));

		s.add(new Vec(0, -1, 0));
		s.add(new Vec(-1, 0.5f, 0));
		s.add(new Vec(-1, -0.5f, 0));
		return s;
	}

	/**
	 * see {@link GLFactory#newTexturedSquare(String, Bitmap, float)}
	 * 
	 * @param bitmapName
	 * @param bitmap
	 * @return A 1 x 1 meter square
	 */
	public MeshComponent newTexturedSquare(String bitmapName, Bitmap bitmap) {
		return newTexturedSquare(bitmapName, bitmap, 1);
	}

	/**
	 * see {@link GLFactory#newTexturedSquare(String, Bitmap, float)}
	 * 
	 * @param context
	 * @param iconId
	 *            The id of the icon that should be used as the texture (will
	 *            also be used as the unique texture name)
	 * @param heightInMeters
	 * @return
	 */
	public MeshComponent newTexturedSquare(Context context, int iconId,
			float heightInMeters) {
		return newTexturedSquare("" + iconId,
				IO.loadBitmapFromId(context, iconId), heightInMeters);
	}

	/**
	 * Please read
	 * {@link TextureManager#addTexture(TexturedRenderData, Bitmap, String)} for
	 * information about the parameters.
	 * 
	 * @param bitmapName
	 *            see
	 *            {@link TextureManager#addTexture(TexturedRenderData, Bitmap, String)}
	 * @param bitmap
	 *            see
	 *            {@link TextureManager#addTexture(TexturedRenderData, Bitmap, String)}
	 * @param heightInMeters
	 *            the square will have this height and width
	 * @return
	 */
	public MeshComponent newTexturedSquare(String bitmapName, Bitmap bitmap,
			float heightInMeters) {

		if (bitmapName == null) {
			Log.e(LOG_TAG,
					"No bitmap id set, can't be added to Texture Manager!");
			return null;
		}

		if (bitmap == null) {
			Log.e(LOG_TAG, "Passed bitmap was null!");
			return null;
		}

		TexturedShape s = new TexturedShape(bitmapName, bitmap);
		float f = (float) bitmap.getHeight() / (float) bitmap.getWidth();
		float x = heightInMeters / f;

		float w2 = -x / 2;
		float h2 = -heightInMeters / 2;

		Log.d(LOG_TAG, "Creating textured mesh for " + bitmapName);
		Log.v(LOG_TAG, "   > bitmap.getHeight()=" + bitmap.getHeight());
		Log.v(LOG_TAG, "   > bitmap.getWidth()=" + bitmap.getWidth());
		Log.v(LOG_TAG, "   > height/width factor=" + f);
		Log.v(LOG_TAG, "   > w2=" + w2);
		Log.v(LOG_TAG, "   > h2=" + h2);

		s.add(new Vec(-w2, 0, -h2), 0, 0);
		s.add(new Vec(-w2, 0, h2), 0, 1);
		s.add(new Vec(w2, 0, -h2), 1, 0);

		s.add(new Vec(w2, 0, h2), 1, 1);
		s.add(new Vec(-w2, 0, h2), 0, 1);
		s.add(new Vec(w2, 0, -h2), 1, 0);

		return s;
	}

	public MeshComponent newTextured2dShape(Bitmap bitmap, String bitmapName) {
		Textured2dShape s = new Textured2dShape(bitmap, bitmapName);
		return s;
	}

	public MeshComponent newArrow() {
		Color top = Color.blue();
		Color bottom = Color.red();
		Color edge1 = Color.red();
		Color edge2 = Color.redTransparent();
		float height = 4f;
		float x = 0.7f;
		float y = 0f;
		return newArrow(x, y, height, top, edge1, bottom, edge2);
	}

	public MeshComponent newCuror() {
		Color top = Color.silver1();
		Color bottom = Color.silver2();
		Color edge1 = Color.blackTransparent();
		Color edge2 = Color.blackTransparent();
		float height = 2;
		float x = 0.7f;
		float y = 0f;
		MeshComponent a = newArrow(x, y, height, top, edge1, bottom, edge2);
		a.setScale(new Vec(0.3f, 0.3f, 0.3f));
		return a;
	}

	private MeshComponent newArrow(float x, float y, float height, Color top,
			Color edge1, Color bottom, Color edge2) {

		MeshComponent pyr = new Shape(null);

		MultiColoredShape s = new MultiColoredShape();

		s.add(new Vec(-x, 0, height), top);
		s.add(new Vec(1, 0, 0), edge1);
		s.add(new Vec(-y, 0, -height), bottom);

		MultiColoredShape s2 = new MultiColoredShape();
		s2.add(new Vec(0, -x, height), top);
		s2.add(new Vec(0, 1, 0), edge2);
		s2.add(new Vec(0, -y, -height), bottom);

		MultiColoredShape s3 = new MultiColoredShape();
		s3.add(new Vec(x, 0, height), top);
		s3.add(new Vec(-1, 0, 0), edge1);
		s3.add(new Vec(y, 0, -height), bottom);

		MultiColoredShape s4 = new MultiColoredShape();
		s4.add(new Vec(0, x, height), top);
		s4.add(new Vec(0, -1, 0), edge2);
		s4.add(new Vec(0, y, -height), bottom);

		pyr.addChild(s);
		pyr.addChild(s2);
		pyr.addChild(s3);
		pyr.addChild(s4);

		GLFactory.getInstance().addRotateAnimation(pyr, 120, new Vec(0, 0, 1));

		return pyr;
	}

	public GeoObj newPositionMarker(GLCamera camera) {
		GeoObj o = camera.getGPSPositionAsGeoObj();
		Shape diamond = newDiamond(Color.getRandomRGBColor());
		diamond.setScale(new Vec(0.4f, 0.4f, 0.4f));
		o.setComp(diamond);
		return o;
	}

	private void addRotateAnimation(MeshComponent target, int speed,
			Vec rotationVec) {
		AnimationRotate a = new AnimationRotate(speed, rotationVec);
		target.addAnimation(a);
	}

	public MeshComponent newGrid(Color netColor, float spaceBetweenNetStrings,
			int lineCount) {
		Shape s = new Shape(netColor);
		s.setLineDrawing();
		float coord = (lineCount - 1) * spaceBetweenNetStrings / 2;
		Vec start = new Vec(coord, coord, 0);
		Vec end = new Vec(coord, -coord, 0);
		for (int i = 0; i < lineCount; i++) {
			s.add(start.copy());
			s.add(end.copy());
			start.x -= spaceBetweenNetStrings;
			end.x -= spaceBetweenNetStrings;
		}
		start = new Vec(coord, coord, 0);
		end = new Vec(-coord, coord, 0);
		for (int i = 0; i < lineCount; i++) {
			s.add(start.copy());
			s.add(end.copy());
			start.y -= spaceBetweenNetStrings;
			end.y -= spaceBetweenNetStrings;
		}
		return s;
	}

	public Obj newSolarSystem(Vec position) {
		Obj solarSystem = new Obj();
		MeshComponent sunBox = new Shape();
		if (position != null)
			sunBox.setPosition(position);
		solarSystem.setComp(sunBox);

		MeshComponent earthRing = new Shape();
		MeshComponent earthBox = new Shape();
		earthRing.addChild(earthBox);

		MeshComponent sun = GLFactory.getInstance().newNSidedPolygonWithGaps(
				20, Color.red());
		GLFactory.getInstance().addRotateAnimation(sun, 30, new Vec(1, 1, 1));
		sunBox.addChild(sun);

		GLFactory.getInstance().addRotateAnimation(earthRing, 40,
				new Vec(0.5f, 0.3f, 1));
		earthBox.setPosition(new Vec(3, 0, 0));
		sunBox.addChild(earthRing);

		MeshComponent earth = GLFactory.getInstance().newCircle(Color.green());
		earth.scaleEqual(0.5f);
		earthBox.addChild(earth);

		MeshComponent moonring = new Shape();

		MeshComponent moon = GLFactory.getInstance().newCircle(Color.white());
		moon.setPosition(new Vec(1, 0, 0));
		moon.scaleEqual(0.2f);
		GLFactory.getInstance().addRotateAnimation(moonring, 80,
				new Vec(0, 1, -1));
		moonring.addChild(moon);

		earthBox.addChild(moonring);

		return solarSystem;
	}

	public Obj newHexGroupTest(Vec pos) {
		Obj hex = new Obj();
		MeshComponent g1 = new Shape(null, pos);
		hex.setComp(g1);
		g1.addChild(this.newHexagon(null));
		MeshComponent g2 = new Shape(Color.blue(), new Vec(0, 5, 0.1f));
		g2.addAnimation(new AnimationRotate(60, new Vec(0, 0, 1)));
		g1.addChild(g2);

		g2.addChild(this.newHexagon(null));
		MeshComponent g3 = new Shape(Color.red(), new Vec(0, 4, 0));
		g3.addAnimation(new AnimationRotate(30, new Vec(0, 0, 1)));
		g2.addChild(g3);

		g3.addChild(this.newHexagon(null));
		MeshComponent g4 = new Shape(Color.green(), new Vec(0, 2, 0));
		g4.addAnimation(new AnimationRotate(15, new Vec(0, 0, 1)));
		g3.addChild(g4);

		g4.addChild(this.newHexagon(null));

		// Vec v = g4.getAbsolutePosition();
		// System.out.println("absolut Pos: " + v);

		return hex;
	}

	public Shape newDiamond(Color canBeNull) {
		Shape s = new Shape(canBeNull);
		float width = 0.7f;
		float heigth = 2f;
		float c = -0.1f; // a factor for asymmetric shaping in x direction

		Vec top = new Vec(0, 0, heigth);
		Vec bottom = new Vec(0, 0, -heigth);

		Vec e1 = new Vec(-width + c, 0, 0);
		Vec e4 = new Vec(width - c, 0, 0);
		Vec e2 = new Vec(-width / 2 + c, width, 0);
		Vec e6 = new Vec(-width / 2 + c, -width, 0);
		Vec e3 = new Vec(width / 2 - c, width, 0);
		Vec e5 = new Vec(width / 2 - c, -width, 0);

		s.add(top);
		s.add(e1);
		s.add(e2);
		s.add(top);
		s.add(e2);
		s.add(e3);
		s.add(top);
		s.add(e3);
		s.add(e4);
		s.add(top);
		s.add(e4);
		s.add(e5);
		s.add(top);
		s.add(e5);
		s.add(e6);
		s.add(top);
		s.add(e6);
		s.add(e1);

		s.add(bottom);
		s.add(e1);
		s.add(e2);
		s.add(bottom);
		s.add(e2);
		s.add(e3);
		s.add(bottom);
		s.add(e3);
		s.add(e4);
		s.add(bottom);
		s.add(e4);
		s.add(e5);
		s.add(bottom);
		s.add(e5);
		s.add(e6);
		s.add(bottom);
		s.add(e6);
		s.add(e1);

		return s;

	}

	public Shape newDirectedPath(GeoObj from, GeoObj to, Color color) {
		return GLFactory.getInstance().newDirectedPath(
				to.getVirtualPosition(from), color);
	}

	public Shape newDirectedPath(Vec lineEndPos, Color c) {

		Shape s = new Shape(c);
		Vec orth = Vec.getOrthogonalHorizontal(lineEndPos).normalize()
				.mult(0.9f);
		Vec orthClone = orth.getNegativeClone();
		float down = 0.5f;
		Vec l = lineEndPos.copy().setLength(0.3f);
		orth.add(l);
		orth.z -= down;
		orthClone.add(l);
		orthClone.z -= down;

		Vec start = new Vec().add(l.mult(3));
		s.add(orth);
		s.add(start);
		s.add(lineEndPos);

		s.add(start);
		s.add(orthClone);
		s.add(lineEndPos);

		return s;
	}

	public MeshComponent newUndirectedPath(GeoObj from, GeoObj to, Color color) {
		return GLFactory.getInstance().newUndirectedPath(
				to.getVirtualPosition(from), color);
	}

	public Shape newUndirectedPath(Vec lineEnd, Color c) {

		float down = 0.5f;

		Vec e2 = Vec.getOrthogonalHorizontal(lineEnd).normalize().mult(0.9f);
		Vec e1 = e2.getNegativeClone();
		e2.z -= down;
		e1.z -= down;

		Vec l25percent = Vec.mult(0.25f, lineEnd);
		Vec l75percent = Vec.mult(0.75f, lineEnd);

		Vec e3 = Vec.add(lineEnd, e1);
		Vec e4 = Vec.add(lineEnd, e2);
		e3.z -= down;
		e4.z -= down;

		Shape s = new Shape(c);

		s.add(e1);
		s.add(e2);
		s.add(l75percent);

		s.add(e3);
		s.add(e4);
		s.add(l25percent);

		return s;
	}

	public Shape newNSidedPolygon(int numberOfSides, float radius, Color c) {
		Shape s = new Shape(c);

		Vec v = new Vec(radius, 0, 0);
		double factor = 360. / numberOfSides;

		// there have to be n triangles:
		for (int i = 0; i < numberOfSides; i++) {
			s.add(v.copy());
			v.rotateAroundZAxis(factor);
			s.add(v.copy());
			// v.rotateAroundZAxis(factor);
			s.add(new Vec()); // middle
		}
		return s;
	}

	public Shape newCircle(Color c) {
		return newNSidedPolygon(20, 1, c);
	}

	public Shape newNSidedPolygonWithGaps(int numberOfSides, Color c) {
		Shape s = new Shape(c);

		Vec v = new Vec(1, 0, 0);
		double factor = 360 / numberOfSides;

		// there have to be n triangles:
		for (int i = 0; i < numberOfSides / 2; i++) {
			s.add(v.copy());
			v.rotateAroundZAxis(factor);
			s.add(v.copy());
			v.rotateAroundZAxis(factor);
			s.add(new Vec()); // middle
		}
		return s;
	}

	private static final float HEIGHT_TO_SIDE_FACTOR = (float) (2f / Math
			.sqrt(3f));

	public Shape newPyramid(Vec center, float height, Color color) {
		Shape p = new Shape(color);
		// side length:
		float a = HEIGHT_TO_SIDE_FACTOR * Math.abs(height);

		Vec p1 = new Vec(center.x - 1f / 2f * a, center.y - 1f / 3f
				* Math.abs(height), 0f);
		Vec p2 = new Vec(center.x + 1f / 2f * a, center.y - 1f / 3f
				* Math.abs(height), 0f);
		Vec p3 = new Vec(center.x, center.y + 2f / 3f * Math.abs(height), 0f);
		Vec p4 = new Vec(center.x, center.y, height);

		p.add(p1);
		p.add(p2);
		p.add(p3);

		p.add(p1);
		p.add(p2);
		p.add(p4);

		p.add(p2);
		p.add(p3);
		p.add(p4);

		p.add(p3);
		p.add(p1);
		p.add(p4);

		return p;
	}

	public static void resetInstance() {
		myInstance = new GLFactory();
	}

	public MeshComponent newCube() {
		return newCube(null);
	}

	public MeshComponent newCoordinateSystem() {
		return new MeshComponent(null) {

			@Override
			public boolean accept(Visitor visitor) {
				return false;
			}

			@Override
			public void draw(GL10 gl, Renderable parent) {
				CordinateAxis.draw(gl);
			}

		};
	}

	/**
	 * will face to the camera. also read
	 * {@link GLFactory#newTexturedSquare(String, Bitmap, float)}
	 * 
	 * @param textToDisplay
	 * @param textPosition
	 * @param textSize
	 * @param context
	 * @param glCamera
	 * @return
	 */
	public Obj newTextObject(String textToDisplay, Vec textPosition,
			Context context, GLCamera glCamera) {

		float textSize = 1;

		TextView v = new TextView(context);
		v.setTypeface(null, Typeface.BOLD);
		// Set textcolor to black:
		// v.setTextColor(new Color(0, 0, 0, 1).toIntARGB());
		v.setText(textToDisplay);

		Obj o = new Obj();
		MeshComponent mesh = this.newTexturedSquare("textBitmap"
				+ textToDisplay, util.IO.loadBitmapFromView(v), textSize);
		mesh.setPosition(textPosition);
		mesh.addAnimation(new AnimationFaceToCamera(glCamera));
		o.setComp(mesh);
		return o;
	}

	/**
	 * also read {@link GLFactory#newTexturedSquare(String, Bitmap, float)}
	 * 
	 * @param latitude
	 * @param longitude
	 * @param bitmap
	 *            the loaded bitmap (e.g. via
	 *            {@link IO#loadBitmapFromURL(String)}
	 * @param uniqueBitmapName
	 *            a unique bitmap name
	 * @param heightInMeters
	 * @param glCamera
	 * @return an {@link GeoObj} which can be added to the {@link World} e.g.
	 */
	public GeoObj newIconFacingToCamera(double latitude, double longitude,
			Bitmap bitmap, String uniqueBitmapName, float heightInMeters,
			GLCamera glCamera) {
		MeshComponent triangleMesh = GLFactory.getInstance().newTexturedSquare(
				uniqueBitmapName, bitmap, heightInMeters);
		triangleMesh.addChild(new AnimationFaceToCamera(glCamera, 0.5f));
		GeoObj o = new GeoObj(latitude, longitude);
		o.setComp(triangleMesh);
		return o;
	}

}
