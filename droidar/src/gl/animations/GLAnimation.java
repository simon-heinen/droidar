package gl.animations;

import logger.ARLogger;
import worlddata.RenderableEntity;
import worlddata.Updateable;

/**
 * An animation can be used to do predefined movements (which might be continous
 * but dont have to be). Some of the most important {@link GLAnimation}s are
 * {@link AnimationFaceToCamera} or {@link AnimationRotate}.
 * 
 * @author Spobo
 * 
 */
public abstract class GLAnimation implements RenderableEntity {

	private static final String LOG_TAG = GLAnimation.class.getSimpleName();

	@Override
	public Updateable getMyParent() {
		ARLogger.error(LOG_TAG, "Get parent called which is not "
				+ "implemented for this component!");
		return null;
	}

	@Override
	public void setMyParent(Updateable parent) {
		// can't have children so the parent does not have to be stored
		ARLogger.error(LOG_TAG, "Set parent called which is not "
				+ "implemented for this component!");
	}

}
