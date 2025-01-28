package voluntas.fileserver.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

  private final Socket clientSocket;
  Logger logger = Logger.getLogger("ClientHandler.class");

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    String serverFilesDir = "./server-files/";

    if (!Files.isDirectory(Paths.get(serverFilesDir))) {
      try {
        Files.createDirectory(Paths.get(serverFilesDir));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      String clientRequest;
      while ((clientRequest = clientIn.readLine()) != null) {
        System.out.println(clientRequest);
        String[] request = clientRequest.split(" ");
        String command = request[0];
        String fileName = request[1];
        switch (command) {
          case "UPLOAD":
            handleUpload(fileName, serverFilesDir, clientSocket);
            break;
          case "DOWNLOAD":
            handleDownload(fileName, clientSocket);
            break;
          case "LIST":
            handleList(clientSocket);
            break;
          default:
            break;
        }
      }

    } catch (EOFException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleUpload(String filename, String fileDir, Socket clientSocket) {
    try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(
        new FileOutputStream(fileDir + filename));
        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream());) {

      long fileSize = clientIn.read();
      byte[] buffer = new byte[4096];
      long totalBytesRead = 0;
      int bytesRead;
      while ((totalBytesRead <= fileSize) && (bytesRead = clientSocket.getInputStream().read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, bytesRead);
        totalBytesRead += bytesRead;
      }
      fileOutputStream.close();
      System.out.println("upload");
      clientOut.printf("File '%s' was successfully uploaded to server.", filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleDownload(String filename, Socket clientSocket) {
    System.out.println("DOWNLOAD " + filename);
  }

  private void handleList(Socket clientSocket) {
    System.out.println("LIST");
  }

}
