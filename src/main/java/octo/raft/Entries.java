package octo.raft;

public class Entries {
  private String value;

  public Entries(String value) {
    this.value = value;
  }

  public Entries() {}

  public String getValue() {
    return this.value;
  }

  public boolean isHeartbeat(){
    return this.value == null;
  }
}
