package octo.raft;

import octo.raft.view.ReplicationRepository;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private int currentTerm;
  private final List<Entry> entries;
  private int lastCommitIndex = 0;
  private boolean isLeader;
  private final ReplicationRepository replicationRepository;

  public Node(int currentTerm, boolean isLeader, ReplicationRepository replicationRepository) {
    this.currentTerm = currentTerm;
    this.replicationRepository = replicationRepository;
    this.entries = new ArrayList<>();
    this.isLeader = isLeader;
  }

  public boolean acceptClientMessage(String message){
    if(!isLeader){
      throw new RuntimeException("Je ne peux pas accepter de message, je ne suis pas leader");
    }
    Entry newEntry = new Entry(message, this.currentTerm);
    entries.add(newEntry);
    if(replicationRepository.replicate(newEntry)){
      lastCommitIndex++;
      return true;
    } else {
      return false;
    }
  }

  public AppendEntryResult appendEntries(Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
    if (leaderTerm < this.currentTerm) {
      return new AppendEntryResult(false, this.currentTerm);
    }
    if(isPreviousEntryUnknown(prevLogIndex, prevLogTerm)){
      return new AppendEntryResult(false, this.currentTerm);
    }
    if (!entry.isHeartbeat()) {
      appendOrReplace(entry, prevLogIndex);
    }
    this.lastCommitIndex = Math.min(lastCommitIndex, this.getLastIndex());
    this.currentTerm = leaderTerm;
    return new AppendEntryResult(true, leaderTerm);
  }

  private boolean isPreviousEntryUnknown(int prevLogIndex, int prevLogTerm) {
    return isPrevLogIndexUnknown(prevLogIndex) || (isNotFirstEntry(prevLogIndex) && isPrevLogTermUnKnown(prevLogTerm, prevLogIndex));
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
    return prevLogIndex > getLastIndex();
  }

  private boolean isExistingEntry(int prevLogIndex) {
    return !(prevLogIndex >= getLastIndex());
  }

  private int getLastIndex() {
    return this.entries.size();
  }

  public int getCurrentTerm() {
    return this.currentTerm;
  }

  public List<Entry> getEntries() {
    return this.entries;
  }

  public int getLastCommitIndex() {
    return this.lastCommitIndex;
  }
}
