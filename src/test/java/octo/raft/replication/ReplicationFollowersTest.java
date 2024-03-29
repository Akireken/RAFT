package octo.raft.replication;

import octo.raft.entity.Entry;
import octo.raft.replication.ReplicationFollowers;
import octo.raft.replication.Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReplicationFollowersTest {

  // Le leader retente d'envoyer un message à un follower tant qu'il n'est pas up
  // Le leader envoit un heartbeat à interval régulier
  // Après avoir reçu un rejet du follower, le leader tente d'envoyer le message précédent

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé sur un cluster de 3 noeuds, il réplique sur chaque follower et notifie que le message est safely replicated")
  void name() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(true);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    verify(repository).send("follower1", entry, 1, 0, 0, 0);
    verify(repository).send("follower2", entry, 1, 0, 0, 0);
    assertTrue(isSafelyReplicated);
  }

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé sur un cluster de 3 noeuds et que le message n'est pas répliqué sur le quorum, il notifie que le message n'est pas safely replicated")
  void name2() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(false);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(false);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    assertFalse(isSafelyReplicated);
  }

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé sur un cluster de 3 noeuds et que le message est répliqué sur le quorum, il notifie que le message est safely replicated")
  void name3() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(false);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    assertTrue(isSafelyReplicated);
  }

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé sur un cluster de 5 noeuds et que le message n'est pas répliqué sur le quorum (3 noeuds en comptant le leader), il notifie que le message n'est safely replicated")
  void name4() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2", "follower3", "follower4");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(false);
    when(repository.send("follower3", entry, 1, 0, 0, 0)).thenReturn(false);
    when(repository.send("follower4", entry, 1, 0, 0, 0)).thenReturn(false);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    assertFalse(isSafelyReplicated);
  }

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé sur un cluster de 5 noeuds et que le message est répliqué sur le quorum (3 noeuds en comptant le leader), il notifie que le message est safely replicated")
  void name5() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2", "follower3", "follower4");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower3", entry, 1, 0, 0, 0)).thenReturn(false);
    when(repository.send("follower4", entry, 1, 0, 0, 0)).thenReturn(false);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    assertTrue(isSafelyReplicated);
  }

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé, il contacte les différents followers en parallèle")
  void name6() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    when(repository.send("follower1", entry, 1, 0, 0, 0)).thenReturn(true);
    when(repository.send("follower2", entry, 1, 0, 0, 0)).thenReturn(true);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean isSafelyReplicated = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    assertTrue(isSafelyReplicated);
  }

  @Test
  @DisplayName("Dans un cluster de 5 noeuds avec 4 follower, quand chaque follower met 100ms à répondre, la réplication en parallèle retourne le résultat en moins en 150 millisecondes")
  void name7() {
    // Given
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2", "follower3", "follower4");
    Repository repository = mock(Repository.class);
    when(repository.send(anyString(), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
      sleep(100);
      return true;
    });
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    // When
    long start = System.currentTimeMillis();
    replicationFollowers.replicate(entry, 1, 0, 0, 0);

    // Then
    long end = System.currentTimeMillis();
    assertTrue(end - start < 150);
  }

  @Test
  @DisplayName("Dans un cluster de 3 noeuds avec 2 followers, quand 1 followers met 100ms à répondre et que le dernier met 200ms, la réplication en parallèle retourne le résultat en moins en 150 millisecondes")
  void name8() {
    // Given
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    when(repository.send(eq("follower1"), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
      sleep(100);
      return true;
    });
    when(repository.send(eq("follower2"), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
      sleep(200);
      return true;
    });
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    // When
    long start = System.currentTimeMillis();
    replicationFollowers.replicate(entry, 1, 0, 0, 0);

    // Then
    long end = System.currentTimeMillis();
    assertTrue(end - start < 150);
  }

  @Test
  @DisplayName("Dans un cluster de 3 noeuds avec 2 followers, quand le preimier follower met 100ms à répondre et que le dernier met 500ms, la réplication en parallèle s'effectue totalement après avoir atteint le quorum")
  void name9() throws InterruptedException {
    // Given
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("follower1", "follower2");
    Repository repository = mock(Repository.class);
    AtomicInteger nbEffectivesReplications = new AtomicInteger(0);
    when(repository.send(eq("follower1"), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
      sleep(100);
      nbEffectivesReplications.getAndIncrement();
      return true;
    });
    when(repository.send(eq("follower2"), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
      sleep(500);
      nbEffectivesReplications.getAndIncrement();
      return true;
    });
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    // When
    replicationFollowers.replicate(entry, 1, 0, 0, 0);

    // Then
    sleep(550);
    assertEquals(2, nbEffectivesReplications.get());
  }

}