<html>
<body>

<?php

// module  raspberry-pi-php
use Pkj\Raspberry\PiFace\PiFaceDigital;
require '/home/raspberry-pi-php/vendor/autoload.php';

//init carte piface
$piface = PiFaceDigital::create();
$piface->init();

      // test port 0   portail en mouvement
      $mouvement=$piface->getInputPins()[0]->getValue();
      // test port 1  portail ouvert
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


