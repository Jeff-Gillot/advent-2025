package be.deltaflow.puzzle10

import java.io.File

fun main() {
    val input =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_10.txt")
            .readText()
            .trim()

//    part1(input)
    part2(input)
}

private fun part2(input: String) {
    val machines = parseInput(input).sortedBy { it.buttons.size }
    println(machines.maxOf { it.expectedJoltage.size })
    println(machines.maxOf { it.lights.size })
    println(Long.MAX_VALUE)

//    machines.forEach {
//        println(it)
//    }
//    println(machines.maxOf { it.buttons.size })
//    var bestScore: List<Int>? = null
//    val knownPath = ConcurrentHashMap<List<Int>, Int>()
//
//    fun findBestScore(
//        machine: Machine,
//        presses: List<Int>,
//    ) {
// //        print("\r$presses $bestScore $machine")
//        if (bestScore != null && presses.size >= bestScore!!.size) return
//        if (machine.joltage == machine.expectedJoltage) {
//            bestScore = presses
//        }
//
//        val knownPathSize = knownPath[presses] ?: Int.MAX_VALUE
//        if (knownPathSize <= presses.size) {
//            println("memoize")
//            return
//        }
//
//        if (machine.joltage.zip(machine.expectedJoltage).any { (joltage, expectedJoltage) -> joltage > expectedJoltage }) {
//            return
//        }
//
//        val buttonsToTest = (presses.lastOrNull() ?: 0)..machine.buttons.lastIndex
//
//        buttonsToTest.forEach { buttonIndex ->
//            findBestScore(machine.flip(buttonIndex), presses + buttonIndex)
//        }
//        knownPath[presses] = presses.size
//    }

    val totalScore =
        machines
            .mapIndexed { index, machine ->
//            bestScore = null
//            knownPath.clear()
//            findBestScore(machine, emptyList())
//            println("$bestScore -> $machine")
//            bestScore!!.size
                println("====== $index/${machines.size}")
                findBestSolution(machine).length
            }.sum()

    println("part2: $totalScore")
//    machines.forEach { println(it) }
}

private fun findBestSolution(machine: Machine): String {
    // 0101, 0011, 1010, 1100, 0001, 0010
    //    A     B     C     D     E     F
    // 3, 5, 4, 7

    // 3 -> xc+(x-3)d
    // 5 -> ya+(y-5)d
    // 4 ->

    // 3 -> 3x(2, 3) -> joltageLeft + list<buttons>
    // 5 -> 5x(0, 3)
    // 4 -> 4x(1, 2, 5)
    // 7 -> 7x(0, 1, 4)

    // 0 -> (0..5) w 2 // Button + range
    // 1 -> (0..4) w 2
    // 2 -> (0..3) w 2
    // 3 -> (0..3) w 2
    // 4 -> (0..7) w 1
    // 5 -> (0..4) w 1

    // Boucle
    // button(0) -> 5..0 -> recurse 1 -> 0..4

    println(machine)
    val remainingJoltages =
        machine.expectedJoltage
            .mapIndexed { index, joltage ->
                val buttons = machine.buttons.indices.filter { it -> machine.buttons[it].get(machine.joltage.size - (index + 1)) }
                joltage to buttons
            }.map { RemainigJoltages(it.first, it.second) }

    remainingJoltages.forEach { println(it) }

    val buttonsPress =
        machine.buttons
            .mapIndexed { index, _ ->
                index to (
                    remainingJoltages
                        .filter { index in it.buttons }
                        .minByOrNull { it.joltage }
                        ?.joltage ?: 0
                )
            }.sortedByDescending { (buttonIndex, _) -> remainingJoltages.count { buttonIndex in it.buttons } }
            .sortedByDescending { (buttonIndex, it) -> it }

    buttonsPress.forEach { (index, maxPush) -> println("$index -> $maxPush") }

    fun findSolution(
        joltages: List<RemainigJoltages>,
        remainingButtonPresses: List<Pair<Int, Int>>,
        solution: String,
    ): String? {
//        print("\r$solution")
        if (joltages.any { it.joltage < 0 }) return null
        if (joltages.sumOf { it.joltage } == 0) return solution
        if (remainingButtonPresses.isEmpty()) return null

        val (buttonIndex, remainingButtonPress) = remainingButtonPresses.first()

        (remainingButtonPress downTo 0).forEach { buttonPresses ->
            val newJoltage =
                joltages.map {
                    if (buttonIndex in it.buttons) {
                        it.copy(joltage = it.joltage - buttonPresses)
                    } else {
                        it
                    }
                }

            val solution =
                findSolution(
                    joltages = newJoltage,
                    remainingButtonPresses = remainingButtonPresses.drop(1),
                    solution = solution + "$buttonIndex".repeat(buttonPresses),
                )
            if (solution != null) return solution
        }

        return null
    }

    val result = findSolution(remainingJoltages, buttonsPress, "")
    println("$result -> ${result!!.length}")

//    TODO()
    return result!!
}

