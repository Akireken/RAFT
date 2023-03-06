package octo.raft.replication;

import octo.raft.Node;
import octo.raft.entity.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplicationITest {


    @Test
    @DisplayName("Quand le leader réplique un message, les followers ont enregistré le message")
    public void test() throws InterruptedException {
        RepositoryTestImpl testRepository = new RepositoryTestImpl();
        Node leader = new Node(1, true, new ReplicationFollowers(
            List.of("follower1", "follower2"),
            testRepository));

        leader.acceptClientMessage("pizza1");

        sleep(100);
        List<Entry> entries1 = testRepository.retrieveNode("follower1").getEntries();
        List<Entry> entries2 = testRepository.retrieveNode("follower2").getEntries();
        Entry expectedEntry = new Entry("pizza1", 1);
        assertEquals(List.of(expectedEntry), entries1);
        assertEquals(List.of(expectedEntry), entries2);
    }
}
