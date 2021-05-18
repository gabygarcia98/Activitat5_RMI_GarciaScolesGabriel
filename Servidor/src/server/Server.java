package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Server {

    private static String[] examFinal = new String[50];
    private static int numQuestion = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String word1 = "start";
            String word2= "finish";
            Registry registry = startRegistry(1099);
            ServerImplementation obj = new ServerImplementation();
            System.out.println("Hi teacher!");
            System.out.println("Type the file's name that you want to upload: ");
            uploadExam(scanner.nextLine());
            registry.bind("Exam", obj);
            System.out.println("Examen succesfully uploaded! Now students will connect to the exam room.");

            Interrupted.Interrupt interrupt = new Interrupted.Interrupt(obj, word1); // interrupt que ens servirà quan escribim start en la consola per començar l'examen
            //The tread starts reading for the key
            interrupt.start();//define the semaphore object
            synchronized (obj){
                while(!interrupt.interrupted){
                    System.out.println("Type "+ word1+ " to start the exam:");
                    obj.wait();
                }
            }
            obj.examStarted(); //es canviara el boolean com a que l'examen ha començat
            obj.notifyExamStart(numQuestion, examFinal); //es notifca que l'examen comença amb la pregunta a la que esta el client i l'examen

            Interrupted.Interrupt interrupt2 = new Interrupted.Interrupt(obj, word2);// interrupt que servira per quan el profesor vulgui acabar l'examen escribint finish
            //The tread starts reading for the key
            interrupt2.start();//define the semaphore object
            synchronized (obj){
                while(!interrupt2.interrupted){
                    System.out.println("Type "+ word2 + " to end the exam:");
                    obj.wait();
                }
            }
            obj.examFinish(); //es canviara el boolean com a que l'examen ha acabat
            obj.notifyExamFinished(); //notificara que l'examen ha acabat a cada client
            obj.getGrades();//obtindrem el .csv amb les notes dels alumnes


        } catch (Exception var3) {

            System.err.println("Server exception: " + var3.toString());
            var3.printStackTrace();
        }
    }

    public static void uploadExam(String args) throws IOException { //funció que llegeix el .csv que se li passa al Scanner
        BufferedReader fileReader = null;
        final String DELIMITER = "\n";
        try{
            String line = "";
            fileReader = new BufferedReader(new FileReader(java.lang.String.valueOf(args)));
            int i = 0;
            while ((line = fileReader.readLine()) != null){
                String[] exam = line.split(DELIMITER);
                for (String preguntes : exam){
                    examFinal[i] = preguntes;
                    i++;
                }
            }
            numQuestion = i;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private static Registry startRegistry(Integer port) throws RemoteException { //registre de cada client
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list();
            return registry;
        } catch (RemoteException ex) {
            Registry registry = LocateRegistry.createRegistry(port);
            return registry;
        }
    }
}

class Interrupted {
    static class Interrupt extends Thread {
        String interrupt_key = null;
        Object semaphore = null;
        boolean interrupted = false;

        Interrupt(Object semaphore, String interrupt_key){
            this.semaphore = semaphore;
            this.interrupt_key = interrupt_key;
        }

        public void run(){
            while (true) {
                //read the key
                Scanner scanner = new Scanner(System.in);
                String x = scanner.nextLine();
                if (x.equals(this.interrupt_key)) {
                    //if is the key we expect, change the variable, notify and return(finish thread)
                    synchronized (this.semaphore) {
                        interrupted = true;
                        this.semaphore.notify();
                        return;
                    }
                }
            }
        }
    }
}

