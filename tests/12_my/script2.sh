#!/bin/bash

for i in *.mms
do
	mmixal.exe $i
done

for i in *.mmo
do
	mmix.exe $i
done

exit 0