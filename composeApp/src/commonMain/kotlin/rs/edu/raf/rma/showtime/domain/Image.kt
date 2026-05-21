package rs.edu.raf.rma.showtime.domain

data class Image(
    val id: Int,
    val filePath: String,
    val width: Int? = null,
    val height: Int? = null,
    val voteAverage: Float? = null,
    val language: String? = null,
)