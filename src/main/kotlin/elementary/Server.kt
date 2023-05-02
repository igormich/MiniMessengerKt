package elementary

import java.io.BufferedWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    val PORT = 6666//Порт для соединения, в целом любое число больше 1024 и меньше 10000
    val server = ServerSocket(PORT)//Запускам сервер
    println("Run as Server, for client use args: ${InetAddress.getLocalHost().hostAddress} $PORT")
    val clients = mutableListOf<Pair<Socket, BufferedWriter>>()
    fun send2all(name: String, message: String) {
        clients.removeIf { (client, writer) ->
            try {
                writer.appendLine("$name : $message")
                writer.flush()
                false
            } catch (e: Exception) {
                client.close()
                true
            }
        }
    }
    while (true) {
        val client = server.accept()//Ждём подключения второго клиента
        thread(isDaemon = true) {
            val reader = client.getInputStream().bufferedReader()//Поток входящих сообщений
            val writer = client.getOutputStream().bufferedWriter()//Поток исходящих сообщений
            val name = reader.readLine()
            send2all(name, "Join to chat")
            clients.add(client to writer)
            try {
                while (true) {
                    val message = reader.readLine()
                    send2all(name, message)
                }
            } catch (e: Exception) {
                send2all(name, "Leave chat")
                client.close()
            }
        }
    }
}