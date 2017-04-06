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
	
	private final String host;
	private final int serverPort;
	
	public UDPSocket(String host) throws InterruptedException {
		
		this.host = host;
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
		
		serverThread.join();
	}
	
	public void UDPClient(String message) throws IOException {
		
		DatagramSocket clientSocket = null;
		String nicknameReceived;
		String msgReceived = new String();
		byte mClient[];

		clientSocket = new DatagramSocket();
		
		mClient = message.getBytes();
		
		InetAddress aHost = InetAddress.getByName(this.host);
		DatagramPacket request = new DatagramPacket(mClient, mClient.length, aHost, this.serverPort);
		
		clientSocket.send(request);
		
		byte[] buffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        clientSocket.receive(reply);

        String[] chunks = new String(reply.getData()).split("\\|\\|\\|");
        nicknameReceived = chunks[0].trim();
        msgReceived = chunks[1].trim();
        System.out.println(nicknameReceived + ": " + msgReceived);
        clientSocket.close();
	}
	
	private void UDPServer() throws IOException {
		
		DatagramSocket serverSocket = new DatagramSocket(this.serverPort);
		String msgServer = new String();
		while(!msgServer.equalsIgnoreCase("Fim")) {
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(request);
			
			String[] chunks = new String(request.getData()).split("\\|\\|\\|");
			msgServer = chunks[1].trim();
			System.out.println(chunks[0] + ": " + chunks[1]);
			
			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort()); // cria um pacote com os dados
			serverSocket.send(reply);
		}
		serverSocket.close();
		System.out.println("Server disconnected");
	}
	
	public static void main (String args[]) {
		
		if(args.length == 2) {
			try {
				new UDPSocket(args[0]);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Insira por linha de comando: ip_destino número_porta");
		}

	}
	
	
}
