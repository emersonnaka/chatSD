import java.io.IOException;
import java.util.Scanner;

public class chatSD {

	private String nickname;
	private String message;
	private final Scanner scanner;
	private final Multicast multicastChat;
	private final UDPSocket udpChat;
	
	public chatSD() throws IOException, InterruptedException {
		
		scanner = new Scanner(System.in);
		
		System.out.print("Choose your nickname: ");
		nickname = new String(scanner.nextLine()).trim();
		
		multicastChat = new Multicast(nickname);		
		multicastChat.sendMessage("--JOIN [" + nickname + "]");
		
		udpChat = new UDPSocket();
		
		message = new String();
		String nicknameSend = new String();
		
		while(!message.contains("--LEAVE")) {
			message = scanner.nextLine();
			
			if(message.contains("--MSGDIV FROM")) {
				nicknameSend = message.split(" ")[2].trim();
				System.out.println(nicknameSend);
				String nicknameSendTo = message.split(" ")[4].trim();
				System.out.println(nicknameSendTo);
				if(verifyNickname(nicknameSend) && verifyNickname(nicknameSendTo)) {
					System.out.println("Nickanames aprovador");
					if(multicastChat.getOnlineMap().containsKey(nicknameSendTo.substring(1, nicknameSendTo.length() - 1))) {
						String host = (String) multicastChat.getOnlineMap().get(nicknameSendTo.substring(1, nicknameSendTo.length() - 1));
						udpChat.sendMessage(host, message);
					} else
						System.out.println("O usuário " + nicknameSendTo + " não está conectado no grupo!");;
				} else
					System.out.println("É necessário que o apelido esteja entre colchetes!");
			} else if(message.contains("--MSG")) {
				nicknameSend = message.split(" ")[1].trim();
				if(verifyNickname(nicknameSend)) {
					if(verifyDoubleQuotes(message))
						multicastChat.sendMessage(message);
					else
						System.out.println("A mensagem deve ser escrita entre aspas duplas!");
				} else
					System.out.println("É necessário que o apelido esteja entre colchetes!");
			} else if(message.contains("--LISTFILES")) {
				System.out.println("--LISTFILES");
			} else if(message.contains("--FILES")) {
				System.out.println("--FILES");
			} else if(message.contains("--DOWNFILE")) {
				System.out.println("--DOWNFILE");
			} else if(message.contains("--DOWNINFO")) {
				System.out.println("--DOWNINFO");
			} else if(message.contains("--LEAVE")) {
				multicastChat.sendMessage("--LEAVE" + " [" + this.nickname + "]");
			} else {
				printHelp();
			}
		}
		System.out.println("Conexão finalizada");
	}
	
	private void printHelp() {
		System.out.println("Commands:");
		System.out.println("--MSG [apelido] \"texto\" \t\t Mensagem enviada a todos os membros do grupo pelo IP 225.1.2.3 e porta 6789");
		System.out.println("--MSGIDV FROM [apelido] TO [apelido] \"texto\" \t\t Mensagem enviada a um membro do grupo para ser recebida na porta 6799");
		System.out.println("--LISTFILES [apelido] \t\t Solicitação de listagem de arquivos para um usuário");
		System.out.println("--FILES [arq1, arq2, arqN] \t\t Resposta para o LISTFILES");
		System.out.println("--DOWNFILE [apelido] filename \t\t Solicita arquivo do servidor.");
		System.out.println("--DOWNINFO [filename, size, IP, PORTA] \t\t Resposta com informações sobre o arquivo e conexão TCP.");
		System.out.println("--LEAVE [apelido] \t\t Deixa o grupo de conversação");
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
	
}
