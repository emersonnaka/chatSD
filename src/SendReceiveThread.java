/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * Gustavo Correia Gonzalez 1551787
 * SendReceiveThread: receber e enviar mensagens via protocolo TCP
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
		
		if(receivedMsg.contains("LISTFILES")) {
			String homePath = System.getProperty("user.home");
			String pathShared = homePath + "/Downloads";
			
			File filesShared = new File(pathShared);

			String files = new String();
			files = "FILES [";
			
			for(File file : filesShared.listFiles()) {
				files = files + file.getName() + ", ";
			}
			files = files.substring(0, files.length() - 2) + "]";
			
			sendThread(files);
		} else if(receivedMsg.contains("FILES")) {
			System.out.println(receivedMsg);
			
		} else if(receivedMsg.contains("DOWNFILE")) {
			String homePath = System.getProperty("user.home");
			String pathShared = homePath + "/Downloads";
			String fileName = receivedMsg.split(" ")[2].trim();
			
			pathShared = pathShared + "/" + fileName;
			System.out.println("Filename: " + pathShared);

			sendFile(pathShared);
	        
		} else if(receivedMsg.contains("DOWNINFO")) {

			String propertyFile = receivedMsg.split(" ")[1].trim();
			String fileName = propertyFile.substring(1, propertyFile.length() - 1);
			String homePath = System.getProperty("user.home");
			fileName = homePath + "/Downloads/" + fileName;
			
			byte[] contents = new byte[10000];
			FileOutputStream fileOutput = new FileOutputStream(fileName);
			BufferedOutputStream bufferOutput = new BufferedOutputStream(fileOutput);
			InputStream inputStream = sendReceiveSocket.getInputStream();
			
			int bytesRead = 0;
			while((bytesRead = inputStream.read(contents)) != -1) {
				bufferOutput.write(contents, 0, bytesRead);
			}
			bufferOutput.flush();
			bufferOutput.close();
			System.out.println("Arquivo recebido com sucesso: " + fileName);
		}
	}
	
	private void sendFile(String fileName) throws IOException {
		File fileShared = new File(fileName);
		
		if(fileShared.exists() && !fileShared.isDirectory()) {
			FileInputStream fileInput = new FileInputStream(fileShared);
			BufferedInputStream bufferInput = new BufferedInputStream(fileInput);
			
			String reply = "DOWNINFO [" + fileShared.getName() + ", " 
					+ fileShared.length() + ", "
					+ sendReceiveSocket.getLocalAddress().toString()
					+ ", " + sendReceiveSocket.getLocalPort() + "]";
			
			sendThread(reply);
			
			OutputStream outStream = this.sendReceiveSocket.getOutputStream();
			
			byte[] contents;
			long fileLength = fileShared.length(); 
	        long current = 0;
	        
	        while(current != fileLength) {
	        	int size = 10000;
	        	
	        	if((fileLength - current) >= size)
	                current += size;    
	            else{ 
	                size = (int) (fileLength - current); 
	                current = fileLength;
	            } 
	        	
	        	contents = new byte[size];
	        	bufferInput.read(contents, 0, size);
	        	outStream.write(contents);
	        	System.out.println("Carregando arquivo: " + (current * 100) / fileLength + "%");
	        }
			outStream.flush();
			
			bufferInput.close();
	        outStream.close();
	        System.out.println("Arquivo enviado");
		}
	}
	
	private void sendThread(String message) throws IOException {
		out.writeUTF(message);
	}
}
