package client;

import common.ClientInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {
    boolean exam_started = false;
    int numQuestions;
    boolean examFinished = false;


    public ClientImplementation() throws RemoteException {
        super();
    }

    public String chooseAnswers() throws RemoteException{ //El estudiant selecciona la resposta que vol de l'examen
        Scanner scanner = new Scanner(System.in);
        String x = scanner.nextLine();
        return x;
    }
    public void finishedExam(int grade){ //quan l'examen acaba per l'estudiant printarà la nota i el missatge canviarà depenent de la nota que tregui (suspes o aprovat)
        if(examFinished){
            System.out.println("---------------EXAM FINISHED---------------\n");
            System.out.println("You finished the exam succesfully!\n");
            if(grade < 5){
                System.out.println("Your grade is: " + grade + " See your soon in the resit exam!");
            }else{
                System.out.println("Your grade is: " + grade + " Congratulations!");
            }
        }
    }
    public void printQuestion(int numQuestion, String question){ //s'encarrega de printar les preguntes per a cada estudiant que estigui fent l'examen
        if(!examFinished){
            String[] q = question.split(";");
            System.out.println("Question " + numQuestion);
            for(String s : q){
                System.out.println(s);
            }

        }
    }

    @Override
    public void notifyExamStarted(int examQuestion) throws RemoteException{ //notifiquem que l'examen comença
        numQuestions = examQuestion;
        synchronized (this){
            this.notify();
        }
    }
    @Override
    public void notifyExamFinished(String id, int gradeFinal) throws RemoteException{ //quan l'examen es acabat pel professor utilitzarà aquesta funci´po per printar la nota que portava l'alumne
        System.out.println("---------------TIME OUT---------------");
        System.out.print("The exam was finished by the teacher! \n");
        if(gradeFinal < 5) {
            System.out.println("Your grade is: " + gradeFinal + " See you soon in the resit exam!");
        } else {
            System.out.println("Your grade is: " + gradeFinal + "Congratulations!");
        }
    }

    @Override
    public void examStarted(){  //quan l'examen ha començat aquesta variable booleana agafara el valor de true

        exam_started = true;
    }

    @Override
    public void examFinished(){  //quan l'examen ha acabat(ja sigui pel professor o pel alumne) aquesta variable booleana agafara el valor de true

        examFinished = true; }

}



