package octo.raft;

import octo.raft.entity.Entry;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;

public class Main {

  private static final int DEFAULT_FOR_NOW = 0;
  private static Random rand = new Random();

  public static void main(String[] args) throws InterruptedException {
//    ReplicationFollowers replicationFollowers = new ReplicationFollowers();
//
//    Node node1 = new Node(1, false, replicationFollowers);
//    Node node2 = new Node(1, false, replicationFollowers);
//    Node node3 = new Node(1, false, replicationFollowers);
//    List<Node> cluster = List.of(node1, node2, node3);
//
//    Executors.newCachedThreadPool().execute(() -> {
//      doStuff(cluster);
//    });
//
//    ConsoleView view = new ConsoleView();
//    view.start(cluster);
  }

  private static void doStuff(List<Node> cluster) {
    while (true) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      String generatedString = RandomStringUtils.randomAlphanumeric(10);
      Entry entry = new Entry(generatedString, 0);
      int term = rand.nextInt(2022);
      for (Node node : cluster) {
        if (rand.nextInt(10) >= 1) {
          // TODO : prevlogindex par defaut
          node.appendEntries(entry, term, DEFAULT_FOR_NOW, 0, 0);
        }
      }
    }
  }
}