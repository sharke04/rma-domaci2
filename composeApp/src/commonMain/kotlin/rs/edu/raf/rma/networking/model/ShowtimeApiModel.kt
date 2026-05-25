package rs.edu.raf.rma.networking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterApiModel(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: UserApiModel,
)

@Serializable
data class UserApiModel(
    val id: Int,
    val username: String,
    @SerialName("full_name") val fullName: String,
)

@Serializable
data class ErrorResponse(
    val error: String,
    val httpCode: Int,
    val message: String,
)
