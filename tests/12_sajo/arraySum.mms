#---Global Registers---
SP GREG
FP GREG
HP GREG
#---Data Segment---
	LOC Data_Segment
	GREG @
_i	OCTA 0
_a	OCTA 0
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
	SUB $254,$254,32
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L8	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_foo
	LDO $0,$254,0
	LDA $1,_a
	STO $0,$1,0
	SETL $0,0
	STO $0,$254,0
	LDA $0,_a
	LDO $0,$0,0
	STO $0,$254,8
	PUSHJ $8,_printint
	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_println
	LDA $0,_a
	LDO $0,$0,0
L9	SET $0,$0
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
_foo	SET $0,$253
	SET $253,$254
	SUB $254,$254,184
	SETL $1,176
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L10	STO $253,$254,0
	PUSHJ $8,L0
	LDO $0,$254,0
L11	SET $0,$0
#---EPILOGUE---
	STO $0,$253
	SETL $1,176
	SUB $1,$253,$1
	LDO $0,$1,8
	PUT rJ,$0
	SET $254,$253
	LDO $253,$1,0
	POP 0,0
#---------
#---PROLOGUE---
L0	SET $0,$253
	SET $253,$254
	SUB $254,$254,32
	SETL $1,24
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L12	STO $253,$254,0
	PUSHJ $8,L1
	LDO $0,$254,0
L13	SET $0,$0
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
#---PROLOGUE---
L1	SET $0,$253
	SET $253,$254
	SUB $254,$254,16
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L14	SETL $1,0
	LDO $2,$253,0
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	STO $1,$2,$0
	SETL $0,20
	LDA $1,_i
	STO $0,$1,0
L2	LDA $0,_i
	LDO $0,$0,0
	CMP $0,$0,0
	ZSP $0,$0,1
	BNZ $0,L3
L4	SETL $1,20
	LDA $0,_i
	STO $1,$0,0
L5	LDA $0,_i
	LDO $0,$0,0
	CMP $0,$0,0
	ZSP $0,$0,1
	BNZ $0,L6
L7	LDO $0,$253,0
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	LDO $0,$0,$1
	JMP L15
L6	LDO $0,$253,0
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	LDO $1,$0,$1
	LDA $0,_i
	LDO $0,$0,0
	SUB $0,$0,1
	MUL $3,$0,8
	LDO $0,$253,0
	LDO $2,$0,0
	SETL $0,65376
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	ADD $0,$2,$0
	LDO $0,$3,$0
	ADD $0,$1,$0
	LDO $2,$253,0
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	STO $0,$2,$1
	LDA $0,_i
	LDO $0,$0,0
	SUB $0,$0,1
	LDA $1,_i
	STO $0,$1,0
	JMP L5
L3	LDA $0,_i
	LDO $0,$0,0
	SUB $0,$0,1
	MUL $3,$0,8
	LDA $0,_i
	LDO $2,$0,0
	LDO $0,$253,0
	LDO $1,$0,0
	SETL $0,65376
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	ADD $0,$1,$0
	STO $2,$3,$0
	LDA $0,_i
	LDO $0,$0,0
	SUB $1,$0,1
	LDA $0,_i
	STO $1,$0,0
	JMP L2
L15	SET $0,$0
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