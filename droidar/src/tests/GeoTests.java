package tests;

import geo.GeoGraph;
import geo.GeoObj;
import gl.GLCamera;
import gl.GLFactory;
import util.Vec;
import worldData.World;
import actions.ActionCalcRelativePos;
import android.util.Log;

import commands.DebugCommandPositionEvent;

public class GeoTests extends SimpleTesting {

	private static final String LOG_TAG = "Geo Tests";

	public static final GeoObj a1 = new GeoObj(50.769118, 6.097568, 0, "A1");
	public static final GeoObj a2 = new GeoObj(50.769328, 6.097514, 0, "A2");
	public static final GeoObj a3 = new GeoObj(50.769159, 6.097986, 0, "A3");
	public static final GeoObj n1 = new GeoObj(50.769444, 6.095191, 0, "N1");
	public static final GeoObj n2 = new GeoObj(50.769617, 6.09481, 0, "N2");
	public static final GeoObj n3 = new GeoObj(50.769174, 6.095156, 0, "N3");
	public static final GeoObj rwthI9 = new GeoObj(50.778393, 6.060886, 0, "I9");
	public static final GeoObj iPark1 = new GeoObj(50.778771, 6.061074, 0, "P1");
	public static final GeoObj iPark2 = new GeoObj(50.778661, 6.060497, 0, "P2");
	public static final GeoObj iPark3 = new GeoObj(50.779134, 6.060202, 0, "P3");
	public static final GeoObj iPark4 = new GeoObj(50.779242, 6.060787, 0, "P4");
	public static final GeoObj p = new GeoObj(50.781161, 6.078752, 0, "Ponttor");

	@Override
	public void run() throws Exception {
		t8();
		distanceCalcTest();
		virtualPosToGPSPosTest();
		positioningTests();
		t7();
		t6();
		t5();
		t4();
		t3();
		t2();
		t1();
	}

	/**
	 * 
	 * <pre>
	 *                 s1
	 *           pUp2
	 *            |
	 *         pUp1
	 *         |
	 *      _ pL2   
	 *  pL1     \      _ pR2
	 *   s3        pR1
	 *          s2
	 * 
	 * </pre>
	 * 
	 * @throws Exception
	 */
	private void t8() throws Exception {

		GeoGraph g = new GeoGraph();
		GeoObj pL1 = new GeoObj(53.465876, 2.895327, 0);
		GeoObj pL2 = new GeoObj(53.469146, 2.902665, 0);
		pL2.getInfoObject().setShortDescr("test");
		GeoObj pR1 = new GeoObj(53.466285, 2.907386, 0);
		GeoObj pR2 = new GeoObj(53.467613, 2.915068, 0);
		GeoObj pU1 = new GeoObj(53.47377, 2.902622, 0);
		GeoObj pU2 = new GeoObj(53.477448, 2.906785, 0);
		GeoObj s1 = new GeoObj(53.477626, 2.90597, 0);
		GeoObj s2 = new GeoObj(53.465033, 2.906399, 0);
		GeoObj s3 = new GeoObj(53.464624, 2.895799, 0);
		assertTrue(g.add(pL1));
		assertTrue(g.add(pL2));
		assertTrue(g.add(pR1));
		assertTrue(g.add(pR2));
		assertTrue(g.add(pU1));
		assertTrue(g.add(pU2));
		assertTrue(g.addEdge(pL1, pL2, null) != null);
		assertTrue(g.addEdge(pL2, pR1, null) != null);
		assertTrue(g.addEdge(pR1, pR2, null) != null);
		assertTrue(g.addEdge(pL2, pU1, null) != null);
		assertTrue(g.addEdge(pU1, pU2, null) != null);
		// System.out.println("geotest " + g.findPath(gL1, gL2).getMyItems());
		assertTrue(g.findPath(pL1, pL2).getAllItems().myLength == 2);
		assertTrue(g.findPath(pL1, pR1).getAllItems().myLength == 3);
		assertTrue(g.findPath(pL1, pR2).getAllItems().myLength == 4);
		assertTrue(g.findPath(pL2, pR2).getAllItems().myLength == 3);
		assertTrue(g.findPath(pL2, pU2).getAllItems().myLength == 3);
		assertTrue(g.findPath(g.getClosesedObjTo(s1), pL1).getAllItems().myLength == 4);
		assertTrue(g.findPath(g.getClosesedObjTo(s1), pL2).getAllItems().myLength == 3);
		assertTrue(g.findPath(g.getClosesedObjTo(s1), pR2).getAllItems().myLength == 5);
		assertTrue(g.findPath(g.getClosesedObjTo(s2), pU2).getAllItems().myLength == 4);
		assertTrue(pL2.matchesSearchTerm("test") == 1);
		assertEquals(g.findBestPointFor("test"), pL2);
		assertTrue(g.findPath(g.getClosesedObjTo(s1),
				g.findBestPointFor("test")).getAllItems().myLength == 3);
		assertTrue(g.findPath(g.getClosesedObjTo(s2),
				g.findBestPointFor("test")).getAllItems().myLength == 2);
		assertTrue(g.findPath(g.getClosesedObjTo(s3),
				g.findBestPointFor("test")).getAllItems().myLength == 2);
	}

