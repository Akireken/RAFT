package octo.raft;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private int currentTerm;
  private final List<Entries> entries;

  public Node(int currentTerm) {
    this.currentTerm = currentTerm;
    this.entries = new ArrayList<>();
  }

  public Result appendEntries(Entries entries, int leaderTerm, int prevLogIndex, int prevLogTerm) {
    if (leaderTerm < this.currentTerm) {
      return new Result(false, this.currentTerm);
    }
    if(prevLogTerm != 0){
      return new Result(false, this.currentTerm);
    }
    if (!isNewEntry(prevLogIndex) && !isPrevLogIndexKnown(prevLogIndex)) {
      return new Result(false, this.currentTerm);
    }
    if (!entries.isHeartbeat()) {
      appendOrReplace(entries, prevLogIndex);
    }
    this.currentTerm = leaderTerm;
    return new Result(true, leaderTerm);
  }

  private void appendOrReplace(Entries entries, int prevLogIndex) {
    if(isPrevLogIndexKnown(prevLogIndex)) {
      this.entries.set(prevLogIndex, entries);
    } else {
      this.entries.add(entries);
    }
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

  public List<Entries> getEntries() {
    return this.entries;
  }
}
