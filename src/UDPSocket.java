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
import java.util.Scanner;

public class UDPSocket {
	
	String host;
	int serverPort;
	
	public UDPSocket(String host, int serverPort) throws InterruptedException {
		
		this.host = host;
		this.serverPort = serverPort;
		
		Runnable clientRunnable = new Runnable() {
			public void run() {
				try {
					UDPClient();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread clientThread = new Thread(clientRunnable);
		clientThread.start();
		
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
		
		clientThread.join();
		serverThread.join();
	}
	
	public void UDPClient() throws IOException {
		
		DatagramSocket clientSocket = null;
		Scanner scanner = new Scanner(System.in);
		String nickname = new String();
		String nicknameReceived;
		String msgClient = new String();
		String msgReceived = new String();
		byte mClient[];
		
		System.out.print("Nickname: ");
		nickname = scanner.nextLine();
		
		while(!(msgReceived.equalsIgnoreCase("Fim"))) {
			clientSocket = new DatagramSocket();
			msgClient = scanner.nextLine().trim();
			
			msgClient = nickname + "|||" + msgClient;
			mClient = msgClient.getBytes();
			
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
		System.out.println("Connection finished");
		scanner.close();
	}
	
	public void UDPServer() throws IOException {
		
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
				new UDPSocket(args[0], Integer.parseInt(args[1]));
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