	private void positioningTests() throws Exception {

		/*
		 * The test playground looks like this:
		 * 
		 * A B
		 * 
		 * Cam
		 * 
		 * D C
		 */

		GLCamera camera = new GLCamera();
		World world = new World(camera);
		ActionCalcRelativePos gpsAction = new ActionCalcRelativePos(world,
				camera);
		GeoObj posA = new GeoObj(50.78095, 6.06607);
		posA.setComp(GLFactory.getInstance().newArrow());
		world.add(posA);
		GeoObj posB = new GeoObj(50.78094, 6.06788);
		posB.setComp(GLFactory.getInstance().newArrow());
		world.add(posB);
		GeoObj posC = new GeoObj(50.77998, 6.06790);
		posC.setComp(GLFactory.getInstance().newArrow());
		world.add(posC);
		GeoObj posD = new GeoObj(50.77997, 6.06584);
		posD.setComp(GLFactory.getInstance().newArrow());
		world.add(posD);

		moveCenterAndTest(camera, gpsAction, posA, posB, posC, posD);
		moveNorthAndTest(camera, gpsAction, posA, posB, posC, posD);
		moveCenterAndTest(camera, gpsAction, posA, posB, posC, posD);
		moveEastAndTest(camera, gpsAction, posA, posB, posC, posD);
		moveNorthAndTest(camera, gpsAction, posA, posB, posC, posD);
		moveSouthEastAndTest(camera, gpsAction, posA, posB, posC, posD);

		assertTrue(posA.getVirtualPosition().x < posC.getVirtualPosition().x);
		assertTrue(posA.getVirtualPosition().y > posC.getVirtualPosition().y);
		assertTrue(posD.getVirtualPosition().x < posB.getVirtualPosition().x);
		assertTrue(posD.getVirtualPosition().y < posB.getVirtualPosition().y);

	}

	private void moveSouthEastAndTest(GLCamera camera,
			ActionCalcRelativePos gpsAction, GeoObj posA, GeoObj posB,
			GeoObj posC, GeoObj posD) throws Exception {
		new DebugCommandPositionEvent(gpsAction, new GeoObj(50.77959, 6.06914))
				.execute();

		assertTrue(isNorthOf(posA, camera));
		assertTrue(isNorthOf(posB, camera));
		assertTrue(isNorthOf(posC, camera));
		assertTrue(isNorthOf(posD, camera));

		assertFalse(isEastOf(posC, camera));
		assertFalse(isEastOf(posB, camera));
		assertFalse(isEastOf(posA, camera));
		assertFalse(isEastOf(posD, camera));
	}

	private void moveEastAndTest(GLCamera camera,
			ActionCalcRelativePos gpsAction, GeoObj posA, GeoObj posB,
			GeoObj posC, GeoObj posD) throws Exception {
		// move camera above of the park:
		new DebugCommandPositionEvent(gpsAction, new GeoObj(50.78046, 6.06524))
				.execute();

		assertTrue(isNorthOf(posA, camera));
		assertTrue(isNorthOf(posB, camera));
		assertFalse(isNorthOf(posC, camera));
		assertFalse(isNorthOf(posD, camera));

		assertTrue(isEastOf(posC, camera));
		assertTrue(isEastOf(posB, camera));
		assertTrue(isEastOf(posA, camera));
		assertTrue(isEastOf(posD, camera));
	}

	private void moveNorthAndTest(GLCamera camera,
			ActionCalcRelativePos gpsAction, GeoObj posA, GeoObj posB,
			GeoObj posC, GeoObj posD) throws Exception {
		// move camera above of the park:
		new DebugCommandPositionEvent(gpsAction, new GeoObj(50.78171, 6.06718))
				.execute();

		assertFalse(isNorthOf(posA, camera));
		assertFalse(isNorthOf(posB, camera));
		assertFalse(isNorthOf(posC, camera));
		assertFalse(isNorthOf(posD, camera));

		assertTrue(isEastOf(posC, camera));
		assertTrue(isEastOf(posB, camera));
		assertFalse(isEastOf(posA, camera));
		assertFalse(isEastOf(posD, camera));
	}

