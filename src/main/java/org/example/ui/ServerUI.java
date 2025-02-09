package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class ServerUI extends JFrame {
    private JTextArea logArea;
    private JList<String> playerList;

    public ServerUI() {
        setTitle("Game Server");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Player list
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(new JLabel("Connected Players:"), BorderLayout.NORTH);
        playerList = new JList<>();
        playerPanel.add(new JScrollPane(playerList), BorderLayout.CENTER);
        playerPanel.setPreferredSize(new Dimension(150, 0));

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);

        mainPanel.add(playerPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    public void updatePlayerList(java.util.List<String> players) {
        playerList.setListData(players.toArray(new String[0]));
    }

    public void log(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerUI::new);
    }
}

