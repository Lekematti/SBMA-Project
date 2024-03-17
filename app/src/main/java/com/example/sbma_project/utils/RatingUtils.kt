package com.example.sbma_project.utils

sealed class Rating(val value: Int) {
    data object VeryBad : Rating(1)
    data object Bad : Rating(2)
    data object Neutral : Rating(3)
    data object Good : Rating(4)
    data object VeryGood : Rating(5)
}

fun emojiToRating(emoji: String): Rating {
    return when (emoji) {
        "😞" -> Rating.VeryBad
        "😐" -> Rating.Bad
        "😊" -> Rating.Neutral
        "😃" -> Rating.Good
        "😄" -> Rating.VeryGood
        else -> throw IllegalArgumentException("Invalid emoji")
    }

}
fun ratingToEmoji(rating: Int): String {
    return when (rating) {
        1 -> "😞"
        2 -> "😐"
        3 -> "😊"
        4 -> "😃"
        5 -> "😄"
        else -> throw IllegalArgumentException("Invalid rating value")
    }
}
