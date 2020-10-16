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

        while (true)
        {
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
                    String resultado;
                    Protocolo protocolo = new Protocolo();
                    protocolo.converterStringValue(received);

                    switch (protocolo.getOperador()) {
                        case "^" :
                            int po = (int) Math.pow(protocolo.getValor1(), protocolo.getValor2());
                            resultado = String.valueOf(po);
                            break;

                        case "%" :
                            resultado = String.valueOf((protocolo.getValor1() * protocolo.getValor2()) / 100);
                            break;

                        case "+" :
                            int soma = (int) (protocolo.getValor1() + protocolo.getValor2());
                            resultado = String.valueOf(soma);
                            break;

                        case "-" :
                            int sub = (int) (protocolo.getValor1() - protocolo.getValor2());
                            resultado = String.valueOf(sub);
                            break;

                        case "*" :
                            int mult = (int) (protocolo.getValor1() * protocolo.getValor2());
                            resultado = String.valueOf(mult);
                            break;

                        case "/" :
                            if (protocolo.getValor2() == 0) {
                                resultado = "Não é um numero";
                            } else {
                                resultado = String.valueOf(protocolo.getValor1() / protocolo.getValor2());
                            }

                            break;
                        default: resultado = "Ocoreu um erro";
                    }

                    dos.writeUTF(resultado);

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