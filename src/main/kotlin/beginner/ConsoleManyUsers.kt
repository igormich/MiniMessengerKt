package beginner
import java.io.BufferedWriter
import java.net.*
import kotlin.concurrent.thread
fun server(port: Int) {
    val server = ServerSocket(port)//Запускам сервер
    println("Run as Server, for client use args: ${InetAddress.getLocalHost().hostAddress} $port YOU_NAME")
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
fun client(host: String, port: Int, name: String) {
    val client = Socket(host, port)
    val reader = client.getInputStream().bufferedReader()//Поток входящих сообщений
    val writer = client.getOutputStream().bufferedWriter()//Поток исходящих сообщений
    writer.appendLine(name)
    writer.flush()
    thread(isDaemon = true) {//создаём отдельный поток-демон чтобы печатать сообщения
        while (true) {//бесконечно печатаем сообщения на консоль
            println(reader.readLine())
        }
    }
    while (true) {//бесконечно принимаем сообщения с консоли и передаём их по сети
        writer.appendLine(readln())
        writer.flush()
    }
}
fun main(args: Array<String>) = try {//Тут что то может пойти не так, готовимся обработать ошибку
    when (args.size) {
        1 -> server(args[0].toInt())//Если запустили с одним параметров значит мы сервер
        3 -> client(args[0], args[1].toInt(), args[2])//подключаемся к серверу
        else -> throw IllegalStateException()
    }
} catch (e: Exception) {//если что то пошло не так
    println("Something get wrong, try run script with PORT arguments for server, then run with arguments printed by server")
}