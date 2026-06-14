package sistema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import interfaces.Cliente;
import interfaces.PreRegistrado;

import tiposClientes.ClienteAluno;
import tiposClientes.ClienteAvulso;
import tiposClientes.ClienteProfessor;

import customExceptions.ClienteBloqueadoException;
import customExceptions.PlacaNaoEncontradaException;
import customExceptions.VeiculoJaEstacionadoException;

public class Estacionamento {
    private final int id;
    private final String nome;
    private final HashMap<Placa, Cliente> veiculosEstacionados;
    private final HashSet<PreRegistrado> clientesPreRegistrados;
    private final HashSet<Cliente> clientesBloqueados;
    private final HashSet<Ticket> registros;
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Estacionamento(int id, String nome){
        this.id = id;
        this.nome = nome;
        this.veiculosEstacionados = new HashMap<>();
        this.clientesPreRegistrados = new HashSet<>();
        this.clientesBloqueados = new HashSet<>();
        this.registros = new HashSet<>();
    }

    public int getId(){ return id; }
    public String getNome() { return nome; }

    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.keySet().stream()
            .anyMatch(v -> v.equals(placa));
    }

    public List<String> procuraRegistros(String idCliente){
        return registros.stream()
            .map(Ticket::getIdCliente)
            .filter(id -> id.equals(idCliente))
            .toList();
    }

    public boolean estaBloqueado(String idCliente){
        return clientesBloqueados.stream()
            .anyMatch(c -> c.equals(idCliente));
    }

    public boolean estacionaVeiculo(String placa, String hEntrada){
        if (estaEstacionado(placa)) throw new VeiculoJaEstacionadoException(placa);
        PreRegistrado cliente = procuraClientesPreRegistrados(placa);

        if (cliente == null){ // significa que ou o cliente nao eh pre registrado
            if (estaBloqueado(placa)) throw new ClienteBloqueadoException(); // se nao for pre registrado o id eh a propria placa
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            registros.add(new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            veiculosEstacionados.put(new Placa(placa), clienteAvulso);
            return true;
        }

        if (estaBloqueado(cliente.getId())) throw new ClienteBloqueadoException();

        Placa veiculo = procuraPlaca(cliente, placa);
        if (veiculo == null) throw new PlacaNaoEncontradaException(placa);

        if (cliente instanceof ClienteProfessor && temVeiculoEstacionado(cliente)){
            // se o professor ja tiver um outro veiculo estacionado
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            registros.add(new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            veiculosEstacionados.put(new Placa(placa), clienteAvulso);
            return true;
        }
        registros.add(new Ticket(cliente, LocalDateTime.parse(hEntrada, formatadorData)));
        veiculosEstacionados.put(veiculo,cliente);
        return true;
    }

    public boolean retiraVeiculo(String placa, String hSaida){
        if (!estaEstacionado(placa)) throw new PlacaNaoEncontradaException(placa);
        Cliente cliente = procuraCliente(placa);
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

    //----------------------------- METODOS DE BUSCA COM RETORNO OU USO DE REFERENCIA -----------------------------

    private PreRegistrado procuraClientesPreRegistrados(String placa){
        return clientesPreRegistrados.stream()
                                     .filter(c -> c.getPlacas().stream()
                                                               .anyMatch(p -> p.equals(placa)))
                                     .findAny()
                                     .orElse(null);
    }

    private Cliente procuraCliente(String placa){
        return veiculosEstacionados.entrySet().stream()
            .filter(e -> e.getKey().equals(placa))
            .map(e -> e.getValue())
            .findFirst()
            .orElse(null);
    }

    private Ticket procuraTicketAberto(Cliente cliente){
        return registros.stream()
            .filter(t -> t.getIdCliente().equals(cliente.toString()))
            .filter(t -> t.getValor() == null)
            .findFirst()
            .orElse(null);
    }

    private Placa procuraPlaca(PreRegistrado cliente, String placa){
        return cliente.getPlacas().stream().filter(p -> p.equals(placa)).findFirst().orElse(null);
    }

    private boolean temVeiculoEstacionado(PreRegistrado cliente){
        return cliente.getPlacas().stream().anyMatch(p -> estaEstacionado(p.toString()));
    }

}
