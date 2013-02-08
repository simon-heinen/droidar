package gl;

import gl.scenegraph.MeshComponent;

import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import util.Log;
import util.Vec;
import worldData.Visitor;

/**
 * Great tutorials:
 * http://iphonedevelopment.blogspot.com/2009/05/opengl-es-from-ground-up-part-4
 * -let.html
 * 
 * http://www.sjbaker.org/steve/omniv/opengl_lighting.html
 * 
 * Some good hints (how to create flashlight effect, moving lights etc):
 * http://www.opengl.org/resources/faq/technical/lights.htm
 * 
 * Basics plus code examples: http://glprogramming.com/red/chapter05.html
 * 
 * A lot of text, detailed information:
 * http://www.falloutsoftware.com/tutorials/gl/gl8.htm
 * 
 * 
 * 
 * About light models:
 * http://www.talisman.org/opengl-1.1/Reference/glLightModel.html
 * 
 * 
 * 
 * the current problem with lightning might be that the normals of every shape
 * have to be calculated to enable correct lightning and when working with
 * moving and rotating objects this would cost a lot of time. so at the moment
 * its not fully implemented!
 * 
 * @author Spobo
 * 
 */
public class LightSource extends MeshComponent {

	private static final String LOG_TAG = "LightSource";

	private int myLightId;

	/**
	 * specular light is the white dot on the ball. Try something like { 0.7f,
	 * 0.7f, 0.7f, 1 }
	 */
	private float[] specularLightColor;

	/**
	 * diffuse light is the bright part around the white dot on the ball. Try
	 * something like { 0.5f, 0.5f, 0.5f, 1 }
	 */
	private float[] diffuseLightColor;

	/**
	 * ambient light is the light emitted by everything (has no source, is the
	 * ground level level).
	 * 
	 * The simples way to control the ambient light is to set it to a certain
	 * value on one light source and to { 0, 0, 0, 1 } on all other
	 * light-sources!
	 */
	private float[] ambientLightColor;// = { 0, 0, 0, 1 };

	// private float[] myPosition = { 0, 0, 10, 0 };

	/**
	 * it this is null the light source will not be interpreted as a spot light.
	 * This has to be something like { 0, 0, -1 }
	 */
	private float[] mySpotDirection;

	/**
	 * 45 would meen 90 degree field of vision!
	 */
	private float cutoffAngle = 20;

	/**
	 * @param glLightId
	 *            something between GL10.GL_LIGHT0 and GL10.GL_LIGHT7 (8
	 *            light-sources maximum)
	 */
	public LightSource(int glLightId) {
		super(null);
		this.myLightId = glLightId;
	}

	public void switchOn(GL10 gl) {

		Log.d(LOG_TAG, "Now switching lightsource " + myLightId + " to on!");

		gl.glEnable(myLightId);

		// if it has an ambient component enable it:
		if (ambientLightColor != null)
			gl.glLightfv(myLightId, GL10.GL_AMBIENT, ambientLightColor, 0);

		if (diffuseLightColor != null)
			gl.glLightfv(myLightId, GL10.GL_DIFFUSE, diffuseLightColor, 0);

		if (specularLightColor != null)
			gl.glLightfv(myLightId, GL10.GL_SPECULAR, specularLightColor, 0);

		if (myPosition != null)
			gl.glLightfv(myLightId, GL10.GL_POSITION,
					myPosition.getArrayVersion(), 0);

		// if it is a spotlight:
		if (mySpotDirection != null) {
			gl.glLightfv(myLightId, GL10.GL_SPOT_DIRECTION,
					GLUtilityClass.createAndInitFloatBuffer(mySpotDirection));
			gl.glLightf(myLightId, GL10.GL_SPOT_CUTOFF, cutoffAngle);
		}

		setDefaultSimpleMaterialStuff(gl);

	}

	// default material values which should be overwritten by each mesh later:
	private float x = 0.3f;
	private float materialAmbient[] = new float[] { x, x, x, 1 };
	private float materialDiffuse[] = new float[] { x, x, x, 1 };
	private float materialSpecular[] = new float[] { x, x, x, 1 };

