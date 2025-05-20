import java.util.Random;

public class Jogador {
    private String nome;
    private String comportamento;
    private int dinheiro;
    private int posicao;
    private boolean ativo;
    private int vitorias;

    public Jogador(String nome, String comportamento) {
        this.nome = nome;
        this.comportamento = comportamento;
        this.dinheiro = 300;
        this.posicao = 0;
        this.ativo = true;
        this.vitorias = 0;
    }

    public boolean decidirCompra(Propriedade propriedade) {
        switch (comportamento) {
            case "impulsivo":
                return true;
            case "exigente":
                return propriedade.getAluguel() > 50;
            case "cauteloso":
                return dinheiro - propriedade.getPreco() >= 80;
            case "aleatorio":
                return new Random().nextBoolean();
            default:
                return false;
        }
    }

    public void resetar() {
        dinheiro = 300;
        posicao = 0;
        ativo = true;
    }

    public String getComportamento() { return comportamento; }
    public boolean isAtivo() { return ativo; }
    public int getPosicao() { return posicao; }
    public int getDinheiro() { return dinheiro; }
    public int getVitorias() { return vitorias; }

    public void setPosicao(int posicao) { this.posicao = posicao; }
    public void setDinheiro(int dinheiro) { this.dinheiro = dinheiro; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public void addVitoria() { vitorias++; }
}