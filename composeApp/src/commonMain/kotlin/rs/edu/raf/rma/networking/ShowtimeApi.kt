package rs.edu.raf.rma.networking

import de.jensklingenberg.ktorfit.http.GET
import rs.edu.raf.rma.networking.model.UserApiModel

interface ShowtimeApi {
    @GET("me")
    suspend fun getProfile(): UserApiModel
}