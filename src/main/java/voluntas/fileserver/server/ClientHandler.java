package voluntas.fileserver.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import voluntas.fileserver.Request;
import voluntas.fileserver.RequestHandler;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  Logger logger = Logger.getLogger("ClientHandler.class");

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    String fileDownloadDir = "./files-dir/";

    RequestHandler.makeDirectoryIfNotPresent(fileDownloadDir);

    try (InputStream clientIn = clientSocket.getInputStream();
        OutputStream clientOut = clientSocket.getOutputStream();) {

      while (true) {
        ObjectInputStream objectInputStream = new ObjectInputStream(clientIn);
        Request requested = (Request) objectInputStream.readObject();
        logger.info("Upload: " + requested.getFileName() + " : " + requested.getFileSize());

        RequestHandler.writeInputStreamToFile(requested, fileDownloadDir,
            clientIn);

      }

    } catch (EOFException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

}
