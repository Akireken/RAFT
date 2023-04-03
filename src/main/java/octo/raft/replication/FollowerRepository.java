package octo.raft.replication;

import octo.raft.entity.Entry;

public interface FollowerRepository {

  boolean send(String node, Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex);
}
