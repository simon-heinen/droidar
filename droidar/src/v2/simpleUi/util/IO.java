package v2.simpleUi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class IO {

	private static final String LOG_TAG = "IO";

	public static Bitmap loadBitmapFromId(Context context, int bitmapId) {
		if (context == null) {
			Log.e(LOG_TAG, "Context was null!");
			return null;
		}
		InputStream is = context.getResources().openRawResource(bitmapId);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			Bitmap b = BitmapFactory.decodeStream(is, null, bitmapOptions);
			Log.i(LOG_TAG, "image loaded: " + b);
			return b;
		} catch (Exception ex) {
			Log.e("bitmap loading exeption", "" + ex);
			return null;
		}
	}

	public static String convertInputStreamToString(InputStream stream) {
		if (stream == null) {
			return null;
		}

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
	 * 
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
		if (v.getMeasuredHeight() <= 0) {
			// first calc the size the view will need:
			v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

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
		} else {
			Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
					v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			v.draw(c);
			return b;
		}

	}

	/**
	 * @param context
	 * @param id
	 *            something like "R.drawable.icon"
	 * @return
	 */
	public static Drawable loadDrawableFromId(Context context, int id)
			throws NotFoundException {
		return context.getResources().getDrawable(id);
	}

	/**
	 * @param filename
	 *            something like "/sdcard/test.txt"
	 * @param objectToSave
	 * @throws IOException
	 */
	public static void saveSerializableToExternalStorage(String filename,
			Serializable objectToSave) throws IOException {
		FileOutputStream foStream = new FileOutputStream(filename);
		saveSerializableToStream(objectToSave, foStream);
	}

	public static void saveSerializableToPrivateStorage(Context context,
			String filename, Serializable objectToSave) throws IOException {
		FileOutputStream fileOut = context.openFileOutput(filename,
				Activity.MODE_PRIVATE);
		saveSerializableToStream(objectToSave, fileOut);
	}

	private static void saveSerializableToStream(Serializable objectToSave,
			FileOutputStream foStream) throws IOException {
		GZIPOutputStream gzioStream = new GZIPOutputStream(foStream);
		ObjectOutputStream outStream = new ObjectOutputStream(gzioStream);
		outStream.writeObject(objectToSave);
		outStream.flush();
		outStream.close();
	}

	/**
	 * @param filename
	 *            something like "/sdcard/test.txt"
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws OptionalDataException
	 * @throws StreamCorruptedException
	 */
	public static Object loadSerializableFromExternalStorage(String filename)
			throws StreamCorruptedException, OptionalDataException,
			IOException, ClassNotFoundException {
		FileInputStream fiStream = new FileInputStream(filename);
		return loadSerializableFromStream(fiStream);
	}

	public static Object loadSerializableFromPrivateStorage(Context context,
			String filename) throws StreamCorruptedException,
			OptionalDataException, IOException, ClassNotFoundException {
		FileInputStream fiStream = context.getApplicationContext()
				.openFileInput(filename);
		return loadSerializableFromStream(fiStream);
	}

	private static Object loadSerializableFromStream(FileInputStream fiStream)
			throws IOException, StreamCorruptedException,
			OptionalDataException, ClassNotFoundException {
		GZIPInputStream gzipStream = new GZIPInputStream(fiStream);
		ObjectInputStream inStream = new ObjectInputStream(gzipStream);
		Object loadedObject = inStream.readObject();
		inStream.close();
		return loadedObject;
	}

	public static class Settings {

		Context context;
		private String mySettingsName;
		/**
		 * The editor is stored as a field because every
		 * {@link SharedPreferences}.edit() call will create a new
		 * {@link Editor} object and this way resources are saved
		 */
		private Editor e;
		private int mode = Context.MODE_PRIVATE;

		public Settings(Context target, String settingsFileName) {
			context = target;
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
			return context.getSharedPreferences(mySettingsName, mode)
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
			return context.getSharedPreferences(mySettingsName, mode)
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
			return context.getSharedPreferences(mySettingsName, mode).getInt(
					key, defaultValue);
		}

		public void storeString(String key, String value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putString(key, value);
			e.commit();
		}

		public void storeBool(String key, boolean value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putBoolean(key, value);
			e.commit();
		}

		public void storeInt(String key, int value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putInt(key, value);
			e.commit();
		}
	}

	public static String getSDCardDirectory() {
		return Environment.getExternalStorageDirectory().toString();
	}

	/**
	 * @param newDirectory
	 * @return false if the folder already existed or could not be created
	 */
	public static boolean createFolder(String newDirectory) {
		return new File(newDirectory).mkdirs();
	}

	public static boolean copy(String sourceName, String targetName) {
		File source = new File(sourceName);
		File dest = new File(targetName);
		try {
			copy(source, dest);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static List<File> getFilesInPath(String path) {
		File f = new File(path);
		return Arrays.asList(f.listFiles());
	}

	/**
	 * @param oldPath
	 *            e.g. /myFolder/test.txt
	 * @param newName
	 *            e.g. oldTest.txt
	 * @return
	 */
	public static boolean renameFile(String oldPath, String newName) {
		File source = new File(Environment.getExternalStorageDirectory(),
				oldPath);
		File dest = new File(source.getParent(), newName);
		return source.renameTo(dest);
	}

	/**
	 * http://stackoverflow.com/questions/5715104/copy-files-from-a-folder-of-sd
	 * -card-into-another-folder-of-sd-card
	 * 
	 * If targetLocation does not exist, it will be created.
	 * 
	 * @param sourceLocation
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void copy(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new IOException("Cannot create dir "
						+ targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copy(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			// make sure the directory we plan to store the recording in exists
			File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create dir "
						+ directory.getAbsolutePath());
			}

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
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
