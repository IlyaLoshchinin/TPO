/**
 * @author Loshchinin Illia S13579
 */

package zad1;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Client implements Runnable {

    GUI gui;
    String message = null;
    SocketChannel serverConChannel;
    String nickname = null;

    public static void main(String[] args) {
        (new Thread(new Client())).start();
    }

    public Client() {
        enterNickName();
        gui = new GUI(this);
        new Thread(gui).start();
    }

    @Override
    public void run() {

        InetSocketAddress infoServer = new InetSocketAddress("localhost", 4455);
        SocketChannel serverChannel = null;
        try {
            serverChannel = SocketChannel.open();


            Selector selector = Selector.open();


            serverChannel.configureBlocking(false);
            serverChannel.connect(infoServer);
            serverChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ );

            while (true) {

                int countReadyChannel = selector.select();

                if (countReadyChannel == 0) {
                    continue;
                }


                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                Iterator<SelectionKey> itKey = selectedKeys.iterator();
                while (itKey.hasNext()) {

                    SelectionKey key = itKey.next();

                    if (key.isValid()) {

                        if (key.isConnectable()) {
                            // a connection was established with a remote server.
                            serverConChannel =  finishConnection(key);
                            try {

                                writeToChannel(key, "Hello! I'm new in this chat!");
                            }catch (Exception e){
                                    gui.getMainTextArea().append("Server not found!");
                            }

                        } else if (key.isReadable()) {
                            // a channel is ready for reading
                            //работает малосьть
                            gui.getMainTextArea().append(readFromChannel(key)+"\n");

                        }
//                        else if (key.isWritable()) {
//                            // a channel is ready for writing
//                            writeToChannel(key, message);
//                        }
                    }

                    itKey.remove();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("Duplicates")
    String readFromChannel(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuffer stringBuffer = new StringBuffer();


        Charset charset = Charset.forName("UTF-8");
        while (channel.read(buffer) > 0) {
            buffer.rewind();
            stringBuffer.append(charset.decode(buffer).toString());
            buffer.flip();
        }

        System.out.println(stringBuffer.toString() + "|read client|");
//        channel.close();
//        key.cancel();
//        key.interestOps(SelectionKey.OP_WRITE);
        return stringBuffer.toString();
    }

    @SuppressWarnings("Duplicates")
    void writeToChannel(SelectionKey key, String text) throws IOException {
        if (text == null || text.isEmpty()) {
            message = null;
            return;
        }
        SocketChannel channel = (SocketChannel)key.channel();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(nickname).append(": ").append(text);
        ByteBuffer buffer = ByteBuffer.allocate(stringBuffer.length());

        buffer.put(stringBuffer.toString().getBytes(Charset.forName("UTF-8")));
        buffer.flip();
        channel.write(buffer);


        message = null;
//        channel.close();
//        key.interestOps(SelectionKey.OP_READ);
//      key.cancel();
    }

    void writeToChannel(SocketChannel channel, String text) throws IOException {
        if (text == null || text.isEmpty()) {
            message = null;
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(nickname).append(": ").append(text);
        ByteBuffer buffer = ByteBuffer.allocate(stringBuffer.length());

        buffer.put(stringBuffer.toString().getBytes(Charset.forName("UTF-8")));
        buffer.flip();
        channel.write(buffer);


        message = null;
//        channel.close();
//        key.interestOps(SelectionKey.OP_READ);
//      key.cancel();
    }

    public void enterNickName() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        Font font = new Font("Roboto Slab",Font.PLAIN,18);
        while (true) {
            JLabel label = new JLabel("Nickname in chat");
            label.setFont(font);
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            String nickname = JOptionPane.showInputDialog(null, label,
                    "Enter your nickname",
                    JOptionPane.INFORMATION_MESSAGE);
            if (nickname == null) {
                System.exit(0);
            }
            if (!nickname.isEmpty()) {
                this.nickname = nickname;
                return;
            }


        }
    }

    private SocketChannel finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            // Cancel the channel's registration with our selector
            System.out.println(e);
            socketChannel.close();
            return null;
        }

        // Register an interest in writing on this channel
      key.interestOps(SelectionKey.OP_READ);
        return socketChannel;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
