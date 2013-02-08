package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import util.Log;

public class UDP {

	public static interface ReceiveListener {
		public void onReceive(String message);
	}

	public static class Server implements Runnable {

		private static final long SLEEP_TIME = 100;
		private static final int MAX_LENGTH = 2000;
		private DatagramSocket mySocket;
		private boolean running = true;
		private ReceiveListener myReceiveListener;
		private boolean listenToTheMusic;

		public Server(int serverPort) {
			try {
				mySocket = new DatagramSocket(serverPort);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			Thread t = new Thread(this);
			t.start();
		}

		public void closeConnection() {
			mySocket.close();
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				if (listenToTheMusic) {
					byte[] message = new byte[MAX_LENGTH];
					DatagramPacket p = new DatagramPacket(message,
							message.length);
					try {
						mySocket.receive(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (listenToTheMusic)
						myReceiveListener.onReceive(new String(message, 0, p
								.getLength()));
				}
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void receivePackage(ReceiveListener receiveListener) {
			myReceiveListener = receiveListener;
			listenToTheMusic = true;
		}

		public void pauseListening() {
			listenToTheMusic = false;
		}

		public void resumeListening() {
			listenToTheMusic = true;
		}

	}

	public static class Client {

		private static final String MY_TAG = "Network";
		private String myIp;
		private int myServerPort;
		private DatagramSocket mySocket;

		public Client(String serverIp, int serverPort) {
			myIp = serverIp;
			myServerPort = serverPort;
			try {
				mySocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param message
		 * @return true if the message could be send
		 */
		public boolean sendPackage(String message) {
			try {
				if (myIp == null) {
					Log.e(MY_TAG, "No ip set!");// , maybe no internet
					// connection?");
					return false;
				}
				InetAddress local = InetAddress.getByName(myIp);
				mySocket.send(new DatagramPacket(message.getBytes(), message
						.length(), local, myServerPort));
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		public void closeConnection() {
			mySocket.close();
		}

	}

	/**
	 * iterates over all network interfaces (3G, WiFi,..) to find the ip
	 * 
	 * @return null if there is no connection available
	 */
	public static String getDeviceIp() {
		try {
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces(); interfaces.hasMoreElements();) {
				NetworkInterface netInterface = interfaces.nextElement();
				for (Enumeration<InetAddress> ipAddresses = netInterface
						.getInetAddresses(); ipAddresses.hasMoreElements();) {
					InetAddress inetAddress = ipAddresses.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

}
