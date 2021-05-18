package server;

import common.ClientInterface;
import common.ServerInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {
    private ArrayList<ClientInterface> clients = new ArrayList(); //clients els quals s'uneixen a fer els examens
    private HashMap<ClientInterface, String> idstudents = new HashMap<>(); //ids dels alumnes
    private HashMap<String, Integer> grades = new HashMap<>();
    public String[] exam = new String[50];
    public String currentStudent = new String();
    int grade = 0;
    public boolean start = false;
    public boolean finished = false;


    public ServerImplementation() throws RemoteException {
        super();
    }

    public void examStarted() {
        this.start = true;
    }

    public void examFinish() {
        this.finished = true;
    }

    @Override
    public boolean examFinished() throws RemoteException {
        return this.finished;
    }

    @Override
    public synchronized void register(ClientInterface client, String name) throws RemoteException {
        if (!start) {
            this.clients.add(client);
            this.idstudents.put(client, name);
            System.out.println("Students in the exam room: " + clients.size());
            grades.put(name, 0);
            this.notify();
        } else {
            client.examStarted();
        }
    }

    @Override
    public synchronized void sendAnswer(int numQuestion, String solution, String id) throws RemoteException { //el client envia la opcio seleccionada i es comprova si esta correcta o no, si ho esta amb aquell client se li suma 1 a la seva nota
        String answer = exam[numQuestion].substring(exam[numQuestion].length() - 1);
        currentStudent = id;
        if (answer.equals(solution)) {
            grades.replace(id, (grades.get(id) + 1));
        }
    }
    public void notifyExamStart(int numPregunta, String[] examReal) { //se notifica a cada client que l'examen ha començat i la variable exam agafa el valor de l'examen que se li pasa
        exam = examReal;
        for (ClientInterface c : clients) {
            try {
                c.notifyExamStarted(numPregunta);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyExamFinished() { //Se notifica a cada client que l'examen ha acabat i se li passa a una funció del client el id de cada client i la seva respectiva nota, per a poder fer els métodes finals
        String idalumne;
        int gradeFinal;
        for (ClientInterface c : clients) {
            try {
                idalumne = idstudents.get(c);
                gradeFinal = grades.get(idalumne);
                c.notifyExamFinished(idalumne, gradeFinal);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized String getQuestion(ClientInterface clients, int actualQuestion) throws RemoteException { // per a poder guardar cada pergunta del fitxer csv passat (no es guarda la ultima posicio ja que es la resposta del examem)
        String question = exam[actualQuestion];
        question = question.substring(0, question.length() - 1);
        return question;
    }

    @Override
    public synchronized int finishAndGetGrade(ClientInterface client, String id) throws RemoteException { // si l'examen ha acabat guardarem la nota de aquell client i eliminarem el client de la Arraylist
        if (!finished) {
            grade = grades.get(id);
        }
        clients.remove(client);
        return grade;
    }

    public void getGrades() throws IOException { //funció que s'encarrega de crear el fitxer .csv amb les notes
        final String outputFilePath = "finalmarks.csv";
        HashMap<String, Integer> finalMarks = grades;
        File file = new File(outputFilePath);
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            for (Map.Entry<String, Integer> entry : finalMarks.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {

            }
        }
    }
}
