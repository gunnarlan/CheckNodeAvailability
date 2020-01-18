package com.example.checknodeavailability

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textview_username = username
        val textview_IPaddress = IPaddress
        val textview_password = password

        val filename = "logininfo.txt"



        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val username_string = textview_username.text.toString()
            val IPaddress_string = textview_IPaddress.text.toString()
            val password_string = textview_password.text.toString()
            val data = "Username $username_string Password: $password_string IPaddress: $IPaddress_string"

            /*
            try {
                val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
                fileOutputStream.write(data.toByteArray())
            }catch (e: Exception){
                e.printStackTrace()
            }
            */

            val command_output = ExecuteSSHCommand(username_string, password_string, IPaddress_string).execute()
            Log.d("MA", "$command_output")
            Log.e("MA", "WORKED!")
        }

    }

    private class ExecuteSSHCommand(var user: String, var password: String, var host: String) : AsyncTask<Void, Void, String>()
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
                val command = "rbusy"
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
                return String(tmp)

            } catch (e: JSchException) {
                Log.e("MainActivity", "${e.printStackTrace()}")
                return "ERROR"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }
}
