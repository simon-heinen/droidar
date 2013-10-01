package de.rwth;

import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLFactory;
import gl.LightSource;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;

import javax.microedition.khronos.opengles.GL10;

import system.DefaultARSetup;
import system.EventManager;
import util.EfficientList;
import util.Vec;
import util.Wrapper;
import worldData.MoveComp;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;
import actions.ActionMoveObject;
import android.app.Activity;

import commands.Command;

public class ModelLoaderSetup extends DefaultARSetup {

	private boolean lightsOnOff = true;
	protected static final float zMoveFactor = 1.4f;
	private String fileName;
	private String textureName;
	private LightSource spotLight;
	private Wrapper targetMoveWrapper;
	private GL1Renderer renderer;

	public ModelLoaderSetup(String fileName, String textureName) {
		this.fileName = fileName;
		this.textureName = textureName;
		targetMoveWrapper = new Wrapper();
		
		//instantiated light here, since the method _a2_initLightning() is no longer overridden
		spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 0, 0));
	}

	//@Override
	public boolean _a2_initLightning(EfficientList<LightSource> lights) {
		spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(
				0, 0, 0));
		lights.add(spotLight);
		return lightsOnOff;
	}

	@Override
	public void addObjectsTo(GL1Renderer renderer, final World world,
			GLFactory objectFactory) {
		this.renderer = renderer;
		final Obj lightObject = new Obj();
		
		spotLight.setPosition(new Vec(1, 1, 1));
		MeshComponent circle = objectFactory.newCircle(null);
		circle.setRotation(new Vec(0.2f, 0.2f, 0.2f));
		MeshComponent lightGroup = new Shape();
		lightGroup.addChild(spotLight);
		lightGroup.addChild(circle);
		lightObject.setComp(lightGroup);
		lightObject.setComp(new MoveComp(1));
		lightObject.setOnClickCommand(new Command() {

			@Override
			public boolean execute() {
				targetMoveWrapper.setTo(lightObject);
				return true;
			}
		});
		world.add(lightObject);

		targetMoveWrapper.setTo(lightObject);

		GDXConnection.init(myTargetActivity, renderer);

		new ModelLoader(renderer, fileName, textureName) {
			@Override
			public void modelLoaded(MeshComponent gdxMesh) {
				final Obj o = new Obj();
				o.setComp(gdxMesh);
				world.add(o);
				o.setComp(new MoveComp(1));
				o.setOnClickCommand(new Command() {

					@Override
					public boolean execute() {
						targetMoveWrapper.setTo(o);
						return true;
					}
				});
			}
		};

	}

	@Override
	public void _c_addActionsToEvents(EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		super._c_addActionsToEvents(eventManager, arView, updater);

		// clear some inputs set in default methods
		eventManager.getOnLocationChangedAction().clear();
		eventManager.getOnTrackballEventAction().clear();

		eventManager.addOnTrackballAction(new ActionMoveObject(
				targetMoveWrapper, getCamera(), 10, 200));
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		super._e2_addElementsToGuiSetup(guiSetup, activity);
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				if (targetMoveWrapper.getObject() instanceof Obj) {
					((Obj) targetMoveWrapper.getObject())
							.getComp(MoveComp.class).myTargetPos.z -= zMoveFactor;
					return true;
				}
				return false;
			}
		}, "Obj Down");
		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				if (targetMoveWrapper.getObject() instanceof Obj) {
					((Obj) targetMoveWrapper.getObject())
							.getComp(MoveComp.class).myTargetPos.z += zMoveFactor;
					return true;
				}
				return false;
			}
		}, "Obj up");

		guiSetup.addButtonToBottomView(new Command() {

			@Override
			public boolean execute() {
				if (renderer != null) {
					lightsOnOff = !lightsOnOff;
					renderer.setUseLightning(lightsOnOff);
					return true;
				}
				return false;
			}
		}, "Lights on/of");

	}

	
}
