package sistema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import customExceptions.ClienteBloqueadoException;
import customExceptions.PlacaNaoEncontradaException;
import customExceptions.VeiculoJaEstacionadoException;
import interfaces.Cliente;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteProfessor;

enum TipoCliente {
    AVULSO, ALUNO, PROFESSOR, EMPRESA
}

public class Estacionamento {
    private final int id;
    private final Sistema sys;
    private final String nome;
    private final HashMap<String, Cliente> veiculosEstacionados;
    private final HashSet<String> placasBloqueadas;
    private final HashMap<String, Ticket> ticketsAbertos;
    private final HashSet<Ticket> registros;
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Estacionamento(int id, String nome){
        this.id = id;
        this.sys = new Sistema(id);
        this.nome = nome;
        this.veiculosEstacionados = new HashMap<>();
        this.placasBloqueadas = new HashSet<>();
        this.ticketsAbertos = new HashMap<>();
        this.registros = new HashSet<>();
    }

    public int getId(){ return id; }
    public String getNome() { return nome; }

    //----------------------------- METODOS DE ENTRADA E SAIDA DE VEICULOS -----------------------------    

    public boolean estacionaVeiculo(String placa, String hEntrada){
        if (estaBloqueado(placa)) throw new ClienteBloqueadoException();
        // verifica se o veiculo ja esta estacionado
        if (estaEstacionado(placa)) throw new VeiculoJaEstacionadoException(placa);

        Cliente cliente = sys.procuraClientesPorPlaca(placa);

        if (cliente == null){ // significa que ou o cliente nao eh pre registrado
            // emite um ticket
            ticketsAbertos.put(placa, new Ticket(null, placa, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo aos estacionados
            veiculosEstacionados.put(placa, null);
            return true;
        }

        if (cliente instanceof ClienteProfessor && temVeiculoEstacionado(cliente)){
            // se for professor e ja tiver um outro veiculo estacionado

            // emite um ticket
            ticketsAbertos.put(placa, new Ticket(null, placa, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo aos estacionados
            veiculosEstacionados.put(placa, null);
            return true;
        }
        // todos os outros casos

        // emite um ticket
        ticketsAbertos.put(placa, new Ticket(cliente, placa, LocalDateTime.parse(hEntrada, formatadorData)));
        // adiciona o veiculo aos estacionados
        veiculosEstacionados.put(placa,cliente);
        return true;
    }

    public boolean retiraVeiculo(String placa, String hSaida, boolean pagou){
        if (!estaEstacionado(placa)) throw new PlacaNaoEncontradaException(placa);
        
        Cliente cliente = procuraClientePlaca(placa);
        Ticket ticket = procuraTicketAberto(placa);
        LocalDateTime dataHoraSaida = LocalDateTime.parse(hSaida, formatadorData);
        
        // --- LÓGICA DO CLIENTE FREQUENTE ---
        if (cliente == null) {
            boolean temDesconto = verificarClienteFrequente(placa, dataHoraSaida);
            if (temDesconto) {
                // Passamos o nome do desconto e a porcentagem (10% = 0.10)
                ticket.configurarDesconto("Cliente Frequente", 0.10); 
            } else {
                ticket.configurarDesconto("nenhum", 0.0);
            }
        } else {
            // Clientes cadastrados não recebem esse desconto
            ticket.configurarDesconto("nenhum", 0.0); 
        }

        // Ao setar a saída, o ticket deve calcular o custo, abater o desconto e guardar os valores finais
        ticket.setHSaida(dataHoraSaida);
        
        // --- COBRANÇA DO ALUNO ---
        if (cliente instanceof ClienteAluno clienteAluno){
            boolean saldoFicouNegativo = clienteAluno.pagar(ticket.getValor());
            
            if (saldoFicouNegativo) {
                placasBloqueadas.add(placa);
            }
        }

        // --- REMOÇÃO DO VEÍCULO ---
        String chaveParaRemover = null;
        for (String p : veiculosEstacionados.keySet()) {
            if (p.equals(placa)) {
                chaveParaRemover = p;
                break;
            }
        }
        
        if (chaveParaRemover != null) {
            veiculosEstacionados.remove(chaveParaRemover);
        }

        return true;
    }

    //----------------------------- METODOS DE VERIFICACAO DE ESTADO -----------------------------    

    // verificacao de se um veiculo esta estacionado a partir da placa
    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.containsKey(placa);
    }

    // verifica se uma placa esta bloqueada
    public boolean estaBloqueado(String placa){
        return placasBloqueadas.contains(placa);
    }

    //----------------------------- METODOS DE BUSCA COM RETORNO OU USO DE REFERENCIA -----------------------------

    private Cliente procuraClientePlaca(String placa){
        return veiculosEstacionados.entrySet().stream()
            .filter(e -> e.getKey().equals(placa)) 
            .map(e -> e.getValue())
            .findFirst()
            .orElse(null);
    }

    // retorna uma referencia a um ticket aberto (sem valor e horario de saida) a partir de uma placa
    private Ticket procuraTicketAberto(String placa){
        return ticketsAbertos.get(placa);
    }

    // verifica se um cliente pre registrado ja tem um veiculo estacionado a partir de uma referencia
    private boolean temVeiculoEstacionado(Cliente cliente){
        return cliente.getPlacas().stream().anyMatch(p -> estaEstacionado(p));
    }

    // procura tickets a partir de um id de cliente
    public List<Ticket> procuraRegistrosPorCliente(String idCliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(idCliente))
            .toList();
    }

    // procura tickets a partir de uma placa
    public List<Ticket> procuraRegistrosPorPlaca(String placa){
        return registros.stream()
            .filter(t -> t.getPlaca().equals(placa))
            .toList();
    }

    //----------------------------- METODOS AUXILIARES DE DESCONTO -----------------------------

    private boolean verificarClienteFrequente(String placa, LocalDateTime dataSaidaAtual) {
        LocalDateTime tresDiasAtras = dataSaidaAtual.minusDays(3);
        
        return registros.stream()
            // Filtra os tickets dessa mesma placa (lembrando que para avulso, o ID é a placa)
            .filter(t -> t.getIdCliente().equals(placa))
            // Filtra apenas tickets já finalizados (que possuem hora de saída)
            .filter(t -> t.getHSaida() != null)
            // Verifica se alguma dessas saídas ocorreu entre 3 dias atrás e a data atual
            .anyMatch(t -> t.getHSaida().isAfter(tresDiasAtras) && t.getHSaida().isBefore(dataSaidaAtual));
    }

}