package octo.raft.view;

import octo.raft.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReplicationFollowersTest {

  // Le leader réplique le message reçu d'un client à tous les followers
  // Le leader retente d'envoyer un message à un follower tant qu'il n'est pas up
  // todo : ne pas péter au moindre appel en erreur
  // Le leader connait le quorum
  // Le leader doit envoyer toutes les varaibles du contrat d'interface appendEntries

  @Test
  @DisplayName("Quand ReplicationFollowers est appelé, il réplique sur chaque noeud")
  void name() {
    Entry entry = new Entry("pizza1", 1);
    List<String> followers = List.of("node1", "node2");
    Repository repository = mock(Repository.class);
    when(repository.call("node1", entry)).thenReturn(true);
    when(repository.call("node2", entry)).thenReturn(true);
    ReplicationFollowers replicationFollowers = new ReplicationFollowers(followers, repository);

    boolean status = replicationFollowers.replicate(entry, 1, 0, 0, 0);

    verify(repository).call("node1", entry);
    verify(repository).call("node2", entry);
    assertTrue(status);
  }

}