# Compte rendu de session RAFT 9

## Date

Date : 20/02/2023


## Participants

- Alexandre (LEAL)
- Borémi (BTO)
- Miranda (LIMI)
- Cédric (CEM)

## Précédement

On a commencé la mise en place du leader deux sessions auparavant via la mise en place des couches hautes.
On a un leader qui est capable de prendre un message d'un client, de l'enregistrer et de déléguer la réplication sur les followers.
On a un début d'orchestration de réplication sur les followers, en synchrone, en série et qu'on considère en échec à la moindre erreur sans notion de quorum.


## Objectifs de la session

### Définir si la réplication est reussi sur le quorum 

Le but de la session a été de : 
- définir si la réplication est reussi quand la donnée est répliqué sur le quorum
- mettre en place un mécanisme de réplication en parallèle
- 
## Réalisation

#### Ajout du concept de quorum

Le quorum est défini comme étant le nombre de noeud nécessaire pour atteindre la majorité des noeuds du cluster.
La réplication est considérée comme réussie si le message est répliqué au moins sur le quorum.
`ReplicationFollower` prend en compte la notion de quorum afin de définir si une réplication est réussie ou non.

#### Mise en place de la réplication en parallèle

`ReplicationFollower` est maintenant capable de répliquer un message en parallèle sur tous les noeuds du cluster.

#### Schéma d'archi

![couche applicative de la gestion du leader](img/couche_appli_session9.png "Text to show on mouseover")

## Prochaine étape

- Faire en sorte que `ReplicationFollower` soit capable de renvoyer que la réplicaiton est en succès dès que le quorum est atteint
- Mettre en place des heartsbeats depuis le leader à intervalle régulier pour détecter les noeuds tombés
- Mettre en place un reset d'intervale de heartbeats en cas d'envoi de message de réplication