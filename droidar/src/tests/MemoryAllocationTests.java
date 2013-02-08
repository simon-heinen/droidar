package tests;

import java.util.ArrayList;
import java.util.Iterator;

import util.EfficientList;
import util.Log;
import android.os.SystemClock;

/**
 * This demonstrates that the use of {@link EfficientList} on older devices is
 * much faster (often 2x faster) than the use of {@link ArrayList}! The the
 * Log-Output for test-results!
 * 
 * The use of get needs also 2
 * 
 * @author Spobo
 * 
 */
public class MemoryAllocationTests extends SimpleTesting {

	@Override
	public void run() throws Exception {
		int number = 60000;
		speedTestEffiListA(number);// G1: 57 ms; Nexus: 29ms
		speedTestEffiListB(number);// G1: 110 ms; Nexus: 32ms
		speedTestEffiListC(number);// G1: 130 ms; Nexus: 34ms
		speedTestArrayList(number);// Google G1: 268 ms; Nexus: 33ms
	}

	private void speedTestEffiListA(int objectOfNumber) {
		long start;
		long end;

		Log.d("Test", "EfficientList creation with " + objectOfNumber
				+ " objects.");
		start = SystemClock.uptimeMillis();
		EfficientList<String> list = new EfficientList<String>();
		for (int i = 0; i < objectOfNumber; i++) {
			list.add("Text " + i);
		}
		end = SystemClock.uptimeMillis();
		Log.d("Test", "Creation ends. Duration=" + (end - start));

		Log.e("Test", "EfficientList Test starts with " + objectOfNumber
				+ " objects.");
		start = SystemClock.uptimeMillis();
		int l = list.myLength;
		for (int i = 0; i < l; i++) {
			if (list.get(i) == "") {
				System.out.println("Will not happen");
			}
		}
		end = SystemClock.uptimeMillis();
		Log.e("Test", "Test ends. Duration=" + (end - start));
	}

	private void speedTestEffiListB(int objectOfNumber) {
		long start;
		long end;

		Log.d("Test", "EfficientList creation with " + objectOfNumber
				+ " objects.");
		start = SystemClock.uptimeMillis();
		EfficientList<String> list = new EfficientList<String>();
		for (int i = 0; i < objectOfNumber; i++) {
			list.add("Text " + i);
		}
		end = SystemClock.uptimeMillis();
		Log.d("Test", "Creation ends. Duration=" + (end - start));

		Log.e("Test", "EfficientList Test >with get()< starts with "
				+ objectOfNumber + " objects.");
		start = SystemClock.uptimeMillis();
		int l = list.myLength;
		for (int i = 0; i < l; i++) {
			if (list.get(i) == "") {
				System.out.println("Will not happen");
			}
		}
		end = SystemClock.uptimeMillis();
		Log.e("Test", "Test ends. Duration=" + (end - start));
	}

	private void speedTestEffiListC(int objectOfNumber) {
		long start;
		long end;

		Log.d("Test", "EfficientList creation with " + objectOfNumber
				+ " objects.");
		start = SystemClock.uptimeMillis();
		EfficientList<String> list = new EfficientList<String>();
		for (int i = 0; i < objectOfNumber; i++) {
			list.add("Text " + i);
		}
		end = SystemClock.uptimeMillis();
		Log.d("Test", "Creation ends. Duration=" + (end - start));

		Log.e("Test",
				"EfficientList Test >with get() and lengh as field< starts with "
						+ objectOfNumber + " objects.");
		start = SystemClock.uptimeMillis();
		for (int i = 0; i < list.myLength; i++) {
			if (list.get(i) == "") {
				System.out.println("Will not happen");
			}
		}
		end = SystemClock.uptimeMillis();
		Log.e("Test", "Test ends. Duration=" + (end - start));
	}

	private void speedTestArrayList(int objectOfNumber) {
		long start;
		long end;

		Log.d("Test", "ArrayList creation with " + objectOfNumber + " objects.");
		start = SystemClock.uptimeMillis();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < objectOfNumber; i++) {
			list.add("Text " + i);
		}
		end = SystemClock.uptimeMillis();
		Log.d("Test", "Creation ends. Duration=" + (end - start));

		Log.e("Test", "ArrayList Test starts with " + objectOfNumber
				+ " objects.");
		start = SystemClock.uptimeMillis();
		int l = list.size();
		// Object[] a = list.;
		for (int i = 0; i < l; i++) {
			if (list.get(i) == "") {
				System.out.println("Will not happen");
			}
		}
		end = SystemClock.uptimeMillis();
		Log.e("Test", "Test ends. Duration=" + (end - start));
	}

	/**
	 * The "good" way to do it. This way no iterator objects are created
	 * 
	 * @param durationInMS
	 * @throws Exception
	 */
	private void CollectionTestA(int durationInMS) throws Exception {

		ArrayList<String> x = new ArrayList<String>();
		for (int j = 0; j < 200; j++) {
			String s = "String " + j;
			x.add(s);
		}
		System.out.println("Allocation Test starts");
		long start = SystemClock.uptimeMillis();
		while (SystemClock.uptimeMillis() - start < durationInMS) {
			for (int j = 0; j < x.size(); j++) {
				String s = x.get(j);
				if (s == null) {
					System.out.println("tutut");
				}
			}
		}
		System.out.println("Allocation Test ends");
	}

	/**
	 * One of the bad ways. THis way many iterator objects will be created!
	 * 
	 * @param durationInMS
	 * @throws Exception
	 */
	private void CollectionTestB(int durationInMS) throws Exception {

		ArrayList<String> x = new ArrayList<String>();
		for (int j = 0; j < 200; j++) {
			String s = "String " + j;
			x.add(s);
		}
		System.out.println("Allocation Test starts");
		long start = SystemClock.uptimeMillis();
		while (SystemClock.uptimeMillis() - start < durationInMS) {

			for (Iterator iterator = x.iterator(); iterator.hasNext();) {
				String s = (String) iterator.next();
				if (s == null) {
					System.out.println("tutut");
				}
			}

		}
		System.out.println("Allocation Test ends");
	}

	/**
	 * One of the bad ways. THis way many iterator objects will be created!
	 * 
	 * @param durationInMS
	 * @throws Exception
	 */
	private void CollectionTestC(int durationInMS) throws Exception {

		ArrayList<String> x = new ArrayList<String>();
		for (int j = 0; j < 200; j++) {
			String s = "String " + j;
			x.add(s);
		}
		System.out.println("Allocation Test starts");
		long start = SystemClock.uptimeMillis();
		while (SystemClock.uptimeMillis() - start < durationInMS) {

			for (String s : x) {
				if (s == null) {
					System.out.println("tutut");
				}
			}

		}
		System.out.println("Allocation Test ends");
	}

}
