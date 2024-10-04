package org.example.game

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
            val split = string.split(" ");

            val name = split.subList(0, split.size - 1).joinToString(" ");
            val points = split.last();

            return Player(name, points.toIntOrNull() ?: return null);
        }
    }
}