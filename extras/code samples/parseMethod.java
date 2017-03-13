private DerNode parseSome() {
    currSymb = currSymb == null ? lexAn.lexer() : currSymb;
    DerNode node = new DerNode(Nont.Some);

    switch (currSymb.token) {
        case NOT:

        default:
            throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseSome");

    }
    return node;
}