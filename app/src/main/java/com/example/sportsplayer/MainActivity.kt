package com.example.sportsplayer

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import view.Dashboard
import view.PlayerSignUp
import kotlin.system.exitProcess


class MainActivity :AppCompatActivity()
{
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        // [END initialize_auth]
    }


    override fun onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }


    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()

        //check if Internet is available
        // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            if (currentUser != null) {
                startActivity<Dashboard>()
                toast("welcome to dashboard")
                finish()
            } else {

                startActivity<PlayerSignUp>()
            }
        }

    // [END on_start_check_user]


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected

    }

}
