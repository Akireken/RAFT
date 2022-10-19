package octo.raft;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
  private static final int CURRENT_TERM = 2;
// Quand je recois un message avec un index précédent que je connais, mais un prevLogTerm différent, je renvois false
// Quand je recois un message commité 10 et que je contient 10 messages, j'update mon commit à 10
// Quand je revois un message commité à 10, que mon dernier message est 8, que last commit et 7, j'update le commit à 8
// Quand un client me demande un état, je renvois l'état de tout les logs commités
// la suite pour les message commit en rattrapage

// Quand je n'ai pas de message et que j'ajoute un message avec un prevLogIndex différent de 0, je renvoie false
// Quand je n'ai pas de message et que j'ajoute un message avec un prevLogTerm différent de 0, je renvoie false


  @DisplayName("Quand je recois un heartbeat (message sans entries), je renvois succes, je renvois le term actuel  et je ne stock rien")
  @Test
  void testHeartBeat() {
    // given
    Node node = new Node(0);

    // when
    Result result = node.appendEntries(new Entries(), 0, 0, 0);

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
    Result result = node.appendEntries(new Entries(), 0, 0, 0);

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
    Result result = node.appendEntries(new Entries(null), 2, 0, 0);

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
    Result result = node.appendEntries(new Entries("pizza1"), 2, 0, 0);

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
    node.appendEntries(new Entries("pizza"), 2, 0, 0);

    // then
    assertEquals(node.getEntries(), List.of("pizza"));
  }

  @DisplayName("Quand je reçois un heartbeat je ne touche pas aux entries")
  @Test
  void testHeartbeatNeModifiePasLesEntries() {
    // given
    Node node = new Node(1);

    // when
    node.appendEntries(new Entries(), 1, 0, 0);

    // then
    assertTrue(node.getEntries().isEmpty());
  }

  @DisplayName("Quand je reçois une entry avec un term périmé, je renvois false et je n'enregistre pas l'entry")
  @Test
  void test10() {
    // given
    Node node = new Node(2);

    // when
    Result result = node.appendEntries(new Entries("pizza"), 1, 0, 0);

    // then
    assertFalse(result.getStatus());
    assertTrue(node.getEntries().isEmpty());
  }

  @DisplayName("Quand je recois un message avec un index qui correspond au premier message existant, je l'écrase, je renvoie succes")
  @Test
  void test11() {
    // given
    Node node = new Node(CURRENT_TERM);
    Entries pizza = new Entries("pizza");
    node.appendEntries(pizza, CURRENT_TERM, 0, 0);
    Entries pomme = new Entries("pomme");

    // when
    Result result = node.appendEntries(pomme, CURRENT_TERM, 0, 0);

    // then
    assertEquals(1, node.getEntries().size());
    assertEquals(pomme.getValue(), node.getEntries().get(0));
    assertTrue(result.getStatus());
  }

  @DisplayName("Quand je reçois un message avec preLogIndex égal à 1 avec un liste d'entries de taille 4, j'écrase le message index 2 et je renvoie succes")
  @Test
  void test12() {
    // given
    Node node = new Node(CURRENT_TERM);
    Entries pizza1 = new Entries("pizza1");
    Entries pizza2 = new Entries("pizza2");
    Entries pizza3 = new Entries("pizza3");
    Entries pizza4 = new Entries("pizza4");
    node.appendEntries(pizza1, CURRENT_TERM, 0, 0);
    node.appendEntries(pizza2, CURRENT_TERM, 1, 0);
    node.appendEntries(pizza3, CURRENT_TERM, 2, 0);
    node.appendEntries(pizza4, CURRENT_TERM, 3, 0);
    Entries pomme = new Entries("pomme");

    // when
    Result result = node.appendEntries(pomme, CURRENT_TERM, 1, 0);

    // then
    assertEquals(4, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0));
    assertEquals(pomme.getValue(), node.getEntries().get(1));
    assertEquals(pizza3.getValue(), node.getEntries().get(2));
    assertEquals(pizza4.getValue(), node.getEntries().get(3));
    assertTrue(result.getStatus());
  }

  @DisplayName("Quand je reçois 4 messages, liste d'entries est bonne et de taille 4")
  @Test
  void test13() {
    // given
    Node node = new Node(CURRENT_TERM);
    Entries pizza1 = new Entries("pizza1");
    Entries pizza2 = new Entries("pizza2");
    Entries pizza3 = new Entries("pizza3");
    Entries pizza4 = new Entries("pizza4");

    // when
    node.appendEntries(pizza1, CURRENT_TERM, 0, 0);
    node.appendEntries(pizza2, CURRENT_TERM, 1, 0);
    node.appendEntries(pizza3, CURRENT_TERM, 2, 0);
    node.appendEntries(pizza4, CURRENT_TERM, 3, 0);

    // then
    assertEquals(4, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0));
    assertEquals(pizza2.getValue(), node.getEntries().get(1));
    assertEquals(pizza3.getValue(), node.getEntries().get(2));
    assertEquals(pizza4.getValue(), node.getEntries().get(3));
  }

  //
  @DisplayName("Quand je recois un message avec un index précédent que je ne connais pas, (j'ai la commande 1 2 et je recois la commande 4 avec un prevLogIndex à 3), je renvois false")
  @Test
  void test14() {
    // given
    Node node = new Node(CURRENT_TERM);
    Entries pizza1 = new Entries("pizza1");
    Entries pizza2 = new Entries("pizza2");
    Entries pizza3 = new Entries("pizza3");
    node.appendEntries(pizza1, CURRENT_TERM, 0, 0);
    node.appendEntries(pizza2, CURRENT_TERM, 1, 0);

    // when
    Result result = node.appendEntries(pizza3, CURRENT_TERM, 3, 0);

    // then
    assertFalse(result.getStatus());
    assertEquals(2, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0));
    assertEquals(pizza2.getValue(), node.getEntries().get(1));
  }

  @DisplayName("Quand je recois un message avec un index précédent que je connais, mais un prevLogTerm différent, je renvois false")
  @Test
  void test15() {
    // given
    Node node = new Node(CURRENT_TERM);
    Entries pizza1 = new Entries("pizza1");
    Entries pizza2 = new Entries("pizza2");
    node.appendEntries(pizza1, CURRENT_TERM, 0, 0);

    // when
    Result result = node.appendEntries(pizza2, CURRENT_TERM, 1, 1);

    // then
    assertFalse(result.getStatus());
    assertEquals(1, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0));
  }

  /*@DisplayName("Quand je recois un message avec un index précédent que je connais, mais un prevLogTerm différent, je renvois false")
  @Test
  void test16() {
    // given
    Node node = new Node(0);
    Entries pizza1 = new Entries("pizza1");
    Entries pizza2 = new Entries("pizza2");
    Entries pizza3 = new Entries("pizza3");

    node.appendEntries(pizza1, 1, 0, 0);
    node.appendEntries(pizza2, 1, 1, 1);

    // when
    Result result = node.appendEntries(pizza3, 2, 2, 2);

    // then
    assertFalse(result.getStatus());
    assertEquals(2, node.getEntries().size());
    assertEquals(pizza1.getValue(), node.getEntries().get(0));
  }*/

}