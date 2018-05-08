package sintatico;

import enums.Classe;
import enums.Tipo;
import enums.Token;
import lexico.Lexico;
import lexico.RegistroLexico;
import semantico.GeradorCodigo;
import semantico.Variaveis;
import util.FilePosition;

public class Sintatico {

    private static Token token;
    private static RegistroLexico result;
    private static final FilePosition pos = FilePosition.getInstance();
    private static final Variaveis variaveis = Variaveis.getInstance();
    private static final GeradorCodigo codigo = GeradorCodigo.getInstance();

    public static Token readToken() throws Exception {
        result = Lexico.getToken();

        //System.out.println("lex: " + result.getLexema() + " tok: " + result.getToken() + " linha: " + result.getLine());
        return result.getToken();
    }

    public static void casaToken(Token tokenEsperado) throws Exception {

        if (token == tokenEsperado) {
            //System.out.println("CASOU TOKEN " + token);
        } else {
            error();
        }

        token = readToken();
    }

    public static void error() throws Exception {

        if (result.getToken() == null) {
            System.out.println(pos.getLineNumber() + ":fim de arquivo nao esperado.");
        } else {
            System.out.println(pos.getLineNumber() + ":token nao esperado[" + result.getLexema() + "].");
        }

        throw new Exception();
    }

    public static void principal() throws Exception {
        token = readToken();
        S();
    }

    public static void S() throws Exception {

        codigo.inicioASM();

        // VARIAVEL ou CONSTANTE
        while (token == Token.INT || token == Token.CHAR || token == Token.FINAL) {
            if (token == Token.INT || token == Token.CHAR) {
                VARIAVEL();

            } else if (token == Token.FINAL) {
                CONSTANTE();
            }
        }

        codigo.fimVariaveisASM();

        RegistroLexico s = new RegistroLexico();
        // COMANDO
        COMANDO(s);

        codigo.fimASM();
    }

    public static void VARIAVEL() throws Exception {

        Tipo tipo = null;
        int tamanho = 0;
        Classe classe = Classe.VARIAVEL;

        boolean varInicializada = false;

        if (token == Token.INT) {
            casaToken(Token.INT);

            tipo = Tipo.INTEIRO;
        } else if (token == Token.CHAR) {
            casaToken(Token.CHAR);

            tipo = Tipo.CARACTERE;
        }

        RegistroLexico id = result.clone();
        id.setTipo(tipo);
        id.setTamanho(tamanho);
        id.setClasse(classe);

        casaToken(Token.ID);
        variaveis.addVariavel(id);

        if (token == Token.OPEN_BRACKET) {
            casaToken(Token.OPEN_BRACKET);

            tamanho = Integer.parseInt(result.getLexema());

            if ((tipo == Tipo.INTEIRO && tamanho > 2000) || (tipo == Tipo.CARACTERE && tamanho > 4000)) {
                System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o máximo permitido. ");
                throw new Exception();
            } else {
                id.setTamanho(tamanho);
            }

            casaToken(Token.CONST);
            casaToken(Token.CLOSE_BRACKET);
        } else if (token == Token.ATTR) {
            ATRIBUICAO();
            varInicializada = true;
        }

        // se a variavel nao foi inicializada
        // gera o codigo dela.
        // mas se ela foi inicializada o procedimento
        // ATRIBUICAO ja escreveu ela no arquivo
        if (!varInicializada) {
            codigo.adicionarVariavel(id);
        }

        while (token == Token.COMMA) {
            casaToken(Token.COMMA);

            varInicializada = false;

            id = result.clone();
            id.setTipo(tipo);
            id.setTamanho(tamanho);
            id.setClasse(classe);

            casaToken(Token.ID);
            variaveis.addVariavel(id);

            if (token == Token.OPEN_BRACKET) {
                casaToken(Token.OPEN_BRACKET);

                tamanho = Integer.parseInt(result.getLexema());

                if ((tipo == Tipo.INTEIRO && tamanho > 2000) || (tipo == Tipo.CARACTERE && tamanho > 4000)) {
                    System.out.println(pos.getLineNumber() + ":tamanho do vetor excede o máximo permitido. ");
                    throw new Exception();
                } else {
                    id.setTamanho(tamanho);
                }

                casaToken(Token.CONST);

                casaToken(Token.CLOSE_BRACKET);
            } else if (token == Token.ATTR) {
                ATRIBUICAO();
                varInicializada = true;
            }

            // se a variavel nao foi inicializada
            // gera o codigo dela.
            // mas se ela foi inicializada o procedimento
            // ATRIBUICAO ja escreveu ela no arquivo
            if (!varInicializada) {
                codigo.adicionarVariavel(id);
            }
        }

        casaToken(Token.SEMICOLON);
    }

