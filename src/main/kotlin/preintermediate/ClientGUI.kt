package preintermediate

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.net.Socket
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread

fun startMessenger(client: Socket, name: String) {
    val reader = client.getInputStream().bufferedReader()
    val writer = client.getOutputStream().bufferedWriter()
    fun send(text: String) {
        writer.appendLine(text)
        writer.flush()
    }
    send(name)
    val messenger = JFrame()
    val chat = JTextArea()
    chat.isEditable = false
    messenger.add(
        JScrollPane(
            chat,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        ).also {
            it.preferredSize = Dimension(800, 600)
        })
    val input = JTextField()
    input.addActionListener {
        send(input.text)
        input.text = ""
    }
    messenger.add(input, BorderLayout.SOUTH)
    messenger.pack()
    messenger.setLocationRelativeTo(null)
    messenger.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    messenger.isVisible = true
    thread(isDaemon = true) {//создаём отдельный поток-демон чтобы печатать сообщения
        while (true) {//бесконечно печатаем сообщения на консоль
            chat.text += reader.readLine() + "\r\n"
        }
    }
}

fun main() {
    val login = JFrame()
    val requisites = JPanel(GridLayout(3, 2, 10, 10))
    requisites.border = EmptyBorder(10, 10, 10, 10)
    requisites.add(JLabel("Server ip:"))
    val host = JTextField("localhost")
    requisites.add(host)
    requisites.add(JLabel("Server port:"))
    val port = JSpinner(SpinnerNumberModel(6666, 1, 65000, 1))
    requisites.add(port)
    requisites.add(JLabel("You name:"))
    val name = JTextField("")
    requisites.add(name)
    login.add(requisites)
    login.add(JButton("Connect").also {
        it.addActionListener {
            if (name.text.isNotBlank()) {
                try {
                    val client = Socket(host.text, port.value as Int)//подключаемся к серверу
                    startMessenger(client, name.text)
                    login.isVisible = false
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(login, "Enter connection parameters")
                }

            } else {
                JOptionPane.showMessageDialog(login, "Enter valid name")
            }
        }
    }, BorderLayout.SOUTH)
    login.pack()
    login.setLocationRelativeTo(null)
    login.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    login.isVisible = true
}