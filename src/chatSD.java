import java.util.Scanner;

public class chatSD {

	private String nickname;
	private String message;
	private String command;
	private final Scanner scanner;
	
	public chatSD() {
		
		scanner = new Scanner(System.in);
		
		System.out.print("Choose your nickname: ");
		nickname = new String(scanner.nextLine());
		message = new String();
		command = new String();
		
		while(!message.equalsIgnoreCase("--LEAVE")) {
			message = scanner.nextLine();
			command = message.split(" ")[0];
			
			if(command.equalsIgnoreCase("--JOINACK")) {
				System.out.println("JOINACK");
			} else if(command.equalsIgnoreCase("--JOIN")) {
				System.out.println("--JOIN");
			} else if(command.equalsIgnoreCase("--MSG")) {
				System.out.println("--MSG");
			} else if(command.equalsIgnoreCase("--MSGDIV")) {
				System.out.println("--MSGDIV");
			} else if(command.equalsIgnoreCase("--LISTFILES")) {
				System.out.println("--LISTFILES");
			} else if(command.equalsIgnoreCase("--FILES")) {
				System.out.println("--FILES");
			} else if(command.equalsIgnoreCase("--DOWNFILE")) {
				System.out.println("--DOWNFILE");
			} else if(command.equalsIgnoreCase("--DOWNINFO")) {
				System.out.println("--DOWNINFO");
			} else if(command.equalsIgnoreCase("--LEAVE")) {
				System.out.println("--LEAVE");
			} else {
				printHelp();
			}
		}
	}
	
	private void printHelp() {
		System.out.println("Commands:");
		System.out.println("--JOIN [apelido] \t\t Junta-se ao grupo de conversação");
		System.out.println("--JOINACK [apelido] \t\t Resposta ao JOIN para possibilitar a manutenção da lista de usuários ativos");
		System.out.println("--MSG [apelido] texto \t\t Mensagem enviada a todos os membros do grupo pelo IP 225.1.2.3 e porta 6789");
		System.out.println("--MSGIDV FROM [apelido] TO [apelido] texto \t\t Mensagem enviada a um membro do grupo para ser recebida na porta 6799");
		System.out.println("--LISTFILES [apelido] \t\t Solicitação de listagem de arquivos para um usuário");
		System.out.println("--FILES [arq1, arq2, arqN] \t\t Resposta para o LISTFILES");
		System.out.println("--DOWNFILE [apelido] filename \t\t Solicita arquivo do servidor.");
		System.out.println("--DOWNINFO [filename, size, IP, PORTA] \t\t Resposta com informações sobre o arquivo e conexão TCP.");
		System.out.println("--LEAVE [apelido] \t\t Deixa o grupo de conversação");
	}
	
	public static void main(String[] args) {
		new chatSD();
	}

}
