package org.example

import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.exists

class FileHandler(val filePath: Path) {

    fun getPlayers(): List<Player>{
        try{
            if(!filePath.exists())
                throw IOException("File does not exist")

            val players = mutableListOf<Player>();

            File(filePath.toString()).readLines().forEach{ line ->
                val player = Player.deserialize(line);
                if(player != null)
                    players.add(player);
                else
                    Debug.printDebug("Unable to deserialize Player from string: $line");
            };

            return players;
        }
        catch (e: IOException){
            Debug.printDebug("Unable to read game file, defaulting to empty Player list. Reason: ${e.message}");
            return listOf();
        }
    }

    fun setPlayers(players: List<Player>){
        try{
            File(filePath.toString()).writeText(players.map(Player::serialize).joinToString("\n"));
        }
        catch (e: IOException){
            Debug.printDebug("Unable to write to the game file, progress not saved. Reason: ${e.message}");
        }
    }
}
