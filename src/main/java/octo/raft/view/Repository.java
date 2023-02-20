package octo.raft.view;

import octo.raft.Entry;

public interface Repository {

  boolean send(String node, Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex);
}
