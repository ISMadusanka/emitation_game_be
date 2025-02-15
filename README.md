# Multiplayer Role-Based Game

## 📌 Overview
This is a fun multiplayer game where players take turns asking and answering questions to determine the Queen. The game supports **3 or more players** who must be connected to the **same WiFi network**.

### 🎮 How to Play
1. Connect **at least 3 devices** to the **same WiFi network**.
2. One player runs the **Game Server**.
3. Other players run the **Game Client** and join the server.
4. Each round, one player is the **Judge** and asks a question.
5. The other players, called **Knights** and **ONE** player is randomly assigned as **Queen**, must answer.
6. The **Judge** selects the Queen based on the answers.
7. Wrong answers are penalized.
8. Roles shift in each round.

## 🚀 Getting Started

### 1️⃣ Clone the Repository
```sh
git clone https://github.com/ISMadusanka/emitation_game_be.git
cd emitation_game_be
```

### 2️⃣ Build the Project Using Maven
- Ensure you have **Maven** installed.
- Navigate to the project directory and run:
  ```sh
  mvn clean install
  ```

### 3️⃣ Set Up the Game
- **Ensure all devices are on the same WiFi network.**
- **Run the Game Server on ONE device:**
  
  - The **IP address** will be displayed in the console.
- **Run the Game Client on all other devices:**
  
  - Enter the **IP address** shown on the Game Server.

### 4️⃣ Enjoy the Game!
- The default game has **2 rounds**.
- You can modify the number of rounds by changing `MAX_ROUNDS` in `GameServer.java`:
  ```java
  private static final int MAX_ROUNDS = 6; // Change this value as needed
  ```

## 🏆 Have Fun!
Enjoy playing 👑
