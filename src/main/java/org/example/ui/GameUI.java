package org.example.ui;

import org.example.GameClient;
import org.example.Response;
import org.example.Task;
import org.example.enums.ResponseType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameUI extends JFrame {
    private final GameClient client;
    private JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
    private JLabel roleLabel = new JLabel("Role: ", SwingConstants.CENTER);
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private JPanel currentTaskPanel;

    public GameUI(GameClient client) {
        this.client = client;
        setTitle("Game Client");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        infoPanel.add(scoreLabel);
        infoPanel.add(roleLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        add(mainPanel);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void handleTask(Task task) {
        roleLabel.setText("Role: " + task.getRole());
        if (currentTaskPanel != null) {
            mainPanel.remove(currentTaskPanel);
        }

        switch (task.getType()) {
            case SUBMIT_QUESTION:
                currentTaskPanel = createQuestionPanel();
                break;
            case ANSWER_QUESTION:
                currentTaskPanel = createAnswerPanel((String) task.getData());
                break;
            case SELECT_ANSWER:
                currentTaskPanel = createSelectionPanel((List<String>) task.getData());
                break;
        }

        mainPanel.add(currentTaskPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField questionField = new JTextField(20);
        JButton submit = new JButton("Submit Question");

        submit.addActionListener(e -> {
            client.sendResponse(new Response(ResponseType.QUESTION, questionField.getText()));
            panel.removeAll();
            panel.add(new JLabel("Question submitted!"));
            revalidate();
            repaint();
        });

        panel.add(new JLabel("Enter your question:"), gbc);
        gbc.gridy++;
        panel.add(questionField, gbc);
        gbc.gridy++;
        panel.add(submit, gbc);
        return panel;
    }

    private JPanel createAnswerPanel(String question) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField answerField = new JTextField(20);
        JButton submit = new JButton("Submit Answer");

        submit.addActionListener(e -> {
            client.sendResponse(new Response(ResponseType.ANSWER, answerField.getText()));
            panel.removeAll();
            panel.add(new JLabel("Answer submitted!"));
            revalidate();
            repaint();
        });

        panel.add(new JLabel("Question: " + question), gbc);
        gbc.gridy++;
        panel.add(answerField, gbc);
        gbc.gridy++;
        panel.add(submit, gbc);
        return panel;
    }

    private JPanel createSelectionPanel(List<String> answers) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < answers.size(); i++) {
            JRadioButton rb = new JRadioButton((i + 1) + ") " + answers.get(i));
            rb.setActionCommand(String.valueOf(i));
            group.add(rb);
            panel.add(rb);
        }

        JButton submit = new JButton("Select Answer");
        submit.addActionListener(e -> {
            if (group.getSelection() != null) {
                int selection = Integer.parseInt(group.getSelection().getActionCommand());
                client.sendResponse(new Response(ResponseType.SELECTION, selection));
                panel.removeAll();
                panel.add(new JLabel("Selection submitted!"));
                revalidate();
                repaint();
            }
        });

        panel.add(submit);
        return panel;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showGameOver(String message) {
        if (currentTaskPanel != null) {
            mainPanel.remove(currentTaskPanel);
            currentTaskPanel = null;
        }

        JLabel gameOverLabel = new JLabel(message, SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 24));
        mainPanel.add(gameOverLabel, BorderLayout.CENTER);

        for (Component comp : mainPanel.getComponents()) {
            comp.setEnabled(false);
        }

        revalidate();
        repaint();
    }
}
