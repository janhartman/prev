package compiler.phases.lexan;

/**
 * Tokens.
 *
 * @author sliva
 */
public enum Term {

    EOF,

    IOR,
    XOR,

    AND,

    EQU, NEQ, LTH, GTH, LEQ, GEQ,

    ADD, SUB, MUL, DIV, MOD,

    NOT,

    MEM, VAL, NEW, DEL,

    ASSIGN,

    COLON, COMMA, DOT, SEMIC,

    LBRACE, RBRACE, LBRACKET, RBRACKET, LPARENTHESIS, RPARENTHESIS,

    VOIDCONST, BOOLCONST, CHARCONST, INTCONST, PTRCONST,

    VOID, BOOL, CHAR, INT, PTR, ARR, REC,

    DO, ELSE, END, FUN, IF, THEN, TYP, VAR, WHERE, WHILE,

    IDENTIFIER,

}
