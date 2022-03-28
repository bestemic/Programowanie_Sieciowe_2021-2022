#!/bin/bash

path=tests
testy=14

for (( c=1; c<=$testy; c++ ))
do
    if [ $c -lt 4 ];
    then
        make klient$c > $path/wynik$c.txt;
    else
        socat -t 0.5 stdio udp4:127.0.0.1:2020 < $path/test$c.txt > $path/wynik$c.txt;
    fi

    output=$(cat $path/wynik$c.txt)
    value=$(cat $path/spr$c.txt)
    if [ "$output" = "$value" ]; then
        echo "[$c] Test: Success!"
    else
        echo "[$c] Test: Failure!"
    fi

done
rm $path/wynik*
