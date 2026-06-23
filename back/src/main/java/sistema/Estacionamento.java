package sistema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import customExceptions.ClienteBloqueadoException;
import customExceptions.PlacaJaRegistradaException;
import customExceptions.PlacaNaoEncontradaException;
import customExceptions.VeiculoJaEstacionadoException;
import interfaces.Cliente;
import interfaces.PreRegistrado;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteAvulso;
import tiposClientes.ClienteProfessor;

enum TipoCliente {
    AVULSO, ALUNO, PROFESSOR, EMPRESA
}

public class Estacionamento {
    private final int id;
    private final Sistema sys;
    private final String nome;
    private final HashMap<Placa, Cliente> veiculosEstacionados;
    private final HashSet<Cliente> clientesBloqueados;
    private final HashSet<Ticket> registros;
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Estacionamento(int id, String nome){
        this.id = id;
        this.sys = new Sistema(id);
        this.nome = nome;
        this.veiculosEstacionados = new HashMap<>();
        this.clientesBloqueados = new HashSet<>();
        this.registros = new HashSet<>();
    }

    public int getId(){ return id; }
    public String getNome() { return nome; }

    //----------------------------- METODOS DE ENTRADA E SAIDA DE VEICULOS -----------------------------    

    public boolean retiraVeiculo(String placa, String hSaida){
        if (!estaEstacionado(placa)) throw new PlacaNaoEncontradaException(placa);
        
        Cliente cliente = procuraClientePlaca(placa);
        Ticket ticket = procuraTicketAberto(cliente);
        LocalDateTime dataHoraSaida = LocalDateTime.parse(hSaida, formatadorData);
        
        // --- LÓGICA DO CLIENTE FREQUENTE ---
        if (cliente instanceof ClienteAvulso) {
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
        if (cliente instanceof ClienteAluno){
            ClienteAluno clienteAluno = (ClienteAluno)cliente; 
            boolean saldoFicouNegativo = clienteAluno.pagar(ticket.getValor());
            
            if (saldoFicouNegativo) {
                clientesBloqueados.add(cliente);
            }
        }

        // --- REMOÇÃO DO VEÍCULO ---
        Placa chaveParaRemover = null;
        for (Placa p : veiculosEstacionados.keySet()) {
            if (p.toString().equals(placa)) {
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

    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.keySet().stream()
            .anyMatch(v -> v.toString().equals(placa)); 
    }

    public boolean estaBloqueado(String idCliente){
        return clientesBloqueados.stream()
            .anyMatch(c -> c.getId().equals(idCliente)); 
    }

    //----------------------------- METODOS DE BUSCA COM RETORNO OU USO DE REFERENCIA -----------------------------

    private Cliente procuraClientePlaca(String placa){
        return veiculosEstacionados.entrySet().stream()
            .filter(e -> e.getKey().toString().equals(placa)) 
            .map(e -> e.getValue())
            .findFirst()
            .orElse(null);
    }

    private Ticket procuraTicketAberto(Cliente cliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(cliente.getId())) 
            .filter(t -> t.getValor() == null)
            .findFirst()
            .orElse(null);
    }

    private Placa procuraPlaca(PreRegistrado cliente, String placa){
        return cliente.getPlacas().stream().filter(p -> p.toString().equals(placa)).findFirst().orElse(null);
    }

    private boolean temVeiculoEstacionado(PreRegistrado cliente){
        return cliente.getPlacas().stream().anyMatch(p -> estaEstacionado(p.toString()));
    }

    public List<Ticket> procuraRegistros(String idCliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(idCliente))
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