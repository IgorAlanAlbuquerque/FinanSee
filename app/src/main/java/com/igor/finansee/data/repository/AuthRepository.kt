package com.igor.finansee.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.igor.finansee.R
import com.igor.finansee.data.daos.UserDao
import com.igor.finansee.data.models.User
import kotlinx.coroutines.tasks.await
import java.util.Date

class AuthRepository(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun registerUser(email: String, password: String, name: String): User? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!

            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                statusPremium = false,
                registrationTimestamp = Date()
            )

            firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            userDao.insert(newUser)
            newUser
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no registro: $e")
            null
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            getCurrentUser()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no login: $e")
            null
        }
    }

    suspend fun loginWithGoogle(idToken: String): User? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!

            val userDocRef = firestore.collection("users").document(firebaseUser.uid)
            val document = userDocRef.get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                user
            } else {
                val newUser = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "Usuário",
                    email = firebaseUser.email ?: "",
                    statusPremium = false,
                    registrationTimestamp = Date()
                )
                userDocRef.set(newUser).await()
                newUser
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro no login com Google: ${e.message}")
            null
        }
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val document = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = document.toObject(User::class.java)
            user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao buscar usuário do Firestore", e)
            null
        }
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
}