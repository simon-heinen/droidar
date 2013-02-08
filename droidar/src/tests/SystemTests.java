package tests;

import android.location.Location;
import gl.ObjectPicker;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import util.Calculus;
import util.LimitedQueue;
import util.Log;
import util.Vec;
import util.Wrapper;
import worldData.Entity;
import worldData.Obj;

import components.ProximitySensor;

public class SystemTests extends SimpleTesting {

	private static final String LOG_TAG = "SystemTests";

	@Override
	public void run() throws Exception {
		wrapperTests();
		vecTests();
		objTests();
		utilTests();
		LimitedQueueTests();
		colorTests();
	}

	private void LimitedQueueTests() throws Exception {
		LimitedQueue<Integer> l = new LimitedQueue<Integer>(20);
		for (int i = 0; i < 30; i++) {
			l.add(i);
		}

		for (Integer i : l) {
			/*
			 * work on list
			 */
			i.byteValue();
		}
		// make sure no elements were removed automatically
		assertTrue(l.size() == 20);
		assertTrue(l.getFirst() == 10);
		assertTrue(l.getFirst() == 10);

	}

	private void colorTests() throws Exception {

		float input = 0x000001;
		// TODO
		System.out.println("" + input + " is converted to " + convInput(input));

	}

	private byte convInput(float input) {
		byte r = ObjectPicker.floatToByteColorValue(
				ObjectPicker.rgb565to888(input), false);
		return r;
	}

	private void utilTests() throws Exception {
		for (int i = 0; i < 20; i++) {
			assertTrue(Calculus.getRandomFloat(0, 2) >= 0);
			assertTrue(Calculus.getRandomFloat(0, 2) <= 2);
		}

		System.out.println(Log.getCurrentMethod(2));

	}

	private void objTests() throws Exception {
		Obj o = new Obj();
		o.setComp(new Shape());
		assertTrue(o.hasComponent(Shape.class));
		assertTrue(o.hasComponent(MeshComponent.class));
		assertTrue(o.hasComponent(Entity.class));
		assertFalse(o.hasComponent(ProximitySensor.class));

	}

	private void vecTests() throws Exception {
		normalCalculationTest();
		assertEquals(Vec.rotatedVecInXYPlane(10, 90), new Vec(0, 10, 0));
		vecRoundingTest();
		vecRotateTests();
	}

	private void vecRotateTests() throws Exception {
		assertTrue(Vec.getRotationAroundZAxis(10, 10) == 45);
		assertTrue(Vec.getRotationAroundZAxis(3, 0) == 0);
		assertTrue(Vec.getRotationAroundZAxis(-4, -4) == 180 + 45);
		assertTrue(Vec.getRotationAroundZAxis(2, -2) == 360 - 45);

		Vec x = new Vec(0, 10, 0);
		x.rotateAroundZAxis(90);
		assertEquals(x, new Vec(-10, 0, 0));

		{
			Vec r = new Vec(0, 0, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(0, 0, 0));
		}
		{
			Vec r = new Vec(0, 0, 1);
			r.toAngleVec();
			assertEquals(r, new Vec(180, 0, 0));
		}
		{
			Vec r = new Vec(12, 12, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(90, 0, 45));
		}
		{
			Vec r = new Vec(-10, -10, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(90, 0, 180 + 45));
		}
		{
			Vec r = new Vec(-10, 10, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(90, 0, 180 + 45 + 90));
		}
		{
			Vec r = new Vec(0, 1, -1);
			r.toAngleVec();
			assertEquals(r, new Vec(45, 0, 0));
		}
		{
			Vec r = new Vec(0, 1, 1);
			r.toAngleVec();
			assertEquals(r, new Vec(45 + 90, 0, 0));
		}
		{
			Vec r = new Vec(13, 0, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(90, 0, 90));
		}
		{
			Vec r = new Vec(-0.2f, 0, 0);
			r.toAngleVec();
			assertEquals(r, new Vec(90, 0, 180 + 90));
		}

	}

	private void vecRoundingTest() throws Exception {
		Vec v = new Vec(0.12345678f, 0.12345678f, 0.12345678f);
		v.round(100);
		assertTrue(v.x == 0.12f);
	}

	private void normalCalculationTest() throws Exception {
		Vec a = new Vec(5, 0, 0);
		Vec b = new Vec(0, 5, 0);
		Vec c = new Vec(5, -5, 0);
		Vec n1 = Vec.calcNormalVec(a, b).normalize();
		Vec n2 = Vec.calcNormalVec(a, c).normalize();
		Vec n3 = Vec.calcNormalVec(b, c).normalize();
		assertTrue(n1.equals(n2) || n1.equals(Vec.mult(-1, n2)));
		assertTrue(n2.equals(n3) || n2.equals(Vec.mult(-1, n3)));
	}

	private void wrapperTests() throws Exception {
		Wrapper w = new Wrapper();
		assertTrue(w.getType() == Wrapper.Type.None);
		w.setTo(true);
		assertTrue(w.getType() == Wrapper.Type.Bool);
		assertTrue(!w.equals(false));
		w.setTo(1.0f);
		assertTrue(w.getType() == Wrapper.Type.Float);
	}

}
