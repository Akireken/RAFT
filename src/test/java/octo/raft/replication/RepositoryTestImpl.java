package octo.raft.replication;

import octo.raft.Node;
import octo.raft.entity.Entry;

import java.util.Map;

public class RepositoryTestImpl implements Repository {

    private final Map<String, Node> nodesList;

    public RepositoryTestImpl() {

        this.nodesList = Map.of(
            "follower1", new Node(1, false, null),
            "follower2", new Node(1, false, null));
    }

    @Override
    public boolean send(String node, Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {

        return false;
    }

    public Node retrieveNode(String follower) {
        return nodesList.get(follower);
    }
}
