package rs.edu.raf.rma.networking

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import rs.edu.raf.rma.networking.model.AccountApiModel
import rs.edu.raf.rma.networking.model.LoginBody
import rs.edu.raf.rma.networking.model.RegisterBody

interface AccountsApi {
    @POST("auth/signup")
    suspend fun signUp(@Body body: RegisterBody): AccountApiModel

    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): AccountApiModel
}
