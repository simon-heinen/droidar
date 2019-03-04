package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class IO {

	private static final String LOG_TAG = "IO";

	public static Bitmap loadBitmapFromId(Context context, int bitmapId) {
		if (context == null)
			return null;
		InputStream is = context.getResources().openRawResource(bitmapId);
		try {
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeStream(is, null, bitmapOptions);
		} catch (Exception ex) {
			Log.e("bitmap loading exeption", ex.getLocalizedMessage());
			return null;
		}
	}

	public static String convertInputStreamToString(InputStream stream) {
		if (stream == null)
			return null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			stream.close();
			return sb.toString();

		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not convert input stream to string");
		}
		return null;
	}

	public static Bitmap createRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(new RectF(rect), pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * any type of image can be imported this way
	 * 
	 * @param imagePath
	 *            for example "/sdcard/abc.PNG"
	 * @return
	 */
	public static Bitmap loadBitmapFromFile(String imagePath) {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(imagePath, bitmapOptions);
	}

	/**
	 * @param url
	 * @return
	 */
	public static Bitmap loadBitmapFromURL(String url) {

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			int length = connection.getContentLength();
			if (length > -1) {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
				return BitmapFactory.decodeStream(connection.getInputStream(),
						null, bitmapOptions);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while loading an image from an URL: " + e);
		}
		return null;
	}

	/**
	 * turns any view in a bitmap to load it to openGL eg
	 * 
	 * @param v
	 *            the view to transform into the bitmap
	 * @return the bitmap with the correct size of the view
	 */
	public static Bitmap loadBitmapFromView(View v) {
		int width = LayoutParams.WRAP_CONTENT;
		int heigth = LayoutParams.WRAP_CONTENT;
		return loadBitmapFromView(v, width, heigth);
	}

	/**
	 * turns any view in a bitmap to load it to openGL eg
	 * 
	 * @param v
	 *            the view to convert to the bitmap
	 * @param width
	 *            e.g. LayoutParams.WRAP_CONTENT or
	 *            MeasureSpec.makeMeasureSpec(*some width*, MeasureSpec.AT_MOST)
	 * @param heigth
	 * @return
	 */
	public static Bitmap loadBitmapFromView(View v, int width, int heigth) {
		// first calc the size the view will need:
		v.measure(width, heigth);
		// then create a bitmap to store the views drawings:
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
				v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		// wrap the bitmap:
		Canvas c = new Canvas(b);
		// set the view size to the mesured values:
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		// and draw the view onto the bitmap contained in the canvas:
		v.draw(c);
		return b;
	}

	/**
	 * @param context
	 * @param id
	 *            something like "R.drawable.icon"
	 * @return
	 */
	public static Drawable loadDrawableFromId(Context context, int id) {
		return context.getResources().getDrawable(id);
	}

	/**
	 * @param filename
	 *            something like "/sdcard/test.txt"
	 * @param objectToSave
	 */
	public static void saveSerializable(String filename,
			Serializable objectToSave) {
		try {
			FileOutputStream foStream = new FileOutputStream(filename);
			GZIPOutputStream gzioStream = new GZIPOutputStream(foStream);
			ObjectOutputStream outStream = new ObjectOutputStream(gzioStream);
			outStream.writeObject(objectToSave);
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object loadSerializable(String filename) {
		try {
			FileInputStream fiStream = new FileInputStream(filename);
			GZIPInputStream gzipStream = new GZIPInputStream(fiStream);
			ObjectInputStream inStream = new ObjectInputStream(gzipStream);
			Object loadedObject = inStream.readObject();
			inStream.close();
			return loadedObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class Settings {

		Activity myActivity;
		private String mySettingsName;
		/**
		 * The editor is stored as a field because every
		 * {@link SharedPreferences}.edit() call will create a new
		 * {@link Editor} object and this way resources are saved
		 */
		private Editor e;
		private int mode = Context.MODE_PRIVATE;

		public Settings(Activity target, String settingsFileName) {
			myActivity = target;
			mySettingsName = settingsFileName;
		}

		/**
		 * @param mode
		 *            default value is {@link Context}.MODE_PRIVATE
		 */
		public void setMode(int mode) {
			this.mode = mode;
		}

		public String loadString(String key) {
			return myActivity.getSharedPreferences(mySettingsName, mode)
					.getString(key, null);
		}

		/**
		 * @param key
		 * @param defaultValue
		 *            the value which will be returned if there was no value
		 *            found for the given key
		 * @return
		 */
		public boolean loadBool(String key, boolean defaultValue) {
			return myActivity.getSharedPreferences(mySettingsName, mode)
					.getBoolean(key, defaultValue);
		}

		/**
		 * @param key
		 * @param defaultValue
		 *            the value which will be returned if there was no value
		 *            found for the given key
		 * @return
		 */
		public int loadInt(String key, int defaultValue) {
			return myActivity.getSharedPreferences(mySettingsName, mode)
					.getInt(key, defaultValue);
		}

		public void storeString(String key, String value) {
			if (e == null)
				e = myActivity.getSharedPreferences(mySettingsName, mode)
						.edit();
			e.putString(key, value);
			e.commit();
		}

		public void storeBool(String key, boolean value) {
			if (e == null)
				e = myActivity.getSharedPreferences(mySettingsName, mode)
						.edit();
			e.putBoolean(key, value);
			e.commit();
		}

		public void storeInt(String key, int value) {
			if (e == null)
				e = myActivity.getSharedPreferences(mySettingsName, mode)
						.edit();
			e.putInt(key, value);
			e.commit();
		}
	}

	public static String getSDCard() {
		return Environment.getExternalStorageDirectory().toString();
	}

	/**
	 * @param newDirectory
	 * @return false if the folder already existed or could not be created
	 */
	public static boolean createFolder(String newDirectory) {
		return new File(newDirectory).mkdirs();
	}

	public static Bitmap loadBitmapFromAssetsFolder(Context context,
			String fileName) {
		try {
			Log.e(LOG_TAG, "Trying to load " + fileName
					+ " from assets folder!");
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeStream(context.getAssets()
					.open(fileName), null, bitmapOptions);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not load " + fileName
					+ " from assets folder!");
		}
		return null;
	}

}
