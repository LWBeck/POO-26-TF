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
import interfaces.PreRegistrado;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteAvulso;
import tiposClientes.ClienteEmpresa;
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
        // verifica se o veiculo ja esta estacionado
        if (estaBloqueado(placa)) throw new ClienteBloqueadoException();
        if (estaEstacionado(placa)) throw new VeiculoJaEstacionadoException(placa);

        PreRegistrado cliente = sys.procuraClientesPorPlaca(placa);

        if (cliente == null){ // significa que ou o cliente nao eh pre registrado
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            // emite um ticket
            ticketsAbertos.put(placa, new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo ao set de estacionados
            veiculosEstacionados.put(placa, clienteAvulso);
            return true;
        }

        if (cliente instanceof ClienteProfessor && temVeiculoEstacionado(cliente)){
            // se for professor e ja tiver um outro veiculo estacionado
            ClienteAvulso clienteAvulso = new ClienteAvulso(placa);
            // emite um ticket
            ticketsAbertos.put(placa, new Ticket(clienteAvulso, LocalDateTime.parse(hEntrada, formatadorData)));
            // adiciona o veiculo ao set de estacionados
            veiculosEstacionados.put(placa, clienteAvulso);
            return true;
        }
        else if(cliente instanceof ClienteEmpresa clienteEmpresa){
            clienteEmpresa.setDebito(true);
        }
        // todos os outros casos

        // emite um ticket
        ticketsAbertos.put(placa, new Ticket(cliente, LocalDateTime.parse(hEntrada, formatadorData)));
        // adiciona o veiculo ao set de estacionados
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
        else if (cliente instanceof ClienteEmpresa clienteEmpresa){
            if(!pagou){
                bloqueados.add(placa);
            }
            else{
                clienteEmpresa.setDebito(false);
            }
        }
        if (!pagou && cliente instanceof ClienteAvulso){
            bloqueados.add(placa);
        }
    }

    //----------------------------- METODOS DE VERIFICACAO DE ESTADO -----------------------------    

    // verificacao de se um veiculo esta estacionado a partir da placa (string)
    public boolean estaEstacionado(String placa){
        return veiculosEstacionados.containsKey(placa);
    }

    // verifica se um cliente esta bloqueado
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

    // retorna uma referencia a um ticket aberto (sem valor e horario de saida) a partir de uma referencia a um cliente
    private Ticket procuraTicketAberto(String placa){
        return ticketsAbertos.get(placa);
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
