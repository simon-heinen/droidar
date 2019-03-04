package tests;

public abstract class SimpleTesting {

	public SimpleTesting() {
		// TODO execute run automatically
	}

	public abstract void run() throws Exception;

	public void assertTrue(boolean b) throws Exception {
		if (!b) {
			throw new Exception("A result was not true!");
		}
	}

	public void assertFalse(boolean b) throws Exception {
		if (b) {
			throw new Exception("A result was not false!");
		}
	}

	public void assertNotNull(Object o) throws Exception {
		if (o == null) {
			throw new Exception("An object was null!");
		}
	}

	public void assertEquals(Object a, Object b) throws Exception {
		if (!a.equals(b)) {
			throw new Exception("Two objects were not equal! a=" + a + ", b="
					+ b);
		}
	}

}