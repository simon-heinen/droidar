package gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Use this interface for custom rendering.
 * 
 * @author Spobo
 * 
 */
public interface Renderable {
	/**
	 * Implement this method to render an GL object.
	 * 
	 * @param gl
	 *            {@link GL10}
	 * @param parent
	 *            {@link Renderable}'s parent
	 */
	void render(GL10 gl, Renderable parent);
}
