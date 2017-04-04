/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * SendReceiveThread: receber e enviar mensagens
 * Descrição: uma thread para cada operação, receber e enviar
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class SendReceiveThread {
	
	DataInputStream in;
	DataOutputStream out;
	Socket sendReceiveSocket;
	Scanner scanner;
	String sendedMsg;
	String receivedMsg;
	boolean connectionCompleted;
	
	public SendReceiveThread (Socket connection) throws IOException, InterruptedException {
		sendReceiveSocket = connection;
		in = new DataInputStream(sendReceiveSocket.getInputStream());
		out = new DataOutputStream(sendReceiveSocket.getOutputStream());
		scanner = new Scanner (System.in);
		sendedMsg = new String();
		receivedMsg = new String();
		connectionCompleted = false;
		
		Runnable receiveRunnable = new Runnable(){
			public void run(){
				try {
					receiveThread();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Thread receiveThread = new Thread(receiveRunnable);
		receiveThread.start();
		
		Runnable sendRunnable = new Runnable() {
			public void run() {
				try {
					sendThread();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Thread sendThread = new Thread(sendRunnable);
		sendThread.start();
		
		receiveThread.join();
		System.out.println("Receive thread is dead");
		sendThread.join();
		System.out.println("Send thread is dead");
		in.close();
		out.close();
		sendReceiveSocket.close();
		System.out.println("Connection is closed");
	}
	
	public void receiveThread() throws IOException {
		while(!(receivedMsg = in.readUTF()).equalsIgnoreCase("Fim") && !(connectionCompleted))
			System.out.println("Received: " + receivedMsg);
		
		System.out.println("Received: " + receivedMsg);
		
		if(!connectionCompleted)
			connectionCompleted = true;
	}
	
	public void sendThread() throws IOException {
		while(!(sendedMsg = scanner.nextLine()).equalsIgnoreCase("Fim") && !(connectionCompleted))
			out.writeUTF(sendedMsg);
		
		out.writeUTF("Fim");
		
		if(!connectionCompleted)
			connectionCompleted = true;
	}
}
