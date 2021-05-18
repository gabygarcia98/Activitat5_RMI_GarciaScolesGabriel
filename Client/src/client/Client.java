package client;

import common.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.rmi.RemoteException;


public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String host = (args.length < 1) ? null : args[0];
        try {

            Registry registry = LocateRegistry.getRegistry(host);

            ClientImplementation client = new ClientImplementation();
            ServerInterface server = (ServerInterface) registry.lookup("Exam");
            System.out.println("Enter your ID:");
            String id = scanner.nextLine();
            server.register(client, id);

            if (!client.exam_started) { //mentres que l'examen no comenci els students es podran registrar
                System.out.println("Student" + id + " succesfully registered ");
                System.out.println("The exam will start soon! Please be attentive.");
                synchronized (client){
                    client.wait();
                    int nQ = 0;
                    boolean timeOut = false;
                    while(nQ < client.numQuestions && !client.examFinished){ // bucle que comprova si encara hi han preguntes a contestar i l'examen no ha acabat
                        if(server.examFinished()) {
                            client.examFinished();
                            timeOut = true;

                        }else{
                            String question = server.getQuestion(client, nQ);  //es printen i es contesten les preguntes, el servirdor mirara si al resposta seleccionada es la correcta i actualitzara la nota
                            client.printQuestion(nQ + 1, question);
                            String selected = client.chooseAnswers();
                            server.sendAnswer(nQ, selected, id);
                            nQ++;
                        }
                    }
                    client.examFinished();
                    if(!timeOut){ //si l'examen acaba pel professor s'acabarà l'examen, es printara les notes a cada client.
                        int grade = server.finishAndGetGrade(client,id);
                        client.finishedExam(grade);
                    }
                }

            } else {
                System.err.println("Failed to enter in the exam room");
            }

        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}