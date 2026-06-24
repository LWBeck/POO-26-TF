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
    private final HashSet<String> bloqueados;
    private final HashMap<String, Ticket> ticketsAbertos;
    private final HashSet<Ticket> registros;
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Estacionamento(int id, String nome){
        this.id = id;
        this.sys = new Sistema(id);
        this.nome = nome;
        this.veiculosEstacionados = new HashMap<>();
        this.bloqueados = new HashSet<>();
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

    public void retiraVeiculo(String placa, String hSaida, boolean pagou){
        if (!estaEstacionado(placa)) throw new PlacaNaoEncontradaException(placa);
        Cliente cliente = procuraClientePlaca(placa);
        Ticket ticket = procuraTicketAberto(placa);
        ticket.setHSaida(LocalDateTime.parse(hSaida, formatadorData));
        if (cliente instanceof ClienteAluno clienteAluno){
            if(!clienteAluno.pagar(ticket.getValor())){
                bloqueados.add(placa);
            }
        }
        if (!pagou && cliente == null){ // significa que é cliente avulso
            bloqueados.add(placa);
        }
    }

    //----------------------------- METODOS DE VERIFICACAO DE ESTADO -----------------------------    

    // verificacao de se um veiculo esta estacionado a partir da placa
    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.containsKey(placa);
    }

    // verifica se uma placa esta bloqueada
    public boolean estaBloqueado(String placa){
        return bloqueados.contains(placa);
    }

    //----------------------------- METODOS DE BUSCA COM RETORNO OU USO DE REFERENCIA -----------------------------

    // retorna uma referencia a um cliente pre registrado a partir de uma placa
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

}
