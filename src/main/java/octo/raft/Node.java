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

  public Result appendEntries(Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
    if (leaderTerm < this.currentTerm) {
      return new Result(false, this.currentTerm);
    }
    if (!isNewEntry(prevLogIndex) && isPrevLogIndexUnknown(prevLogIndex)) {
      return new Result(false, this.currentTerm);
    }
    if(isNotFirstEntry(prevLogIndex) && isPrevLogTermUnKnown(prevLogTerm, prevLogIndex)){
      return new Result(false, this.currentTerm);
    }
    if (!entry.isHeartbeat()) {
      appendOrReplace(entry, prevLogIndex);
    }
    this.currentTerm = leaderTerm;
    return new Result(true, leaderTerm);
  }

  private boolean isPrevLogTermUnKnown(int prevLogTerm, int prevLogIndex) {
    return prevLogTerm != this.entries.get(prevLogIndex-1).getTerm();
  }

  private boolean isNotFirstEntry(int prevLogIndex) {
    return prevLogIndex > 0;
  }

  private void appendOrReplace(Entry entry, int prevLogIndex) {
    if(isExistingEntry(prevLogIndex)) {
      this.entries.set(prevLogIndex, entry);
    } else {
      this.entries.add(entry);
    }
  }

  private boolean isPrevLogIndexUnknown(int prevLogIndex) {
    return prevLogIndex >= getLastIndex();
  }

  private boolean isExistingEntry(int prevLogIndex) {
    return !isPrevLogIndexUnknown(prevLogIndex);
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

  public int getLastCommitIndex() {
    return 1;
  }
}
