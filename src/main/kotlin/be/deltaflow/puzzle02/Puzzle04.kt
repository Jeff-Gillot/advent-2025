package be.deltaflow.puzzle02

import java.io.File
import java.util.concurrent.atomic.AtomicLong

fun main() {
    val input =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_04_sample.txt")
            .readText()
            .trim()

//    part1(input)
    part2(input)
}

private fun part1(input: String) {
    val grid = Grid.fromString(input)
    val count = AtomicLong(0)
    println(grid)
    grid.cells.values.forEach { cell ->
        if (cell.isPaper && grid.adjacentCells(cell).count { it.isPaper } < 4) {
            grid.adjacentCells(cell).forEach { adjacent -> println("$cell -> $adjacent") }
            println()
            count.incrementAndGet()
        }
    }
    println(count)
}

private fun part2(input: String) {
    var grid = Grid.fromString(input)
    var count = 0
    println(grid)
    println()

    do {
        val removedPapers =
            grid.cells.values.filter { cell ->
                cell.isPaper && grid.adjacentCells(cell).count { it.isPaper } < 4
            }
        count += removedPapers.size
        grid = grid.removePaper(removedPapers)
        println(grid)
        println()
    } while (removedPapers.isNotEmpty())

    println(count)
}

private class Grid(
    val cells: Map<Position, Cell>,
) {
    fun adjacentCells(cell: Cell): List<Cell> = cell.position.adjacent.mapNotNull { cells[it] }

    fun removePaper(removedPapers: List<Cell>): Grid {
        val removedPositions = removedPapers.map { it.position }.toSet()
        val updatedCells =
            cells.mapValues { (_, cell) ->
                when (cell.position) {
                    in removedPositions -> cell.copy(isPaper = false)
                    else -> cell
                }
            }
        return Grid(updatedCells)
    }

    data class Cell(
        val position: Position,
        val isPaper: Boolean,
    )

    @JvmInline
    value class Row(
        val value: Int,
    ) {
        operator fun plus(other: Int) = Row(value + other)

        operator fun minus(other: Int) = Row(value - other)
    }

    @JvmInline
    value class Col(
        val value: Int,
    ) {
        operator fun plus(other: Int) = Col(value + other)

        operator fun minus(other: Int) = Col(value - other)
    }

    data class Position(
        val row: Row,
        val col: Col,
    ) {
        val adjacent: List<Position> get() {
            return listOf(
                Position(row - 1, col - 1),
                Position(row - 1, col),
                Position(row - 1, col + 1),
                Position(row, col - 1),
                Position(row, col + 1),
                Position(row + 1, col - 1),
                Position(row + 1, col),
                Position(row + 1, col + 1),
            )
        }
    }

    override fun toString(): String {
        val rowIndices = 0..<cells.keys.maxOf { it.row.value }
        val colIndices = 0..<cells.keys.maxOf { it.col.value }

        return buildString {
            rowIndices.forEach { row ->
                colIndices.forEach { col ->
                    val cell = cells[Position(Row(row), Col(col))]!!
                    if (cell.isPaper) append("@") else append(".")
                }
                appendLine()
            }
        }
    }

    companion object {
        fun fromString(input: String): Grid {
            val cells =
                input
                    .lines()
                    .mapIndexed { row, line ->
                        line.trim().mapIndexed { col, char ->
                            Cell(Position(Row(row), Col(col)), char == '@')
                        }
                    }.flatten()
            return Grid(cells.associateBy { it.position })
        }
    }
}
