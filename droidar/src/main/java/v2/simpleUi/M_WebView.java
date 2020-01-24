package v2.simpleUi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class M_WebView implements ModifierInterface {

	private boolean useDefaultZoomControls;
	private boolean useTransparentBackground;

	public M_WebView(boolean useDefaultZoomControls,
			boolean useTransparentBackground) {
		this.useDefaultZoomControls = useDefaultZoomControls;
		this.useTransparentBackground = useTransparentBackground;
	}

	@Override
	public View getView(final Context context) {
		WebView w = new WebView(context);
		w.getSettings().setBuiltInZoomControls(useDefaultZoomControls);
		if (useTransparentBackground)
			w.setBackgroundColor(0x00000000);
		w.getSettings().setJavaScriptEnabled(true);

		w.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				onPageLoadProgress(progress * 100);
			}
		});

		w.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.startsWith("market://")) {
					try {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(url));
						marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
								| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						context.startActivity(marketIntent);
						return true;
					} catch (Exception e) {
					}
				}
				view.loadUrl(url);
				return dontLoadUrlInWebview(url); // then it is not handled by
													// default action
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				CookieSyncManager.getInstance().sync();
				view.loadUrl("javascript:window.HTMLOUT.processHTML("
						+ "document.getElementsByTagName("
						+ "'body')[0].innerHTML);");
			}
		});

		w.addJavascriptInterface(new Object() {
			@SuppressWarnings("unused")
			public void processHTML(String html) {
				onPageLoaded(html);
			}
		}, "HTMLOUT");
		w.clearView();
		w.loadUrl(getUrlToDisplay());
		return w;
	}

	protected abstract void onPageLoaded(String html);

	/**
	 * @param url
	 * @return true if the new loaded url should not be loaded in the web-view
	 */
	protected boolean dontLoadUrlInWebview(String url) {
		return false;
	}

	public abstract void onPageLoadProgress(int progressInPercent);

	/**
	 * @return e.g. "www.google.de" or "file:///android_asset/" + "myFile.htm"
	 */
	public abstract String getUrlToDisplay();

	@Override
	public boolean save() {
		return true;
	}

}
