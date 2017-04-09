/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * Implementação do socket UDP P2P
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSocket {
	
	private final int serverPort;
	
	public UDPSocket() throws InterruptedException {
		
		this.serverPort = 6799;
		
		Runnable serverRunnable = new Runnable() {
			public void run() {
				try {
					UDPServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverRunnable);
		serverThread.start();
	}
	
	private void UDPClient(String host, String message) throws IOException {
		
		DatagramSocket clientSocket = null;
		byte mClient[];

		clientSocket = new DatagramSocket();
		
		mClient = message.getBytes();
		
		InetAddress aHost = InetAddress.getByName(host);
		DatagramPacket request = new DatagramPacket(mClient, mClient.length, aHost, this.serverPort);
		
		clientSocket.send(request);
		
        clientSocket.close();
	}
	
	private void UDPServer() throws IOException {
		
		DatagramSocket serverSocket = new DatagramSocket(this.serverPort);
		String nicknameReceived = new String();
		String message = new String();
		
		while(true) {
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(request);
			
			String[] chunks = new String(request.getData()).split(" ");
			nicknameReceived = chunks[2].trim().substring(1, chunks[2].trim().length() - 1);
			
			chunks = new String(request.getData()).split("\"");
			message = chunks[1].trim().substring(0, chunks[1].trim().length());
			
			System.out.println(nicknameReceived + ": " + message);
		}
	}

	public void sendMessage(String host, String message) throws IOException {
		UDPClient(host, message);
	}
	
}
