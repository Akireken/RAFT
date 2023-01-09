package octo.raft;

public class AppendEntryResult {
  private boolean status;
  private int term;

  public AppendEntryResult(boolean status, int term) {

    this.status = status;
    this.term = term;
  }

  public boolean getStatus() {
    return status;
  }

  public int getTerm() {
    return term;
  }
}
