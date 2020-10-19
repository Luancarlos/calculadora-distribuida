package calculadora;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// ClientHandler class
class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {
        String received;

        while (true) {
            try {
                // receive the answer from client
                received = dis.readUTF();

                if(received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                if (received != null) {
                    ProtocoloRequest protocolo = new ProtocoloRequest();
                    ProtocoloResponse protocoloResponse = new ProtocoloResponse();

                    protocolo.converterStringValue(received);

                    switch (protocolo.getOperador()) {
                        case "^" :
                            int po = (int) Math.pow(protocolo.getValor1(), protocolo.getValor2());
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(String.valueOf(po));
                            break;

                        case "%" :
                            String resultado = String.valueOf((protocolo.getValor1() * protocolo.getValor2()) / 100);
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(resultado);
                            break;

                        case "+" :
                            int soma = (int) (protocolo.getValor1() + protocolo.getValor2());
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(String.valueOf(soma));
                            break;

                        case "-" :
                            int sub = (int) (protocolo.getValor1() - protocolo.getValor2());
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(String.valueOf(sub));
                            break;

                        case "*" :
                            int mult = (int) (protocolo.getValor1() * protocolo.getValor2());
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(String.valueOf(mult));
                            break;

                        case "√" :
                            double raiz = Math.sqrt(protocolo.getValor2());
                            protocoloResponse.setCode(CODE.C10);
                            protocoloResponse.setMensagem(String.valueOf(raiz));
                            break;

                        case "/" :
                            if (protocolo.getValor2() == 0) {
                                protocoloResponse.setCode(CODE.C20);
                                protocoloResponse.setMensagem("Não é um numero");
                            } else {
                                String result = String.valueOf(protocolo.getValor1() / protocolo.getValor2());
                                protocoloResponse.setCode(CODE.C10);
                                protocoloResponse.setMensagem(result);
                            }

                            break;

                        default:
                            protocoloResponse.setCode(CODE.C20);
                            protocoloResponse.setMensagem(protocoloResponse.getCode().mensagem(null));
                            break;
                    }

                    protocoloResponse.conveterValoresEmString();
                    dos.writeUTF(protocoloResponse.getStrinResponse());

                } else {
                    break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch(IOException e){
            e.printStackTrace();
        }
    }
}