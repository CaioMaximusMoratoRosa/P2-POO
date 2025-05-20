import java.util.*;

public class Jogo {
    private List<Propriedade> propriedades;
    private List<Jogador> jogadores;
    private int rodadas;
    private boolean timeout;

    public Jogo(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
        this.jogadores = Arrays.asList(
            new Jogador("Jogador 1", "impulsivo"),
            new Jogador("Jogador 2", "exigente"),
            new Jogador("Jogador 3", "cauteloso"),
            new Jogador("Jogador 4", "aleatorio")
        );
    }

    public int getRodadas() { return rodadas; }
    public boolean isTimeout() { return timeout; }

    public void jogarPartida() {
        for (Propriedade p : propriedades) p.resetar();
        for (Jogador j : jogadores) j.resetar();

        List<Jogador> ordem = new ArrayList<>(jogadores);
        Collections.shuffle(ordem);
        rodadas = 0;
        timeout = false;

        while (rodadas < 1000 && ativos(ordem) > 1) {
            for (Jogador jogador : ordem) {
                if (!jogador.isAtivo()) continue;
                jogarTurno(jogador);
                if (ativos(ordem) == 1) break;
            }
            rodadas++;
        }

        Jogador vencedor;
        if (rodadas == 1000) {
            timeout = true;
            List<Jogador> vivos = new ArrayList<>();
            for (Jogador j : ordem)
                if (j.isAtivo()) vivos.add(j);
            vencedor = Collections.max(vivos, Comparator.comparingInt(Jogador::getDinheiro));
        } else {
            for (Jogador j : ordem)
                if (j.isAtivo()) {
                    vencedor = j;
                    vencedor.addVitoria();
                    return;
                }
            return;
        }

        vencedor.addVitoria();
    }

    private void jogarTurno(Jogador jogador) {
        Random rand = new Random();
        int dado = rand.nextInt(6) + 1;
        int novaPosicao = (jogador.getPosicao() + dado) % propriedades.size();
        if (jogador.getPosicao() + dado >= propriedades.size())
            jogador.setDinheiro(jogador.getDinheiro() + 100);

        jogador.setPosicao(novaPosicao);
        Propriedade prop = propriedades.get(novaPosicao);

        if (prop.getDono() == null) {
            if (jogador.decidirCompra(prop) && jogador.getDinheiro() >= prop.getPreco()) {
                jogador.setDinheiro(jogador.getDinheiro() - prop.getPreco());
                prop.setDono(jogador);
            }
        } else if (prop.getDono() != jogador) {
            jogador.setDinheiro(jogador.getDinheiro() - prop.getAluguel());
            prop.getDono().setDinheiro(prop.getDono().getDinheiro() + prop.getAluguel());

            if (jogador.getDinheiro() < 0) {
                jogador.setAtivo(false);
                for (Propriedade p : propriedades)
                    if (p.getDono() == jogador)
                        p.setDono(null);
            }
        }
    }

    private int ativos(List<Jogador> jogadores) {
        int count = 0;
        for (Jogador j : jogadores)
            if (j.isAtivo()) count++;
        return count;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }
}