	private void moveCenterAndTest(GLCamera camera,
			ActionCalcRelativePos gpsAction, GeoObj posA, GeoObj posB,
			GeoObj posC, GeoObj posD) throws Exception {
		// move camera to center of the park:
		new DebugCommandPositionEvent(gpsAction, new GeoObj(50.78043, 6.06707))
				.execute();

		assertTrue(isNorthOf(posA, camera));
		assertTrue(isNorthOf(posB, camera));
		assertFalse(isNorthOf(posC, camera));
		assertFalse(isNorthOf(posD, camera));

		assertTrue(isEastOf(posC, camera));
		assertTrue(isEastOf(posB, camera));
		assertFalse(isEastOf(posA, camera));
		assertFalse(isEastOf(posD, camera));
	}

	private boolean isNorthOf(GeoObj a, GLCamera b) throws Exception {
		float dist = a.getVirtualPosition().y - b.getMyNewPosition().y;
		Log.v(LOG_TAG, "north dist=" + dist);
		return (dist > 0);
	}

	private boolean isEastOf(GeoObj a, GLCamera b) throws Exception {
		float dist = a.getVirtualPosition().x - b.getMyNewPosition().x;
		Log.v(LOG_TAG, "east dist=" + dist);
		return (dist > 0);
	}

	private void distanceCalcTest() throws Exception {
		/*
		 * Calculated with google maps:
		 * 
		 * Bonn = 50.732979,7.086181
		 * 
		 * Frankfurt = 50.113532,8.679199
		 * 
		 * x=111366m result=113719m (error 2%)
		 * 
		 * 2% is as good as 0% because the 111366 were calculated by hand and
		 * are not absolute accurate
		 * 
		 * y=69155m result=68841m (error 0%)
		 */

		GeoObj bonn = new GeoObj(50.732979, 7.086181, 0);
		GeoObj frankfurt = new GeoObj(50.113532, 8.679199, 0);
		Vec result = bonn.getVirtualPosition(frankfurt);
		// System.out.println("bonn frankfurt");
		// System.out.println("x=" + result.x);
		// System.out.println("y=" + result.y);

		// error must be under 3%:

		System.out.println("test geoerror.x ="
				+ Math.abs(1 - (Math.abs(result.x) / 111366)));
		System.out.println("test geoerror.y ="
				+ Math.abs(1 - (Math.abs(result.y) / 69155)));

		assertTrue(Math.abs(1 - (Math.abs(result.x) / 111366)) < 0.03);
		assertTrue(Math.abs(1 - (Math.abs(result.y) / 69155)) < 0.03);

		/*
		 * small distance tests:
		 * 
		 * 50.786838,6.06514
		 * 
		 * 50.758287,6.109085
		 * 
		 * x=3128m result=3094m (error < 2%)
		 * 
		 * y=3202m result=3172m (error < 1%)
		 * 
		 * 
		 * 50.770964,6.095545
		 * 
		 * 50.768955,6.100287
		 * 
		 * x=336m result=333m (error < 1%)
		 * 
		 * y=216m result=223m (error < 3%)
		 * 
		 * 
		 * 50.769401,6.095323
		 * 
		 * 50.769164,6.095787
		 * 
		 * x=32,7m result=32,6m (error 0%)
		 * 
		 * y= 26,4m result=26,3m (error 0%)
		 */
	}

	private void virtualPosToGPSPosTest() throws Exception {
		GeoObj g = new GeoObj(50.754539489746094, 7.227184295654297, 0);

		for (int i = 0; i < 50; i++) {
			Vec rand = Vec.getNewRandomPosInXYPlane(new Vec(), 1, 50);
			float dist = rand.getLength();
			GeoObj g2 = new GeoObj();
			g2.calcGPSPosition(rand, g);
			double calculatedDist = g2.getDistance(g);
			// Log.d(LOG_TEST, "Random distance:" + dist);
			// Log.d(LOG_TEST, "    Random distance:" + calculatedDist);
			double difference = Math.abs(dist - calculatedDist);
			// Log.d(LOG_TEST, "    difference:" + difference);
			assertTrue(difference < 1);
		}

	}

	private void t7() throws Exception {
		// TODO create test to check distance calculation of GeoObj. for example
		// one left from 0 lat one right from 0 lat
	}

	private void t6() throws Exception {
		GeoGraph g = new GeoGraph();
		assertTrue(g.add(a1));
		assertTrue(!g.add(a1));
		assertTrue(g.add(a2));
		assertTrue(g.add(a3));
		assertTrue(g.add(n1));
		assertTrue(g.add(n2));
		assertTrue(g.add(n3));
		assertTrue(!g.add(n3));
		assertTrue(g.addEdge(a1, a2, null) != null);
		assertTrue(g.addEdge(a1, a2, null) == null);
		assertTrue(g.addEdge(a1, a3, null) != null);
		assertTrue(g.addEdge(a1, n1, null) != null);
		assertTrue(g.addEdge(n1, n2, null) != null);
		assertTrue(g.addEdge(n2, n3, null) != null);
		assertTrue(g.findPath(a3, a3).getAllItems().myLength == 1);
	}

