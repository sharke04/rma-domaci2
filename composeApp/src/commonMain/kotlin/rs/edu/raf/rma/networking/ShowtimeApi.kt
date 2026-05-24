package rs.edu.raf.rma.networking

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import rs.edu.raf.rma.networking.model.RegisterBody
import rs.edu.raf.rma.networking.model.RegisterApiModel

interface ShowtimeApi {
    @POST("auth/signup")
    suspend fun signUp(@Body body: RegisterBody): RegisterApiModel
}
