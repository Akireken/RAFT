package octo.raft;

public class LeaderTest {

  // Le leader peut recevoir un message d'un client
  // Le leader réplique le message reçu d'un client à tous les followers
  // Le leader retente d'envoyer un message à un follower tant qu'il n'est pas up
  // Le leader acquitte le message quand il est répliqué sur le quorum
  // Le leader commit le message dernier message répliqué sur le quorum au le cluster
  // Le leader timeout après un certain temps s'il il n'a pas une réplication du message sur le quorum
  // Le leader envoit un heartbeat à interval régulier
  // Après avoir reçu un rejet du follower, le leader tente d'envoyer le message précédent
  // En temps que follower, je ne peux pas accepter de message d'un client
  // Les messages répliqué et commité dans l'ordre exact de la reception sur le leader
}
