-This program is free software: you can redistribute it and/or modify
-it under the terms of the GNU General Public License as published by
-the Free Software Foundation, either version 3 of the License, or
-(at your option) any later version.
-
-This program is distributed in the hope that it will be useful,
-but WITHOUT ANY WARRANTY; without even the implied warranty of
-MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-GNU General Public License for more details.
-
-You should have received a copy of the GNU General Public License
-along with this program.  If not, see <http://www.gnu.org/licenses/>.

FP IS $253
SP IS $254
IO IS $255

_printStr	ADD $0,SP,8
	LDO IO,$0,0
	TRAP 0,Fputs,StdOut
	POP 0,0



_printChr	 LDO $0,SP,8
	STB $0,SP,1
	ADD IO,SP,8
	TRAP 0,Fputs,StdOut
	POP 0,0



_printInt	ADD $0,SP,8
	LDO $0,$0,0
	GET $3,rJ
	SETL $2,1 # Base divider
	
	# check if it's minus
	CMP $1,$0,0
	BNN $1,_printInt_radix
	
	# make it positive
	NEG $0,$0
	SETL $1,45
	STO $1,SP,8
	PUSHJ $64,_printChr
	
	# Calculate largest divider with base 10
_printInt_radix	CMP $1,$2,$0
	BP $1,_printInt_print_start
	MUL $2,$2,10
	JMP _printInt_radix
_printInt_print_start DIV $2,$2,10
_printInt_print CMP $1,$2,0
	BNP $1,_printInt_end
	DIV $0,$0,$2
	ADD $0,$0,48 # Convert number to ascii number and print
	STO $0,SP,8
	PUSHJ $64,_printChr
	GET $0,rR
	DIV $2,$2,10
	JMP _printInt_print
_printInt_end PUT rJ,$3
	POP 0,0