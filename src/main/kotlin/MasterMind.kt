import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow

const val BOXES = 4
const val COLORS = 6

val allOptions = List(COLORS.toDouble().pow(BOXES.toDouble()).toInt()) { it }

val EVALUATION_OFFSET = ceil(log(BOXES.toDouble(), 2.toDouble())).toInt() + 1
val ALL_CORRECT = BOXES shl EVALUATION_OFFSET or BOXES

fun main() {
    allOptions.groupingBy(::solve)
        .eachCount()
        .entries
        .forEach { println("Solved " + it.value + " games in " + it.key + " turns") }
}

fun solve(solution: Int): Int {
    println("Solving for solution " + optionToString(solution))
    val turns = solve(solution, 1, allOptions)
    println("  Solved in $turns turns")
    return turns
}

fun solve(solution: Int, turn: Int, remainingOptions: List<Int>): Int {
    val guess = getGuess(remainingOptions)
//    println("  Guessing ${optionToString(guess)}")
    val evaluation = getEvaluation(guess, solution)
//    println("  Evaluation was ${evaluation shr EVALUATION_OFFSET} total and ${evaluation and 7 } correct location")
    if (evaluation == ALL_CORRECT) return turn
    return solve(solution, turn + 1, remainingOptions.filter { getEvaluation(it, guess) == evaluation })
}

fun getGuess(remainingOptions: List<Int>) =
    // Try select from remaining options and from all options
    remainingOptions.map { Pair(it, getHeuristicRemainingOptionsCount(it, remainingOptions)) }
        .reduce { best, current -> if (current.second < best.second) current else best }
        .first

fun getHeuristicRemainingOptionsCount(guess: Int, remainingOptions: List<Int>) =
    remainingOptions
        .groupingBy { getEvaluation(it, guess) }
        .eachCount()
        .values
        .reduce { best, current -> if (current > best) current else best }

fun getEvaluation(solution: Int, guess: Int): Int {
    val totalCorrectColor = (0 until COLORS).sumOf { minOf(getColorCount(solution, it), getColorCount(guess, it)) }
    val correctLocation = (0 until BOXES).count { getColor(solution, it) == getColor(guess, it) }
    return totalCorrectColor shl EVALUATION_OFFSET or correctLocation
}

fun getColorCount(option: Int, color: Int): Int {
    var counter = 0
    var scooter = option
    for (i in 0 until BOXES) {
        if (scooter.mod(COLORS) == color)
            ++counter
        scooter /= COLORS
    }
    return counter
}

fun getColor(option: Int, index: Int): Int {
    var scooter = option
    for (i in 0 until index) {
        scooter /= COLORS
    }
    return scooter.mod(COLORS)
}

fun optionToString(option: Int): String {
    return (0 until BOXES).map { getColor(option, it) }
        .joinToString(" ")
}