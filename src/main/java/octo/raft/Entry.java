package octo.raft;

public class Entry {
  private String value;
  private int term;

  public Entry(String value, int terme) {
    this.value = value;
    this.term = terme;
  }

  public Entry() {}


  public String getValue() {
    return this.value;
  }

  public boolean isHeartbeat(){
    return this.value == null;
  }

  public int getTerm() {
    return this.term;
  }
}
