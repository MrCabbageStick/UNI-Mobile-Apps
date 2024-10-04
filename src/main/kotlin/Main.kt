package org.example

import org.example.game.FileHandler
import org.example.game.Game
import org.example.game.Player
import org.example.tools.Debug
import kotlin.io.path.Path

fun showScores(players: List<Player>){
    var longestName = players.maxOf { player -> player.name.length };
    var longestScore = players.maxOf { player -> player.points.toString().length};

    val playerNameLabel = "Player Name";
    val scoreLabel = "Points";

    longestName = if(playerNameLabel.length > longestName) playerNameLabel.length else longestName;
    longestScore = if(scoreLabel.length > longestScore) scoreLabel.length else longestScore;

    val printSeparator = { println("-" + "-".repeat(longestName) + "-+-" + "-".repeat(longestScore + 1)); }
    val printNameScore = { name: String, score: String ->
        println(" " + name + " ".repeat(longestName - name.length) + " | " + score);
    }


    printSeparator();
    printNameScore(playerNameLabel, scoreLabel);
    printSeparator();

    for (player in players.sortedByDescending { player -> player.points }){
        printNameScore(player.name, player.points.toString());
    }

    printSeparator();
}

fun main() {
//    Debug.on();

    val fileHandler = FileHandler(Path("game.txt"));
    val players = fileHandler.getPlayers().toMutableList();

    print("Username: ")
    val playerName = readln();

    var player = players.find { player -> player.name == playerName }

    if(player == null){
        player = Player(playerName);
        players.add(player);
    }

    val game = Game(player);

    val roundCount: Int;

    input_loop@ while(true){
        print("Number of rounds: ");
        val _roundCount = readln().toIntOrNull();

        if (_roundCount == null){
            println("Given value is not a number");
            continue@input_loop // Oh, god one can add labels to loops
        }

        roundCount = _roundCount;
        break;
    }

    showScores(players);

    game.playRounds(roundCount);

    fileHandler.setPlayers(players);

    println("Game ends with scores:")
    showScores(players);
}