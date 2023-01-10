package octo.raft;

import octo.raft.view.ReplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AcceptMessageTest {

  private ReplicationRepository replicationRepository = mock(ReplicationRepository.class);

  // Le leader réplique le message reçu d'un client à tous les followers
  // Le leader retente d'envoyer un message à un follower tant qu'il n'est pas up
  // Le leader acquitte le message quand il est répliqué sur le quorum
  // Le leader commit le message dernier message répliqué sur le quorum au le cluster
  // Le leader timeout après un certain temps s'il il n'a pas une réplication du message sur le quorum
  // Le leader envoit un heartbeat à interval régulier
  // Après avoir reçu un rejet du follower, le leader tente d'envoyer le message précédent
  // Les messages répliqué et commité dans l'ordre exact de la reception sur le leader


  @Test
  @DisplayName("Quand un leader accepte un message d'un client, il l'enregistre au terme courant dans ses entries")
  void test1() {
    // Given
    Node node = new Node(1, true, replicationRepository);
    String message = "pizza1";

    // When
    node.acceptMessage(message);

    // Then
    Entry expectedEntry = new Entry(message, 1);
    assertEquals(List.of(expectedEntry), node.getEntries());
  }

  @Test
  @DisplayName("Quand un leader accepte un message d'un client, et qu'il n'a pas été acquité par le quorum, il ne le commit pas")
  void test2() {
    // Given
    Node node = new Node(1, true, replicationRepository);
    String message = "pizza1";

    // When
    node.acceptMessage(message);

    // Then
    assertEquals(0, node.getLastCommitIndex());
    verify(replicationRepository, times(0)).replicate();
  }

  @Test
  @DisplayName("Quand je suis un follower je renvoie une exception lorsqu'on me demande d'accepter un message")
  void test3() {
    // Given
    Node node = new Node(1, false, replicationRepository);
    String message = "pizza1";

    // When then
    try {
      node.acceptMessage(message);
      fail();
    } catch (Exception e) {
      assertEquals("Je ne peux pas accepter de message, je ne suis pas leader", e.getMessage());
    }

  }
}