	/**
	 * TODO move this somewhere else, if material is used it should be set to
	 * default values by each mesh individually!
	 * 
	 * @param gl
	 */
	private void setDefaultSimpleMaterialStuff(GL10 gl) {
		/*
		 * A default material is defined here but all objects should define a
		 * custom one if they have a special type of meterial!
		 */
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
				materialAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
				materialDiffuse, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
				materialSpecular, 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 5.0f);

		// otherMaterialStuffThatDoesNotWork(gl);

		// use the colors of the meshes, this should not be set if every element
		// has a correct material i think.. not sure
		gl.glEnable(GL10.GL_COLOR_MATERIAL);

	}

	// private FloatBuffer mab;
	//
	// private FloatBuffer mdb;
	//
	// private FloatBuffer msb;
	//
	// private void otherMaterialStuffThatDoesNotWork(GL10 gl) {
	// mab = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())
	// .asFloatBuffer();
	// mab.put(new float[] { 1.0f, 0.0f, 0.0f, 1.0f });
	// mab.position(0);
	// mdb = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())
	// .asFloatBuffer();
	// mdb.put(new float[] { 0.0f, 1.0f, 0.0f, 1.0f });
	// mdb.position(0);
	// msb = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())
	// .asFloatBuffer();
	// msb.put(new float[] { 0.0f, 0.0f, 1.0f, 1.0f });
	// msb.position(0);
	//
	// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mab);
	// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mdb);
	// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, msb);
	// gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 128.0f);
	// }

	public void switchOff(GL10 gl) {
		gl.glDisable(myLightId);
	}

	public static LightSource newDefaultAmbientLight(int lightId) {
		LightSource l = new LightSource(lightId);
		float b = 0.05f;
		float[] color = { b, b, b, 1 };
		l.ambientLightColor = color;
		return l;
	}

	public static LightSource newDefaultDefuseLight(int lightId,
			Vec lightPosition) {
		LightSource l = new LightSource(lightId);
		float b = 0.7f;
		float[] color = { b, b, b, 1 };
		l.diffuseLightColor = color;
		l.myPosition = lightPosition.copy();
		return l;
	}

	public static LightSource newDefaultSpotLight(int lightId,
			Vec lightPosition, Vec lightTargetPosition) {
		LightSource l = new LightSource(lightId);
		float b = 0.2f;
		float[] color = { b, b, b, 1 };
		l.specularLightColor = color;
		l.myPosition = lightPosition.copy();
		if (lightTargetPosition != null) {
			Vec directionVec = Vec.sub(lightTargetPosition, lightPosition)
					.normalize();
			float[] direction = { directionVec.x, directionVec.y,
					directionVec.z };
			l.mySpotDirection = direction;
		}
		return l;
	}

	/**
	 * // TODO calculate the position of the sun to set the sun light-source at
	 * the correct place
	 * 
	 * http://www.srrb.noaa.gov/highlights/sunrise/calcdetails.html
	 * 
	 * and
	 * 
	 * http://www.srrb.noaa.gov/highlights/sunrise/program.txt
	 * 
	 * @param lightId
	 * @param currentDate
	 * @return
	 */
	public static LightSource newDefaultDayLight(int lightId, Date currentDate) {
		LightSource l = new LightSource(lightId);
		float b = 0.4f;
		float[] color = { b, b, b, 1 };
		l.specularLightColor = color;
		Vec lightPosition = new Vec(100, 100, 100);
		l.myPosition = lightPosition;
		Vec directionVec = Vec.sub(new Vec(), lightPosition).normalize();
		float[] direction = { directionVec.x, directionVec.y, directionVec.z };
		l.mySpotDirection = direction;
		return l;
	}

	@Override
	public boolean accept(Visitor visitor) {
		return true;
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {
		/*
		 * the lightsource can be added as a normal mesh to the world to allow
		 * movements
		 * 
		 * read the part about moving lightsources
		 * http://www.opengl.org/resources/faq/technical/lights.htm
		 * 
		 * also read "Position and Attenuation" in
		 * http://fly.cc.fer.hr/~unreal/theredbook/chapter06.html
		 */
		if (myPosition == null) {
			myPosition = new Vec();
		}
		gl.glLightfv(myLightId, GL10.GL_POSITION, myPosition.getArrayVersion(),
				0);
	}

}
