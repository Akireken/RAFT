package octo.raft;

import octo.raft.entity.Entry;
import octo.raft.replication.ReplicationFollowers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcceptClientMessageTest {

  private final ReplicationFollowers replicationFollowers = mock(ReplicationFollowers.class);

  // Le leader réplique le message reçu d'un client à tous les followers
  // Le leader timeout après un certain temps s'il n'a pas une réplication sur le quorum
  // Le leader envoit un heartbeat à interval régulier
  // Après avoir reçu un rejet du follower, le leader tente d'envoyer le message précédent
  // Les messages sont répliqués et commités dans l'ordre exact de la reception du leader

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, il l'enregistre au terme courant dans ses entries")
  void test1() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message = "pizza1";
    Entry entry = new Entry(message, 1);
    int leaderTerm = 1;
    int prevLogIndex = 0;
    int prevLogTerm = 0;
    int lastCommitIndex = 0;
    when(replicationFollowers.replicate(entry,leaderTerm,prevLogIndex,prevLogTerm,lastCommitIndex)).thenReturn(true);

    // When
    node.acceptClientMessage(message);

    // Then
    Entry expectedEntry = new Entry(message, 1);
    assertEquals(List.of(expectedEntry), node.getEntries());
  }

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, et qu'il n'a pas été acquité par le quorum, il ne le commit pas")
  void test2() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message = "pizza1";
    Entry entry = new Entry(message, 1);
    int leaderTerm = 1;
    int prevLogIndex = 0;
    int prevLogTerm = 0;
    int lastCommitIndex = 0;
    when(replicationFollowers.replicate(entry,leaderTerm,prevLogIndex, prevLogTerm,lastCommitIndex)).thenReturn(false);

    // When
    node.acceptClientMessage(message);

    // Then
    assertEquals(0, node.getLastCommitIndex());
  }

  @Test
  @DisplayName("Quand le node est un follower je renvoie une exception lorsqu'on me demande d'accepter un message")
  void test3() {
    // Given
    Node node = new Node(1, false, replicationFollowers);
    String message = "pizza1";

    // When then
    try {
      node.acceptClientMessage(message);
      fail();
    } catch (Exception e) {
      assertEquals("Je ne peux pas accepter de message, je ne suis pas leader", e.getMessage());
    }
  }

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, il l'envoit à tous les followers")
  void test4() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message = "pizza1";
    Entry entry = new Entry(message, 1);
    int leaderTerm = 1;
    int prevLogIndex = 0;
    int prevLogTerm = 0;
    int lastCommitIndex = 0;
    when(replicationFollowers.replicate(entry,leaderTerm,prevLogIndex,prevLogTerm,lastCommitIndex)).thenReturn(true);

    // When
    node.acceptClientMessage(message);

    // Then
    Entry expectedEntry = new Entry(message, 1);
    verify(replicationFollowers).replicate(expectedEntry, leaderTerm, prevLogIndex, prevLogTerm, lastCommitIndex);
  }

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, et qu'il a été acquitté par le quorum, il le commit")
  void test5() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message = "pizza1";
    Entry entry = new Entry(message, 1);
    int leaderTerm = 1;
    int prevLogIndex = 0;
    int prevLogTerm = 0;
    int lastCommitIndex = 0;
    when(replicationFollowers.replicate(entry,leaderTerm,prevLogIndex,prevLogTerm,lastCommitIndex)).thenReturn(true);

    // When
    node.acceptClientMessage(message);

    // Then
    assertEquals(1, node.getLastCommitIndex());
  }

  @Test
  @DisplayName("Quand un leader accepte trois messages d'un client, et qu'ils ont été acquittés par le quorum, il les commitent")
  void test6() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message1 = "pizza1";
    String message2 = "pizza2";
    String message3 = "pizza3";
    Entry entry1 = new Entry(message1, 1);
    Entry entry2 = new Entry(message2, 1);
    Entry entry3 = new Entry(message3, 1);
    when(replicationFollowers.replicate(entry1, 1, 0, 0, 0)).thenReturn(true);
    when(replicationFollowers.replicate(entry2, 1, 1, 1, 1)).thenReturn(true);
    when(replicationFollowers.replicate(entry3, 1, 2, 1, 2)).thenReturn(true);
    node.acceptClientMessage(message1);
    node.acceptClientMessage(message2);

    // When
    node.acceptClientMessage(message3);

    // Then
    assertEquals(3, node.getLastCommitIndex());
  }

  @Test
  @DisplayName("Quand un leader accepte trois messages d'un client, et que seuls les deux premiers messages sont acquittés par le quorum, il ne commit que les deux premiers messages")
  void test7() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message1 = "pizza1";
    String message2 = "pizza2";
    String message3 = "pizza3";
    Entry entry1 = new Entry(message1, 1);
    Entry entry2 = new Entry(message2, 1);
    Entry entry3 = new Entry(message3, 1);
    when(replicationFollowers.replicate(entry1, 1, 0, 0, 0)).thenReturn(true);
    when(replicationFollowers.replicate(entry2, 1, 1, 1, 1)).thenReturn(true);
    when(replicationFollowers.replicate(entry3, 1, 2, 1, 2)).thenReturn(false);
    node.acceptClientMessage(message1);
    node.acceptClientMessage(message2);

    // When
    node.acceptClientMessage(message3);

    // Then
    assertEquals(2, node.getLastCommitIndex());
  }

  @Test
  @DisplayName("Quand un leader n'arrive pas a repliquer un message sur le quorum, il envoie un erreur")
  void test8() {
    // given
    Node node = new Node(1, true, replicationFollowers);
    String message1 = "pizza1";
    Entry entry1 = new Entry(message1, 1);
    when(replicationFollowers.replicate(entry1, 1, 0, 0, 0)).thenReturn(false);

    // when/then
    boolean status = node.acceptClientMessage(message1);

    // Then
    assertFalse(status);
  }

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, et qu'il a été acquitté par le quorum, il acquitte le message")
  void test9() {
    // Given
    Node node = new Node(1, true, replicationFollowers);
    String message = "pizza1";
    Entry entry = new Entry(message, 1);
    when(replicationFollowers.replicate(entry, 1, 0, 0, 0)).thenReturn(true);

    // When
    boolean status = node.acceptClientMessage(message);

    // Then
    assertTrue(status);
  }


}
