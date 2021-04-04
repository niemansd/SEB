#!/bin/bash

#basic curl parameters:
curl="curl -i"
server=" localhost:8000"
path="/messages"
data=" -d TEST"
put=" -X PUT -d PUTtest"
del=" -X DELETE"
err1=" -X DEL"
r200="HTTP/1.1 200 OK"$'\r\n'
r201="HTTP/1.1 201 CREATED"$'\r\n'
r204="HTTP/1.1 200 NO CONTENT"$'\r\n'
r400="HTTP/1.1 400 BAD REQUEST"$'\r\n'
r404="HTTP/1.1 404 NOT FOUND"$'\r\n'
headermain='Server: Kiste'$'\r\nContent-Type: text/plain'$'\r\nAccept-Ranges: bytes\r\n'$'Content-Length: '
counter=1
putcount=1

##TESTEN nach Serverneustart fuer Vergleichbarkeit##
#test
#diff <($curl $server$path) <($curl $server$path)
clear

#1
#GET ohne nachrichten
test=$($curl $server$path)
control=$r404$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
#echo "$test"
#echo "<--->"
#echo "$control"
#if [ "$test" = "$control" ] ; then
#echo "yess"
#fi
#for i in $(seq ${#test}); do echo "${test:$i-1:1}""${control:$i-1:1}"; done
echo ""

#2
#GET Nachricht #1 ohne nachrichten
test=$($curl $server$path/1)
control=$r404$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
echo ""

#3
#POST Nachricht #1
test=$($curl $data$counter $server$path)
control=$r201$headermain$'3\r\n\r\n1\r'
diff <(echo "$test") <(echo "$control")
echo ""
((counter++))

#4
#POST Nachricht #2
test=$($curl $data$counter $server$path)
control=$r201$headermain$'3\r\n\r\n2\r'
diff <(echo "$test") <(echo "$control")
echo ""
((counter++))

#5
#GET alle Nachrichten
test=$($curl $server$path)
control=$r200$headermain$'36\r\n\r\nMessage 1: TEST1\r\nMessage 2: TEST2\r'
diff <(echo "$test") <(echo "$control")
echo ""

#6
#GET erste Nachricht
test=$($curl $server$path/1)
control=$r200$headermain$'18\r\n\r\nMessage 1: TEST1\r'
diff <(echo "$test") <(echo "$control")
echo ""

#7
#DELETE erste Nachricht
test=$($curl $del $server$path/1)
control=$r200$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
echo ""

#8
#GET gelöschte Nachricht
test=$($curl $server$path/1)
control=$r404$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
echo ""

#9
#GET nicht gelöschrte Nachricht #2
test=$($curl $server$path/2)
control=$r200$headermain$'18\r\n\r\nMessage 2: TEST2\r'
diff <(echo "$test") <(echo "$control")
echo ""

#10
#POST Nachricht #3
test=$($curl $data$counter $server$path)
control=$r201$headermain$'3\r\n\r\n3\r'
diff <(echo "$test") <(echo "$control")
echo ""
((counter++))

#11
#GET Nachricht #3
test=$($curl $server$path/3)
control=$r200$headermain$'18\r\n\r\nMessage 3: TEST3\r'
diff <(echo "$test") <(echo "$control")
echo ""

#12
#GET alle Nachrichten
test=$($curl $server$path)
control=$r200$headermain$'36\r\n\r\nMessage 2: TEST2\r\nMessage 3: TEST3\r'
diff <(echo "$test") <(echo "$control")
echo ""

#13
#PUT in gelöschte Nachricht
test=$($curl $put$putcount $server$path/1)
control=$r404$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
echo ""
((putcount++))

#14
#PUT in vorhandene Nachricht
test=$($curl $put$putcount $server$path/2)
control=$r201$headermain$'0\r\n\r'
diff <(echo "$test") <(echo "$control")
echo ""
((putcount++))

#15
#GET neuen Stand
test=$($curl $server$path)
control=$r200$headermain$'39\r\n\r\nMessage 2: PUTtest2\r\nMessage 3: TEST3\r'
diff <(echo "$test") <(echo "$control")
echo ""