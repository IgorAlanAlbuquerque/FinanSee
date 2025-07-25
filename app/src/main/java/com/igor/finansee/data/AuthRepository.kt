package com.igor.finansee.data

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.igor.finansee.R
import com.igor.finansee.data.daos.UserDao
import com.igor.finansee.data.models.User
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class AuthRepository(private val userDao: UserDao) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun registerUser(email: String, password: String, name: String): User? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val newUser = User(
                    name = name,
                    email = email,
                    registrationDate = LocalDate.now(),
                    password = "",
                    statusPremium = false,
                    fotoPerfil = null
                )
                userDao.insert(newUser)
                newUser
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no registro: $e")
            null
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            firebaseUser?.let {
                userDao.findByEmail(it.email ?: "")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no login: $e")
            null
        }
    }

    suspend fun loginWithGoogle(idToken: String): User? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                var localUser = userDao.findByEmail(firebaseUser.email ?: "")
                if (localUser == null) {
                    val newUser = User(
                        name = firebaseUser.displayName ?: "Usuário",
                        email = firebaseUser.email ?: "",
                        registrationDate = LocalDate.now(),
                        password = "",
                        statusPremium = false,
                        fotoPerfil = null
                    )
                    userDao.insert(newUser)
                    localUser = newUser
                }
                localUser
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no login com Google: ${e.message}")
            null
        }
    }

    /**
     * Busca o usuário logado diretamente do banco de dados local.
     */
    suspend fun getCurrentLocalUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return userDao.findByEmail(firebaseUser.email ?: "")
    }

    /**
     * Retorna o UID (String) do usuário logado no Firebase.
     */
    fun getFirebaseUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao resetar senha: $e")
            false
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }
}
