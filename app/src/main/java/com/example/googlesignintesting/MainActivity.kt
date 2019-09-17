package com.example.googlesignintesting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var alreadySignedIn = false

    private val TAG = MainActivity::class.java.simpleName

    lateinit var googleSignInClient: GoogleSignInClient

    var googleSignInAccount: GoogleSignInAccount? = null

    private val RC_SIGN_IN = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        var btnGoogleSignIn = findViewById<com.google.android.gms.common.SignInButton>(R.id.google_signIn_button)

        btnGoogleSignIn.setOnClickListener(this)

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (googleSignInAccount != null) {
            alreadySignedIn = true
        }

    }

    private fun showAccountDetails(account: GoogleSignInAccount?) {
        Log.e(TAG,"Email: "+ account?.email )
        Log.e(TAG,"Display name: "+ account?.displayName)
        Log.e(TAG,"Given name: "+ account?.givenName)
        Log.e(TAG,"Family name: "+ account?.familyName)
        Log.e(TAG,"ID: "+ account?.id)
        Log.e(TAG,"Token: "+ account?.idToken)
        Log.e(TAG,"Photo URL: "+ account?.photoUrl)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            googleSignInAccount = account
            showAccountDetails(account)
        } catch (exception: ApiException) {
            Log.e(TAG,"SignInResult: failed code = "+exception.statusCode)
        }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnSuccessListener {
            Log.e(TAG,"Successsfully signed out")
            revokeAccess()
        }.addOnFailureListener { exception ->
            Log.e(TAG,"Failed to sign out: Exception = "+exception.message)
        }
    }

    private fun revokeAccess() {
        googleSignInClient.revokeAccess().addOnSuccessListener {
            Log.e(TAG,"Revoked the app access")
        }.addOnFailureListener { exception ->
            Log.e(TAG,"failed to revoke app access: Exception = "+exception.message)
        }
    }

    override fun onClick(p0: View?) {

        when(p0?.id) {
            R.id.google_signIn_button ->
                if (alreadySignedIn) {
                    Toast.makeText(this,"Already signed in to account: "+ (googleSignInAccount?.email
                        ?:null ),Toast.LENGTH_SHORT).show()
                    Log.e(TAG,"User already Signd In")
                } else {
                    signIn()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            alreadySignedIn = true
        }
    }

    override fun onStop() {
        super.onStop()
        signOut()
    }

}
