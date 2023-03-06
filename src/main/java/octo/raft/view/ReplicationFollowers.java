package octo.raft.view;

import octo.raft.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class ReplicationFollowers {
  private final List<String> followers;
  private final Repository repository;
  private final ExecutorService threadpool;

  public ReplicationFollowers(List<String> followers, Repository repository) {
    this.followers = followers;
    this.repository = repository;
    this.threadpool = Executors.newCachedThreadPool();
  }

  public boolean replicate(Entry expectedEntry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
    AtomicInteger nbSuccess = new AtomicInteger(1);
    for (String follower : followers) {
      threadpool.execute(() -> {
        if (repository.send(follower, expectedEntry, leaderTerm, prevLogIndex, prevLogTerm, lastCommitIndex)) {
          nbSuccess.getAndIncrement();
        }
      });
    }
    threadpool.shutdown();
    try {
      threadpool.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return isReplicatedInQuorum(nbSuccess);
  }

  private boolean isReplicatedInQuorum(AtomicInteger nbSuccess) {
    return nbSuccess.get() >= (followers.size() + 1) / 2 + 1;
  }
}
