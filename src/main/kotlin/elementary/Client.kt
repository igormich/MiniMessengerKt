package elementary

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val (host, port) = if(args.size>=2) {
        args[0] to args[1].toInt()
    } else {
        println("Enter server ip")
        val host = readln()
        println("Enter server port(number)")
        val port = readln().toInt()
        host to port
    }
    val name = if(args.size==3) {
        args[2]
    } else {
        println("Enter you name")
        readln()
    }
    val client = Socket(host, port)//подключаемся к серверу
    val reader = client.getInputStream().bufferedReader()//Поток входящих сообщений
    val writer = client.getOutputStream().bufferedWriter()//Поток исходящих сообщений
    thread(isDaemon = true) {//создаём отдельный поток-демон чтобы печатать сообщения
        while (true) {//бесконечно печатаем сообщения на консоль
            println(reader.readLine())
        }
    }
    writer.write(name)
    writer.newLine()
    writer.flush()
    while (true) {//бесконечно принимаем сообщения с консоли и передаём их по сети
        writer.write(readln())
        writer.newLine()
        writer.flush()
    }
}