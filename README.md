Sistemas Distribuídos
=====================

Trabalho - Sockets - Implementação de serviço de chat
-----------------------------------------------------

Implementar um serviço de chat que possibilite:
* Envio de mensagens para um grupo de pessoas (MulticastSocket);
* Envio de mensagens individuais para as pessoas ativas (DatagramSocket) - receber na porta 6799;
* Compartilhamento e download de arquivos (Socket -- TCP);
* Interface de interação (GUI ou CLI).

Protocolo textual:
* --JOIN [apelido]
    * Junta-se ao grupo de conversação
* --JOINACK [apelido]
    * Resposta ao JOIN para possibilitar a manutenção da lista de usuários ativos
* --MSG [apelido] "texto"
    * Mensagem enviada a todos os membros do grupo pelo IP 225.1.2.3 e porta 6789
* --MSGIDV FROM [apelido] TO [apelido] "texto"
    * Mensagem enviada a um membro do grupo para ser recebida na porta 6799
* --LISTFILES [apelido]
    * Solicitação de listagem de arquivos para um usuário
* --FILES [arq1, arq2, arqN]
    * Resposta para o LISTFILES
* --DOWNFILE [apelido] filename
    * Solicita arquivo do servidor.
* --DOWNINFO [filename, size, IP, PORTA]
    * Resposta com informações sobre o arquivo e conexão TCP.
* --LEAVE [apelido]
    * Deixa o grupo de conversação

Alunos
------
Emerson Yudi Nakashima 1451600
Gustavo Correia Gonzalez 1551787