package ch.hearc.ariahelper.sensors.wifip2p.connection

import android.util.Log
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

/**
 * Class working with a WifiP2PReceiver
 *
 * - Open a TCP server on the given port, listening for connections
 * - Perform the given action once a client has connected
 * - One-time connection : Automatically closes and disconnect
 *
 * @constructor
 * @param port The port of the connection
 * @param action The action to perform to the client socket
 */
class WifiP2pServer (
    private val port: Int,
    private val action : SocketAction,
    private val shouldDisconnect : Boolean
) : Thread() {
    private lateinit var ephemeralServerSocket : ServerSocket
    private var client: Socket ? = null
    private val TIMEOUT_MS = 10000

    override fun run() {
        try {
            //init server in waiting
            ephemeralServerSocket = ServerSocket(port)
            ephemeralServerSocket.soTimeout = TIMEOUT_MS //timer avoids infinite wait
            client = ephemeralServerSocket.accept()
            if(client == null){
                throw Exception("Timeout reached waiting for client")//abort
            }
            Log.d("TAG", "server is connected")
            action.perform(client!!)
            //everything went correctly
            WifiP2PReceiver.onConnectionResult(true)
        } catch(e : Exception){
            WifiP2PReceiver.onConnectionResult(false)
            Log.d("server", "Error in server : ${e.printStackTrace()}")
        } finally {
            //always disconnect from peer
            Log.d("TAG", "server disconnecting connection")
            client?.close()
            ephemeralServerSocket.close()
            if(shouldDisconnect) WifiP2PReceiver.disconnect()
        }
    }
}