package org.example;

import org.example.enums.Role;
import org.example.enums.TaskType;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 3;
    public static BlockingQueue<Pair<PlayerHandler, Response>> queue = new LinkedBlockingQueue<>();
    private final List<PlayerHandler> players = new ArrayList<>();
    private int currentJudgeIndex = 0;
    private int round = 0;

    // In GameServer class, modify the start() method:
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            System.out.println("Players should connect to one of these IPs:");

            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // Filter out loopback and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Filter IPv6 addresses
                    if (addr instanceof Inet4Address) {
                        System.out.println("- " + addr.getHostAddress());
                    }
                }
            }

            System.out.println("Port: " + PORT);
            System.out.println("Waiting for players...");

            // Rest of the existing code...
            while (players.size() < MAX_PLAYERS) {
                Socket socket = serverSocket.accept();
                PlayerHandler player = new PlayerHandler(socket, players.size() + 1);
                players.add(player);
                player.start();
                System.out.println("Player " + player.getIdd() + " connected");
            }

            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startGame() {
        for (round = 0; round < 6; round++) {
            assignRoles();
            conductJudgeTurn();
            currentJudgeIndex = (currentJudgeIndex + 1) % players.size();
        }
        declareWinner();
    }

    private void assignRoles() {
        PlayerHandler judge = players.get(currentJudgeIndex);
        List<PlayerHandler> nonJudges = new ArrayList<>(players);
        nonJudges.remove(judge);
        Collections.shuffle(nonJudges);

        judge.setRole(Role.JUDGE);
        nonJudges.get(0).setRole(Role.QUEEN);
        nonJudges.subList(1, nonJudges.size()).forEach(p -> p.setRole(Role.KNIGHT));
    }

    private void conductJudgeTurn() {
        try {
            PlayerHandler judge = players.get(currentJudgeIndex);
            PlayerHandler queen = players.stream()
                    .filter(p -> p.getRole() == Role.QUEEN)
                    .findFirst()
                    .orElseThrow();

            String question = getQuestionFromJudge(judge);
            Map<PlayerHandler, String> answers = collectAnswers(question, queen);
            int score = processAnswers(judge, queen, answers);
            judge.addScore(score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getQuestionFromJudge(PlayerHandler judge) throws InterruptedException, IOException {
        judge.sendTask(new Task(TaskType.SUBMIT_QUESTION, null,judge.getRole()));
        Pair<PlayerHandler, Response> responsePair = queue.take();
        return (String) responsePair.getValue().getData();
    }

    private Map<PlayerHandler, String> collectAnswers(String question, PlayerHandler queen) {
        Map<PlayerHandler, String> answers = new HashMap<>();
        players.stream()
                .filter(p -> p.getRole() != Role.JUDGE)
                .forEach(p -> {
                    try {
                        p.sendTask(new Task(TaskType.ANSWER_QUESTION, question,p.getRole()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        for (int i = 0; i < players.size() - 1; i++) {
            try {
                Pair<PlayerHandler, Response> responsePair = queue.take();
                answers.put(responsePair.getKey(), (String) responsePair.getValue().getData());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return answers;
    }

    private int processAnswers(PlayerHandler judge, PlayerHandler queen, Map<PlayerHandler, String> answers) {
        try {
            List<String> shuffledAnswers = new ArrayList<>(answers.values());
            Collections.shuffle(shuffledAnswers);
            int queenIndex = shuffledAnswers.indexOf(answers.get(queen));

            for (int questionNum = 1; questionNum <= 3; questionNum++) {
                judge.sendTask(new Task(TaskType.SELECT_ANSWER, shuffledAnswers,judge.getRole()));
                Pair<PlayerHandler, Response> responsePair = queue.take();
                int selectedIndex = (Integer) responsePair.getValue().getData();

                if (selectedIndex == queenIndex) {
                    return 12 - 2 * questionNum;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void declareWinner() {
        players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        System.out.println("Final Scores:");
        players.forEach(p -> System.out.println("Player " + p.getIdd() + ": " + p.getScore()));

        // Notify all players of the winner
        int winningScore = players.get(0).getScore();
        for (PlayerHandler player : players) {
            String resultMessage;
            if (player.getScore() == winningScore) {
                resultMessage = "You won!";
            } else {
                resultMessage = "You lost. The winner is Player " + players.get(0).getIdd();
            }
            try {
                player.sendTask(new Task(TaskType.GAME_OVER, resultMessage, player.getRole()));
            } catch (IOException e) {
                System.out.println("Failed to send game over message to Player " + player.getIdd());
            }
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}
