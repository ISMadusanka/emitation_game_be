package org.example;

import org.example.enums.Role;
import org.example.enums.TaskType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerHandler extends Thread {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private int id;
    private int score = 0;
    private Role role;

    public PlayerHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                Object obj = input.readObject();
                if (obj instanceof Response) {
                    processResponse((Response) obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Player " + id + " disconnected");
        }
    }

    private void processResponse(Response response) {
        GameServer.queue.add(new Pair<>(this, response));
    }

    public void sendTask(Task task) throws IOException {
        output.writeObject(task);
        output.flush();
    }

    public int getIdd() { return id; }
    public int getScore() { return score; }
    public void addScore(int points) {
        score += points;
        try {
            sendTask(new Task(TaskType.UPDATE_SCORE, score, this.role));
        } catch (IOException e) {
            System.out.println("Error sending score update to player " + id);
        }
    }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
