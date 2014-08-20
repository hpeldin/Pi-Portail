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


