# PREV compiler
This is a compiler for the (made-up) programming language called PREV.

To compile:  
create folder out on the same level as the folder prev  
`mkdir out`  
`cd prev/srcs`  
`javac -d ../../out compiler/Main.java`

To run:  
`java -cp out compiler.Main source.prev`

with optional flags:
- `--logged-phase=`
- `--target-phase=`
- etc.