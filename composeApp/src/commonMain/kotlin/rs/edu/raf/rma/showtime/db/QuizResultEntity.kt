package rs.edu.raf.rma.showtime.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "quiz_results",
    indices = [Index("userId")],
)
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val timeUsed: Int,
    val points: Int,
    val finishedAt: LocalDateTime,
)
