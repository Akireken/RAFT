package octo.raft.replication;

import octo.raft.entity.Entry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class ReplicationFollowers {
  private static final int LEADER_SUCCESS = 1;
  private final List<String> followers;
  private final FollowerRepository followerRepository;
  private final ExecutorService threadpool;

  public ReplicationFollowers(List<String> followers, FollowerRepository followerRepository) {
    this.followers = followers;
    this.followerRepository = followerRepository;
    this.threadpool = Executors.newCachedThreadPool();
  }

  public boolean replicate(Entry expectedEntry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
    AtomicInteger nbSuccess = new AtomicInteger(0);
    AtomicInteger nbFailures = new AtomicInteger(0);
    for (String follower : followers) {
      threadpool.execute(() -> {
        if (followerRepository.send(follower, expectedEntry, leaderTerm, prevLogIndex, prevLogTerm, lastCommitIndex)) {
          nbSuccess.getAndIncrement();
        } else{
          nbFailures.getAndIncrement();
        }
      });
  }
    while (!isReplicatedInQuorum(nbSuccess) && nbFailures.get() + nbSuccess.get() < followers.size() ) {
      try {
        sleep(1);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    threadpool.shutdown();

    return isReplicatedInQuorum(nbSuccess);
  }

  private boolean isReplicatedInQuorum(AtomicInteger nbSuccess) {
    return nbSuccess.get() + LEADER_SUCCESS >= getQuorum();
  }

  private int getQuorum() {
    return getClusterSize() / 2 + 1;
  }

  private int getClusterSize() {
    return followers.size() + 1;
  }
}
