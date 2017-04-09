/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * SendReceiveThread: receber e enviar mensagens
 * Descrição: uma thread para cada operação, receber e enviar
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class SendReceiveThread {
	
	DataInputStream in;
	DataOutputStream out;
	Socket sendReceiveSocket;
	String receivedMsg;
	
	public SendReceiveThread (Socket connection) throws IOException, InterruptedException {
		sendReceiveSocket = connection;
		in = new DataInputStream(sendReceiveSocket.getInputStream());
		out = new DataOutputStream(sendReceiveSocket.getOutputStream());
		receivedMsg = new String();
		
		receive();
		in.close();
		out.close();
	}
	
	public SendReceiveThread (Socket connection, boolean isClient, String message) throws IOException, InterruptedException {
		sendReceiveSocket = connection;
		in = new DataInputStream(sendReceiveSocket.getInputStream());
		out = new DataOutputStream(sendReceiveSocket.getOutputStream());
		receivedMsg = new String();
		
		sendThread(message);
		receive();
		
		in.close();
		out.close();
	}
	
	private void receive() throws IOException {
		receivedMsg = in.readUTF();
		
		if(receivedMsg.contains("--LISTFILES")) {
			String homePath = System.getProperty("user.home");
			String pathShared = homePath + "/Imagens";
			
			File filesShared = new File(pathShared);

			String files = new String();
			files = "--FILES [";
			
			for(File file : filesShared.listFiles()) {
				files = files + file.getName() + ", ";
			}
			files = files.substring(0, files.length() - 2) + "]";
			
			sendThread(files);
		} else if(receivedMsg.contains("--FILES")) {
			System.out.println(receivedMsg);
		}
	}
	
	private void sendThread(String message) throws IOException {
		System.out.println(message);
		out.writeUTF(message);
		System.out.println("enviado");
	}
}
