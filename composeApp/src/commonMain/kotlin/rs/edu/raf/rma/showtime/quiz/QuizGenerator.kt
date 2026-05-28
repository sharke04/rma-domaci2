package rs.edu.raf.rma.showtime.quiz

import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.showtime.db.MovieEntity
import rs.edu.raf.rma.showtime.domain.ShowtimeRepository
import kotlin.random.Random

class QuizGenerator(
    private val db: AppDatabase,
    private val repository: ShowtimeRepository,
) {

    private enum class QuizType { GUESS_MOVIE, GUESS_YEAR, GUESS_ACTOR }

    suspend fun generate(): List<QuizQuestion> {
        val count = db.showtimeDao().getMovieCount()
        if (count < 10) {
            throw IllegalStateException("Not enough movies in the database. Try opening the app a moment to let movies load.")
        }

        val moviesPool = db.showtimeDao().getRandomMovies(30)
        repository.refreshActorsForMovies(moviesPool.map { it.id })
        val types = buildTypeList()
        val questions = mutableListOf<QuizQuestion>()
        val usedIds = mutableSetOf<String>()

        for (type in types) {
            val candidates = moviesPool.filter { it.id !in usedIds }
            val result = tryBuildQuestion(type, candidates, moviesPool)
                ?: tryBuildQuestion(QuizType.GUESS_YEAR, candidates, moviesPool)
                ?: tryBuildQuestion(QuizType.GUESS_MOVIE, candidates, moviesPool)
                ?: continue
            questions.add(result.first)
            usedIds.add(result.second)
        }

        return questions
    }

    private suspend fun tryBuildQuestion(
        type: QuizType,
        candidates: List<MovieEntity>,
        pool: List<MovieEntity>,
    ): Pair<QuizQuestion, String>? {
        for (movie in candidates) {
            val q = buildQuestion(type, movie, pool) ?: continue
            return Pair(q, movie.id)
        }
        return null
    }

    private suspend fun buildQuestion(
        type: QuizType,
        movie: MovieEntity,
        pool: List<MovieEntity>,
    ): QuizQuestion? = when (type) {
        QuizType.GUESS_MOVIE -> buildGuessMovie(movie, pool)
        QuizType.GUESS_YEAR -> buildGuessYear(movie)
        QuizType.GUESS_ACTOR -> buildGuessActor(movie)
    }

    private fun buildGuessMovie(movie: MovieEntity, pool: List<MovieEntity>): QuizQuestion? {
        val imagePath = movie.backdropPath ?: movie.posterPath ?: return null
        val imageUrl = "https://image.tmdb.org/t/p/w500$imagePath"

        val wrongTitles = pool
            .filter { it.id != movie.id && it.title.isNotBlank() }
            .shuffled()
            .take(3)
            .map { it.title }

        if (wrongTitles.size < 3) return null

        val allAnswers = (wrongTitles + movie.title).shuffled()
        val correctIndex = allAnswers.indexOf(movie.title)

        return QuizQuestion.GuessTheMovie(
            imageUrl = imageUrl,
            answers = allAnswers,
            correctAnswerIndex = correctIndex,
        )
    }

    private fun buildGuessYear(movie: MovieEntity): QuizQuestion? {
        val year = movie.year ?: return null

        val offsets = mutableSetOf<Int>()
        var attempts = 0
        while (offsets.size < 3 && attempts < 50) {
            val sign = if (Random.nextBoolean()) 1 else -1
            offsets.add(Random.nextInt(1, 11) * sign)
            attempts++
        }

        if (offsets.size < 3) return null

        val wrongYears = offsets.map { (year + it).toString() }
        val allAnswers = (wrongYears + year.toString()).shuffled()
        val correctIndex = allAnswers.indexOf(year.toString())

        return QuizQuestion.GuessTheYear(
            imageUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            title = movie.title,
            answers = allAnswers,
            correctAnswerIndex = correctIndex,
        )
    }

    private suspend fun buildGuessActor(movie: MovieEntity): QuizQuestion? {
        val actors = db.showtimeDao().getActorsForMovie(movie.id)
        if (actors.isEmpty()) return null

        val correctActor = actors.take(3).random()
        val wrongActors = db.showtimeDao().getRandomActorsExcluding(listOf(movie.id), 3)
        if (wrongActors.size < 3) return null

        val allAnswers = (wrongActors.map { it.name } + correctActor.name).shuffled()
        val correctIndex = allAnswers.indexOf(correctActor.name)

        return QuizQuestion.GuessTheActor(
            imageUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            title = movie.title,
            answers = allAnswers,
            correctAnswerIndex = correctIndex,
        )
    }

    private fun buildTypeList(): List<QuizType> =
        (List(4) { QuizType.GUESS_MOVIE } +
         List(3) { QuizType.GUESS_YEAR } +
         List(3) { QuizType.GUESS_ACTOR }).shuffled()
}
