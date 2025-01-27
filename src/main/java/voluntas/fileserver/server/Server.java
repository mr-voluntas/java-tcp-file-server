package voluntas.fileserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

  public static void main(String[] args) {

    try (ServerSocket serverSocket = new ServerSocket()) {
      serverSocket.bind(new InetSocketAddress("127.0.0.1", 42069));

      ExecutorService executor = Executors.newCachedThreadPool();

      while (true) {
        Socket client = serverSocket.accept();
        executor.execute(new ClientHandler(client));
      }

    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
