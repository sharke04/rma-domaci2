package rs.edu.raf.rma.showtime.domain

data class Person(
    val id: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null,
)