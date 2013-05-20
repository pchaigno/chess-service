Distributed Chess Service
=====

Les échecs ont été révolutionnés par l’informatique. De nos jours, peu de joueurs
peuvent encore prétendre remporter une partie contre les meilleurs pro- grammes, et en
tout cas pas un tournoi en plusieurs manches. Cette supériorité n’annule pas l’intérêt
du jeu : les humains sont du coup de nouveau obligés de jouer entre humains, les
challenges ”grand maître contre programme” ayant perdu tout intérêt. De plus, les
programmes d’échecs sont des partenaires d’étude et d’apprentissage du jeu.

Une partie d’échecs se déroule en plusieurs phases. Dans la première phase, appelée
ouverture, les deux adversaires choisissent un type de partie. Les coups échangés et
leurs réponses ont été étudiés depuis des siècles. Les enchainements sont donc
automatiques et appris par coeur par les joueurs. Généralement, une mauvaise réponse
à un coup joué met le joueur fautif dans une position très défavorable. Les ouvertures
et leurs réponses sont répertoriées, classées, et il existe des bases de données
d’ouvertures avec leur popularité, des statistiques sur leurs victoires, etc.

Dans une deuxième phase, les deux adversaires développent leurs stratégies. C’est
finalement le moment de la partie qui fait le plus appel `a l’intelligence des deux
joueurs. Enfin, la dernière phase, ou fin de partie commence lorsqu’il ne reste plus que
quelques pièces sur l’échiquier. Là encore, il existe des bases de données recensant
pour une configuration de l’échiquier comportant au plus 6 pièces la manière de
terminer la partie et son issue: gain pour les blancs ou pour les noirs, match nul.

Les programmes d’échecs ne sont pas seulement des machines à calculer; ils
s’appuient sur des bibliothèques d’ouvertures et de fin de partie. En milieu de partie, le
programme évalue, par différentes techniques, la valeur d’un coup dans une position donnée.
Comme on le voit, un bon programme d’échecs va donc faire appel à des
compétences et connaissances variées pour développer sa stratégie. De telles
compétence n’ont pas forcément besoin de se trouver sur une seule machine, et
peuvent être distribuées sur un réseau, et accessibles au moyen de services web.

L’objectif de cette Etude Pratique est de mettre en oeuvre une chorégraphie de services
web utilisant les réponses de services spécialisés (dans les ouvertures, dans les fins de
parties, dans l’évaluation d’un coup, ...) pour proposer un coup sélectionné comme
étant le meilleur dans une position.

Une première étape de cette étude consistera à se familiariser avec l’utilisation des
services web (nous recommandons l’utilisation de REST), et à imaginer l’architecture
d’un système distribué sur le web fournissant le meilleur coup sélectionné dans une
position donnée. Cela nécessite entre autres de savoir comment interroger un service
ou une base de donnée distante sur le web, mais aussi de réfléchir à la manière
d’exploiter les réponses fournies par les différentes entités, principalement lorsque
celles-ci sont différentes.

La deuxième étape de cette étude consistera à mettre en oeuvre les choix faits dans la
phase initiale.
