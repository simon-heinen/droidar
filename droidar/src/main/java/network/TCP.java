package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class TCP {

	public static interface ResponseListener {
		public void onResponse(String responseMessage);
	}

	public static class Client {

		private Socket mySocket;
		private PrintWriter myWriter;
		private BufferedReader myReader;

		public Client(String serverIp, int serverPort) {
			try {
				mySocket = new Socket(serverIp, serverPort);
				myWriter = new PrintWriter(mySocket.getOutputStream());
				myReader = new BufferedReader(new InputStreamReader(
						mySocket.getInputStream()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void killConnection() {
			try {
				mySocket.close();
				System.out.println("TCP Client killed by user");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(final String message, final ResponseListener listener) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						myWriter.println(message);
						System.out
								.println("TCP Client send message, waiting for response");
						listener.onResponse(myReader.readLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

	}

	public static interface MessageListener {
		public void onMessage(String message, Responder response);
	}

	public static class Responder {

		private PrintWriter myWriter;

		public Responder(PrintWriter printWriter) {
			myWriter = printWriter;
		}

		public void send(String response) {
			myWriter.println(response);
			System.out.println("TCP Server send response=" + response);
		}

	}

	public static class Server {
		// private ServerSocket myServerSocket;
		// private MessageListener myListener;
		private boolean running = true;

		public void waitForMessage(int port, MessageListener listener) {
			try {
				final ServerSocket myServerSocket = new ServerSocket(port);
				final MessageListener myListener = listener;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							System.out
									.println("TCP Server waiting for connection");
							Socket socket = myServerSocket.accept();
							BufferedReader inputStream = new BufferedReader(
									new InputStreamReader(socket
											.getInputStream()));
							Responder response = new Responder(new PrintWriter(
									socket.getOutputStream(), true));
							System.out
									.println("TCP Server has connection, waiting for message");
							while (running) {
								System.out
										.println("TCP Server is waiting for a new Line");
								myListener.onMessage(inputStream.readLine(),
										response);
							}
							System.out
									.println("TCP Server stoped, closing connection");
							socket.close();
							myServerSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void killServer() {
			System.out.println("TCP Server killed by user");
			running = false;
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
