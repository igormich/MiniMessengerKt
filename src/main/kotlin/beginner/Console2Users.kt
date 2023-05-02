package beginner
import java.net.*
import kotlin.concurrent.thread
fun main(args: Array<String>) {
    val client = try {//Тут что то может пойти не так, готовимся обработать ошибку
        when (args.size) {
            1 -> {//Если запустили с одним параметров значит мы сервер
                val server = ServerSocket(args[0].toInt())//Запускам сервер
                println("Run as Server, for client use args: ${InetAddress.getLocalHost().hostAddress} ${args[0]}")
                server.accept()//Ждём подключения второго клиента
            }
            2 -> Socket(args[0], args[1].toInt())//подключаемся к серверу
            else -> throw IllegalStateException()
        }
    } catch(e:Exception) {//если что то пошло не так
        println("Something get wrong, try run script with PORT arguments for server, then run with arguments printed by server")
        return//печатаем сообщение и заверщаем программу
    }//Важно код ниже одинаковый для сервера и клиента
    val reader = client.getInputStream().bufferedReader()//Поток входящих сообщений
    val writer = client.getOutputStream().bufferedWriter()//Поток исходящих сообщений
    println("Ready for communication!")
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