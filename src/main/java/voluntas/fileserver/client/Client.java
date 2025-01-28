package voluntas.fileserver.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

  public static void main(String[] args) {
    String clientFileDir = "./client-files/";

    if (!Files.isDirectory(Paths.get(clientFileDir))) {
      try {
        Files.createDirectory(Paths.get(clientFileDir));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    try (Socket clientSocket = new Socket("127.0.0.1", 42069);
        Scanner userInput = new Scanner(System.in);) {

      System.out.println("\nWelcome to the file server...\n");

      printCommands();

      while (userInput.hasNextLine()) {
        String[] parsedInput = userInput.nextLine().split(" ");
        String command = parsedInput[0];
        String fileName = parsedInput[1];

        switch (command) {
          case "/u":
            uploadFile(fileName, clientFileDir, clientSocket);
            break;
          case "/d":
            downloadFile(fileName, clientSocket);
            break;
          case "/l":
            listFiles(clientSocket);
            break;
          case "/c":
            printCommands();
            break;
          default:
            System.out.println("Command not found.");
            break;
        }

      }

    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void printCommands() {
    String header = String.format("%-15s | %-15s | %-15s", "Command", "Prefix", "Description");
    String separator = "-".repeat(header.length());
    System.out.println("");
    System.out.println(header);
    System.out.println(separator);
    System.out.printf("%-15s | %-15s | %-15s%n", "upload", "/u 'file'", "upload a file");
    System.out.printf("%-15s | %-15s | %-15s%n", "download", "/d 'file'", "download a file");
    System.out.printf("%-15s | %-15s | %-15s%n", "list", "/l", "list files");
    System.out.printf("%-15s | %-15s | %-15s%n", "verbose list", "/lv", "list files verbose");
    System.out.printf("%-15s | %-15s | %-15s%n", "print commands", "/c", "print commands");
    System.out.println("");
  }

  private static void uploadFile(String fileName, String fileDir, Socket clientSocket) {

    File uploadFile = new File(fileDir + fileName);

    if (!uploadFile.exists()) {
      System.err.printf("Could not find file '%s'\n", uploadFile.getName());
    }

    try (
        BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(uploadFile));) {

      clientOut.println("UPLOAD " + fileName);
      clientOut.print(uploadFile.length());

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        clientSocket.getOutputStream().write(buffer, 0, bytesRead);
      }

      fileInputStream.close();
      System.out.println(clientIn.readLine());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void downloadFile(String fileName, Socket clientSocket) {
    try (PrintWriter printToServer = new PrintWriter(clientSocket.getOutputStream(), true)) {
      printToServer.println("DOWNLOAD " + fileName);
      System.out.println("DOWNLOAD");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void listFiles(Socket clientSocket) {
    try (PrintWriter printToServer = new PrintWriter(clientSocket.getOutputStream(), true)) {
      printToServer.println("LIST");
      System.out.println("LIST");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
