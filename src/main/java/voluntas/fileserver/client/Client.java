package voluntas.fileserver.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import voluntas.fileserver.RequestHandler;
import voluntas.fileserver.Request;
import voluntas.fileserver.RequestType;

public class Client {

  public static void main(String[] args) {
    String fileDir = "./file-upload-dir/";

    RequestHandler.makeDirectoryIfNotPresent(fileDir);

    try (Socket clientSocket = new Socket("127.0.0.1", 42069);
        InputStream clientIn = clientSocket.getInputStream();
        OutputStream clientOut = clientSocket.getOutputStream();
        Scanner cliInput = new Scanner(System.in);) {

      System.out.println("\nWelcome to the file server...\n");

      printCommands();

      String userInput;
      while ((userInput = cliInput.nextLine()) != null) {
        String[] parsedInput = userInput.split(" ");
        String command = parsedInput[0];
        String fileName = parsedInput[1];

        switch (command) {
          case "/u":
            Request uploadRequest = new Request(RequestType.UPLOAD, fileName, fileDir);
            if (RequestHandler.handle(uploadRequest, clientOut)) {
              System.out.println("Upload complete");
            }
            break;
          case "/d":
            System.out.println("Download");
            break;
          case "/l":
            System.out.println("List");
            break;
          case "/lv":
            System.out.println("List Verbose");
            break;
          case "/c":
            printCommands();
            break;
          default:
            System.out.println("Command not found.");
            break;
        }

      }

      System.out.println("Disconnected from server");

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

}