data class RemainigJoltages(
    val joltage: Int,
    val buttons: List<Int>,
)

private fun part1(input: String) {
    val machines = parseInput(input)

    fun findBestScore(
        machine: Machine,
        presses: List<Int>,
        bestScore: List<Int>?,
    ): List<Int>? {
        println("$presses $bestScore $machine")
        if (bestScore != null && presses.size >= bestScore.size) return bestScore
        if (machine.lights == machine.expectedLights) return presses

        val buttonsToTest = (presses.lastOrNull()?.let { it + 1 } ?: 0)..machine.buttons.lastIndex

        return buttonsToTest
            .mapNotNull { buttonIndex ->
                findBestScore(machine.flip(buttonIndex), presses + buttonIndex, bestScore)
            }.minByOrNull { it.size }
    }

    val totalScore =
        machines.sumOf { machine ->
            val score = findBestScore(machine, emptyList(), null)
            println("$score -> $machine")
            score!!.size
        }

    println("part1: $totalScore")
//    machines.forEach { println(it) }
}

private fun parseInput(input: String): List<Machine> =
    input
        .lines()
        .map { line ->
            val lightsString =
                line
                    .takeWhile { it != ' ' }
                    .drop(1)
                    .dropLast(1)
                    .reversed()
            val size = lightsString.length
            val expectedLights =
                lightsString.foldIndexed(BitInt(0, size)) { index, acc, char ->
                    if (char == '#') acc.set(index) else acc
                }
            val buttons =
                line
                    .split(" ")
                    .drop(1)
                    .dropLast(1)
                    .map {
                        it
                            .replace("(", "")
                            .replace(")", "")
                            .split(",")
                            .fold(BitInt(0, size)) { acc, index ->
                                acc.set(size - (index.toInt() + 1))
                            }
                    }
            val expectedJoltage =
                line
                    .dropWhile { it != '{' }
                    .drop(1)
                    .takeWhile { it != '}' }
                    .split(",")
                    .map { it.toInt() }

            Machine(
                expectedLights = expectedLights,
                lights = BitInt(0, size),
                expectedJoltage = expectedJoltage,
                joltage = List(size) { 0 },
                buttons = buttons.sortedByDescending { it.count() },
            )
        }

private data class Machine(
    val expectedLights: BitInt,
    val lights: BitInt,
    val expectedJoltage: List<Int>,
    val joltage: List<Int>,
    val buttons: List<BitInt>,
) {
    fun flip(button: Int): Machine {
        val button = buttons[button]
        val newJoltage = joltage.toIntArray()
        newJoltage.forEachIndexed { index, joltage ->
            if (button.get(expectedLights.size - (index + 1))) {
                newJoltage[index] = newJoltage[index] + 1
            }
        }
        return Machine(
            expectedLights = expectedLights,
            buttons = buttons,
            expectedJoltage = expectedJoltage,
            joltage = newJoltage.toList(),
            lights = lights.xor(button),
        )
    }
}

private data class BitInt(
    private val bits: Int,
    val size: Int,
) {
    fun get(index: Int): Boolean = (bits and (1 shl index)) != 0

    fun set(index: Int): BitInt = BitInt(bits or (1 shl index), size)

    fun clear(index: Int): BitInt = BitInt(bits and (1 shl index).inv(), size)

    fun xor(other: BitInt): BitInt = BitInt(this.bits xor other.bits, size)

    fun and(other: BitInt): BitInt = BitInt(this.bits and other.bits, size)

    override fun toString(): String = bits.toString(2).padStart(size, '0')

    fun count(): Int = (0..<size).count { get(it) }
}
