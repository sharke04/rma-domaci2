package rs.edu.raf.rma.showtime.domain

import kotlinx.datetime.LocalDateTime

data class QuizResult(
    val id: Int,
    val timeUsed: Int,
    val points: Int,
    val finishedAt: LocalDateTime,
)
