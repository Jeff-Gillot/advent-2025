package be.deltaflow.puzzle02

import java.io.File

fun main() {
    val ranges =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_03.txt")
            .readText()
            .lines()
//    part1(ranges)
    part2(ranges)
}

private fun part1(batteryBanks: List<String>) {
    val result =
        batteryBanks
            .map { batteryBank ->
                val firstDigit = batteryBank.dropLast(1).max().toString()
                val secondDigit =
                    batteryBank
                        .dropWhile { it.toString() != firstDigit }
                        .drop(1)
                        .max()
                        .toString()
                firstDigit + secondDigit
            }.sumOf { it.toLong() }

    println("part1 - $result")
}

private fun part2(batteryBanks: List<String>) {
    val result =
        batteryBanks.sumOf { batteryBank ->
            var remainingBattery = batteryBank
            val cells = mutableListOf<Char>()

            repeat(12) { index ->
                val maxChar = remainingBattery.dropLast(11 - index).max()
                remainingBattery = remainingBattery.dropWhile { it != maxChar }.drop(1)
                cells.add(maxChar)
            }

            println("$batteryBank -> ${cells.joinToString("")}")
            cells.joinToString("").toLong()
        }

    println("part2 - $result")
}
