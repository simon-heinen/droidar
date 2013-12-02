package preview;

import de.rwth.R;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLRenderer;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class AugmentedView extends FrameLayout {
	
	private CustomGLSurfaceView mGLSurfaceView;
	private CameraView mCameraView;
	private GL1Renderer mRenderer;
	private View mGuiView;
	
	
	public AugmentedView(Context context) {
		super(context);
		initialize(context);
		addOverlays();
	}
	
	private void initialize(Context context){
		mGLSurfaceView = createGLSurface(context);
		mCameraView    = createCameraView(context);
		mGuiView       = createGuiView(context);
		mRenderer   = createRenderer(context);
		mGLSurfaceView.setRenderer(mRenderer);
	}
	
	private void addOverlays(){
		mGLSurfaceView.setZOrderMediaOverlay(true);
		addView(mGuiView);
		addView(mCameraView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(mGLSurfaceView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mGLSurfaceView.setZOrderMediaOverlay(true);
	}
		
	/**
	 * Get the current gl surface view. 
	 * @return
	 */
	public CustomGLSurfaceView getGLSurfaceView(){
		return mGLSurfaceView;
	}
	
	/**
	 * Get the current camera view.
	 * @return
	 */
	public CameraView getCameraView(){
		return mCameraView;
	}
	
	/**
	 * 
	 * @return
	 */
	public View getGuiView(){
		return mGuiView;
	}
	
	/**
	 * 
	 * @return
	 */
	public GL1Renderer getRenderer(){
		return mRenderer;
	}
		
	/**
	 * Override this method if you want to implement custom <code>GLSurfaceView</code>
	 * @param context
	 * @return
	 */
	protected CustomGLSurfaceView createGLSurface(Context context){
		return new CustomGLSurfaceView(context);
	}
	
	/**
	 * Override this method if you want to implement custom <code>CameraView</code>
	 * @param context
	 * @return
	 */
	protected CameraView createCameraView(Context context){
		return new CameraView(context);
	}
	
	/**
	 * Override this method if you want to implement custom <code>GLRenderer</code>
	 * @param context
	 * @return
	 */
	protected GL1Renderer createRenderer(Context context){
		return new GL1Renderer();
	}
	
	/**
	 * Override this method if you want to implement custom gui view
	 * @param context
	 * @return
	 */
	protected View createGuiView(Context context){
		return View.inflate(context, R.layout.defaultlayout, null);
	}
	

}
