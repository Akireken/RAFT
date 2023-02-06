package octo.raft.view;

import octo.raft.Entry;

import java.util.List;

public class ReplicationFollowers {
  private List<String> followers;
  private Repository repository;

  public ReplicationFollowers(List<String> followers, Repository repository) {
    this.followers = followers;
    this.repository = repository;
  }

  public boolean replicate(Entry expectedEntry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
    for (String follower : followers) {
      if (!repository.send(follower, expectedEntry)) {
        return false;
      }
    }
    return true;  }
}
