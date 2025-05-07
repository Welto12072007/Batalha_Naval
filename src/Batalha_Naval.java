import java.util.Scanner;
import java.util.Random;

public class Batalha_Naval {
    char AGUA = '~';
    char NAVIO = 'N';
    char TIRO_AGUA = 'o';
    char TIRO_NAVIO = 'X';

    Scanner ler = new Scanner(System.in);
    Random aleatorio = new Random();

    class Jogador {
        String nome;
        char[][] mapa = new char[10][10];
        boolean[][] tirosRecebidos = new boolean[10][10];

        Jogador(String n) {
            nome = n;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    mapa[i][j] = AGUA;
                    tirosRecebidos[i][j] = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        Batalha_Naval jogo = new Batalha_Naval();
        jogo.iniciarJogo();
    }

    public void iniciarJogo() {
        System.out.println("=== BATALHA NAVAL ===");
        System.out.print("1 - Jogar contra outro jogador\n2 - Jogar contra o computador\nEscolha: ");
        int modo = lerIntValido(1, 2);

        Jogador jogador1 = new Jogador(lerNome("Jogador 1"));
        boolean auto1 = escolherModoAlocacao(jogador1.nome);
        alocarNavios(jogador1, auto1);

        Jogador jogador2;
        boolean auto2;

        if (modo == 1) {
            jogador2 = new Jogador(lerNome("Jogador 2"));
            auto2 = escolherModoAlocacao(jogador2.nome);
        } else {
            jogador2 = new Jogador("Computador");
            auto2 = true;
        }
        alocarNavios(jogador2, auto2);

        Jogador atual = jogador1;
        Jogador oponente = jogador2;
        int turno = 1;
        boolean jogoAtivo = true;

        while (jogoAtivo) {
            boolean vezDoJogador = true;
            while (vezDoJogador && jogoAtivo) {
                System.out.println("\n=== PLACAR ===");
                System.out.println("Turno: " + turno);
                System.out.println("Jogador atual: " + atual.nome);

                System.out.println("\n" + atual.nome + ", é sua vez!");
                mostrarMapaParaTiro(oponente);

                int linha, coluna;
                if (atual.nome.equals("Computador")) {
                    do {
                        linha = aleatorio.nextInt(10);
                        coluna = aleatorio.nextInt(10);
                    } while (oponente.tirosRecebidos[linha][coluna]);
                    System.out.println("Computador atirou em " + linha + "," + coluna);
                } else {
                    linha = lerIntValido(0, 9, "Linha (0-9): ");
                    coluna = lerColunaLetra("Coluna (A-J): ");
                    if (oponente.tirosRecebidos[linha][coluna]) {
                        System.out.println("Você já atirou aí! Tente novamente.");
                        continue;
                    }
                }

                oponente.tirosRecebidos[linha][coluna] = true;
                if (oponente.mapa[linha][coluna] == NAVIO) {
                    System.out.println("ACERTOU!");
                    oponente.mapa[linha][coluna] = TIRO_NAVIO;
                    if (barcoAfundado(oponente, linha, coluna)) {
                        System.out.println("Você afundou um navio!");
                    }
                    if (todosNaviosAfundados(oponente)) {
                        System.out.println("\nFIM DE JOGO! " + atual.nome + " venceu!");
                        jogoAtivo = false;
                    } else {
                        vezDoJogador = false;
                    }
                } else {
                    System.out.println("ÁGUA!");
                    oponente.mapa[linha][coluna] = TIRO_AGUA;
                    vezDoJogador = false;
                }
            }

            if (jogoAtivo) {
                Jogador temp = atual;
                atual = oponente;
                oponente = temp;
                turno++;
            }
        }
    }

    public String lerNome(String texto) {
        System.out.print(texto + " - Digite seu nome: ");
        return ler.nextLine().trim();
    }

    public boolean escolherModoAlocacao(String nome) {
        System.out.println("\n" + nome + ", como deseja posicionar seus navios?");
        System.out.println("1 - Manual");
        System.out.println("2 - Automático");
        System.out.print("Escolha: ");
        int opcao = lerIntValido(1, 2);
        return opcao == 2;
    }

