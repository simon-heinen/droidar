package tests;

import util.EfficientList;
import util.EfficientListQualified;

public class EfficientListTests extends SimpleTesting {

	@Override
	public void run() throws Exception {
		t1();
		t2();
	}

	private void t1() throws Exception {
		EfficientList<String> e = new EfficientList<>();
		assertTrue(e.myLength == 0);
		String s = "";
		String s2 = "";
		String s3 = "";

		e.add(s);
		e.add(s2);
		assertTrue(e.myLength == 2);
		assertTrue(e.getArrayCopy()[0] == s);
		assertTrue(e.getArrayCopy()[1] == s2);
		assertTrue(e.insert(0, s3));
		assertTrue(e.getArrayCopy()[0] == s3);
		assertTrue(e.getArrayCopy()[1] == s);
		assertTrue(e.getArrayCopy()[2] == s2);
		assertTrue(e.myLength == 3);
		assertTrue(e.remove(s3));
		assertTrue(e.insert(2, s3));
		assertTrue(e.getArrayCopy()[0] == s);
		assertTrue(e.getArrayCopy()[1] == s2);
		assertTrue(e.getArrayCopy()[2] == s3);
		assertTrue(e.myLength == 3);
		assertTrue(e.remove(s));
		assertTrue(e.remove(s2));
		assertTrue(e.remove(s3));
		assertTrue(e.myLength == 0);
		// TODO make more operations
	}

	private void t2() throws Exception {
		EfficientListQualified<String> e = new EfficientListQualified<>();
		assertTrue(e.myLength == 0);
		String s = "";
		String s2 = "";
		String s3 = "";

		assertTrue(e.add(s));
		assertTrue(e.add(s2));
		assertTrue(e.myLength == 2);
		assertTrue(e.getArrayCopy()[0] == s);
		assertTrue(e.getArrayCopy()[1] == s2);
		assertTrue(e.insert(0, s3));
		assertTrue(e.getArrayCopy()[0] == s3);
		assertTrue(e.getArrayCopy()[1] == s);
		assertTrue(e.getArrayCopy()[2] == s2);
		assertTrue(e.myLength == 3);
		assertTrue(e.remove(s3));
		assertTrue(e.insert(2, s3));
		assertTrue(e.getArrayCopy()[0] == s);
		assertTrue(e.getArrayCopy()[1] == s2);
		assertTrue(e.getArrayCopy()[2] == s3);
		assertTrue(e.myLength == 3);
		assertTrue(e.remove(s));
		assertTrue(e.remove(s2));
		assertTrue(e.remove(s3));
		assertTrue(e.myLength == 0);

		e.add(s, 1);
		e.add(s2, 2);
		e.add(s3, 1.5f);
		// e.printDebugInfos();
		assertTrue(e.myLength == 3);
		assertTrue(e.getArrayCopy()[0] == s);
		assertTrue(e.getArrayCopy()[1] == s3);
		assertTrue(e.getArrayCopy()[2] == s2);
		// TODO make more operations
	}

}
