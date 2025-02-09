package org.example.ui;

import org.example.enums.Role;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClientUI extends JFrame {
    private JTextArea gameArea;
    private JPanel inputPanel;
    private JTextField inputField;
    private JButton submitButton;
    private String currentAnswer;

    public ClientUI() {
        setTitle("Game Client");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        gameArea = new JTextArea();
        gameArea.setEditable(false);

        inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        submitButton = new JButton("Submit");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        inputPanel.setVisible(false);

        mainPanel.add(new JScrollPane(gameArea), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public void showQuestionInput() {
        gameArea.append("\nYou are the Judge! Enter your question:\n");
        inputPanel.setVisible(true);
        inputField.setText("");
        submitButton.addActionListener(this::handleQuestionSubmit);
    }

    public void showAnswerInput(String question) {
        gameArea.append("\nQuestion from Judge: " + question + "\nEnter your answer:\n");
        inputPanel.setVisible(true);
        inputField.setText("");
        submitButton.addActionListener(this::handleAnswerSubmit);
    }

    public void showAnswerSelection(java.util.List<String> answers) {
        gameArea.append("\nSelect the Queen's answer:\n");
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1));

        for (int i = 0; i < answers.size(); i++) {
            JButton btn = new JButton((i+1) + ") " + answers.get(i));
            int finalI = i;
            btn.addActionListener(e -> handleAnswerSelection(finalI));
            optionsPanel.add(btn);
        }

        getContentPane().add(optionsPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void handleQuestionSubmit(ActionEvent e) {
        currentAnswer = inputField.getText();
        inputPanel.setVisible(false);
        // Send answer to server
    }

    private void handleAnswerSubmit(ActionEvent e) {
        currentAnswer = inputField.getText();
        inputPanel.setVisible(false);
        // Send answer to server
    }

    private void handleAnswerSelection(int index) {
        // Send selection to server
        getContentPane().remove(1); // Remove options panel
        revalidate();
        repaint();
    }

    public void updateGameStatus(String status) {
        gameArea.append(status + "\n");
    }

    public void showRole(Role role) {
        gameArea.append("You are: " + role + "\n");

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientUI::new);
    }
}