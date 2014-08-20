
	Pi-Portail


	Contact Henry-Pascal ELDIN eldin@eldin.net

	Application sous Licence GNU 3.0

	Application Android permettant d'ouvrir un portail  piloté par un 
	Raspberry Pi équipé d'une carte Pi-Face.

	Voir http://www.eldin.net/spip/spip.php?article8

	Le portail a une quinzaine d’années et les télécommandes d’ouvertures sont fragiles, il faut les changer tous les 2/3 ans, elles coutent de plus en plus chères ( env 80 euros pièce... ) et de moins en moins de magasins les proposent.

D’où l’idée de piloter l’ouverture du portail depuis un smarphone.

Le raspberry est installé suivant les articles précédants.

Il faut que la connexion Internet disponible ait soit une adresse IP fixe ou une adresse de type dyndns.

Le portail est de type One de chez Motorstar. Il a fallut ajouter 2 petits relais pour l’interfacer avec le raspberry+piface, un relais 220v et un relais 24v ( env 17 euros ttc avec le port chez Conrad).

Sur le portail, on utilise 3 connecteurs :

- le connecteur pour le bouton poussoir d’ouverture qui est relié au relais 1 du raspberry+piface

- une sortie 24v pour accessoires reliée au relais 24v puis a l’entrée 0 du raspberry+piface.

- une sortie 220V pour la lampe de signalisation de mouvement du portail reliée au relais 220V puis à l’entrée 1 du raspberry+piface.

Les relais sont positionner sur le boitier électronique du portail, relié au raspberry+piface par un câble enterré.

Sur le raspberry, on met en place l’application Web minimale dans /home/portail.

on fait un lien depuis /var/www vers ce répertoire :
ln -s /home/portail /var/www/portail

On met en place la sécurité d’accès à l’application web :
cd /home/portail
la première fois :
htpasswd -bc .passwd LOGIN MOT-DE-PASSE
les suivantes :
htpasswd -b .passwd LOGIN1 MOT-DE-PASSE1

Il est possible de créer autant de comptes que l’on souhaite.

Fichier s.php qui permet d’avoir le ’status’ du portail :
<html>
<body>
<?php
// module  raspberry-pi-php
use Pkj\Raspberry\PiFace\PiFaceDigital;
require '/home/raspberry-pi-php/vendor/autoload.php';
//init carte piface
$piface = PiFaceDigital::create();
$piface->init();
$m=$piface->getInputPins()[0]->getValue();
$o=$piface->getInputPins()[1]->getValue()+"-\n";
echo "P:$m$o\n";
?>
</body>
</html>

Ce fichier renvoi 3 valeurs :
P:00 : le portail est fermé
P:11 : le portail est en mouvement, ouverture ou fermeture
P:10 : le portail est ouvert
tout autre résultat est pris pour une erreur

Fichier o.php pour ouvrir le portail
<html>
<body>
<?php
// module  raspberry-pi-php
use Pkj\Raspberry\PiFace\PiFaceDigital;
require '/home/raspberry-pi-php/vendor/autoload.php';
//init carte piface
$piface = PiFaceDigital::create();
$piface->init();
     // test port 0   portail ouvert
     $mouvement=$piface->getInputPins()[0]->getValue();
     // test port 1  portail en mouvement
     $ouvert=$piface->getInputPins()[1]->getValue();
     if ( $mouvement==1 || $ouvert==1 ){
            echo "P:NO\n";
     }else{
         // ouverture
         echo "P:ON\n";
         $piface->getLeds()[0]->turnOn();
         sleep(2);
         $piface->getLeds()[0]->turnOff();
     }
?>
</body>
</html>

Ce fichier renvoi 2 valeurs
P:ON : portail ouvert
P:NO : portail déjà ouvert
tout autre résultat est pris pour une erreur

L’application est téléchargeable sur : http://www.eldin.net/APK/piportail-...

Lors du premier lancement, l’application demande les paramètres :

SRV Internet : nom ou adresse IP publique du site pour l’accès en 3g
chezmoi.dyndns.org par exemple

SRV Intranet : nom ou adresse IP du raspberry pour l’accès en wifi
192.168.1.122 par exemple

Port  : port http d’utilisation
9542 par exemple
au niveau du routeur ou box d’accès une redirection de port doit être faite.
le fichier /etc/apache2/ports.conf doit etre modifier :
NameVirtualHost *:9542
Listen 9542
relancer apache

URL : le répertoire des programmes avec / au début et à la fin
/portail/ en principe

Cmd Status : nom du fichier php pour avoir le status
s.php par exemple

Cmd Ouvrir : nom du fichier php pour ouvrir le portail
o.php par exemple

Login  : login de l’utilisateur

Mot de passe  : mot de passe de l’utilisateur

Testé et utilisé au quotidien sur smartphone sous Android 2.3 à 4.4 sans soucis.

Adaptation : il est possible d’adapter cette application a tous les types de portail en adaptant les 2 fichiers s.php et o.php en fonction des entrées/sorties utilisées sur le raspberry+piface.





