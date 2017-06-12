# PREV compiler
This is a compiler for the (made-up) programming language called PREV.
I implemented this compiler (in Java) as part of the coursework for the Compilers course taken at the Faculty of Computer and Information Science (University of Ljubljana, Slovenia). 
All credits to Assistant Professor Boštjan Slivnik _(`@author sliva`)_ for providing the description of the language, template code and of course, excellent lectures.

PREV is a simple, strongly-typed language with static type checking. It features primitive data types int, char and bool and more complex types: pointers, fixed-size arrays and records. There is also an option to define custom data types. A more detailed definition of the language can be found in _`prev.pdf`_.

The compiler compiles PREV to [MMIX](http://mmix.cs.hm.edu/doc/index.html), producing a plain-text file containing MMIX assembly code.

The compiler is split into phases:
- Lexical analysis (*lexan*)
- Syntax analysis (*synan*)
- Abstract syntax (*abstr*)
- Semantic analysis (*seman*)
- Activation records / frames and accesses (*frames*)
- Intermediate code generation (*imcgen*)
- Intermediate code linearization (*lincode*)
- Assembly code generation (*asmgen*)
- Liveness analysis (*liveness*)
- Register allocation (*regalloc*)

To compile:  
`mkdir out`  
`cd srcs`  
`javac -d ../out compiler/Main.java`

To run:  
`java -cp out compiler.Main source.prev`

with optional flags:
- `--logged-phase=` which phase to log (depending on the phase, produce XML output or write to standard output)
- `--target-phase=` which phase to target during compilation
- `--xml=` where to write the output XML
- `--xsl=` path to XSL files for XML styling
- `--src-file-name=` source file name
- `--dst-file-name=` destination file name
