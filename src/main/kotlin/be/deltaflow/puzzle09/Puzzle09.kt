package be.deltaflow.puzzle09

import java.io.File

fun main() {
    val input =
        File("/home/jgillot/IdeaProjects/advent-2025/src/main/resources/input_09.txt")
            .readText()
            .trim()

//    part1(input)
    part2(input)
}

private fun part1(input: String) {
    val points = parse(input)

    val allPairs =
        points.flatMap { left ->
            points.dropWhile { it != left }.drop(1).map { right -> left to right }
        }

    println(allPairs.size)

    val result = allPairs.maxOf { it.area() }

    println(result)
}

private fun part2(input: String) {
    val points = parse(input)

    val grid = Grid(points)

    println(grid)

//    println(points.distinctBy { it.x }.count())
//    println(points.maxOf { it.x })
//    println(points.minOf { it.y })
//    println(points.maxOf { it.y })
//
    val allPairs =
        points.flatMap { left ->
            points.dropWhile { it != left }.drop(1).map { right -> left to right }
        }

//    val grid = Array(points.maxOf { it.x + 1 }) { Array(points.maxOf { it.y + 1 }) { Tile.Unknown } }
//
//    points.forEach { point -> grid[point.x][point.y] = Tile.Red }
//
    allPairs
        .asSequence()
        .filter { it.isSameX || it.isSameY }
        .forEach { pair ->
            grid.mapTiles(pair.first, pair.second) { Tile.Green }
        }

    println("==================")
    println(grid)

    grid.fill(0, 0, Tile.White)

    println("==================")
    println(grid)

    grid.mapAll { if (it == Tile.Unknown) Tile.Green else it }

    println("==================")
    println(grid)

    val bestRectangle =
        allPairs
            .sortedByDescending { it.area() }
            .first { pair ->
                grid.listTiles(pair.first, pair.second).all { it != Tile.White }
            }

    println(bestRectangle.area())
}

private fun Pair<Point, Point>.area(): Long {
    val minX = minOf(first.x, second.x).toLong()
    val maxX = (maxOf(first.x, second.x) + 1).toLong()
    val minY = (minOf(first.y, second.y)).toLong()
    val maxY = (maxOf(first.y, second.y) + 1).toLong()
    return (maxX - minX) * (maxY - minY)
}

private fun Array<Array<Tile>>.fill(
    x: Int,
    y: Int,
    tile: Tile,
    queue: ArrayDeque<Pair<Int, Int>> = ArrayDeque(),
) {
    queue.add(x to y)
    while (queue.isNotEmpty()) {
        val (x, y) = queue.removeFirst()
        val grid = this
        val indicesX = grid.indices
        val indicesY = grid[0].indices

        if (x !in indicesX || y !in indicesY) continue
        if (grid[x][y] != Tile.Unknown) continue

        grid[x][y] = tile

        queue.addLast(x - 1 to y)
        queue.addLast(x + 1 to y)
        queue.addLast(x to y - 1)
        queue.addLast(x to y + 1)
    }
}

private val Pair<Point, Point>.rangeX: IntRange get() = minOf(first.x, second.x)..maxOf(first.x, second.x)

private val Pair<Point, Point>.rangeY: IntRange get() = minOf(first.y, second.y)..maxOf(first.y, second.y)

private val Pair<Point, Point>.isSameX: Boolean get() = first.x == second.x

private val Pair<Point, Point>.isSameY: Boolean get() = first.y == second.y

private val Array<Array<Tile>>.prettyString: String get() {
    val array = this

    return buildString {
        for (y in array[0].indices) {
            for (x in array.indices) {
                append(
                    when (array[x][y]) {
                        Tile.Unknown -> " "
                        Tile.Red -> "#"
                        Tile.Green -> "X"
                        Tile.White -> "."
                    },
                )
            }
            appendLine()
        }
    }
}

private fun parse(input: String): List<Point> =
    input.lines().map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Point(x, y)
    }

private data class Point(
    val x: Int,
    val y: Int,
)

private enum class Tile {
    Unknown,
    Red,
    Green,
    White,
}

private class Grid(
    points: List<Point>,
) {
    private val data: Array<Array<Tile>>
    private val pointMapping: Map<Point, Pair<Int, Int>>

    init {
        val xMapping = points.map { it.x }.distinct().sorted()
        val yMapping = points.map { it.y }.distinct().sorted()

        pointMapping = points.associateWith { point -> xMapping.indexOf(point.x) + 1 to yMapping.indexOf(point.y) + 1 }

        data = Array(xMapping.size + 2) { Array(yMapping.size + 2) { Tile.Unknown } }

        points.forEach { set(it, Tile.Red) }
    }

    operator fun get(point: Point): Tile {
        val (x, y) = pointMapping[point]!!
        return data[x][y]
    }

    operator fun set(
        point: Point,
        tile: Tile,
    ) {
        val (x, y) = pointMapping[point]!!
        data[x][y] = tile
    }

    fun mapTiles(
        from: Point,
        to: Point,
        block: (Tile) -> Tile,
    ) {
        val minX = minOf(pointMapping[from]!!.first, pointMapping[to]!!.first)
        val maxX = maxOf(pointMapping[from]!!.first, pointMapping[to]!!.first)
        val minY = minOf(pointMapping[from]!!.second, pointMapping[to]!!.second)
        val maxY = maxOf(pointMapping[from]!!.second, pointMapping[to]!!.second)
        (minX..maxX).forEach { x -> (minY..maxY).forEach { y -> data[x][y] = block(data[x][y]) } }
    }

    fun listTiles(
        from: Point,
        to: Point,
    ): List<Tile> {
        val minX = minOf(pointMapping[from]!!.first, pointMapping[to]!!.first)
        val maxX = maxOf(pointMapping[from]!!.first, pointMapping[to]!!.first)
        val minY = minOf(pointMapping[from]!!.second, pointMapping[to]!!.second)
        val maxY = maxOf(pointMapping[from]!!.second, pointMapping[to]!!.second)
        return (minX..maxX).flatMap { x -> (minY..maxY).map { y -> data[x][y] } }
    }

    fun mapAll(block: (Tile) -> Tile) {
        data.indices.forEach { x ->
            data[0].indices.forEach { y -> data[x][y] = block(data[x][y]) }
        }
    }

    fun fill(
        x: Int,
        y: Int,
        tile: Tile,
    ) {
        data.fill(x, y, tile)
    }

    override fun toString(): String =
        buildString {
            for (y in data[0].indices) {
                for (x in data.indices) {
                    append(
                        when (data[x][y]) {
                            Tile.Unknown -> " "
                            Tile.Red -> "#"
                            Tile.Green -> "X"
                            Tile.White -> "."
                        },
                    )
                }
                appendLine()
            }
        }
}
