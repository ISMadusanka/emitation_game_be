package org.example;

import org.example.enums.ResponseType;
import org.example.enums.TaskType;
import org.example.ui.GameUI;

import javax.swing.*;
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
    private GameUI ui;

    public void start(String serverIP) {
        try {
            socket = new Socket(serverIP, 12345);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            ui = new GameUI(this);
            ui.setVisible(true);
            new Thread(this::listenForTasks).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
        }
    }

    private void listenForTasks() {
        try {
            while (!socket.isClosed()) {
                Task task = (Task) input.readObject();
                processTask(task);
            }
        } catch (IOException | ClassNotFoundException e) {
            SwingUtilities.invokeLater(() ->
                    ui.showMessage("Disconnected from server"));
        }
    }

    private void processTask(Task task) {
        SwingUtilities.invokeLater(() -> {
            if (task.getType() == TaskType.UPDATE_SCORE) {
                ui.updateScore((Integer) task.getData());
            } else if (task.getType() == TaskType.GAME_OVER) {
                ui.showGameOver((String) task.getData());
            } else {
                ui.handleTask(task);
            }
        });
    }

    public void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        String serverIP = JOptionPane.showInputDialog("Enter server IP:");
        new GameClient().start(serverIP);
    }
}