package octo.raft;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private int currentTerm;
  private final List<String> entries;

  public Node(int currentTerm) {
    this.currentTerm = currentTerm;
    this.entries = new ArrayList<>();
  }

  public Result appendEntries(Entries entries, int term, int prevLogIndex, int prevLogTerm) {
    if (term >= this.currentTerm) {
      if (!entries.isHeartbeat()) {
        if(prevLogTerm != 0){
          return new Result(false, getCurrentTerm());
        }
        if (isNewEntry(prevLogIndex)) {
          this.entries.add(entries.getValue());
        } else {
          if (isPrevLogIndexKnown(prevLogIndex)){
            this.entries.set(prevLogIndex, entries.getValue());
          } else{
            return new Result(false, this.currentTerm);
          }
        }
      }
      this.currentTerm = term;
      return new Result(true, term);
    }
    return new Result(false, this.currentTerm);
  }

  private boolean isPrevLogIndexKnown(int prevLogIndex) {
    return prevLogIndex < getLastIndex();
  }

  private boolean isNewEntry(int prevLogIndex) {
    return prevLogIndex == getLastIndex();
  }

  private int getLastIndex() {
    return this.entries.size();
  }

  public int getCurrentTerm() {
    return this.currentTerm;
  }

  public List<String> getEntries() {
    return this.entries;
  }
}