    public static void ATRIBUICAO() throws Exception {
        casaToken(Token.ATTR);

        codigo.adicionarVariavel(result, Integer.parseInt(result.getLexema()));

        casaToken(Token.CONST);
    }

    public static void CONSTANTE() throws Exception {
        Classe classe = Classe.CONSTANTE;
        Tipo tipo = null;
        int tamanho = 0;
        int valor = -1;

        casaToken(Token.FINAL);

        RegistroLexico id = result.clone();
        id.setTamanho(tamanho);
        id.setClasse(classe);

        casaToken(Token.ID);
        casaToken(Token.EQUAL);

        id.setTipo(result.getTipo());

        boolean isNegativo = false;

        if (token == Token.SUM) {
            casaToken(Token.SUM);
        } else if (token == Token.MINUS) {
            casaToken(Token.MINUS);
            isNegativo = true;
        }

        if (id.getTipo() == Tipo.INTEIRO) {
            valor = Integer.parseInt(result.getLexema());
            if (isNegativo) {
                valor *= -1;
            }
        } else {
            valor = result.getLexema().charAt(0);
        }

        variaveis.addVariavel(id);
        codigo.adicionarVariavel(id, valor);

        casaToken(Token.CONST);
        casaToken(Token.SEMICOLON);
    }

