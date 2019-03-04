package tests;

public class ThreadTest extends SimpleTesting {

	private class TestRunnalbe1 implements Runnable {
		W w;

		public TestRunnalbe1(W w) {
			this.w = w;
		}

		@Override
		public void run() {
			for (int i = 0; i < 500; i++) {
				// will increase the first number:
				String p1 = w.x.substring(0, w.x.indexOf("/"));
				String p2 = w.x.substring(w.x.indexOf("/") + 1, w.x.length());
				int i1 = Integer.parseInt(p1);
				int i2 = Integer.parseInt(p2);
				i1++;
				w.x = i1 + "/" + i2;
				System.out.println("1 w.x=" + w.x);
			}

		}
	}

	private class TestRunnalbe2 implements Runnable {
		W w;

		public TestRunnalbe2(W w) {
			this.w = w;
		}

		@Override
		public void run() {
			for (int i = 0; i < 500; i++) {
				// will increase the second number:
				String p1 = w.x.substring(0, w.x.indexOf("/"));
				String p2 = w.x.substring(w.x.indexOf("/") + 1, w.x.length());
				int i1 = Integer.parseInt(p1);
				int i2 = Integer.parseInt(p2);
				i2++;
				w.x = i1 + "/" + i2;
				System.out.println("2 w.x=" + w.x);
			}

		}
	}

	private class W {
		public volatile String x;

		public W(String x) {
			this.x = x;
		}
	}

	@Override
	public void run() throws Exception {
		String x = "0/0";
		W w = new W(x);
		/*
		 * the log-output will demonstrate the problem. it loop iteration will
		 * be 500 times but the end-value of w.x will not be 500 like expected.
		 * This is just a demonstration that sometimes it is very difficult to
		 * work thread-save:
		 */
		new Thread(new TestRunnalbe1(w)).start();
		new Thread(new TestRunnalbe2(w)).start();
	}

}
