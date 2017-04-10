/* * Sistemas Distribuídos * Profº. Rodrigo Campiolo * Emerson Yudi Nakashima 1451600 * Gustavo Correia Gonzalez 1551787 * TCPClient: Cliente para conexao TCP * Descrição: Envia uma informacao ao servidor e finaliza a conexao */import java.io.EOFException;import java.io.IOException;import java.net.Socket;import java.net.UnknownHostException;public class TCPClient {		public TCPClient (String host, String message) throws InterruptedException {		Socket connectionSocket = null;		try{			String serverAddress = host;			int serverPort = 7896;			connectionSocket = new Socket(serverAddress, serverPort);						new SendReceiveThread(connectionSocket, true, message);			connectionSocket.close();		} catch (UnknownHostException e){			System.out.println("Socket:"+e.getMessage());		} catch (EOFException e){			System.out.println("EOF:"+e.getMessage());		} catch (IOException e){			System.out.println("leitura:"+e.getMessage());		}	}}