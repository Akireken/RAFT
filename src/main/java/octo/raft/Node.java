package octo.raft;

import octo.raft.entity.AppendEntryResult;
import octo.raft.entity.Entry;
import octo.raft.replication.ReplicationFollowers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Node {
  private int currentTerm;
  private final List<Entry> entries;
  private int lastCommitIndex = 0;
  private boolean isLeader;
  private final ReplicationFollowers replicationFollowers;

  private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

  public Node(int currentTerm, boolean isLeader, ReplicationFollowers replicationFollowers) {
    this.currentTerm = currentTerm;
    this.replicationFollowers = replicationFollowers;
    this.entries = new ArrayList<>();
    this.isLeader = isLeader;
  }

  public boolean acceptClientMessage(String message){
    if(!isLeader){
      throw new RuntimeException("Je ne peux pas accepter de message, je ne suis pas leader");
    }
    int prevLogTerm = getPrevLogTerm();
    int prevLogIndex = getLastIndex();
    Entry newEntry = new Entry(message, this.currentTerm);
    entries.add(newEntry);
    if(replicationFollowers.replicate(newEntry, currentTerm, prevLogIndex, prevLogTerm, lastCommitIndex)){
      lastCommitIndex++;
      return true;
    } else {
      return false;
    }
  }

  private int getPrevLogTerm() {
    Entry lastEntry = getLastEntry();
    if (lastEntry != null) {
      return lastEntry.getTerm();
    }
    return 0;
  }

  private Entry getLastEntry() {
    Entry entry = null;
    if(entries.size() > 0) {
      entry = entries.get(entries.size() - 1);
    }
    return entry;
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

  public void initHeartbeat(int i)  {
    scheduledExecutorService.scheduleAtFixedRate(() -> {
      if (isLeader) {
        System.out.println("hey");
        replicationFollowers.replicate(new Entry(), currentTerm, getLastIndex(), getPrevLogTerm(), lastCommitIndex);
      }
    }, 100, i, TimeUnit.MILLISECONDS);
  }
}
