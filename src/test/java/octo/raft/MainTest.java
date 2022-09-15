package octo.raft;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
// Quand je recois un message avec un index déjà existant, je l'écrase, je renvois succes et je renvois le terme actuel
// Quand je recois un message avec un index précédent que je ne connais pas, (j'ai la commande 1 2 et 3, et je recois 5 avec un prevLogIndex à 4), je renvois false
// Quand je recois un message commité 10 et que je contient 10 messages, j'update mon commit à 10

  // la suite pour les message commit en rattrapage




  @DisplayName("Quand je recois un heartbeat (message sans entries), je renvois succes, je renvois le term actuel  et je ne stock rien")
  @Test
  void testHeartBeat() {
    // given
    Node node = new Node(0);

    // when
    Result result = node.appendEntries(new Entries(), 0);

    // then
    assertTrue(result.getStatus());
    assertEquals(result.getTerm(), 0);
  }

  @DisplayName("Quand je recois un heartbeat  (message sans entries) d'un term précédent, je renvois false et je renvois le term actuel")
  @Test
  void testHeartBeatInvalide() {
    // given
    Node node = new Node(1);

    // when
    Result result = node.appendEntries(new Entries(),0);

    // then
    assertFalse(result.getStatus());
    assertEquals(result.getTerm(), 1);
  }

  @DisplayName("Quand je recois un heartbeat  (message sans entries) avec un term plus à jour, je renvois true et j'update le term du noeud et je renvois le term à jour")
  @Test
  void testHeartBeat3() {
    // given
    Node node = new Node(1);

    // when
    Result result = node.appendEntries(new Entries(),2);

    // then
    assertTrue(result.getStatus());
    assertEquals(result.getTerm(), 2);
    assertEquals(node.getCurrentTerm(), 2);
  }

  @DisplayName("Quand je reçois une entry contenant une valeur, on stocke la valeur")
  @Test
  void testReceptionEntry() {
    // given
    Node node = new Node(1);

    // when
    node.appendEntries(new Entries("pizza"),2);

    // then
    assertEquals(node.getEntries(), List.of("pizza"));
  }

  @DisplayName("Quand je reçois un heartbeat je ne touche pas aux entries")
  @Test
  void testHeartbeatNeModifiePasLesEntries() {
    // given
    Node node = new Node(1);

    // when
    node.appendEntries(new Entries(), 1);

    // then
    assertTrue(node.getEntries().isEmpty());
  }

  @DisplayName("Quand je reçois une entry avec un term périmé, je renvois false et je n'enregistre pas l'entry")
  @Test
  void test10() {
    // given
    Node node = new Node(2);

    // when
    Result result = node.appendEntries(new Entries("pizza"), 1);

    // then
    assertFalse(result.getStatus());
    assertTrue(node.getEntries().isEmpty());
  }

}