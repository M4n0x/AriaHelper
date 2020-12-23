package ch.hearc.ariahelper.sensors.wifip2p.connection

import android.util.Log
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class WifiP2pClient(
    private val hostAdd: String,
    private val port: Int,
    private val action : SocketAction
) : Thread() {
    private val socket: Socket = Socket()

    override fun run() {
        try {
            Log.d("client", "Connecting...")
            socket.bind(null)
            socket.connect((InetSocketAddress(hostAdd, port)), 5000)
            Log.d("client", "Connected, performing... : ${socket.toString()}")
            action.perform(socket!!)
            Log.d("client", "Closing...")
        } catch (e: IOException) {
            Log.d("client", "Error in client : ${e.printStackTrace()}")
        } finally {
            socket?.takeIf { it.isConnected }?.close()
            WifiP2PReceiver.disconnect()
        }
    }
}