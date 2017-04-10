/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * Gustavo Correia Gonzalez 1551787
 * Multicast
 * Descrição: chat em grupo
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		Thread serverThread = new Thread(serverRun);
		serverThread.start();
	}
	
	private void clientMulticast(String message) throws IOException {
		byte[] msgByte = message.getBytes();
		DatagramPacket msgDataOut = new DatagramPacket(msgByte,  msgByte.length, this.group, this.port);
		mSocket.send(msgDataOut);
	}
	
	private void serverMulticast() throws IOException, InterruptedException {
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
			
			if(command.equals("--MSG")) {
				msg = msg.substring(msg.indexOf("\"") + 1, msg.length() - 1);
				System.out.println(nickname + ": " + msg);
			} else if(command.equals("--JOIN")) {
				if(!onlineMap.containsKey(nickname)) {
					sendMessage("--JOINACK [" + this.nickname + "]");
				}
			} else if(command.equals("--JOINACK")) {
				if(!onlineMap.containsKey(nickname)) {
					System.out.println(nickname + " entrou no grupo!");
					onlineMap.put(nickname, msgDataIn.getAddress().getHostAddress());
				}
			} else if(command.equals("--LEAVE")) {
				System.out.println(nickname + " saiu do grupo!");
				onlineMap.remove(nickname);
				mSocket.leaveGroup(group);
				System.exit(0);
			}
		}
	}
	
	public void sendMessage(String message) throws IOException {
		clientMulticast(message);
	}

	public HashMap<String, String> getOnlineMap() {
		return onlineMap;
	}
	
}
