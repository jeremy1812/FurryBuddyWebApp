This is an attempt to build the Web Applicaton of FurryBuddy. 

Dans la partie pom.xml, on a une dependency envers le FurryBuddy domain. pour que cette dernière se fasse correctement vous devrez ouvrir le projet furrybuddy(domain)
et run la commande "mvn clean install" une fois installé furrybuddy se trouvera dans le maven et Maven pourra reconnaître la dépendance entre les deux
ou alors vous pouvez run le fameux (clean, compile, package, install). Ensuite retour à FurryBuddyWebApp là vous pourrez cliqué sur le petit m puis sur l'icône de rafraichissement pour "reload all maven projects incrementally".
Ensuite la dépendance devrait fonctionner. 
