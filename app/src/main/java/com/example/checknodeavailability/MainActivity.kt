package com.example.checknodeavailability

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    val user = System.getenv("username") ?: ""
    val password = System.getenv("password") ?: ""
    val host = System.getenv("host") ?: ""

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
                val command = "ls"
                channel.setOutputStream(baos)
                channel.setCommand(command)
                channel.setInputStream(null)
                channel.setErrStream(System.err)
                val input: InputStream = channel.inputStream
                channel.connect()

                Log.d("MA", "successful connect to server")
                Log.d("MA", "Executing command $command")

                val tmp = ByteArray(1024)

                while(true) {
                    Log.d("MainActivity", "Pressed true... executing")
                    Log.d("MainActivity", "input is ${input} and ${input.available()}")
                    while(input.available() > 0) {
                        val i = input.read(tmp, 0, 1024)
                        if (i < 0) {
                            break
                        }
                        Log.d("MainActivity", String(tmp,0, i))
                    }

                    if (channel.isClosed) {
                        Log.d("MainActivity", "closed channel")
                        break
                    }

                    try {
                        Log.d("MainActivity", "sleeping...")
                        Thread.sleep(1000)
                    } catch (e: Exception) {
                        Log.d("MainActivity", "$e")
                    }
                }

                channel.disconnect()
                session.disconnect()
            } catch (e: JSchException) {
                Log.e("MainActivity", "${e.printStackTrace()}")
            }
            return "Finished"

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }
}
