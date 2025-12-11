package be.deltaflow.puzzle08

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val input =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_08.txt")
            .readText()
            .trim()

    part1(input)
//    part2(input)
}

private fun part1(input: String) {
    val junctionBoxes = parseInput(input)

    val allPairs =
        junctionBoxes.flatMap { left ->
            junctionBoxes.dropWhile { it != left }.drop(1).map { right ->
                left to right
            }
        }
    val allSortedPairs = allPairs.sortedBy { it.second.distanceTo(it.first) }.toMutableList()

    val pairs = mutableSetOf<Pair<JunctionBox, JunctionBox>>()
    val circuits = mutableListOf<Set<JunctionBox>>()

    while (circuits.size != 1 || circuits.first().size != junctionBoxes.size) {
//    repeat(10) {
        val (first, second) = allSortedPairs.removeFirst()

        pairs.add(first to second)

        val existingCircuits = circuits.filter { first in it || second in it }
        circuits.removeAll(existingCircuits)
        circuits.add(
            buildSet {
                add(first)
                add(second)
                existingCircuits.forEach { addAll(it) }
            },
        )
        print("\r${circuits.size}: ${circuits.maxOf { it.size }}            ")
    }
    println()

    circuits.sortedByDescending { it.size }.forEach { println(it) }
    val result = circuits.sortedByDescending { it.size }.take(3).fold(1L) { acc, set -> acc * set.size }
    println("part1: $result")
    println(pairs.last())
    println("part2: ${pairs.last().first.x * pairs.last().second.x}")
}

private fun parseInput(input: String): List<JunctionBox> =
    input
        .lines()
        .filter { it.isNotBlank() }
        .mapIndexed { index, line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            JunctionBox(index, x, y, z)
        }

private data class JunctionBox(
    val id: Int,
    val x: Int,
    val y: Int,
    val z: Int,
) {
    fun distanceTo(other: JunctionBox): Double =
        sqrt((x.toDouble() - other.x).pow(2) + (y.toDouble() - other.y).pow(2) + (z.toDouble() - other.z).pow(2))
}
