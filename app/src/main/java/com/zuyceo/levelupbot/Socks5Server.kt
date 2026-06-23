package com.zuyceo.levelupbot

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class Socks5Server(
    private val onLog: ((String) -> Unit)? = null
) {
    private var serverSocket: ServerSocket? = null
    private var running = false

    private val AUTH_USERNAME = "bot"
    private val AUTH_PASSWORD = "bot"

    fun start() {
        if (running) return
        running = true

        thread(isDaemon = true) {
            try {
                val bindAddr = InetAddress.getByName(ProxyConfig.host())
                serverSocket = ServerSocket(ProxyConfig.port(), 50, bindAddr)
                onLog?.invoke("SOCKS5 server started")

                while (running) {
                    try {
                        val client = serverSocket?.accept() ?: break
                        thread(isDaemon = true) {
                            handleClient(client)
                        }
                    } catch (e: Exception) {
                        if (running) onLog?.invoke("Accept error: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                onLog?.invoke("Server error: ${e.message}")
            }
        }
    }

    fun stop() {
        running = false
        try {
            serverSocket?.close()
        } catch (e: Exception) { }
        serverSocket = null
        onLog?.invoke("SOCKS5 server stopped")
    }

    fun isRunning() = running

    private fun handleClient(client: Socket) {
        try {
            client.soTimeout = 30000
            val ins = client.getInputStream()
            val out = client.getOutputStream()

            val ver = ins.read()
            if (ver != 5) { client.close(); return }

            val nmethods = ins.read()
            val methods = ByteArray(nmethods)
            ins.read(methods)

            out.write(byteArrayOf(5, 2))
            out.flush()

            val subVer = ins.read()
            if (subVer != 1) { client.close(); return }

            val uLen = ins.read()
            val uBytes = ByteArray(uLen)
            ins.read(uBytes)

            val pLen = ins.read()
            val pBytes = ByteArray(pLen)
            ins.read(pBytes)

            val user = String(uBytes)
            val pass = String(pBytes)

            if (user != AUTH_USERNAME || pass != AUTH_PASSWORD) {
                out.write(byteArrayOf(1, 1))
                out.flush()
                client.close()
                return
            }

            out.write(byteArrayOf(1, 0))
            out.flush()

            val reqVer = ins.read()
            val cmd = ins.read()
            ins.read()

            val atyp = ins.read()
            val destAddr: String
            when (atyp) {
                1 -> {
                    val ipBytes = ByteArray(4)
                    ins.read(ipBytes)
                    destAddr = InetAddress.getByAddress(ipBytes).hostAddress ?: ""
                }
                3 -> {
                    val len = ins.read()
                    val domainBytes = ByteArray(len)
                    ins.read(domainBytes)
                    destAddr = String(domainBytes)
                }
                4 -> {
                    val ipv6 = ByteArray(16)
                    ins.read(ipv6)
                    destAddr = InetAddress.getByAddress(ipv6).hostAddress ?: ""
                }
                else -> { client.close(); return }
            }

            val portHigh = ins.read()
            val portLow = ins.read()
            val destPort = (portHigh shl 8) or portLow

            if (cmd != 1) {
                out.write(byteArrayOf(5, 7, 0, 1, 0, 0, 0, 0, 0, 0))
                out.flush()
                client.close()
                return
            }

            try {
                val remote = Socket()
                remote.connect(InetSocketAddress(destAddr, destPort), 10000)

                out.write(byteArrayOf(5, 0, 0, 1, 0, 0, 0, 0, 0, 0))
                out.flush()

                val t1 = thread(isDaemon = true) {
                    try { pipe(ins, remote.getOutputStream()) } catch (e: Exception) { }
                }
                val t2 = thread(isDaemon = true) {
                    try { pipe(remote.getInputStream(), out) } catch (e: Exception) { }
                }
                t1.join()
                t2.join()
                remote.close()
            } catch (e: Exception) {
                out.write(byteArrayOf(5, 4, 0, 1, 0, 0, 0, 0, 0, 0))
                out.flush()
            }

        } catch (e: Exception) {
            // silent
        } finally {
            try { client.close() } catch (e: Exception) { }
        }
    }

    private fun pipe(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
            output.flush()
        }
    }
}
