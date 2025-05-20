import java.io.*;
import java.util.*;

public class Main {
    public static List<Propriedade> carregarPropriedades(String caminho) throws IOException {
        List<Propriedade> propriedades = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = reader.readLine()) != null) {
            String[] partes = linha.trim().split("\s+");
            int preco = Integer.parseInt(partes[0]);
            int aluguel = Integer.parseInt(partes[1]);
            propriedades.add(new Propriedade(preco, aluguel));
        }
        reader.close();
        return propriedades;
    }

    public static void main(String[] args) throws IOException {
        List<Propriedade> propriedades = carregarPropriedades("gameConfig.txt");
        Jogo jogo = new Jogo(propriedades);

        Map<String, Integer> vitorias = new HashMap<>();
        for (String tipo : Arrays.asList("impulsivo", "exigente", "cauteloso", "aleatorio")) {
            vitorias.put(tipo, 0);
        }

        int timeouts = 0;
        int totalTurnos = 0;

        for (int i = 0; i < 300; i++) {
            jogo.jogarPartida();
            if (jogo.isTimeout()) timeouts++;
            totalTurnos += jogo.getRodadas();
        }

        for (Jogador j : jogo.getJogadores()) {
            vitorias.put(j.getComportamento(), j.getVitorias());
        }

        System.out.println("Partidas que terminaram por timeout: " + timeouts);
        System.out.printf("Média de turnos por partida: %.2f%n", totalTurnos / 300.0);
        System.out.println("Vitórias por comportamento:");
        for (Map.Entry<String, Integer> entry : vitorias.entrySet()) {
            double percent = (entry.getValue() / 300.0) * 100;
            System.out.printf("%s: %d (%.2f%%)%n", capitalize(entry.getKey()), entry.getValue(), percent);
        }

        String campeao = Collections.max(vitorias.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Comportamento que mais venceu: " + capitalize(campeao));
    }

    private static String capitalize(String texto) {
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}