package octo.raft;

import octo.raft.entity.Entry;
import octo.raft.replication.FollowerRepository;
import octo.raft.replication.ReplicationFollowers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    Node node = new Node(1, true,
      new ReplicationFollowers(List.of("follower1", "follower2"), followerRepository));

    // When
    node.initHeartbeat(100);

    //Then
    sleep(550);
    Entry heartbeat = new Entry();
    verify(followerRepository,times(5)).send("follower1", heartbeat, 1, 0, 0, 0);
    verify(followerRepository, times(5)).send("follower2", heartbeat, 1, 0, 0, 0);
  }
}
