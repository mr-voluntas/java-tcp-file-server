package voluntas.fileserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler {

  public static boolean handle(Request request, OutputStream outputStream) {

    switch (request.getRequestType()) {
      case UPLOAD:
        File file = new File(request.getFileDir() + request.getFileName());
        if (isFileInDir(file)) {
          request.setFileSize(file.length());
          writeRequestToOutputStream(request, outputStream);
          writeFileContentsToOutputStream(file, outputStream);
        } else {
          System.out.println("Could not find file: " + request.getFileName());
          return false;
        }
        break;
      case LIST:
        writeRequestToOutputStream(request, outputStream);
        break;
      case DELETE:
        writeRequestToOutputStream(request, outputStream);
        break;
      default:
        break;
    }
    return true;
  }

  public static void makeDirectoryIfNotPresent(String fileUploadsDirectory) {
    if (!Files.isDirectory(Paths.get(fileUploadsDirectory))) {
      try {
        Files.createDirectory(Paths.get(fileUploadsDirectory));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static boolean isFileInDir(File file) {
    if (!file.exists()) {
      System.err.printf("Could not find file '%s'\n", file.getName());
      return false;
    } else {
      return true;
    }
  }

  private static void writeRequestToOutputStream(Request request,
      OutputStream outputStream) {
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(request);
    } catch (InvalidClassException e) {
      throw new RuntimeException(e);
    } catch (NotSerializableException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void writeFileContentsToOutputStream(File file,
      OutputStream outputStream) {
    try {
      BufferedInputStream fileInputStream = new BufferedInputStream(
          new FileInputStream(file));

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      fileInputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeInputStreamToFile(Request request, String writeDir, InputStream clientIn) {
    try {
      BufferedOutputStream fileOutputStream = new BufferedOutputStream(
          new FileOutputStream(writeDir + request.getFileName()));
      byte[] buffer = new byte[4096];
      long totalBytesRead = 0;
      int bytesRead;
      while (totalBytesRead < request.getFileSize() && (bytesRead = clientIn.read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, bytesRead);
        totalBytesRead += bytesRead;
      }
      fileOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
