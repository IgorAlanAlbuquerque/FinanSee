package com.igor.finansee.data
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.igor.finansee.R
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.GoogleAuthProvider
import com.igor.finansee.data.models.User
import java.time.LocalDate

class AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun registerUser(email: String, password: String, name: String): Boolean{
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
            if (uid != null){
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "created_at" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(user).await()
            }
            true
        }catch (e: Exception){
            Log.e("authRepository", "erro + $e")
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean{
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        }catch (e: Exception){
            Log.e("authRepository", "erro + $e")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean{
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        }catch (e: Exception){
            Log.e("authRepository", "erro + $e")
            false
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()

                val name = snapshot.getString("name") ?: "Desconhecido"
                val email = snapshot.getString("email") ?: "Email não disponível"
                val password = snapshot.getString("password") ?: ""
                val registrationDateStr = snapshot.getString("registrationDate")
                val registrationDate = registrationDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now()
                val statusPremium = snapshot.getBoolean("statusPremium") ?: false
                val fotoPerfil = snapshot.getLong("fotoPerfil")?.toInt() ?: R.drawable.perfil

                User(
                    id = uid.toInt(),
                    email = email,
                    name = name,
                    registrationDate = registrationDate,
                    password = password,
                    fotoPerfil = fotoPerfil,
                    statusPremium = statusPremium
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("authRepository", "Erro ao obter usuário: $e")
            null
        }
    }



    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun loginWithGoogle(idToken: String): Boolean{
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            user?.let{
                val uid = it.uid
                val name = it.displayName ?: "usuario"
                val email = it.email ?: ""

                val userRef = firestore.collection("users").document(uid)
                val snapshot = userRef.get().await()

                if(!snapshot.exists()){
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "created_at" to System.currentTimeMillis()
                    )
                    userRef.set(userData).await()
                }
            }
            true
        }catch (e: Exception){
            Log.e("authRepository", "erro + ${e.message}")
            false
        }
    }

    fun logout(){
        auth.signOut()
    }

    fun isUserLogged(): Boolean{
        return auth.currentUser != null
    }
}