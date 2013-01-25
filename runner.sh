#!/bin/sh
while true; do 
    read -p "1) DUMMY RUN WITH PITCH 0 \n 2)Pitch 0 \n 3)Pitch 1 " yn
    case $yn in
        [1]* ) sh run.sh -d -p0& sh vision/runvision.sh -r -p0& break;;
        [2]* ) sh run.sh -p0& sh vision/runvision.sh -r -p0& break;;
		[3]* ) sh run.sh -p1& sh vision/runvision.sh -r -p1& break;;
        * ) echo "Please choose an option";;
    esac
done