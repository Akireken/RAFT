package octo.raft;

public class Result {
  private boolean status;
  private int term;

  public Result(boolean status, int term) {

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
