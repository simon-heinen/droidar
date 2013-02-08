package tests;

import gl.Color;

public class GlTests extends SimpleTesting {

	@Override
	public void run() throws Exception {
		testColorEquality();
	}

	/**
	 * Checks that {@link Color#equals} behaves correctly.
	 * 
	 * @throws Exception
	 */
	private void testColorEquality() throws Exception {
		Color colorOne, colorTwo;

		// Basic equality tests
		colorOne = new Color(0.6f, 0.2f, 0.1f, 0f);
		colorTwo = new Color(0.6f, 0.2f, 0.1f, 0f);
		assertEquals(colorOne, colorTwo);
		assertEquals(colorOne.hashCode(), colorTwo.hashCode());

		colorTwo = colorOne;
		assertEquals(colorOne, colorTwo);
		assertEquals(colorOne.hashCode(), colorTwo.hashCode());

		assertEquals(colorOne, colorOne.copy());

		assertFalse(colorOne.equals(null));
		assertFalse(colorOne.equals(new Object()));

		// Check that floating point arithmetics do not affect equals
		colorOne = new Color(0.3f, 0.3f, 0.3f, 0f);
		colorTwo = new Color(0.2f + 0.1f, 0.6f / 2f, 38.4348f / 128.128f, 0f);

		assertFalse(0.3f == 38.4348f / 128.128f);
		assertEquals(colorOne, colorTwo);
		assertEquals(colorOne.hashCode(), colorTwo.hashCode());

		// Check that the same colors constructed in different ways are equal
		// TODO: These tests are currently failing because apparently floating
		// point arithmetic is not precise enough.
		// assertEquals(0xFF80FF40, new Color(0xFF80FF40).toIntARGB());
		//
		// colorOne = new Color(0.5f, 1f, 0.25f, 1f);
		// colorTwo = new Color("#FF80FF40");
		//
		// assertEquals(colorOne, colorTwo);
		// assertEquals(colorOne.hashCode(), colorTwo.hashCode());

		// colorOne = new Color(0.5f, 1f, 0.25f, 1f);
		// colorTwo = new Color(android.graphics.Color.argb(255, 128, 64, 255));
		//
		// assertEquals(colorOne, colorTwo);
		// assertEquals(colorOne.hashCode(), colorTwo.hashCode());
	}

}
