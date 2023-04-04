package octo.raft;

import octo.raft.entity.Entry;
import octo.raft.replication.FollowerRepository;
import octo.raft.replication.ReplicationFollowers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HeartbeatTest {

  @Test
  @DisplayName("quand le leader envoie un heartbeat, alors le follower reçoit un heartbeat")
  void name() throws InterruptedException {
    // Given
    FollowerRepository followerRepository = mock(FollowerRepository.class);
    Node node = new Node(1, true,
      new ReplicationFollowers(List.of("follower1", "follower2"), followerRepository));

    // When
    node.initHeartbeat(100);

    //Then
    sleep(150);
    Entry heartbeat = new Entry();
    verify(followerRepository).send("follower1", heartbeat, 1, 0, 0, 0);
    verify(followerRepository).send("follower2", heartbeat, 1, 0, 0, 0);
  }

  @Test
  @DisplayName("quand le leader initialise les heartsbeats, alors les followers en reçoivent à interval régulier")
  void name2() throws InterruptedException {
    // Given
    FollowerRepository followerRepository = mock(FollowerRepository.class);
    Entry heartbeat = new Entry();

    Node node = new Node(1, true,
      new ReplicationFollowers(List.of("follower1", "follower2"), followerRepository));

    // When
    node.initHeartbeat(1000);

    //Then
    sleep(15000);
    verify(followerRepository,atLeast(2)).send("follower1", heartbeat, 1, 0, 0, 0);
    verify(followerRepository, atLeast(2)).send("follower2", heartbeat, 1, 0, 0, 0);
  }

  @Test
  @DisplayName("quand le leader reçoit un heartbeat, alors il réinitialise son timer")
  void tes() throws InterruptedException {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(()-> System.out.println("hey"), 0, 200, TimeUnit.MILLISECONDS);
    sleep(13000);
  }
}
