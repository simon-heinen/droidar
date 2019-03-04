package tests;

import network.TCP;
import network.TCP.Responder;
import network.TCP.ResponseListener;
import network.UDP;
import network.UDP.Client;
import network.UDP.Server;
import util.Log;

public class NetworkTests extends SimpleTesting {

	@Override
	public void run() throws Exception {
		udpTest();
		// tcpTest();
		// tcpThreadTest();
	}

	private void tcpTest() {
		int port = 5221;
		String targetIp = TCP.getDeviceIp();
		final String testInput1 = "aaa";
		final String testInput2 = "bbb";
		final String testResponse1 = "raaa";
		final String testResponse2 = "rbbb";

		final TCP.Server server = new TCP.Server();
		server.waitForMessage(port, new TCP.MessageListener() {
			@Override
			public void onMessage(String message, Responder response) {
				System.out.println("TCP Server got message=" + message);
				if (message.equals(testInput1)) {
					response.send(testResponse1);
				}
				if (message.equals(testInput2)) {
					response.send(testResponse2);
				}
				server.killServer();
			}
		});

		final TCP.Client client = new TCP.Client(targetIp, port);
		client.send(testInput1, new ResponseListener() {
			@SuppressWarnings("null")
			@Override
			public void onResponse(String responseMessage) {
				System.out
						.println("TCP Client got response=" + responseMessage);
				try {
					assertEquals(responseMessage, testResponse1);
				} catch (Exception e) {
					Log.e("", "TCP test failed: " + e);
					Object x = null;
					x.toString();
				}
			}
		});

		client.send(testInput2, new ResponseListener() {
			@SuppressWarnings("null")
			@Override
			public void onResponse(String responseMessage) {
				System.out
						.println("TCP Client got response=" + responseMessage);
				try {
					assertEquals(responseMessage, testResponse2);
					client.killConnection();
				} catch (Exception e) {
					Log.e("", "TCP test failed: " + e);
					Object x = null;
					x.toString();
				}
			}
		});

	}

	private void tcpThreadTest() {
		int port = 5221;
		String targetIp = TCP.getDeviceIp();
		final String testInput1 = "aaa";
		final String testInput2 = "bbb";
		final String testResponse1 = "raaa";
		final String testResponse2 = "rbbb";

		final TCP.Server server = new TCP.Server();
		server.waitForMessage(port, new TCP.MessageListener() {
			@Override
			public void onMessage(String message, Responder response) {
				System.out.println("TCP Server got message=" + message);
				if (message.equals(testInput1)) {
					response.send(testResponse1);
				}
				if (message.equals(testInput2)) {
					response.send(testResponse2);
				}
				server.killServer();
			}
		});

		final TCP.Client client = new TCP.Client(targetIp, port);

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					client.send(testInput1, new ResponseListener() {
						@SuppressWarnings("null")
						@Override
						public void onResponse(String responseMessage) {
							System.out.println("TCP Client got response="
									+ responseMessage);
							try {
								assertEquals(responseMessage, testResponse1);
							} catch (Exception e) {
								Log.e("", "TCP test failed: " + e);
								Object x = null;
								x.toString();
							}
						}
					});
				}
			}).start();
		}
	}

	/**
	 * This will send some text from the device ip to the device ip (but could
	 * be any other ip as well)
	 * 
	 * @throws Exception
	 */
	private void udpTest() throws Exception {

		int targetPort = 5221;
		final String text = "some text";
		final Server server = new UDP.Server(targetPort);
		server.receivePackage(new UDP.ReceiveListener() {
			@SuppressWarnings("null")
			@Override
			public void onReceive(String message) {
				System.out.println("UDP: recieved " + message);
				try {
					assertTrue(message.equals(text));
				} catch (Exception e) {
					Log.e("", "UDP test failed: " + e);
					Object x = null;
					x.toString();
				}
				server.closeConnection();
			}
		});

		String targetIp = UDP.getDeviceIp();
		System.out.println("UDP ip: " + targetIp);
		Client x = new UDP.Client(targetIp, targetPort);
		x.sendPackage(text);
		x.closeConnection();

	}

}
