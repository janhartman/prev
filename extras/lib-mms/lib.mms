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

io	IS $255
	LOC Data_Segment
chrBuf	OCTA 0
	BYTE 0
	LOC (@+7)&-8
intBuf	LOC @+21
	LOC (@+7)&-8
prnsd	OCTA #ACE7

	LOC #100
_printStr	ADD $0,sp,8
	LDO io,$0,0
	TRAP 0,Fputs,StdOut
	POP 0,0
_printChr	LDO $0,sp,8
	STB $0,chrBuf
	LDA io,chrBuf
	TRAP 0,Fputs,StdOut
	POP 0,0
_printInt	LDO $0,sp,8
	CMP $3,$0,0
	ZSN $3,$3,1
	BZ $3,cont
	NEG $0,0,$0
cont	LDA $1,intBuf
	ADD $1,$1,20
loop	SUB $1,$1,1
	DIV $0,$0,10
	GET $2,6
	ADD $2,$2,48
	STB $2,$1,0
	CMP $2,$0,0
	ZSZ $2,$2,1
	PBZ $2,loop
	BZ $3,cont2
	SUB $1,$1,1
	SETL $0,45
	STB $0,$1,0
cont2	STO $1,sp,8
	GET $0,rJ
	PUSHJ k,_printStr
	PUT rJ,$0
	POP 0,0
_rand	LDO $0,prnsd
	SR $1,$0,6
	SR $2,$0,5
	XOR $1,$1,$2
	AND $1,$1,1
	SL $2,$0,1
	OR $2,$2,$1
	AND $0,$2,#7F
	STO $0,prnsd
	STO $0,sp,0
	POP 0,0
