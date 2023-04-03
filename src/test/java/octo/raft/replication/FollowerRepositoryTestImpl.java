package octo.raft.replication;

import octo.raft.Node;
import octo.raft.entity.Entry;

import java.util.Map;

public class FollowerRepositoryTestImpl implements FollowerRepository {

    private final Map<String, Node> nodesList;

    public FollowerRepositoryTestImpl(int currentTerm) {

        this.nodesList = Map.of(
            "follower1", new Node(currentTerm, false, null),
            "follower2", new Node(currentTerm, false, null));
    }

    @Override
    public boolean send(String nodeName, Entry entry, int leaderTerm, int prevLogIndex, int prevLogTerm, int lastCommitIndex) {
        Node node = retrieveNode(nodeName);
        return node.appendEntries(entry, leaderTerm, prevLogIndex, prevLogTerm, lastCommitIndex).getStatus();
    }

    public Node retrieveNode(String follower) {
        return nodesList.get(follower);
    }
}
