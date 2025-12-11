package be.deltaflow.puzzle02

import java.io.File
import java.util.concurrent.atomic.AtomicLong

fun main() {
    val input =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_05.txt")
            .readText()
            .trim()

//    part1(input)
    part2(input)
}

private fun part1(input: String) {
    val (ranges, ingredients) = parseInput(input)

    println()

    val result = ingredients.count { ingredient -> ranges.any { range -> ingredient in range } }

    println("part1 $result")
}

private fun part2(input: String) {
    val (ranges, ingredients) = parseInput(input)

    val sortedRanges = ranges.sortedBy { it.first }

    val nonOverlappingRanges =
        buildList {
            add(sortedRanges.first())
            sortedRanges.drop(1).forEach { range ->
                val last = last()
                if (last.last >= range.last) {
                    // Nothing
                } else {
                    add(LongRange(range.first.coerceAtLeast(last.last + 1), range.last))
                }
            }
        }

    val result = nonOverlappingRanges.fold(0L) { acc, range -> acc + (range.last - range.first) + 1 }

    println("part2: $result")
}

private fun parseInput(input: String): Pair<List<LongRange>, List<Long>> {
    val ranges =
        input
            .lines()
            .takeWhile { it.isNotBlank() }
            .map {
                val (from, to) = it.split("-")
                LongRange(from.toLong(), endInclusive = to.toLong())
            }
    val ingrediends =
        input
            .lines()
            .dropWhile { it.isNotBlank() }
            .drop(1)
            .map { it.toLong() }

    return ranges to ingrediends
}
