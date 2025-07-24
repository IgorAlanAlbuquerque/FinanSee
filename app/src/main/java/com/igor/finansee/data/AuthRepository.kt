package com.igor.finansee.data
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.igor.finansee.R
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.GoogleAuthProvider

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

    suspend fun getUserName(): String?{
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null){
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("name")
            }else{
                null
            }
        }catch (e: Exception){
            Log.e("authRepository", "erro + $e")
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