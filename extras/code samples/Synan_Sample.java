private DerNode parseExpr() {
        DerNode node = new DerNode(Nont.expr);
        switch(currSymb.token) {
            case INTCONST:
            case LPARENTHESIS:
                node.add(parseT());
                node.add(parseEt());
                break;
            default:
            throw new Report.Error("FUCK E");
            }

            return node;
        }

private DerNode parseT() {
        DerNode node = new DerNode(Nont.t);
        switch(currSymb.token) {
        case INTCONST:
        currSymb = skip(node);
        currSymb = lexAn.lexer();
        break;
        case LPARENTHESIS:
        currSymb = skip(node);
        currSymb = lexAn.lexer();
        node.add(parseExpr());
        currSymb = skip(node);
        break;
default:
        throw new Report.Error("FUCK T");

        }
        return node;
        }

private DerNode parseEt() {
        DerNode node = new DerNode(Nont.Et);
        switch(currSymb.token) {
        case ADD:
        currSymb = skip(node);
        currSymb = lexAn.lexer();
        node.add(parseT());
        node.add(parseEt());
        break;
        case RPARENTHESIS:
        return node;
        case EOF:
        return node;

default:
        throw new Report.Error("FUCK ET");

        }
        return node;
        }