    public static void COMANDO(RegistroLexico cmd) throws Exception {

        while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
                || token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {

            RegistroLexico id;
            RegistroLexico temp = new RegistroLexico();
            
            if (null != token) {
                switch (token) {
                    case FOR:
                        casaToken(Token.FOR);
                        id = variaveis.getVar( result.getLexema() );
                        
                        if (id == null) {
                            System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
                            throw new Exception("");
                        }
                        
                        casaToken(Token.ID);
                        casaToken(Token.ATTR);
                        EXP(cmd);
                        
                        id.setEndereco( cmd.getEndereco() );
                        
                        casaToken(Token.TO);
                        EXP(cmd);
                        
                        temp.setEndereco( cmd.getEndereco() );
                        
                        int step;
                        if (token == Token.STEP) {
                            casaToken(Token.STEP);
                            
                            step = Integer.parseInt( result.getLexema() );
                            
                            casaToken(Token.CONST);
                        }else{
                            step = 1;
                        }
                        
                        int numRotulo = codigo.novoRotulo();
                        codigo.rotulo( numRotulo );
                        
                        casaToken(Token.DO);
                        BLOCO();
                        
                        codigo.mov("cx","DS:["+id.getEndereco()+"]");
                        codigo.mov("dx","DS:["+temp.getEndereco()+"]");
                        codigo.add("cx", ""+step, "incremento do for");
                        codigo.mov("DS:["+id.getEndereco()+"]", "cx", "retorna o valor de cx para memoria");
                        codigo.cmp("cx","dx");
                        codigo.jle(numRotulo, "fim for");
                        break;
                    case IF:
                        casaToken(Token.IF);
                        EXP(cmd);
                        casaToken(Token.THEN);
                        BLOCO();
                        if (token == Token.ELSE) {
                            casaToken(Token.ELSE);
                            BLOCO();
                        }
                        break;
                    case READLN:
                        casaToken(Token.READLN);
                        casaToken(Token.OPEN_PARENTHESIS);
                        casaToken(Token.ID);
                        if (token == Token.OPEN_BRACKET) {
                            casaToken(Token.OPEN_BRACKET);
                            EXP(cmd);
                            casaToken(Token.CLOSE_BRACKET);
                        }
                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case WRITE:
                        casaToken(Token.WRITE);
                        casaToken(Token.OPEN_PARENTHESIS);
                        EXP(cmd);

                        if (cmd.getTipo() == Tipo.INTEIRO) {
                            codigo.mostrarInt(cmd.getEndereco());
                        } else {
                            codigo.mostrarString(cmd.getEndereco());
                        }

                        while (token == Token.COMMA) {
                            casaToken(Token.COMMA);
                            EXP(cmd);

                            if (cmd.getTipo() == Tipo.INTEIRO) {
                                codigo.mostrarInt(cmd.getEndereco());
                            } else {
                                codigo.mostrarString(cmd.getEndereco());
                            }
                        }

                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case WRITELN:
                        casaToken(Token.WRITELN);
                        casaToken(Token.OPEN_PARENTHESIS);
                        EXP(cmd);

                        if (cmd.getTipo() == Tipo.INTEIRO) {
                            codigo.mostrarInt(cmd.getEndereco());
                        } else {
                            codigo.mostrarString(cmd.getEndereco());
                        }

                        while (token == Token.COMMA) {
                            casaToken(Token.COMMA);
                            EXP(cmd);

                            if (cmd.getTipo() == Tipo.INTEIRO) {
                                codigo.mostrarInt(cmd.getEndereco());
                            } else {
                                codigo.mostrarString(cmd.getEndereco());
                            }
                        }

                        codigo.quebrarLinha();
                       
                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case ID:

                    	// pega a variavel da memoria
                        id = variaveis.getVar(result.getLexema());

                        if (id == null) {
                            System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
                            throw new Exception("");
                        }
                    	
                        casaToken(Token.ID);
                        casaToken(Token.ATTR);
                        EXP(cmd);

                        if (id.getTipo() == cmd.getTipo()) {

                            // Copia o valor de CMD.end para regA
                            codigo.mov("ax", "DS:[" + cmd.getEndereco() + "]", "Copia o valor de E.end para regA");
                            // Copia o valor de regA para ID.end
                            codigo.mov("DS:[" + id.getEndereco() + "]", "ax", "Copia o valor de regA para ID.end");

                        } else {
                            System.out.println("ERRO: tipos incompativeis");
                            throw new Exception();
                        }

                        casaToken(Token.SEMICOLON);
                        break;
                    case SEMICOLON:
                        casaToken(Token.SEMICOLON);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    public static void BLOCO() throws Exception {

        RegistroLexico bloco = new RegistroLexico();
        
        if (token == Token.BEGIN) {
            casaToken(Token.BEGIN);
            COMANDO(bloco);
            casaToken(Token.END);
        } else {
            COMANDO(bloco);
        }
    }

    public static void EXP(RegistroLexico exp) throws Exception {

        EXPS(exp);

        if (null != token) {
            switch (token) {
                case LESS:
                    casaToken(Token.LESS);
                    EXPS(exp);
                    break;
                case GREATHER:
                    casaToken(Token.GREATHER);
                    EXPS(exp);
                    break;
                case LESS_EQUALS:
                    casaToken(Token.LESS_EQUALS);
                    EXPS(exp);
                    break;
                case GREATHER_EQUAL:
                    casaToken(Token.GREATHER_EQUAL);
                    EXPS(exp);
                    break;
                case EQUAL:
                    casaToken(Token.EQUAL);
                    EXPS(exp);
                    break;
                case DIFFERENT:
                    casaToken(Token.DIFFERENT);
                    EXPS(exp);
                    break;
                default:
                    break;
            }
        }
    }

    public static void EXPS(RegistroLexico exps) throws Exception {

        boolean negarExpressao = false;

        if (token == Token.SUM) {
            casaToken(Token.SUM);
        } else if (token == Token.MINUS) {
            casaToken(Token.MINUS);
            negarExpressao = true;
        }

        T(exps);

        // negar o valor de T1 se necessario 
        if (negarExpressao) {

            if (exps.getTipo() == Tipo.INTEIRO && exps.getTamanho() == 0) {

                // copia E.end para regA
                codigo.mov("ax", "DS:[" + exps.getEndereco() + "]", "copia o valor de E.end para regA");
                // nega ax
                codigo.neg("ax", "nega o valor de regA");

                exps.setEndereco(codigo.novoTemp(2));

                // copia para um novo endereco o novo valor de E.end
                codigo.mov("DS:[" + exps.getEndereco() + "]", "ax", "copia para o temporario o valor negado de E");

            } else {
                System.out.println(pos.getLineNumber() + ":tipos incompatíveis.");
                throw new Exception();
            }
        }

        // OBS: olhar a partir daqui se todos os tipos podem entrar
        while (token == Token.SUM || token == Token.MINUS || token == Token.OR) {

            final int SOMA = 0;
            final int SUBTRACAO = 1;
            final int OR = 2;
            int operador = -1;

            if (null != token) {
                switch (token) {
                    case SUM:
                        casaToken(Token.SUM);
                        operador = SOMA;
                        break;
                    case MINUS:
                        casaToken(Token.MINUS);
                        operador = SUBTRACAO;
                        break;
                    case OR:
                        casaToken(Token.OR);
                        operador = OR;
                        break;
                }
            }

            RegistroLexico t2 = new RegistroLexico();

            T(t2);

            // carrega o conteudo de E.end para regA
            codigo.mov("ax", "DS:[" + exps.getEndereco() + "]", "carrega E.end para regA");
            // carrega o conteudo de t2.end para regB
            codigo.mov("bx", "DS:[" + t2.getEndereco() + "]", "carrega T2.end para regB");

            switch (operador) {
                case SOMA:
                    codigo.add("ax", "bx", "soma regA com regB");
                    break;
                case SUBTRACAO:
                    codigo.sub("ax", "bx", "subtrai regA de regB");
                    break;
                case OR:
                    System.out.println("Fazer o oeprador OR");
                    break;
            }

            // E.end = NovoTemp
            exps.setEndereco(codigo.novoTemp(2));

            // guarda resultado de regA em E.end
            codigo.mov("DS:[" + exps.getEndereco() + "]", "ax", "E.end recebe o valor de regA");
        }

    }

    public static void T(RegistroLexico t) throws Exception {
        F(t);

        // OBS: olhar se todos os tipos vao passar daqui
        while (token == Token.DIVIDE || token == Token.MULTIPLY || token == Token.AND || token == Token.MOD) {

            final int DIVISAO = 0;
            final int MULTIPLICACAO = 1;
            final int AND = 2;
            final int MOD = 3;
            int operador = -1;

            if (null != token) {
                switch (token) {
                    case DIVIDE:
                        casaToken(Token.DIVIDE);
                        operador = DIVISAO;
                        break;
                    case MULTIPLY:
                        casaToken(Token.MULTIPLY);
                        operador = MULTIPLICACAO;
                        break;
                    case AND:
                        casaToken(Token.AND);
                        operador = AND;
                        break;
                    case MOD:
                        casaToken(Token.MOD);
                        operador = MOD;
                        break;
                    default:
                        break;
                }
            }

            RegistroLexico f2 = new RegistroLexico();
            F(f2);

            // carrega o conteudo de T.end no regA
            codigo.mov("ax", "DS:[" + t.getEndereco() + "]", "carrega T.end para regA");
            // carrega o conteudo de F2.end no regB
            codigo.mov("bx", "DS:[" + f2.getEndereco() + "]", "carrega F2.end para regA");

            switch (operador) {
                case DIVISAO:
                    codigo.idiv("bx", "divide ax por bx");
                    break;
                case MULTIPLICACAO:
                    codigo.imul("bx", "multiplica ax por bx");
                    break;
                case AND:
                    System.out.println("Olhar como fazer and");
                    break;
                case MOD:
                    System.out.println("olhar como fazer mod");
                    break;
            }

            // OBS: olhar esse tamanho
            // T.end = NovoTemps
            t.setEndereco(codigo.novoTemp(2));
            // Guarda resultado de regA em T.end
            codigo.mov("DS:[" + t.getEndereco() + "]", "ax", "Guarda resultado de regA em T.end");
        }
    }

    public static void F(RegistroLexico f) throws Exception {

        if (null == token) {
            // OLHAR
            error();
        } else switch (token) {
            case OPEN_PARENTHESIS:
                casaToken(Token.OPEN_PARENTHESIS);
                EXP(f);
                casaToken(Token.CLOSE_PARENTHESIS);
                break;
            case NOT:
                casaToken(Token.NOT);
                System.out.println("OLHAR COMO FAZER O NOT");
                F(f);
                break;
            case CONST:
                f.setTipo(result.getTipo());
                f.setClasse(result.getClasse());
                f.setTamanho(0);// eu acho que aqui sempre sera 0
                if (f.getTipo() == Tipo.INTEIRO) {
                    
                    int valor;
                    
                    // F.end = NovoTemp
                    f.setEndereco(codigo.novoTemp(2));
                    valor = Integer.parseInt(result.getLexema());
                    
                    // mov regA, imed
                    codigo.mov("ax", "" + valor, "Valor a ser copiado para o temporario");
                    // mov F.end, regA
                    codigo.mov("DS:[" + f.getEndereco() + "]", "ax", "copia constante para temporario");
                } else {
                    
                    result.setTamanho( result.getLexema().length() - 2 );
                    
                    // F.end = NovoTemp
                    f.setEndereco(codigo.novaVariavel(result.getTamanho() ));
                    
                    String valor = result.getLexema().substring(1, result.getTamanho()+1 );
                    
                    // copia String para o seu temporario
                    codigo.stringToTemp( valor,"const string em "+ f.getEndereco() +"" );
                }   casaToken(Token.CONST);
                break;
            case ID:
                RegistroLexico id = variaveis.getVar(result.getLexema());
                if (id == null) {
                    System.out.println("ERRO: Variavel [" + result.getLexema() + "] nao foi declarada.");
                    throw new Exception("");
                }   f.setClasse(result.getClasse());
                // F.end = id.end
                f.setEndereco(id.getEndereco());
                // F.tipo = id.tipo
                f.setTipo(id.getTipo());
                // F.tam = id.tam
                f.setTamanho(id.getTamanho());
                casaToken(Token.ID);
                if (token == Token.OPEN_BRACKET) {
                    casaToken(Token.OPEN_BRACKET);
                    EXP(f);
                    
                    System.out.println("OLHAR COMO FAZER ACESSO A UM VETOR");
                    System.out.println( f );
                    
                    casaToken(Token.CLOSE_BRACKET);
                }   break;
            default:
                // OLHAR
                error();
                break;
        }
    }

}
