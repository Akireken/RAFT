package octo.raft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Node {
  private int currentTerm;
  private final List<String> entries;

  public Node(int currentTerm) {
    this.currentTerm = currentTerm;
    this.entries = new ArrayList<>();
  }

  public Result appendEntries(Entries entries, int term) {
    if (term >= this.currentTerm) {
      if(!entries.isHeartbeat()){
        this.entries.add(entries.getValue());
      }
      this.currentTerm = term;
      return new Result(true, term);
    }
    return new Result(false, this.currentTerm);
  }

  public int getCurrentTerm() {
    return this.currentTerm;
  }

  public List<String> getEntries() {
    return this.entries;
  }
}
