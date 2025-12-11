package be.deltaflow.puzzle02

import java.io.File

fun main() {
    val ranges =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_02.txt")
            .readText()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.split("-").map { it.toLong() } }
            .map { LongRange(it[0], it[1]) }

    println(ranges)

//    part1(ranges)
    part2(ranges)
}

private fun part1(ranges: List<LongRange>) {
    val result =
        ranges.fold(0L) { sum, range ->
            sum +
                range
                    .filter { value ->
                        val size = value.toString().length
                        if (size % 2 == 0) {
                            val exponent = (1..<(size / 2)).fold(10) { acc, _ -> acc * 10 }
//                            println("$value - $size - $exponent")
                            val match = value / exponent == value % exponent
                            if (match) {
                                println("$value - true")
                            }
                            match
                        } else {
                            false
                        }
                    }.sum()
        }
    println("part1 - $result")
}

private fun part2(ranges: List<LongRange>) {
    val result =
        ranges.fold(0L) { sum, range ->
            sum + range.filter { value -> hasRepeatingPatterns(value) }.sum()
        }
    println("part2 - $result")
}

private fun hasRepeatingPatterns(value: Long): Boolean {
    val stringValue = value.toString()

    return (1..(stringValue.length / 2)).any { size ->
        if (stringValue.length % size != 0) return@any false
        val match = stringValue.chunked(size).toSet().size == 1
        if (match) {
            println("$value - true")
        }
        match
    }
}
