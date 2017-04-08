/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * Multicast
 * Descrição: chat em grupo
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;

public class Multicast {
	
	private final MulticastSocket mSocket;
	private final String host;
	private final int port;
	private final InetAddress group;
	private Map<String, String> onlineMap;	

	public Multicast() throws IOException, InterruptedException {
		
		this.host = "225.1.2.3";
		this.port = 6789;
		this.group = InetAddress.getByName(this.host);
		mSocket = new MulticastSocket(this.port);
		mSocket.joinGroup(this.group);
		
		Runnable serverRun = new Runnable() {
			@Override
			public void run() {
				try {
					serverMulticast();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverRun);
		serverThread.start();
	}
	
	private void clientMulticast(String message) throws IOException {
		byte[] msgByte = message.getBytes();
		DatagramPacket msgDataOut = new DatagramPacket(msgByte,  message.length(), this.group, this.port);
		mSocket.send(msgDataOut);
	}
	
	private void serverMulticast() throws IOException {
		String nickname = new String();
		String msg = new String();
		
		while(!msg.equalsIgnoreCase("Fim")) {
			byte[] msgByte = new byte[1000];
			DatagramPacket msgDataIn = new DatagramPacket(msgByte, msgByte.length);
			mSocket.receive(msgDataIn);
			msg = new String(msgDataIn.getData());
			
			if(msg.contains("--JOIN")) {
				nickname = msg.split(" ")[1];
				nickname = nickname.substring(1, nickname.length() - 1);
				
				onlineMap.put(nickname, msgDataIn.getAddress().getHostAddress());
				sendMessage("--JOINACK [" + nickname + "]");
			}
			
			nickname = msg.split("\\|\\|\\|")[0].trim();
			msg = msg.split("\\|\\|\\|")[1].trim();
			
			System.out.println(nickname + ": " + msg);
		}
		
	}
	
	public void sendMessage(String message) throws IOException {
		clientMulticast(message);
	}
	
	public static void main(String args[]) throws NumberFormatException, IOException, InterruptedException {
		new Multicast();
	}
}
