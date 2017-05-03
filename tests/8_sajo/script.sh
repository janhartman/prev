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
for i in {1..9}
do
test=$(echo "../tests/test$i.prev")
testC=$(echo "../tests/test$i.lincode.xml")
testCC=$(echo "../tests/test$i.xml")
/mnt/c/Program\ Files/Java/jdk1.8.0_91/bin/java.exe compiler.Main $test --logged-phase=lincode --xsl=../data/ >/dev/null
#format .xmls for diff
out=$(cat $testC);
printf "${out//</"\n<"}" > "tmpC";
out=$(cat $testCC);
printf "${out//</"\n<"}" > "tmpCC";
if [ "$#" -ge 1 ] && [ $1 == "-clear" ]; then
	to_del=$(find -name "*.xml");
	if  [[ !  -z  $to_del  ]]; then rm $to_del; fi
	mv $testC $testCC;
else 
	printf "%s\n\n" "----> diff for test$i.prev <----"
	d=$(diff -y -W250 "tmpC" "tmpCC"  | expand | grep -E -C1 '^.{123} [|<>]( |$)')
	if hash colordiff 2>/dev/null;then
		printf "$d"| colordiff
	else
		printf "$d"
	fi
	printf "\n"

fi
done
cd ..
rm -rf bin