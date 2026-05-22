package rs.edu.raf.rma.showtime.domain

data class Video(
    val key: String,
    val site: String,
    val name: String? = null,
    val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
)
