package octo.raft.replication;

import octo.raft.entity.Entry;

public interface Repository {

  boolean send(String node, Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex);
}
