package org.example;

import org.example.enums.ResponseType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class GameClient {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public void start(String serverIP) {
        try {
            socket = new Socket(serverIP, 12345);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server");

            new Thread(this::listenForTasks).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForTasks() {
        try {
            while (!socket.isClosed()) {
                Task task = (Task) input.readObject();
                processTask(task);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disconnected from server");
        }
    }

    private void processTask(Task task) {
        try {
            switch (task.getType()) {
                case SUBMIT_QUESTION:
                    System.out.print("Enter your question: ");
                    String question = new Scanner(System.in).nextLine();
                    output.writeObject(new Response(ResponseType.QUESTION, question));
                    break;

                case ANSWER_QUESTION:
                    String q = (String) task.getData();
                    System.out.println("You are: "+task.getRole());
                    System.out.print("Answer the question '" + q + "': ");
                    String answer = new Scanner(System.in).nextLine();
                    output.writeObject(new Response(ResponseType.ANSWER, answer));
                    break;

                case SELECT_ANSWER:
                    @SuppressWarnings("unchecked")
                    List<String> answers = (List<String>) task.getData();
                    System.out.println("Select the correct answer:");
                    for (int i = 0; i < answers.size(); i++) {
                        System.out.println((i+1) + ") " + answers.get(i));
                    }
                    int selection = new Scanner(System.in).nextInt() - 1;
                    output.writeObject(new Response(ResponseType.SELECTION, selection));
                    break;
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.print("Enter server IP: ");
        String serverIP = new Scanner(System.in).nextLine();
        new GameClient().start(serverIP);
    }
}