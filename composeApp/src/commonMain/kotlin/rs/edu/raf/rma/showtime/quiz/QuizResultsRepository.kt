package rs.edu.raf.rma.showtime.quiz

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.networking.ShowtimeApi
import rs.edu.raf.rma.showtime.db.QuizResultEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface QuizResultsRepository {
    suspend fun saveResult(timeUsed: Int, points: Int)
}

class QuizResultsRepositoryImpl(
    private val db: AppDatabase,
    private val showtimeApi: ShowtimeApi,
) : QuizResultsRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun saveResult(timeUsed: Int, points: Int) {
        val userId = showtimeApi.getProfile().id
        val finishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        db.showtimeDao().insertQuizResult(
            QuizResultEntity(
                userId = userId,
                timeUsed = timeUsed,
                points = points,
                finishedAt = finishedAt,
            )
        )
    }
}
