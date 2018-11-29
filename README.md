# Mini_Cloud

Pour l'usage de ce cloud, il est necessaire d'avoir MySQL sur l'ordinateur hote.

Le script de creation et d'insertion de donnée est dans ./Serveur/cloud.sql

Une base de donnée distante a été mise en place (Peut etre un peu longue).
Administration : https://phpmyadmin.alwaysdata.com/ user : 137505, pass : root

Si vous souhaitez modifier les paramètres de connection, éditez le fichier ./Serveur/configuration.xml : adresseBDD, nomBDD, userBDD, passBDD

le compte de test est le suivant : login : nacerf, password : abc123

Pour lancez le serveur, executez Serveur.core.Serveur.main().
Pour lancer le client, executez Client.IHM.Main.main()

Les notifications ne s'afficheront qu'à votre premiere connexion (ce qui est normal),
pour les faire réaparaitre, il faut changer la date de derniere connexion de l'utilisateur 1 à une date anterieur à la date du jour.
