package rs.edu.raf.rma.showtime.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.showtime.data.toDomain
import rs.edu.raf.rma.showtime.db.QuizResultEntity
import rs.edu.raf.rma.showtime.domain.QuizResult
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface QuizResultsRepository {
    suspend fun saveResult(timeUsed: Int, points: Int)
    fun observeBestResult(userId: Int): Flow<QuizResult?>
}

class QuizResultsRepositoryImpl(
    private val db: AppDatabase,
    private val authStore: AuthStore,
) : QuizResultsRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun saveResult(timeUsed: Int, points: Int) {
        val userId = authStore.requireUserId()
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

    override fun observeBestResult(userId: Int): Flow<QuizResult?> =
        db.showtimeDao()
            .observeBestQuizResult(userId)
            .map { it?.toDomain() }
}
