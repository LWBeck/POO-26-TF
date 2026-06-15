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

    public boolean estacionaVeiculo(String placa, String hEntrada){
        // verifica se o veiculo ja esta estacionado
        if (estaEstacionado(placa)) throw new VeiculoJaEstacionadoException(placa);

        PreRegistrado cliente = sys.procuraClientesPreRegistrados(placa);

        if (cliente == null){ // significa que ou o cliente nao eh pre registrado
            // se nao for pre registrado o id eh a propria placa e por isso pode ser usado para verificar o bloqueio
            if (estaBloqueado(placa)) throw new ClienteBloqueadoException();
            if (sys.placaJaExiste(placa)) throw new PlacaJaRegistradaException(placa);
            
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            // emite um ticket
            registros.add(new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo ao set de estacionados
            veiculosEstacionados.put(new Placa(placa), clienteAvulso);
            return true;
        }

        // verifica se o cliente esta bloqueado
        if (estaBloqueado(cliente.getId())) throw new ClienteBloqueadoException();

        // verifica se a placa esta vinculada ao cliente
        Placa veiculo = procuraPlaca(cliente, placa);
        if (veiculo == null) throw new PlacaNaoEncontradaException(placa);

        if (cliente instanceof ClienteProfessor && temVeiculoEstacionado(cliente)){
            // se for professor e ja tiver um outro veiculo estacionado
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            // emite um ticket
            registros.add(new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo ao set de estacionados
            veiculosEstacionados.put(new Placa(placa), clienteAvulso);
            return true;
        }
        // todos os outros casos

        // emite um ticket
        registros.add(new Ticket(cliente, LocalDateTime.parse(hEntrada, formatadorData)));
        // adiciona o veiculo ao set de estacionados
        veiculosEstacionados.put(veiculo,cliente);
        return true;
    }

    public boolean retiraVeiculo(String placa, String hSaida){
        if (!estaEstacionado(placa)) throw new PlacaNaoEncontradaException(placa);
        Cliente cliente = procuraClientePlaca(placa);
        Ticket ticket = procuraTicketAberto(cliente);
        ticket.setHSaida(LocalDateTime.parse(hSaida, formatadorData));
        if (cliente instanceof ClienteAluno){
            try {
                ClienteAluno clienteAluno = (ClienteAluno)cliente; 
                // duvida: esse cliente aluno vai ter o atributo saldo, considerando que origininalmente sua referencia era para um cliente generico?
                clienteAluno.modSaldo(-ticket.getValor());
            } catch (Exception SaldoInsuficienteException) {
                clientesBloqueados.add(cliente);
            }
        }

        return false;
    }

    //----------------------------- METODOS DE VERIFICACAO DE ESTADO -----------------------------    

    // verificacao de se um veiculo esta estacionado a partir da placa (string)
    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.keySet().stream()
            .anyMatch(v -> v.equals(placa));
    }

    // verifica se um cliente esta bloqueado
    public boolean estaBloqueado(String idCliente){
        return clientesBloqueados.stream()
            .anyMatch(c -> c.equals(idCliente));
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

    // retorna uma referencia a um ticket aberto (sem valor e horario de saida) a partir de uma referencia a um cliente
    private Ticket procuraTicketAberto(Cliente cliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(cliente.toString()))
            .filter(t -> t.getValor() == null)
            .findFirst()
            .orElse(null);
    }

    // procura uma placa entre as placas registradas de um cliente e retorna uma referencia
    private Placa procuraPlaca(PreRegistrado cliente, String placa){
        return cliente.getPlacas().stream().filter(p -> p.equals(placa)).findFirst().orElse(null);
    }

    // verifica se um cliente pre registrado ja tem um veiculo estacionado a partir de uma referencia
    private boolean temVeiculoEstacionado(PreRegistrado cliente){
        return cliente.getPlacas().stream().anyMatch(p -> estaEstacionado(p.toString()));
    }

    // procura tickets a partir de um id de cliente
    public List<Ticket> procuraRegistros(String idCliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(idCliente))
            .toList();
    }

}
