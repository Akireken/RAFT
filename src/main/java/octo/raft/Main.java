package octo.raft;

import octo.raft.view.ConsoleView;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class Main {

  private static Random rand = new Random();

  public static void main(String[] args) throws InterruptedException {
    Node node1 = new Node(1);
    Node node2 = new Node(1);
    Node node3 = new Node(1);
    List<Node> cluster = List.of(node1, node2, node3);

    Executors.newCachedThreadPool().execute(() -> {
        doStuff(cluster);
    });

    ConsoleView view = new ConsoleView();
    view.start(cluster);
  }

  private static void doStuff(List<Node> cluster) {
    while(true){
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      String generatedString = RandomStringUtils.randomAlphanumeric(10);
      Entries entry = new Entries(generatedString);
      int term = rand.nextInt(2022);
      for(Node node : cluster){
        if(rand.nextInt(10)>=1){
          node.appendEntries(entry, term);
        }
      }
    }
  }
}