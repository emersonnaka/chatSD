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
import java.util.Arrays;
import java.util.HashMap;

public class Multicast {
	
	private final MulticastSocket mSocket;
	private final String nickname;
	private final String host;
	private final int port;
	private final InetAddress group;
	private HashMap<String, String> onlineMap;	

	public Multicast(String nickname) throws IOException, InterruptedException {
		
		this.nickname = nickname;
		this.host = "225.1.2.3";
		this.port = 6789;
		this.group = InetAddress.getByName(this.host);
		mSocket = new MulticastSocket(this.port);
		mSocket.joinGroup(this.group);
		this.onlineMap = new HashMap<>();
		
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
		String command = new String();
		String nickname = new String();
		String msg = new String();
		
		while(true) {
			byte[] msgByte = new byte[1000];
			DatagramPacket msgDataIn = new DatagramPacket(msgByte, msgByte.length);
			mSocket.receive(msgDataIn);
			msg = new String(msgDataIn.getData()).trim();
			command = msg.split(" ")[0].trim();
			nickname = msg.split(" ")[1].trim();
			nickname = nickname.substring(1, nickname.length() - 1);
			
			if(command.equalsIgnoreCase("--MSG")) {
				msg = msg.substring(msg.indexOf("\"") + 1, msg.length() - 1);
				System.out.println(nickname + ": " + msg);
			} else if(command.equals("--JOIN")) {
				if(!onlineMap.containsKey(nickname)) {
					System.out.println(nickname + " está conectado!");
					sendMessage("--JOINACK [" + this.nickname + "]");
				}
			} else if(command.equals("--JOINACK")) {
				if(!onlineMap.containsKey(nickname))
					onlineMap.put(nickname, msgDataIn.getAddress().getHostAddress());
			} else if(command.equals("--LEAVE")) {
				System.out.println(nickname + " saiu do grupo!");
				onlineMap.remove(nickname);
				mSocket.leaveGroup(group);
				System.exit(0);
			}
			
			System.out.println(Arrays.asList(onlineMap));
		}
	}
	
	public void sendMessage(String message) throws IOException {
		clientMulticast(message);
	}

	public HashMap<String, String> getOnlineMap() {
		return onlineMap;
	}
	
}
