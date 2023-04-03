package octo.raft.replication;

import octo.raft.Node;
import octo.raft.entity.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReplicationITest {
    // en cours de rédaction
    @Test
    @DisplayName("Quand le leader réplique un message, les followers ont enregistré le message et le message est commité")
    public void test() throws InterruptedException {
        RepositoryTestImpl testRepository = new RepositoryTestImpl(1);
        Node leader = new Node(1, true, new ReplicationFollowers(
            List.of("follower1", "follower2"),
            testRepository));

        leader.acceptClientMessage("pizza1");

        sleep(100);
        List<Entry> entriesNode1 = testRepository.retrieveNode("follower1").getEntries();
        List<Entry> entriesNode2 = testRepository.retrieveNode("follower2").getEntries();
        Entry expectedEntry = new Entry("pizza1", 1);
        assertEquals(List.of(expectedEntry), entriesNode1);
        assertEquals(List.of(expectedEntry), entriesNode2);
        assertEquals(leader.getLastCommitIndex(), 1);
    }

    @Test
    @DisplayName("Quand tous les noeud répondent en erreur, alors le messaged n'est pas commité")
    public void test2() throws InterruptedException {
        RepositoryTestImpl testRepository = new RepositoryTestImpl(2);
        Node leader = new Node(1, true, new ReplicationFollowers(
          List.of("follower1", "follower2"),
          testRepository));

        leader.acceptClientMessage("pizza1");

        sleep(100);
        List<Entry> entriesNode1 = testRepository.retrieveNode("follower1").getEntries();
        List<Entry> entriesNode2 = testRepository.retrieveNode("follower2").getEntries();
        assertTrue(entriesNode1.isEmpty());
        assertTrue(entriesNode2.isEmpty());
        assertEquals(leader.getLastCommitIndex(), 0);
    }
}
