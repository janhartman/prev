#!/bin/bash
help="
arguments:
\t-clear: clears all .xml files at the start
\t-help: displays this message.
"

if [ "$#" -ge 1 ] && [ $1 == "-help" ]; then printf "$help"; exit; fi
mkdir bin
cd ..
sources=$(find -name "*.java")
/mnt/c/Program\ Files/Java/jdk1.8.0_91/bin/javac.exe -d script/bin $sources
cd script/bin
for i in {1..10}
do
test=$(echo "../tests/test$i.prev")
testC=$(echo "../tests/test$i.lincode.xml")
testCC=$(echo "../tests/test$i.xml")
/mnt/c/Program\ Files/Java/jdk1.8.0_91/bin/java.exe compiler.Main $test --logged-phase=asmgen > "../out$i.mms"
echo $?
done
cd ..
rm -rf bin