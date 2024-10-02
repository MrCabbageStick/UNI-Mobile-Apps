package org.example

import kotlin.io.path.Path

fun main() {
    Debug.on();

    val fileHandler = FileHandler(Path("game.txt"));
    val players = fileHandler.getPlayers().toMutableList();

    print("Username: ")
    val playerName = readln();
    print("\n")

    var player = players.find { player -> player.name == playerName }

    if(player == null){
        player = Player(playerName);
        players.add(player);
    }

    val game = Game(player);

    game.playRounds();

    fileHandler.setPlayers(players);
}