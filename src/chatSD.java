/*
 * Sistemas Distribuídos
 * Profº. Rodrigo Campiolo
 * Emerson Yudi Nakashima 1451600
 * Gustavo Correia Gonzalez 1551787
 * Implementação de serviço de chat
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class chatSD {

	private String nickname;
	private String message;
	private final Scanner scanner;
	private final Multicast multicastChat;
	private final UDPSocket udpChat;
	
	public chatSD() throws IOException, InterruptedException {
		
		scanner = new Scanner(System.in);
		
		System.out.print("Insira seu apelido: ");
		nickname = new String(scanner.nextLine()).trim();

		udpChat = new UDPSocket();
		System.out.println("Executando servidor UDP");
		new TCPServer();
		System.out.println("Executando servidor TCP");
		
		multicastChat = new Multicast();		
		multicastChat.sendMessage("JOIN [" + nickname + "]");

		
		message = new String();
		String nicknameSend = new String();
		
		Runnable joinRun = new Runnable() {
			@Override
			public void run() {
				try {
					verifyJoin();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread verifyThread = new Thread(joinRun);
		verifyThread.start();
		
		Runnable joinAckRun = new Runnable() {
			@Override
			public void run() {
				try {
					verifyJoinAck();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread verifyAckThread = new Thread(joinAckRun);
		verifyAckThread.start();
		
		while(!message.contains("LEAVE")) {
			message = scanner.nextLine();
			
			if(message.contains("MSGDIV FROM")) {
				nicknameSend = message.split(" ")[2].trim();
				String nicknameSendTo = message.split(" ")[4].trim();
				
				if(verifyNickname(nicknameSend) && verifyNickname(nicknameSendTo)) {
					if(multicastChat.getOnlineMap().containsKey(nicknameSendTo.substring(1, nicknameSendTo.length() - 1))) {
						String host = (String) multicastChat.getOnlineMap().get(nicknameSendTo.substring(1, nicknameSendTo.length() - 1));
						udpChat.sendMessage(host, message);
					} else
						System.out.println("O usuário " + nicknameSendTo + " não está conectado no grupo!");
				} else
					System.out.println("É necessário que o apelido esteja entre colchetes!");
				
			} else if(message.contains("MSG")) {
				nicknameSend = message.split(" ")[1].trim();
				if(verifyNickname(nicknameSend)) {
					if(verifyDoubleQuotes(message))
						multicastChat.sendMessage(message);
					else
						System.out.println("A mensagem deve ser escrita entre aspas duplas!");
				} else
					System.out.println("É necessário que o apelido esteja entre colchetes!");
				
			} else if(message.contains("LISTFILES") || message.contains("DOWNFILE")) {
				nicknameSend = message.split(" ")[1].trim();
				if(verifyNickname(nicknameSend)) {
					if(multicastChat.getOnlineMap().containsKey(nicknameSend.substring(1, nicknameSend.length() - 1))) {
						String host = (String) multicastChat.getOnlineMap().get(nicknameSend.substring(1, nicknameSend.length() - 1));
						new TCPClient(host, message);
					} else
						System.out.println("O usuário " + nicknameSend + " não está conectado no grupo!");
				} else
					System.out.println("É necessário que o apelido esteja entre colchetes!");
				
			} else if(message.contains("LEAVE")) {
				multicastChat.sendMessage("LEAVE" + " [" + this.nickname + "]");
				
			} else if(message.contains("LIST")) {
				HashMap<String, String> onlineMap = multicastChat.getOnlineMap();
				
				System.out.println("Os seguintes usuários estão online:");
				Set<String> keys = onlineMap.keySet();
				for(String key: keys)
	        		System.out.println(key);
				
			} else {
				printHelp();
			}
		}
	}
	
	private void printHelp() {
		System.out.println("Commands:");
		System.out.println("MSG [apelido] \"texto\" \t\t Mensagem enviada a todos os membros do grupo pelo IP 225.1.2.3 e porta 6789");
		System.out.println("MSGIDV FROM [apelido] TO [apelido] \"texto\" \t\t Mensagem enviada a um membro do grupo para ser recebida na porta 6799");
		System.out.println("LISTFILES [apelido] \t\t Solicitação de listagem de arquivos para um usuário");
		System.out.println("DOWNFILE [apelido] filename \t\t Solicita arquivo do servidor.");
		System.out.println("LEAVE [apelido] \t\t Deixa o grupo de conversação");
		System.out.println("Os colchetes e aspas dulpas são obrigatórios");
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new chatSD();
	}

	private boolean verifyNickname(String nick) {
		if(nick.startsWith("[")) {
			if(nick.indexOf("]") == nick.length() - 1)
				return true;
		}
		return false;
	}
	
	private boolean verifyDoubleQuotes(String message) {
		int i = 0;
		if(message.lastIndexOf("\"") == message.length() - 1) {
			while(message.charAt(i) != '\"')
				i++;
			if(message.charAt(i) == '\"' && i != message.length() - 1)
				return true;
		}
		return false;
	}
	
	private void verifyJoin() throws IOException, InterruptedException {
		String nickname, host;
		System.out.println("VerifyJoin rodando");
		while(true) {
			if(multicastChat.isJoin()) {
				nickname = new String(multicastChat.getNickJoin());
				host = new String(multicastChat.getHostJoin());
				
				if(!multicastChat.getOnlineMap().containsKey(nickname)) {
					multicastChat.getOnlineMap().put(nickname, host);
					udpChat.sendMessage(host, "JOINACK [" + this.nickname + "]");
				}

				multicastChat.setJoin(false);
			}
			TimeUnit.SECONDS.sleep(1);
		}
	}
	
	private void verifyJoinAck() throws IOException, InterruptedException {
		String nickname = new String();
		String host = new String();
		
		while(true) {
			if(udpChat.isJoinAck()) {
				nickname = udpChat.getNickJoinAck();
				if(!multicastChat.getOnlineMap().containsKey(nickname)) {
					host = udpChat.getHostjoinAck();
					multicastChat.getOnlineMap().put(nickname, host);
				}
				udpChat.setJoinAck(false);
			}
			TimeUnit.SECONDS.sleep(1);
		}
	}
	
}
