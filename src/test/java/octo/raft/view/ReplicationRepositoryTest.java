package octo.raft.view;

import octo.raft.Entry;
import octo.raft.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplicationRepositoryTest {

  // Le leader réplique le message reçu d'un client à tous les followers
  // Le leader retente d'envoyer un message à un follower tant qu'il n'est pas up


  @Test
  @DisplayName("Qu")
  void name() {
    ReplicationRepository repository = new ReplicationRepository();
    Entry entry = new Entry("pizza1", 1);
    Node node1 = new Node(1, false, new ReplicationRepository());
    Node node2 = new Node(1, false, new ReplicationRepository());

    repository.replicate(entry);


  }
}