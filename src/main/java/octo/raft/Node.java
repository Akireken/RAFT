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

  public Result appendEntries(Entries entries, int leaderTerm, int prevLogIndex, int prevLogTerm) {
    if (leaderTerm < this.currentTerm) {
      return new Result(false, this.currentTerm);
    }
    if (!entries.isHeartbeat()) {
      if(prevLogTerm != 0){
        return new Result(false, getCurrentTerm());
      }
      if (!isNewEntry(prevLogIndex)) {
        if (!isPrevLogIndexKnown(prevLogIndex)) {
          return new Result(false, this.currentTerm);
        } else {
          this.entries.set(prevLogIndex, entries.getValue());
        }
      } else {
        this.entries.add(entries.getValue());      }
    }
    this.currentTerm = leaderTerm;
    return new Result(true, leaderTerm);
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
