package ch.hearc.ariahelper.sensors.wifip2p.connection

import java.net.Socket

fun interface SocketAction {
    fun perform(socket : Socket)
}