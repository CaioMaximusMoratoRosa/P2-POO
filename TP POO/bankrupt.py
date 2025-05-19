
import random

class Propriedade:
    def __init__(self, preco, aluguel):
        self.preco = preco
        self.aluguel = aluguel
        self.dono = None

    def resetar(self):
        self.dono = None


class Jogador:
    def __init__(self, nome, comportamento):
        self.nome = nome
        self.comportamento = comportamento
        self.dinheiro = 300
        self.posicao = 0
        self.ativo = True
        self.vitorias = 0

    def decidir_compra(self, propriedade):
        if self.comportamento == "impulsivo":
            return True
        elif self.comportamento == "exigente":
            return propriedade.aluguel > 50
        elif self.comportamento == "cauteloso":
            return self.dinheiro - propriedade.preco >= 80
        elif self.comportamento == "aleatorio":
            return random.choice([True, False])
        return False

    def resetar(self):
        self.dinheiro = 300
        self.posicao = 0
        self.ativo = True


class Jogo:
    def __init__(self, propriedades):
        self.propriedades = propriedades
        self.jogadores = [
            Jogador("Jogador 1", "impulsivo"),
            Jogador("Jogador 2", "exigente"),
            Jogador("Jogador 3", "cauteloso"),
            Jogador("Jogador 4", "aleatorio")
        ]
        self.rodadas = 0
        self.timeout = False

    def jogar_partida(self):
        for p in self.propriedades:
            p.resetar()
        for j in self.jogadores:
            j.resetar()

        ordem = self.jogadores[:]
        random.shuffle(ordem)
        self.rodadas = 0
        self.timeout = False

        while self.rodadas < 1000 and sum(j.ativo for j in ordem) > 1:
            for jogador in ordem:
                if not jogador.ativo:
                    continue
                self.jogar_turno(jogador)
                if sum(j.ativo for j in ordem) == 1:
                    break
            self.rodadas += 1

        if self.rodadas == 1000:
            self.timeout = True
            vivos = [j for j in ordem if j.ativo]
            vencedor = max(vivos, key=lambda j: j.dinheiro)
        else:
            vencedor = [j for j in ordem if j.ativo][0]

        vencedor.vitorias += 1
        return self.rodadas, self.timeout

    def jogar_turno(self, jogador):
        dado = random.randint(1, 6)
        nova_posicao = jogador.posicao + dado
        jogador.posicao = nova_posicao % len(self.propriedades)
        if nova_posicao >= len(self.propriedades):
            jogador.dinheiro += 100

        propriedade = self.propriedades[jogador.posicao]
        if propriedade.dono is None:
            if jogador.decidir_compra(propriedade) and jogador.dinheiro >= propriedade.preco:
                jogador.dinheiro -= propriedade.preco
                propriedade.dono = jogador
        elif propriedade.dono != jogador:
            aluguel = propriedade.aluguel
            jogador.dinheiro -= aluguel
            propriedade.dono.dinheiro += aluguel

            if jogador.dinheiro < 0:
                jogador.ativo = False
                for p in self.propriedades:
                    if p.dono == jogador:
                        p.dono = None


def carregar_propriedades(caminho):
    propriedades = []
    with open(caminho, 'r') as arquivo:
        for linha in arquivo:
            preco, aluguel = map(int, linha.strip().split())
            propriedades.append(Propriedade(preco, aluguel))
    return propriedades


def simular_jogo():
    propriedades = carregar_propriedades("gameConfig.txt")
    jogo = Jogo(propriedades)
    resultados = {
        "timeouts": 0,
        "turnos_total": 0,
        "vitorias": {
            "impulsivo": 0,
            "exigente": 0,
            "cauteloso": 0,
            "aleatorio": 0
        }
    }

    for _ in range(300):
        turnos, timeout = jogo.jogar_partida()
        resultados["turnos_total"] += turnos
        if timeout:
            resultados["timeouts"] += 1

    for jogador in jogo.jogadores:
        resultados["vitorias"][jogador.comportamento] += jogador.vitorias

    print("Partidas que treminaram por timeout:", resultados["timeouts"])
    print("Média de turnos por partida:", resultados["turnos_total"] / 300)
    print("Vitórias por comportamento:")
    for comportamento, vitorias in resultados["vitorias"].items():
        print(f"{comportamento.capitalize()}: {vitorias} ({(vitorias / 300) * 100:.2f}%)")
    comportamento_campeao = max(resultados["vitorias"], key=resultados["vitorias"].get)
    print("Comportamento que mais venceu:", comportamento_campeao.capitalize())


simular_jogo()
