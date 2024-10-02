package org.example

import java.nio.ByteBuffer

class Player(val name: String, var points: Int = 0) {

    fun addPoint() {
        points++;
    }

    fun removePoint() {
        points--;
    }

    fun serialize(): String {
        return "$name $points";
    }

    companion object {
        fun deserialize(string: String): Player?{
            val (name, points) = string.split(" ");
            return Player(name, points.toIntOrNull() ?: return null);
        }
    }
}