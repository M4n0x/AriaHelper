package ch.hearc.ariahelper.sensors.wifip2p.connection

import android.util.Log
import ch.hearc.ariahelper.sensors.wifip2p.WifiP2PReceiver
import java.io.IOException
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Class working with a WifiP2PReceiver
 *
 * - Open a TCP socket to the given address
 * - Perform the given action
 * - One-time connection : Automatically closes and disconnect
 *
 * @constructor
 * @param hostAdd The address of the device to connect
 * @param port The port of the connection
 * @param action The action to perform by this client once the socket is connected
 */
class WifiP2pClient(
    private val hostAdd: String,
    private val port: Int,
    private val action : SocketAction,
    private val shouldDisconnect : Boolean
) : Thread() {
    private val socket: Socket ? = null
    private val TIMEOUT_DELAY_MS = 2000
    private val MAX_RETRY = 20
    private val DELAY_RETRY_MS : Long = 500

    override fun run() {
        try {
            var socket : Socket ? = null
            //try to connect
            var retry = 0
            do{
                try {
                    //connect - client side : Simply open a socket to given address
                    socket = Socket()
                    socket.bind(null)
                    socket.connect(InetSocketAddress(hostAdd, port), TIMEOUT_DELAY_MS)
                } catch(e : IOException){
                    //server might not be ready, sleep and retry
                    sleep(DELAY_RETRY_MS)
                }
            }while(++retry < MAX_RETRY && !socket!!.isConnected)
            //failed
            if(!socket!!.isConnected){
                throw Exception("Could not connect to server")
            }
            //perform the action this socket was opened with
            action.perform(socket)
            //everything went correctly
            WifiP2PReceiver.onConnectionResult(true)
        } catch (e: Exception) {
            Log.e("client", "Error in client : ${e.printStackTrace()}")
            WifiP2PReceiver.onConnectionResult(false)
        } finally {
            //always disconnect from peer
            socket?.close()
            if(shouldDisconnect) WifiP2PReceiver.disconnect()
        }
    }
}