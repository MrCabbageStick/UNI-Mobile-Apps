package org.example.game

import org.example.tools.Debug
import org.example.tools.mapOfArray
import java.util.*

enum class Move(val text: String, val emoji: String){
    INVALID("invalid", "🚫"),
    ROCK("rock", "\uD83E\uDEA6"),
    PAPER("paper", "🧻"),
    SCISSORS("scissors", "✂");

    companion object{
        fun fromText(text: String): Move {
            return textMoveMap.getOrDefault(text, INVALID);
        }

        private val textMoveMap = mapOfArray(entries.map { move: Move -> Pair(move.text, move) });
    }
}

val whatBeatsWhat = mapOf(
    Pair(Move.ROCK, Move.SCISSORS),
    Pair(Move.SCISSORS, Move.PAPER),
    Pair(Move.PAPER, Move.ROCK),
);


class Game(val player: Player) {

    fun getUserInput(): Move {
        var userIn = readlnOrNull() ?: "";

        userIn = userIn.trim();

        val userInNumber = userIn.toIntOrNull();
        if(userInNumber != null){
            return Move.entries.getOrNull(userInNumber) ?: Move.INVALID;
        }

        return Move.fromText(userIn.trim().lowercase(Locale.getDefault()));
    }


    fun randomMove(): Move {
        return Move.entries[(1..<Move.entries.size).random()];
    }


    fun getInputOptionsInfo(): String{
        val info = StringBuilder();
        info.append("(")

        Move.entries.forEachIndexed { index: Int, move: Move ->
            if(index != 0)
                info.append("${index}-${move.text} / ");
        };

        info.set(info.length - 2, ')');
        return info.toString().trim();
    }


    fun playRounds(roundCount: Int = 1){

        var roundsLeft = roundCount;

        Debug.printDebug("Rounds left: $roundsLeft")

        while(roundsLeft > 0){

            // Separator
            println("\n> Round: ${roundCount - roundsLeft + 1}");

            roundsLeft--;

            // Info
            println("> Your points: ${player.points}\n");

            // User move
            print("Your move ${getInputOptionsInfo()}: ");
            val userMove = getUserInput();
            print('\n');

            if(userMove == Move.INVALID){
                println("This move is invalid");
                return;
            }

            // Game move
            val gameMove = randomMove();

            // Show
            print(
                """
                You  |  Game
                ${userMove.emoji}     ${gameMove.emoji}
                
                Result: 
            """.trimIndent().trimEnd('\n')
            );

            // Game logic
            if(gameMove == userMove){
                println("PAR");
                println("-".repeat(15));
                continue;
            }

            if(whatBeatsWhat.get(userMove) == gameMove){
                println("You won: +1 point");
                player.addPoint();
                println("-".repeat(15));
                continue;
            }

            println("You lost: -1 point");
            player.removePoint()
            println("-".repeat(15));
        }

    }
}