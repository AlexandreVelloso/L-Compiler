package sintatico;

import enums.Classe;
import lexico.Lexico;
import enums.Tipo;
import enums.Token;
import lexico.*;
import semantico.GeradorCodigo;
import semantico.Variaveis;
import util.FilePosition;

public class Sintatico {

    private static Token token;
    private static RegistroLexico result;
    private static FilePosition pos = FilePosition.getInstance();
    private static Variaveis variaveis = Variaveis.getInstance();
    private static GeradorCodigo codigo = GeradorCodigo.getInstance();

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

        // COMANDO
        COMANDO();

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

    public static void COMANDO() throws Exception {

        while (token == Token.FOR || token == Token.IF || token == Token.READLN || token == Token.WRITE
                || token == Token.WRITELN || token == Token.ID || token == Token.SEMICOLON) {

            if (null != token) {
                switch (token) {
                    case FOR:
                        casaToken(Token.FOR);
                        casaToken(Token.ID);
                        casaToken(Token.ATTR);
                        EXP();
                        casaToken(Token.TO);
                        EXP();
                        if (token == Token.STEP) {
                            casaToken(Token.STEP);
                            casaToken(Token.CONST);
                        }
                        casaToken(Token.DO);
                        BLOCO();
                        break;
                    case IF:
                        casaToken(Token.IF);
                        EXP();
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
                            EXP();
                            casaToken(Token.CLOSE_BRACKET);
                        }
                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case WRITE:
                        casaToken(Token.WRITE);
                        casaToken(Token.OPEN_PARENTHESIS);
                        EXP();
                        while (token == Token.COMMA) {
                            casaToken(Token.COMMA);
                            EXP();
                        }
                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case WRITELN:
                        casaToken(Token.WRITELN);
                        casaToken(Token.OPEN_PARENTHESIS);
                        EXP();
                        while (token == Token.COMMA) {
                            casaToken(Token.COMMA);
                            EXP();
                        }
                        casaToken(Token.CLOSE_PARENTHESIS);
                        casaToken(Token.SEMICOLON);
                        break;
                    case ID:
                        casaToken(Token.ID);
                        casaToken(Token.ATTR);
                        EXP();
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

        if (token == Token.BEGIN) {
            casaToken(Token.BEGIN);

            COMANDO();
            casaToken(Token.END);
        } else {
            COMANDO();
        }
    }

    public static void CMD() throws Exception {

        do {

            if (token == Token.READLN) {
                casaToken(Token.READLN);
                casaToken(Token.OPEN_PARENTHESIS);
                casaToken(Token.ID);

                if (token == Token.OPEN_BRACKET) {
                    casaToken(Token.OPEN_BRACKET);
                    EXP();
                    casaToken(Token.CLOSE_BRACKET);
                }

                casaToken(Token.CLOSE_PARENTHESIS);
            } else if (token == Token.WRITE) {
                casaToken(Token.WRITE);
                casaToken(Token.OPEN_PARENTHESIS);
                EXP();

                if (token == Token.COMMA) {
                    casaToken(Token.COMMA);
                    EXP();
                }

                casaToken(Token.CLOSE_PARENTHESIS);

            } else if (token == Token.WRITELN) {
                casaToken(Token.WRITELN);
                casaToken(Token.OPEN_PARENTHESIS);
                EXP();

                if (token == Token.COMMA) {
                    casaToken(Token.COMMA);
                    EXP();
                }

                casaToken(Token.CLOSE_PARENTHESIS);

            } else if (token == Token.ID) {
                casaToken(Token.ID);
                ATRIBUICAO();
            }

            casaToken(Token.SEMICOLON);

        } while (token == Token.SEMICOLON || token == Token.READLN || token == Token.WRITE || token == Token.WRITELN
                || token == Token.ATTR);
    }

    public static void EXP() throws Exception {
        EXPS();

        if (token == Token.LESS) {
            casaToken(Token.LESS);
            EXPS();
        } else if (token == Token.GREATHER) {
            casaToken(Token.GREATHER);
            EXPS();
        } else if (token == Token.LESS_EQUALS) {
            casaToken(Token.LESS_EQUALS);
            EXPS();
        } else if (token == Token.GREATHER_EQUAL) {
            casaToken(Token.GREATHER_EQUAL);
            EXPS();
        } else if (token == Token.EQUAL) {
            casaToken(Token.EQUAL);
            EXPS();
        } else if (token == Token.DIFFERENT) {
            casaToken(Token.DIFFERENT);
            EXPS();
        }
    }

    public static void EXPS() throws Exception {

        if (token == Token.SUM) {
            casaToken(Token.SUM);
        } else if (token == Token.MINUS) {
            casaToken(Token.MINUS);
        }

        T();

        while (token == Token.SUM || token == Token.MINUS || token == Token.OR) {

            if (token == Token.SUM) {
                casaToken(Token.SUM);
            } else if (token == Token.MINUS) {
                casaToken(Token.MINUS);
            } else if (token == Token.OR) {
                casaToken(Token.OR);
            }

            T();
        }

    }

    public static void T() throws Exception {
        F();

        while (token == Token.MULTIPLY || token == Token.DIVIDE || token == Token.MOD || token == Token.AND) {

            if (token == Token.MULTIPLY) {
                casaToken(Token.MULTIPLY);
            } else if (token == Token.DIVIDE) {
                casaToken(Token.DIVIDE);
            } else if (token == Token.MOD) {
                casaToken(Token.MOD);
            } else if (token == Token.AND) {
                casaToken(Token.AND);
            }

            F();
        }
    }

    public static void F() throws Exception {

        if (token == Token.OPEN_PARENTHESIS) {
            casaToken(Token.OPEN_PARENTHESIS);
            EXP();
            casaToken(Token.CLOSE_PARENTHESIS);

        } else if (token == Token.NOT) {
            casaToken(Token.NOT);
            F();

        } else if (token == Token.CONST) {
            casaToken(Token.CONST);

        } else if (token == Token.ID) {
            casaToken(Token.ID);

            if (token == Token.OPEN_BRACKET) {
                casaToken(Token.OPEN_BRACKET);
                EXP();
                casaToken(Token.CLOSE_BRACKET);
            }
        } else {
            // OLHAR
            error();
        }
    }

}
