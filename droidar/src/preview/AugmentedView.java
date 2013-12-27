package preview;

import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import de.rwth.R;

/**
 *  A view that contains the GLSurfaceView, CameraView and Gui overlay for augmented reality 
 *  applications. 
 *  This class can be extended to customize the following components:
 *  	{@link preview.AugmentedView#createGLSurface(Context)}
 *  	{@link preview.AugmentedView#createCameraView(Context)}
 *  	{@link preview.AugmentedView#createGuiView(Context)}
 *  	{@link preview.AugmentedView#createRenderer(Context)}
 */
public class AugmentedView extends FrameLayout {
	
	private CustomGLSurfaceView mGLSurfaceView;
	private CameraView mCameraView;
	private GL1Renderer mRenderer;
	private View mGuiView;
	
	/**
	 * Constructor - needed by superclass {@link FrameLayou}.
	 * @param context - {@link Context}
	 */
	public AugmentedView(Context context) {
		super(context);
		initialize(context);
		addOverlays();
	}
	
	private void initialize(Context context) {
		mGLSurfaceView = createGLSurface(context);
		mCameraView    = createCameraView(context);
		mGuiView       = createGuiView(context);
		mRenderer   = createRenderer(context);
		mGLSurfaceView.setRenderer(mRenderer);
	}
	
	private void addOverlays() {
		mGLSurfaceView.setZOrderMediaOverlay(true);
		addView(mCameraView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(mGLSurfaceView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(mGuiView);
	}
		
	/**
	 * Get the current gl surface view. 
	 * @return - {@link gl.CustomGLSurfaceView}
	 */
	public CustomGLSurfaceView getGLSurfaceView() {
		return mGLSurfaceView;
	}
	
	/**
	 * Get the current camera view.
	 * @return - {@link preview.CameraView}
	 */
	public CameraView getCameraView() {
		return mCameraView;
	}
	
	/**
	 * Get the current gui view.
	 * @return - {@link View}
	 */
	public View getGuiView() {
		return mGuiView;
	}
	
	/**
	 * Get the current renderer.
	 * @return - {@link gl.GL1Renderer}
	 */
	public GL1Renderer getRenderer() {
		return mRenderer;
	}
		
	/**
	 * Override this method if you want to implement custom <code>GLSurfaceView</code>.
	 * @param context - {@link android.app.Context}
	 * @return - {@link gl.CustomGLSurfaceView}
	 */
	protected CustomGLSurfaceView createGLSurface(Context context) {
		return new CustomGLSurfaceView(context);
	}
	
	/**
	 * Override this method if you want to implement custom <code>CameraView</code>.
	 * @param context - {@link android.app.Context}
	 * @return - {@link preview.CameraView}
	 */
	protected CameraView createCameraView(Context context) {
		return new CameraView(context);
	}
	
	/**
	 * Override this method if you want to implement custom <code>GLRenderer</code>.
	 * @param context - {@link android.app.Context}
	 * @return - {@link gl.GL1Renderer}
	 */
	protected GL1Renderer createRenderer(Context context) {
		return new GL1Renderer();
	}
	
	/**
	 * Override this method if you want to implement custom gui view.
	 * @param context - {@link android.app.Context}
	 * @return - {@link View}
	 */
	protected View createGuiView(Context context) {
		return View.inflate(context, R.layout.defaultlayout, null);
	}
	

}
