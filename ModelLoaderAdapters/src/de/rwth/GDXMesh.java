package de.rwth;

import gl.ObjectPicker;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;

public class GDXMesh extends MeshComponent {

	private static final String LOGTAG = "GDXShape";
	private Model model;
	private Texture texture;
	private KeyframedAnimation anim;
	private float animTime;

	public GDXMesh(Model model, Texture texture) {
		super(null);
		this.model = model;
		this.texture = texture;

		try {
			anim = (KeyframedAnimation) ((KeyframedModel) model)
					.getAnimations()[0];
		} catch (Exception e) {
		}
	}

	@Override
	public boolean accept(Visitor visitor) {
		return false;
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {

		gl.glEnable(GL10.GL_CULL_FACE);

		if (model != null) {
			if (!ObjectPicker.readyToDrawWithColor && texture != null) {
				gl.glEnable(GL10.GL_TEXTURE_2D);
				// Gdx.gl.glEnable(GL10.GL_BLEND);
				// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,
				// GL10.GL_ONE_MINUS_SRC_ALPHA);
				texture.bind();
				model.render();
				gl.glDisable(GL10.GL_TEXTURE_2D);
			} else {
				model.render();
			}
		} else
			Log.e(LOGTAG, "No model object existend");
	}

	@Override
	public synchronized boolean update(float timeDelta, Updateable parent) {
		super.update(timeDelta, parent);
		if (anim != null) {
			animTime += timeDelta;
			if (animTime > anim.totalDuration - anim.frameDuration) {
				animTime = 0;
			}
			try {
				((KeyframedModel) model)
						.setAnimation(anim.name, animTime, true);
			} catch (Exception e) {
			}

		}
		return true;
	}

}