    public void alocarNavios(Jogador j, boolean automatico) {
        System.out.println("\nAlocando navios para " + j.nome);
        int[] tamanhos = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

        for (int tamanho : tamanhos) {
            boolean valido = false;
            while (!valido) {
                int linha, coluna;
                boolean horizontal;

                if (!automatico) {
                    System.out.println("Navio de tamanho " + tamanho);
                    linha = lerIntValido(0, 9, "Linha inicial (0-9): ");
                    coluna = lerColunaLetra("Coluna inicial (A-J): ");
                    System.out.print("Direção (H = horizontal, V = vertical): ");
                    String dir = ler.next().toUpperCase();
                    ler.nextLine();
                    horizontal = dir.equals("H");
                } else {
                    linha = aleatorio.nextInt(10);
                    coluna = aleatorio.nextInt(10);
                    horizontal = aleatorio.nextBoolean();
                }

                if (podeAlocar(j.mapa, linha, coluna, tamanho, horizontal)) {
                    for (int i = 0; i < tamanho; i++) {
                        int r = linha + (horizontal ? 0 : i);
                        int c = coluna + (horizontal ? i : 0);
                        j.mapa[r][c] = NAVIO;
                    }
                    valido = true;
                } else if (!automatico) {
                    System.out.println("Posição inválida. Tente de novo.");
                }
            }
        }
    }

    public boolean podeAlocar(char[][] mapa, int l, int c, int tam, boolean h) {
        if (h && c + tam > 10) return false;
        if (!h && l + tam > 10) return false;
        for (int i = 0; i < tam; i++) {
            int r = l + (h ? 0 : i);
            int col = c + (h ? i : 0);
            if (mapa[r][col] != AGUA) return false;
        }
        return true;
    }

    public boolean barcoAfundado(Jogador j, int l, int c) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int d = 0; d < 4; d++) {
            int nl = l + dx[d];
            int nc = c + dy[d];
            boolean seguir = true;
            while (seguir && nl >= 0 && nl < 10 && nc >= 0 && nc < 10) {
                if (j.mapa[nl][nc] == NAVIO) return false;
                if (j.mapa[nl][nc] == AGUA || j.mapa[nl][nc] == TIRO_AGUA) {
                    seguir = false;
                } else {
                    nl += dx[d];
                    nc += dy[d];
                }
            }
        }
        return true;
    }

    public boolean todosNaviosAfundados(Jogador j) {
        for (int i = 0; i < 10; i++)
            for (int k = 0; k < 10; k++)
                if (j.mapa[i][k] == NAVIO) return false;
        return true;
    }

    public void mostrarMapaParaTiro(Jogador j) {
        System.out.println("     A B C D E F G H I J");
        System.out.println("   +---------------------+");
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + " | ");
            for (int k = 0; k < 10; k++) {
                if (j.tirosRecebidos[i][k]) {
                    System.out.print(j.mapa[i][k] + " ");
                } else {
                    System.out.print(AGUA + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println("   +---------------------+");
    }

    public int lerIntValido(int min, int max) {
        while (true) {
            if (ler.hasNextInt()) {
                int num = ler.nextInt();
                ler.nextLine();
                if (num >= min && num <= max) return num;
            } else {
                ler.nextLine();
            }
            System.out.print("Digite um número entre " + min + " e " + max + ": ");
        }
    }

    public int lerIntValido(int min, int max, String msg) {
        System.out.print(msg);
        return lerIntValido(min, max);
    }

    public int lerColunaLetra(String msg) {
        while (true) {
            System.out.print(msg);
            String entrada = ler.nextLine().trim().toUpperCase();
            if (entrada.length() == 1) {
                char letra = entrada.charAt(0);
                if (letra >= 'A' && letra <= 'J') {
                    return letra - 'A';
                }
            }
            System.out.println("Entrada inválida. Digite uma letra de A a J.");
        }
    }
}