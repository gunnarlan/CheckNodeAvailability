package com.example.checknodeavailability

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            ExecuteSSHCommand().execute()
        }

    }

    private class ExecuteSSHCommand() : AsyncTask<Void, Void, String>()
    {
        override fun doInBackground(vararg params: Void?): String? {
            
            val port = 22
            try {
                val baos = ByteArrayOutputStream()
                val jsch = JSch()
                val session = jsch.getSession(user, host, port)
                session.setPassword(password)
                session.setConfig("StrictHostKeyChecking", "no")
                session.timeout = 10000
                session.connect()
                val channel = session.openChannel("exec") as ChannelExec
                val command = "\u0008 \u0013 \u0013 ls"
                channel.setCommand(command)
                channel.connect()
                channel.setOutputStream(baos)
                Log.d("MainActivity","Hey ${String(baos.toByteArray())}")
                channel.disconnect()
            } catch (e: JSchException) {
                Log.e("MainActivity", "$e")


            }
            return "Finished"

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }
}
