package voluntas.fileserver;

import java.io.Serializable;

public class Request implements Serializable {

  private static final long serialVersionUID = 1L;
  private RequestType requestType;
  private String fileName;
  private String fileDir;
  private long fileSize;

  public Request(RequestType requestType) {
    this.requestType = requestType;
    this.fileName = "";
    this.fileDir = "";
    this.fileSize = 0;
  }

  public Request(RequestType requestType, String fileName) {
    this.requestType = requestType;
    this.fileName = fileName;
    this.fileDir = "";
    this.fileSize = 0;
  }

  public Request(RequestType requestType, String fileName, String fileDir) {
    this.requestType = requestType;
    this.fileName = fileName;
    this.fileDir = fileDir;
    this.fileSize = 0;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFileDir() {
    return fileDir;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

}
