#---Global Registers---
SP GREG
FP GREG
HP GREG
#---Data Segment---
	LOC Data_Segment
	GREG @
_i	OCTA 0
_allocSize	OCTA 0
_bank	OCTA 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
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
	SUB $254,$254,48
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L24	SETL $1,10
	LDA $0,_allocSize
	STO $1,$0,0
	SETL $0,0
	STO $0,$254,0
	LDA $0,_bank
	STO $0,$254,8
	LDA $0,_allocSize
	LDO $0,$0,0
	STO $0,$254,16
	PUSHJ $8,_buildList
	SETL $1,0
	LDA $0,_i
	STO $1,$0,0
L21	LDA $0,_i
	LDO $0,$0,0
	LDA $1,_allocSize
	LDO $1,$1,0
	CMP $0,$0,$1
	ZSN $0,$0,1
	BNZ $0,L22
L23	SETL $0,1
	JMP L25
L22	LDA $0,_i
	LDO $0,$0,0
	MUL $1,$0,16
	SETL $0,0
	STO $0,$254,0
	LDA $0,_bank
	ADD $0,$1,$0
	STO $0,$254,8
	LDA $0,_i
	LDO $0,$0,0
	STO $0,$254,16
	PUSHJ $8,_printList
	LDA $0,_i
	LDO $0,$0,0
	MUL $1,$0,16
	SETL $0,0
	STO $0,$254,0
	LDA $0,_bank
	ADD $0,$1,$0
	STO $0,$254,8
	LDA $0,_i
	LDO $0,$0,0
	STO $0,$254,16
	LDA $0,_i
	LDO $0,$0,0
	STO $0,$254,24
	PUSHJ $8,_printListReverse
	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_println
	LDA $0,_i
	LDO $0,$0,0
	ADD $1,$0,1
	LDA $0,_i
	STO $1,$0,0
	JMP L21
L25	SET $0,$0
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
_printList	SET $0,$253
	SET $253,$254
	SUB $254,$254,40
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L26	LDO $0,$253,16
	LDA $1,_allocSize
	LDO $1,$1,0
	CMP $0,$0,$1
	ZSN $0,$0,1
	BNZ $0,L0
L1	SET $0,$0
L2	LDO $0,$253,16
	LDA $1,_allocSize
	LDO $1,$1,0
	CMP $0,$0,$1
	ZSZ $0,$0,1
	BNZ $0,L6
L7	SET $0,$0
L8	SETL $0,0
	JMP L27
L6	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_println
	JMP L8
L0	LDO $0,$253,8
	ADD $0,$0,0
	SETL $1,0
	STO $1,$254,0
	LDO $0,$0,0
	STO $0,$254,8
	PUSHJ $8,_printint
	LDO $1,$253,16
	LDA $0,_allocSize
	LDO $0,$0,0
	SUB $0,$0,1
	CMP $0,$1,$0
	ZSN $0,$0,1
	BNZ $0,L3
L4	SET $0,$0
L5	LDO $0,$253,8
	ADD $0,$0,8
	LDO $1,$253,16
	ADD $1,$1,1
	LDO $2,$253,0
	STO $2,$254,0
	LDO $0,$0,0
	STO $0,$254,8
	STO $1,$254,16
	PUSHJ $8,_printList
	JMP L2
L3	SETL $0,0
	STO $0,$254,0
	SETL $0,44
	STO $0,$254,8
	PUSHJ $8,_printchar
	SETL $0,0
	STO $0,$254,0
	SETL $0,32
	STO $0,$254,8
	PUSHJ $8,_printchar
	JMP L5
L27	SET $0,$0
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
_printListReverse	SET $0,$253
	SET $253,$254
	SUB $254,$254,48
	SETL $1,16
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L28	LDO $0,$253,24
	LDA $1,_allocSize
	LDO $1,$1,0
	CMP $0,$0,$1
	ZSN $0,$0,1
	BNZ $0,L9
L10	SET $0,$0
L11	LDO $1,$253,24
	LDO $0,$253,16
	CMP $0,$1,$0
	ZSZ $0,$0,1
	BNZ $0,L15
L16	SET $0,$0
L17	SETL $0,0
	JMP L29
L15	SETL $0,0
	STO $0,$254,0
	PUSHJ $8,_println
	JMP L17
L14	JMP L11
L12	SETL $0,0
	STO $0,$254,0
	SETL $0,44
	STO $0,$254,8
	PUSHJ $8,_printchar
	SETL $0,0
	STO $0,$254,0
	SETL $0,32
	STO $0,$254,8
	PUSHJ $8,_printchar
	JMP L14
L9	LDO $0,$253,8
	ADD $1,$0,8
	LDO $0,$253,24
	ADD $2,$0,1
	LDO $0,$253,0
	STO $0,$254,0
	LDO $0,$1,0
	STO $0,$254,8
	LDO $0,$253,24
	STO $0,$254,16
	STO $2,$254,24
	PUSHJ $8,_printListReverse
	LDO $0,$253,8
	ADD $1,$0,0
	SETL $0,0
	STO $0,$254,0
	LDO $0,$1,0
	STO $0,$254,8
	PUSHJ $8,_printint
	LDO $1,$253,24
	LDO $0,$253,16
	CMP $0,$1,$0
	ZSP $0,$0,1
	BNZ $0,L12
L13	JMP L14
L29	SET $0,$0
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
_buildList	SET $0,$253
	SET $253,$254
	SUB $254,$254,24
	SETL $1,24
	SUB $1,$253,$1
	STO $0,$1,0
	GET $0,rJ
	STO $0,$1,8
#---FUNCTION BODY---
L30	SETL $0,0
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	STO $0,$253,$1
L18	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $1,$253,$0
	LDO $0,$253,16
	SUB $0,$0,1
	CMP $0,$1,$0
	ZSN $0,$0,1
	BNZ $0,L19
L20	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	MUL $1,$0,16
	LDO $0,$253,8
	ADD $0,$1,$0
	ADD $0,$0,0
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	LDO $1,$253,$1
	ADD $1,$1,1
	STO $1,$0,0
	SETL $0,0
	JMP L31
L19	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	MUL $0,$0,16
	LDO $1,$253,8
	ADD $0,$0,$1
	ADD $1,$0,8
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	ADD $0,$0,1
	MUL $2,$0,16
	LDO $0,$253,8
	ADD $0,$2,$0
	STO $0,$1,0
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	MUL $1,$0,16
	LDO $0,$253,8
	ADD $0,$1,$0
	ADD $1,$0,0
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	ADD $0,$0,1
	STO $0,$1,0
	SETL $0,65528
	INCML $0,65535
	INCMH $0,65535
	INCH $0,65535
	LDO $0,$253,$0
	ADD $0,$0,1
	SETL $1,65528
	INCML $1,65535
	INCMH $1,65535
	INCH $1,65535
	STO $0,$253,$1
	JMP L18
L31	SET $0,$0
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