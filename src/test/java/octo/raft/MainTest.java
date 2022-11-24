package octo.raft;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {


// Quand un client me demande un état, je renvois l'état de tout les logs commités
// T0D0 : comprendre à quoi sert la condition !isNewEntry(prevLogIndex)


  @DisplayName("Quand je recois un heartbeat (message sans entries), je renvois succes, je renvois le term actuel  et je ne stock rien")
  @Test
  void testHeartBeat() {
    // given
    Node node = new Node(0);

    // when
    Result result = node.appendEntries(new Entry(), 0, 0, 0, 0);

    // then
    assertTrue(result.getStatus());
    assertEquals(result.getTerm(), 0);
  }

  @DisplayName("Quand je n'ai pas de message et que j'ajoute un message avec un prevLogIndex différent de 0, je renvoie false")
  @Test
  void testPrevLogIndexInitialInvalid() {
    // given
    Node node = new Node(0);
    Entry pizza = new Entry("pizza", 0);

    // when
    Result result = node.appendEntries(pizza, 0, 1, 0, 0);

    // then
    assertFalse(result.getStatus());
  }

  @DisplayName("Quand je recois un heartbeat  (message sans entries) d'un term précédent, je renvois false et je renvois le term actuel")
  @Test
  void testHeartBeatInvalide() {
    // given
    Node node = new Node(1);

    // when
    Result result = node.appendEntries(new Entry(), 0, 0, 0, 0);

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
    Result result = node.appendEntries(new Entry(null, 0), 2, 0, 0, 0);

    // then
    assertTrue(result.getStatus());
    assertEquals(result.getTerm(), 2);
    assertEquals(node.getCurrentTerm(), 2);
  }

  @DisplayName("Quand je recois une entry  avec un term plus à jour, je renvois true et j'update le term du noeud et je renvois le term à jour")
  @Test
  void testMiseAJourTerm3() {
    // given
    Node node = new Node(1);

    // when
    Result result = node.appendEntries(new Entry("pizza1", 0), 2, 0, 0, 0);

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
    Entry pizza = new Entry("pizza", 0);
    node.appendEntries(pizza, 2, 0, 0, 0);

    // then
    assertEquals(node.getEntries(), List.of(pizza));
  }

  @DisplayName("Quand je reçois un heartbeat je ne touche pas aux entries")
  @Test
  void testHeartbeatNeModifiePasLesEntries() {
    // given
    Node node = new Node(1);

    // when
    node.appendEntries(new Entry(), 1, 0, 0, 0);

    // then
    assertTrue(node.getEntries().isEmpty());
  }

  @DisplayName("Quand je reçois une entry avec un term périmé, je renvois false et je n'enregistre pas l'entry")
  @Test
  void test10() {
    // given
    Node node = new Node(2);

    // when
    Result result = node.appendEntries(new Entry("pizza", 0), 1, 0, 0, 0);

    // then
    assertFalse(result.getStatus());
    assertTrue(node.getEntries().isEmpty());
  }

  @DisplayName("Quand je recois un message avec un index qui correspond au premier message existant, je l'écrase, je renvoie succes")
  @Test
  void test11() {
    // given
    Node node = new Node(2);
    Entry pizza = new Entry("pizza", 0);
    node.appendEntries(pizza, 2, 0, 0, 0);
    Entry pomme = new Entry("pomme", 0);

    // when
    Result result = node.appendEntries(pomme, 2, 0, 0, 0);

    // then
    assertEquals(1, node.getEntries().size());
    assertEquals(pomme.getValue(), node.getEntries().get(0).getValue());
    assertTrue(result.getStatus());
  }

  @DisplayName("Quand je reçois un message avec preLogIndex égal à 1 avec un liste d'entries de taille 4, j'écrase le message index 2 et je renvoie succes")
  @Test
  void test12() {
    // given
    Node node = new Node(2);
    Entry pizza1 = new Entry("pizza1", 0);
    Entry pizza2 = new Entry("pizza2", 0);
    Entry pizza3 = new Entry("pizza3", 0);
    Entry pizza4 = new Entry("pizza4", 0);
    node.appendEntries(pizza1, 2, 0, 0, 0);
    node.appendEntries(pizza2, 2, 1, 0, 0);
    node.appendEntries(pizza3, 2, 2, 0, 0);
    node.appendEntries(pizza4, 2, 3, 0, 0);
    Entry pomme = new Entry("pomme", 0);

    // when
    Result result = node.appendEntries(pomme, 2, 1, 0, 0);

    // then
    assertEquals(4, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
    assertEquals(pomme.getValue(), node.getEntries().get(1).getValue());
    assertEquals(pizza3.getValue(), node.getEntries().get(2).getValue());
    assertEquals(pizza4.getValue(), node.getEntries().get(3).getValue());
    assertTrue(result.getStatus());
  }

  @DisplayName("Quand je reçois 4 messages, liste d'entries est bonne et de taille 4")
  @Test
  void test13() {
    // given
    Node node = new Node(2);
    Entry pizza1 = new Entry("pizza1", 0);
    Entry pizza2 = new Entry("pizza2", 0);
    Entry pizza3 = new Entry("pizza3", 0);
    Entry pizza4 = new Entry("pizza4", 0);

    // when
    node.appendEntries(pizza1, 2, 0, 0, 0);
    node.appendEntries(pizza2, 2, 1, 0, 0);
    node.appendEntries(pizza3, 2, 2, 0, 0);
    node.appendEntries(pizza4, 2, 3, 0, 0);

    // then
    assertEquals(4, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
    assertEquals(pizza2.getValue(), node.getEntries().get(1).getValue());
    assertEquals(pizza3.getValue(), node.getEntries().get(2).getValue());
    assertEquals(pizza4.getValue(), node.getEntries().get(3).getValue());
  }

  //
  @DisplayName("Quand je recois un message avec un index précédent que je ne connais pas, (j'ai la commande 1 2 et je recois la commande 4 avec un prevLogIndex à 3), je renvois false")
  @Test
  void test14() {
    // given
    Node node = new Node(2);
    Entry pizza1 = new Entry("pizza1", 0);
    Entry pizza2 = new Entry("pizza2", 0);
    Entry pizza3 = new Entry("pizza3", 0);
    node.appendEntries(pizza1, 2, 0, 0, 0);
    node.appendEntries(pizza2, 2, 1, 0, 0);

    // when
    Result result = node.appendEntries(pizza3, 2, 3, 0, 0);

    // then
    assertFalse(result.getStatus());
    assertEquals(2, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
    assertEquals(pizza2.getValue(), node.getEntries().get(1).getValue());
  }

  @DisplayName("Quand je recois un message avec un index précédent que je connais (à 1), mais un prevLogTerm différent (à 1 au lieu de 0), je renvois false")
  @Test
  void test15() {
    // given
    Node node = new Node(2);
    Entry pizza1 = new Entry("pizza1", 0);
    node.appendEntries(pizza1, 2, 0, 0, 0);

    Entry pizza2 = new Entry("pizza2", 1);

    // when
    Result result = node.appendEntries(pizza2, 2, 1, 1, 0);

    // then
    assertFalse(result.getStatus());
    assertEquals(1, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
  }

  @DisplayName("Quand je recois un message avec un prevLogIndex que je connais (à 2), mais un prevLogTerm différent (à 2 au lieu de 1), je renvois false")
  @Test
  void test16() {
    // given
    Node node = new Node(0);
    Entry pizza1 = new Entry("pizza1",1);
    Entry pizza2 = new Entry("pizza2", 1);
    Entry pizza3 = new Entry("pizza3",2);

    node.appendEntries(pizza1, 1, 0, 0, 0);
    node.appendEntries(pizza2, 1, 1, 1, 0);

    // when
    Result result = node.appendEntries(pizza3, 2, 2, 2, 0);

    // then
    assertFalse(result.getStatus());
    assertEquals(2, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
    assertEquals(pizza2.getValue(), node.getEntries().get(1).getValue());
  }

  @DisplayName("Quand je dois remplacer un message existant, je dois vérifier que le message précédent matche le prevLogIndex et le prevLogTerm.")
  @Test
  void test17() {
    // given
    Node node = new Node(0);
    Entry pizza1 = new Entry("pizza1",1);
    Entry pizza2 = new Entry("pizza2", 1);
    Entry pizza3 = new Entry("pizza3",3);

    node.appendEntries(pizza1, 1, 0, 0, 0);
    node.appendEntries(pizza2, 1, 1, 1, 0);
    node.appendEntries(pizza3, 1, 2, 1, 0);

    Entry pizza2Bis = new Entry("pizza2Bis",3);

    // when
    Result result = node.appendEntries(pizza2Bis, 3, 1, 3, 0);

    // then
    assertFalse(result.getStatus());
    assertEquals(3, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0).getValue());
    assertEquals(pizza2.getValue(), node.getEntries().get(1).getValue());
    assertEquals(pizza3.getValue(), node.getEntries().get(2).getValue());
  }

  @Test
  @DisplayName("Quand le leader m'envoi un message avec un lastCommitIndex à 1, et que j'ai un message déjà stocké, je  recois un message commité 1, alors le lastCommitIndex du noeud passe à 1")
  public void test_de_premier_message_commit() {
    // given
    Node node = new Node(0);
    Entry message = new Entry("pizza1",0);
    node.appendEntries(message, 0, 0, 0, 0);

    // when
    Result result = node.appendEntries(new Entry(), 0, 1, 0, 1);

    // then
    assertEquals(node.getLastCommitIndex(), 1);
  }

  @Test
  @DisplayName("Quand le server démmare, lastCommitIndex est à 0")
  public void test18() {
    // when
    Node node = new Node(0);

    // then
    assertEquals(node.getLastCommitIndex(), 0);
  }

  @Test
  @DisplayName("Quand le lastCommitIndex est > au dernier logIndex alors le lastCommitIndex du noeud sera le dernier logIndex")
  public void test19() {
    // given
    Node node = new Node(0);
    Entry message = new Entry("pizza1",0);
    node.appendEntries(message, 0, 0, 0, 0);

    // when
    node.appendEntries(new Entry(), 0, 1, 0, 3);

    // then
    assertEquals(node.getLastCommitIndex(), 1);
  }

  @Test
  @DisplayName("Quand deux messages sont reçu avec un lastCommitIndex = 2, alors le lastCommitIndex du noeud doit être de 2")
  public void test20() {
    // given
    Node node = new Node(0);
    Entry message = new Entry("pizza1",0);
    Entry message2 = new Entry("pizza2",0);
    node.appendEntries(message, 0, 0, 0, 0);

    // when
    node.appendEntries(message2, 0, 1, 0, 2);;

    // then
    assertEquals(node.getLastCommitIndex(), 2);
  }

  @Test
  @DisplayName("Quand trois messages sont reçu avec un lastCommitIndex = 2, alors le lastCommitIndex du noeud doit être de 2")
  public void test21() {
    // given
    Node node = new Node(0);
    Entry message = new Entry("pizza1",0);
    Entry message2 = new Entry("pizza2",0);
    Entry message3 = new Entry("pizza3",0);
    node.appendEntries(message, 0, 0, 0, 0);
    node.appendEntries(message2, 0, 1, 0, 0);

    // when
    node.appendEntries(message3, 0, 2, 0, 2);

    // then
    assertEquals(node.getLastCommitIndex(), 2);
  }

  @Test
  @DisplayName("Quand on recois un message invalid pour cause de prevLogIndex invalid, le lastCommitIndex n'est pas mis à jour")
  public void test22() {
    // given
    Node node = new Node(0);
    Entry message = new Entry("pizza1",0);
    Entry message3 = new Entry("pizza3",0);
    node.appendEntries(message, 0, 0, 0, 0);

    // when
    Result result = node.appendEntries(message3, 0, 2, 0, 1);

    // then
    assertFalse(result.getStatus());
    assertEquals(node.getLastCommitIndex(), 0);
  }

}