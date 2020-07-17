package com.dozen.world.face

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dozen.world.R
import kotlinx.android.synthetic.main.activity_net.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by Hugo on 20-7-17.
 * Describe:
 *
 *
 *
 */
class NetActivity : AppCompatActivity() {

    private lateinit var userSocket: Socket

    private lateinit var userServerSocket: ServerSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net)

        btn_net_server_start.setOnClickListener(clickListener)
        btn_net_server_ip.setOnClickListener(clickListener)
        btn_net_server_send_data.setOnClickListener(clickListener)

        btn_net_client_start.setOnClickListener(clickListener)
        btn_net_client_send_data.setOnClickListener(clickListener)

    }


    private var clickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_net_server_start -> {
                try {
                    userServerSocket = ServerSocket(et_net_server_port.text.toString().toInt())

                    val socket = userServerSocket.accept()
                    val inputStream = socket.getInputStream()

                    var byteBuffer = ByteArray(1024 * 4)
                    var temp = inputStream.read(byteBuffer)
                    var data: String = ""
                    while (temp != -1) {
                        data += String(byteBuffer, 0, temp)
                        temp = inputStream.read(byteBuffer)
                    }
                    showResult(data)

                } catch (e: Exception) {

                }
            }
            R.id.btn_net_client_start -> {
                Thread(Runnable {
                    userSocket = Socket(
                        et_net_client_ip.text.toString(),
                        et_net_client_port.text.toString().toInt()
                    )
                }).start()

            }
            R.id.btn_net_server_ip -> {
                try {
                    val wifiManager: WifiManager =
                        this.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo: WifiInfo = wifiManager.connectionInfo

                    val ip = wifiInfo.ipAddress

                    val sb: StringBuilder = StringBuilder()
                    sb.append(ip and 0xFF).append(".")
                    sb.append((ip shr 8) and 0xFF).append(".")
                    sb.append((ip shr 16) and 0xFF).append(".")
                    sb.append((ip shr 24) and 0xFF)

                    showResult(sb.toString())

                } catch (e: Exception) {

                }


            }
            R.id.btn_net_server_send_data -> serverSendData(et_net_server_data.text.toString())
            R.id.btn_net_client_send_data -> clientSendData(et_net_client_data.text.toString())

        }
    }

    private fun serverSendData(data: String) {
        Thread(Runnable {
            val socket = userServerSocket.accept()
            val inputStream = socket.getInputStream()

            var byteBuffer = ByteArray(1024 * 4)
            var temp = inputStream.read(byteBuffer)
            var data: String = ""
            while (temp != -1) {
                data += String(byteBuffer, 0, temp)
                temp = inputStream.read(byteBuffer)
            }
            showResult(data)
        }).start()
    }

    private fun clientSendData(data: String) {
        Thread(Runnable {
            val bis = ByteArrayInputStream(et_net_client_data.text.toString().toByteArray())
            val inputStream = bis as InputStream

            val outputStream = userSocket.getOutputStream()
            val byteArray = ByteArray(4 * 1024)
            var temp = inputStream.read(byteArray)

            while (temp != -1) {
                outputStream.write(byteArray, 0, temp)
                temp = inputStream.read(byteArray)
            }
            outputStream.flush()
        }).start()
    }

    @SuppressLint("SetTextI18n")
    private fun showResult(result: String) {

        val data: String = tv_net_data_show.text.toString()

        tv_net_data_show.text = data + "\n" + result

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (userSocket != null) {
            userSocket.close()
        }
        if (userServerSocket != null) {
            userServerSocket.close()
        }
    }

}