package ui.smart_connect.subnet

import java.io.IOException
import java.net.InetAddress
import java.time.Duration
import java.time.Instant


fun ByteArray.toMac(): String {
    val sb = StringBuilder()
    for (i in 0 until this.size) {
        sb.append(java.lang.String.format("%02X%s", this.get(i), if (i < this.size - 1) "-" else ""))
    }
    return sb.toString()
}

fun ping(host: String?): Duration? {
    val startTime: Instant = Instant.now()
    try {
        val address = InetAddress.getByName(host)
        if (address.isReachable(1000)) {
            return Duration.between(startTime, Instant.now())
        }
    } catch (e: IOException) {
        // Host not available, nothing to do here
    }
    return Duration.ofDays(1)
}