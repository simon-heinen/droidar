package tests;

import gl.GLCamera;
import gl.animations.AnimationGrow;
import gl.animations.GLAnimation;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import system.Container;
import util.Vec;
import worldData.Obj;
import worldData.World;

public class WorldTests extends SimpleTesting {

	@Override
	public void run() throws Exception {
		GLCamera cam = new GLCamera();
		World w = new World(cam);
		Obj o = new Obj();
		MeshComponent s = new Shape();
		MeshComponent s2 = new Shape();
		GLAnimation a = new AnimationGrow(23f);
		s.addChild(s2);
		s.addAnimation(a);
		assertTrue(s.getChildren() instanceof Container);
		assertTrue(((Container) s.getChildren()).getAllItems().myLength == 2);
		s.remove(a);
		assertTrue(s.getChildren() instanceof Container);
		assertTrue(((Container) s.getChildren()).getAllItems().myLength == 1);
		s.remove(a);
		assertTrue(s.getChildren() instanceof Container);
		assertTrue(((Container) s.getChildren()).getAllItems().myLength == 1);
		s.remove(s2);
		// TODO should s still has a container as a child or just null?
		assertTrue(s.getChildren() instanceof Container);
		assertTrue(((Container) s.getChildren()).getAllItems().myLength == 0);

		o.setComp(s);
		assertTrue(s != null);
		assertTrue(o.getComp(MeshComponent.class) == s);
		o.remove(s);
		assertTrue(o.getComp(MeshComponent.class) == null);
		assertTrue(w.getAllItems().myLength == 0);
		w.add(o);
		assertTrue(w.getAllItems().myLength == 1);
		assertTrue(w.getAllItems().contains(o) >= 0);
		w.remove(o);
		assertTrue(w.getAllItems().myLength == 0);
		assertTrue(w.getAllItems().contains(o) == -1);

		absolutePositionTest();
	}

	private void absolutePositionTest() throws Exception {
		MeshComponent a = new Shape(null, new Vec(10, 0, 0));
		MeshComponent b = new Shape(null, new Vec(0, 10, 0));
		MeshComponent c = new Shape(null, new Vec(0, 0, 10));
		a.addChild(b);
		b.addChild(c);

		a.update(1, null);

		Vec pos = new Vec();
		c.getAbsoluteMeshPosition(pos);
		System.out.println(pos);
		assertEquals(pos, new Vec(10, 10, 10));
	}

}
