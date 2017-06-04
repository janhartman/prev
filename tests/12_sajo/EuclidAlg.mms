#---Global Registers---
SP GREG
FP GREG
HP GREG
#---Data Segment---
	LOC Data_Segment
	GREG @
buf	BYTE 0
#---START UP---
	LOC	#100
Main	PUT rG,250
	SET $7,0
	SETH $253,24576
	SUB $253,$253,8
	SET $254,$253
	SETH $252,16384
	ADD $252,$252,8
	PUSHJ $8,_
	TRAP 0,Halt,0
#--------
#---PROLOGUE---
_	SET $0,$253
	SET $253,$254
	SUB $254,$254,40
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L3	SETL $0,0
	STO $0,$254,0
	SETL $0,124
	STO $0,$254,8
	SETL $0,568
	STO $0,$254,16
	PUSHJ $8,_gcd
	LDO $1,$254,0
	SETL $0,0
	STO $0,$254,0
	STO $1,$254,8
	PUSHJ $8,_printint
	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_println
	SETL $0,1
L4	SET $0,$0
#---EPILOGUE---
	STO $0,$253
	SETL $1,16
	SUB $1,$253,$1
	LDO $0,$1,8
	PUT rJ,$0
	SET $254,$253
	LDO $253,$1,0
	POP 0,0
#---------
#---PROLOGUE---
_gcd	SET $0,$253
	SET $253,$254
	SUB $254,$254,48
	SETL $1,24
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L5	LDO $0,$253,16
	CMP $0,$0,0
	ZSZ $0,$0,1
	BNZ $0,L0
L1	LDO $0,$253,8
	LDO $1,$253,16
	DIV $1,$0,$1
	GET $1,rR
	LDO $0,$253,0
	STO $0,$254,0
	LDO $0,$253,16
	STO $0,$254,8
	STO $1,$254,16
	PUSHJ $8,_gcd
	LDO $1,$254,0
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	STO $1,$253,$0
L2	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	JMP L6
L0	LDO $0,$253,8
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	STO $0,$253,$1
	JMP L2
L6	SET $0,$0
#---EPILOGUE---
	STO $0,$253
	SETL $1,24
	SUB $1,$253,$1
	LDO $0,$1,8
	PUT rJ,$0
	SET $254,$253
	LDO $253,$1,0
	POP 0,0
#---------
#---StdLib
_printchar LDO $0,$254,8
 STB $0,buf
 LDA $255,buf
 TRAP 0,Fputs,StdOut
 POP 0,0

_println SETL $0,10
 STB $0,buf
 LDA $255,buf
 TRAP 0,Fputs,StdOut
 POP 0,0

_printint LDO $0,$254,8
 GET $3,rJ
 SETL $2,1
 CMP $1,$0,0
 BNN $1,_printint_radix
 NEG $0,$0
 SETL $1,45
 STO $1,$254,8
 PUSHJ $4,_printchar
_printint_radix CMP $1,$2,$0
 BP $1,_printint_print_start
 MUL $2,$2,10
 JMP _printint_radix
_printint_print_start DIV $2,$2,10
_printint_print CMP $1,$2,0
 BNP $1,_printint_end
 DIV $0,$0,$2
 ADD $0,$0,48
 STO $0,$254,8
 PUSHJ $4,_printchar
 GET $0,rR
 DIV $2,$2,10
 JMP _printint_print
_printint_end PUT rJ,$3
 POP 0,0