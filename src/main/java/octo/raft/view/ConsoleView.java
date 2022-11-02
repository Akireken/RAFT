package octo.raft.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import octo.raft.Node;

import java.util.List;

public class ConsoleView {

    private long refreshFrequency = 500;

    public void start(List<Node> cluster) throws InterruptedException {
        while(true){
            String table = buildTableAsString(cluster);
            System.out.println(table);
            Thread.sleep(refreshFrequency);
            cleanConsole(cluster);
        }
    }

    private String buildTableAsString(List<Node> nodes){
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Leader", "Node", "Latest entry", "Entry count", "Current term").setTextAlignment(TextAlignment.CENTER);
        for(Node node : nodes){
            String leaderStr = isLeader(node, nodes) ? "->" : "";
            at.addRule();
            at.addRow(leaderStr, node.hashCode(), getLastEntry(node), node.getEntries().size(), node.getCurrentTerm());
        }
        at.addRule();
        return at.render();
    }

    private boolean isLeader(Node node, List<Node> cluster) {
        Node leader = node;
        for(Node other : cluster){
            if(other.getEntries().size() > leader.getEntries().size()){
                leader = other;
            }
        }
        return node.equals(leader);
    }

    private String getLastEntry(Node node){
        int size = node.getEntries().size();
        if(size == 0) return "";
        return node.getEntries().get(size - 1).getValue();
    }

    private void cleanConsole(List<Node> nodes){
        int lineCount = (nodes.size() * 2) + 3;
        System.out.print(String.format("\033[%dA",lineCount)); // Move up of table
        System.out.print("\033[2K"); // Erase line content
    }
}
