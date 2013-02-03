chess
=====

Se lier au repo depuis Eclipse :
http://www.php-geek.fr/configurer-et-utiliser-egit-eclipse.html


Se lier au repo depuis un terminal :
- créer un dossier 'chess', se placer dedans
- faire un 'git init' dedans
- se lier au repo distant avec :
git remote add origin https://github.com/ChessEP/Chess-Service.git 
	et non chess.git (sinon petite prise de tête avec des gits c'est de la merde je comprends rien (c'est peut-être vrai))
'origin' étant le nom du repo distant
- faire un "git pull origin master" pour récupérer les updates
- faire un "git push origin master" pour envoyer vos modifs


Et les classiques :
git add "nom fichier" pour ajouter au git
git commit -m "un commentaire explicite et obligatoire !" pour... benh commiter
