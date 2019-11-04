package com.project.pradyotprakash.polking.utility

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.pradyotprakash.polking.message.ShowMessage

@SuppressLint("Registered")
open class InternetActivity : AppCompatActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var messageBtmSheet: ShowMessage
    var connectivityReceiver: ConnectivityReceiver = ConnectivityReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }


    private fun showMessage(isConnected: Boolean) {
        messageBtmSheet = ShowMessage.newInstance()
        if (!isConnected) {
            if (!messageBtmSheet.isAdded) {
                messageBtmSheet.show(supportFragmentManager, "btmSheet")
                messageBtmSheet.setMessage("You are not connected to the world.", 4)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(connectivityReceiver)
        } catch (e: Exception) {
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}
