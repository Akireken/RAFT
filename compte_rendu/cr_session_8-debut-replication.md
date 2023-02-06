# Compte rendu de session RAFT 8

## Date

Date : 06/02/2023


## Participants

- Noel (NOZA)
- Borémi (BTO)
- Miranda (LIMI)
- Cédric (CEM)

## Objectifs de la session

### Commencer la mise en place du mécanisme de réplication

Le code actuel permet à un client d'envoyer un message à un noeud leader.
Le noeud leader est chargé :
- de sauvegarder le message
- de répliquer le message sur un cluster
- d'acquitter en cas de réplication en succès

Une première version de la sauvegarde et de l'acquittement sont déjà implémentées.

Le but de la session est de mettre en place la réplication sur le cluster.

## Réalisation

#### Renommage de `ReplicationRepository` en `ReplicationFollower` et création d'une classe `Repository`

Le but est de centraliser dans la classe `ReplicationRepository` le mécanisme de réplication et d'abstraire dans la classe `Repository` le mode de communication avec les autres noeuds.

#### Changement de la signature de la méthode `ReplicationRepository::replicate`

La méthode `ReplicationRepository::replicate` prend en paramètre l'ensemble des variables nécessaires à la réplication.

#### Début implémentation de `ReplicationFollower`

`ReplicationFollower` prend en paramètre une liste de noeuds représentant les autres noeuds du cluster et `Repository`, une interface avec une méthode `send` pour envoyer un message à un autre noeud.

## Prochaine étape

- Continuer l'implémentation de `ReplicationFollower`
