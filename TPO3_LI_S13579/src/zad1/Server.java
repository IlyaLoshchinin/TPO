/**
 *
 *  @author Loshchinin Illia S13579
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {

  StringBuffer stringBuffer = new StringBuffer();
  SelectionKey currClient = null;

  public static void main(String[] args) {
    (new Thread(new Server())).start();
  }

  @Override
  public void run() {
    Selector selector;
    ServerSocketChannel socketServerChannel = null;
    InetSocketAddress socketAddress = null;
    try {
      selector = Selector.open();
      socketServerChannel = ServerSocketChannel.open();
      socketAddress = new InetSocketAddress("localhost",4455);

      socketServerChannel.socket().bind(socketAddress);
      socketServerChannel.configureBlocking(false);

    socketServerChannel.register(selector, SelectionKey.OP_ACCEPT);

      System.out.println("Server is running...waiting for client " + socketAddress.getPort() + " - " + socketAddress.getAddress());
      while (true) {

        // Проверяем, если ли какие-либо активности -
        // входящие соединения или входящие данные в
        // существующем соединении.
        int countReadyChannel = selector.select();


//        System.out.println("сколько активных каналов "+ countReadyChannel );
        // Если никаких активностей нет, выходим из цикла
        // и снова ждём.
        if (countReadyChannel == 0) {
          continue;
        }

        // Получим ключи, соответствующие активности,
        // которые могут быть распознаны и обработаны один за другим.
        Set<SelectionKey> selectedKeys = selector.selectedKeys();

        Iterator<SelectionKey> itKey = selectedKeys.iterator();
        while (itKey.hasNext()) {
          // Получим ключ, представляющий один из битов
          // активности ввода/вывода.
          SelectionKey key = itKey.next();

          if (key.isValid()){
            
            try {

            if(key.isAcceptable()) {

              // Приняли
              SocketChannel newClientChannel = ((ServerSocketChannel) key.channel()).accept();
              // Неблокирующий
              newClientChannel.configureBlocking(false);
              // Регистрируем в селекторе
              newClientChannel.register(key.selector(), SelectionKey.OP_READ );
              System.out.println("Client acceptable!");

            }  else if (key.isReadable()) {
              // a channel is ready for reading
              try {
                String data = readFromClientChannel(key);
                System.out.println(data);
                  for (SelectionKey keyL : selector.keys()){
                    if(keyL.channel() instanceof ServerSocketChannel){
                      continue;
                    }
                        writeToChannel(keyL,data);
                  }


              }catch (IOException e){
                key.cancel();
                key.channel().close();
                System.out.println("Client left the room!");
              }

//              System.out.println(stringBuffer.toString());
            }
//            else if (key.isWritable()) {
//                      // a channel is ready for writing
//                  writeToChannel(key,stringBuffer.toString());
//                  stringBuffer.setLength(0);
//
//            }


            }catch (Exception e){
              e.printStackTrace();
              key.cancel();
              key.channel().close();
            }

          }

          itKey.remove();
        }

        // Удаляем выбранные ключи, поскольку уже отработали с ними.
        selectedKeys.clear();
      }


    } catch (IOException e) {
      e.printStackTrace();
    }



  }

  @SuppressWarnings("Duplicates")
  String readFromClientChannel(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    StringBuffer stringBuffer = new StringBuffer();



    Charset charset = Charset.forName("UTF-8");
    while (channel.read(buffer) > 0) {
      buffer.rewind();
      stringBuffer.append(charset.decode(buffer).toString());
      buffer.flip();
    }

    System.out.println(stringBuffer.toString() + "|read server|");




    if(stringBuffer.toString().replaceFirst("\\w+\\s*?:","").equals("close")){
      channel.close();
      System.out.println("Own client left chat!");
    }
//    channel.close();
//    key.cancel();

    return stringBuffer.toString();
  }

  @SuppressWarnings("Duplicates")
  void writeToChannel(SelectionKey key, String text) throws IOException {
    if (text == null || text.isEmpty()){
      return;
    }
    SocketChannel channel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(text.length());

    buffer.put(text.getBytes(Charset.forName("UTF-8")));
    buffer.flip();
    channel.write(buffer);


    if(stringBuffer.toString().replaceFirst("\\w+\\s*?:","").equals("close")){
      channel.close();
      System.out.println("Own client left chat!");
    }
//    key.interestOps(SelectionKey.OP_READ);
//    channel.close();
//    key.cancel();
  }


}
