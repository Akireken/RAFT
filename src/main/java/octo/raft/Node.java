package octo.raft;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private int currentTerm;
  private final List<Entry> entries;

  public Node(int currentTerm) {
    this.currentTerm = currentTerm;
    this.entries = new ArrayList<>();
  }

  public Result appendEntries(Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm) {
    if (leaderTerm < this.currentTerm) {
      return new Result(false, this.currentTerm);
    }
    if(isPrevLogTermKnown(prevLogTerm)){
      return new Result(false, this.currentTerm);
    }
    if (!isNewEntry(prevLogIndex) && !isPrevLogIndexKnown(prevLogIndex)) {
      return new Result(false, this.currentTerm);
    }
    if (!entry.isHeartbeat()) {
      appendOrReplace(entry, prevLogIndex);
    }
    this.currentTerm = leaderTerm;
    return new Result(true, leaderTerm);
  }

  private boolean isPrevLogTermKnown(int prevLogTerm) {
    return this.entries.size() > 0 && prevLogTerm != getLastEntry().getTerm();
  }

  private void appendOrReplace(Entry entry, int prevLogIndex) {
    if(isPrevLogIndexKnown(prevLogIndex)) {
      this.entries.set(prevLogIndex, entry);
    } else {
      this.entries.add(entry);
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

  private Entry getLastEntry() {
    return this.entries.get(getLastIndex() - 1);
  }

  public int getCurrentTerm() {
    return this.currentTerm;
  }

  public List<Entry> getEntries() {
    return this.entries;
  }
}
