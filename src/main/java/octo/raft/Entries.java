package octo.raft;

public class Entries {
  private String value;
  private int term;

  public Entries(String value, int terme) {
    this.value = value;
    this.term = terme;
  }

  public Entries() {}


  public String getValue() {
    return this.value;
  }

  public boolean isHeartbeat(){
    return this.value == null;
  }
}
