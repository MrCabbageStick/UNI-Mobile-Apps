package org.example

import java.time.LocalDateTime


fun <K, V> mapOfArray(pairs: List<Pair<K, V>>): Map<K, V>{
    val map = mapOf<K, V>();

    pairs.forEach { pair: Pair<K, V> ->
        map.plus(pair);
    };

    return map;
}

class Debug{
    companion object {
        private var turnedOn = false;

        fun printDebug(message: String){
            if(turnedOn)
                println("[${LocalDateTime.now()}][DEBUG]: $message");
        }

        fun on(){
            turnedOn = true;
        }
    }
}