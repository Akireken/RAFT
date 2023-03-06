package octo.raft.view;

import octo.raft.Entry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReplicationITest {

    @Test
    @DisplayName("Dans un cluster de 5 noeuds avec 4 follower, quand chaque follower met 100ms à répondre, la réplication en parallèle retourne le résultat en moins en 150 millisecondes")
    void name() {
        // Given
        Entry entry = new Entry("pizza1", 1);
        List<String> followers = List.of("follower1", "follower2", "follower3", "follower4");
        Repository repository = mock(Repository.class);
        when(repository.send(anyString(), any(), eq(1), eq(0), eq(0), eq(0))).then(invocation -> {
            Thread.sleep(100);
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



}
