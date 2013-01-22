rm -r ../bin/balle

nxjc -d ../bin balle/bluetooth/messages/*.java balle/brick/BrickController.java balle/brick/Roboto.java balle/brick/Kick.java balle/brick/milestone1/RollAndKick.java balle/brick/milestone1/RollThroughField.java balle/brick/PenaltyKick.java balle/brick/TestThroughput.java

nxjpcc -d ../bin balle/bluetooth/*.java balle/controller/*.java

nxjpcc -d ../bin -cp ../lib/*:../bin @sources.txt
