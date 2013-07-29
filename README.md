### Déployer les interfaces intermédiaires

Rendre une ressource accessible à distance nécessitera de pouvoir lancer un serveur Apache et PHP sur la machine hôte.
Nous prendrons ici l’exemple de déployer la ressource Chessok fourni avec le code du serveur et contenant une base d’ouverture de parties d’échecs.
Nous nous placerons ici dans le cas d’utilisation d’un serveur local.
Avant de démarrer, il est indispensable de vérier que l’extension Apache Redirect et l’extension PHP cURL ont bien été activés.

Dans le cas de Chessok, seuls deux fichiers sont à copier dans le répertoire www de Wamp : chessok.php et le framework resourcewrapper.class.php.
Il faut ensuite activer la redirection Apache vers le fichier chessok.php, en ajoutant dans le fichier httpd.conf :

```sh
RewriteEngine On
RewriteCond %{REQUEST_URI} /chessok/.*$ [NC]
RewriteRule (.*)$ /chessok.php [L]
```

Une fois le serveur Wamp relancé, Chessok sera alors accessible par le serveur central à l’adresse http://localhost/chessok/.
Il est tout à fait possible de déployer une ressource supplémentaire sur le même serveur en accordant les règles de réécriture Apache avec la nouvelle ressource.