	private void t5() throws Exception {
		GeoGraph g = new GeoGraph();

		assertTrue(g.add(iPark1));
		assertTrue(g.add(iPark2));
		assertTrue(g.add(iPark3));
		assertTrue(g.add(iPark4));
		assertTrue(g.add(rwthI9));

		assertTrue(g.addEdge(rwthI9, iPark1, null) != null);
		assertTrue(g.addEdge(iPark1, iPark2, null) != null);
		assertTrue(g.addEdge(iPark2, iPark3, null) != null);
		assertTrue(g.addEdge(iPark3, iPark4, null) != null);
		assertTrue(g.addEdge(iPark1, iPark4, null) != null);

		assertTrue(g.findPath(rwthI9, iPark4).getAllItems().myLength == 3);

	}

	private void t4() throws Exception {
		GeoGraph g = new GeoGraph();
		assertTrue(g.add(rwthI9));
		assertTrue(g.add(iPark1));
		assertTrue(g.add(iPark2));
		assertTrue(g.add(iPark3));
		assertTrue(g.add(iPark4));

		assertTrue(g.addEdge(rwthI9, iPark1, null) != null);
		assertTrue(g.findPath(iPark1, rwthI9).getAllItems().myLength == 2);

		assertTrue(g.addEdge(iPark1, iPark2, null) != null);
		assertTrue(g.addEdge(iPark2, iPark3, null) != null);
		assertTrue(g.addEdge(iPark3, iPark4, null) != null);

		assertTrue(g.findPath(iPark4, iPark2).getAllItems().myLength == 3);

	}

	private void t1() throws Exception {
		GeoGraph g = new GeoGraph();
		assertTrue(g.add(iPark1));
		assertTrue(g.add(iPark2));
		assertTrue(g.add(rwthI9));
		assertTrue(g.add(iPark3));
		assertTrue(g.add(iPark4));
		assertTrue(!g.add(iPark4));
		assertTrue(g.getAllItems().myLength == 5);

		assertTrue(g.addEdge(rwthI9, iPark1, null) != null);
		assertTrue(g.addEdge(iPark1, iPark2, null) != null);
		assertTrue(g.addEdge(iPark2, iPark3, null) != null);
		assertTrue(g.addEdge(rwthI9, iPark3, null) != null);
		assertTrue(g.addEdge(iPark3, iPark4, null) != null);
		assertTrue(g.addEdge(iPark1, iPark4, null) != null);
		// g.addEdge(GeoObj.infZentPark, GeoObj.infZentPark4);

		assertTrue(g.findPath(rwthI9, iPark1).getAllItems().myLength == 2);
		assertTrue(g.findPath(iPark1, iPark2).getAllItems().myLength == 2);
		assertTrue(g.findPath(rwthI9, iPark3).getAllItems().myLength == 2);

		assertTrue(g.findPath(rwthI9, iPark4) != null);
		assertTrue(g.findPath(rwthI9, iPark4).getAllItems() != null);
		assertTrue(g.findPath(rwthI9, iPark4).getAllItems().myLength == 3);
	}

	private void t3() throws Exception {
		GeoGraph g = new GeoGraph();
		assertTrue(g.add(iPark1));
		assertTrue(g.add(iPark2));
		assertTrue(g.add(rwthI9));
		assertTrue(g.add(iPark3));
		assertTrue(g.add(iPark4));
		// assertTrue(g.add(GeoObj.zollern));

		assertTrue(g.addEdge(rwthI9, iPark1, null) != null);
		assertTrue(g.addEdge(iPark1, iPark2, null) != null);
		assertTrue(g.addEdge(iPark2, iPark3, null) != null);
		assertTrue(g.addEdge(iPark3, iPark4, null) != null);

		assertTrue(g.findPath(rwthI9, iPark4) != null);
		assertTrue(g.findPath(rwthI9, iPark4).getAllItems() != null);
		assertTrue(g.findPath(rwthI9, iPark4).getAllItems().myLength == 5);
	}

	private void t2() throws Exception {
		GeoGraph g = new GeoGraph();
		assertTrue(g.add(iPark2));
		assertTrue(g.add(iPark3));
		assertTrue(g.add(iPark4));
		assertTrue(g.add(rwthI9));

		assertTrue(g.addEdge(iPark2, iPark3, null) != null);
		assertTrue(g.addEdge(iPark3, iPark4, null) != null);

		GeoGraph path = g.findPath(iPark2, iPark4);
		assertTrue(path != null);
		assertTrue(path.getAllItems() != null);
		assertTrue(path.getAllItems().myLength == 3);
	}

}
