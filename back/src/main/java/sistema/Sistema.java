package sistema;

import java.util.HashSet;
import java.util.List;

import customExceptions.ClienteNaoEncontradoException;
import customExceptions.PlacaInvalidaException;
import customExceptions.PlacaJaRegistradaException;
import interfaces.PreRegistrado;
import interfaces.TipoCliente;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteEmpresa;
import tiposClientes.ClienteProfessor;
import utilities.ValidaDados;

public class Sistema {
    private final int id;
    private final HashSet<PreRegistrado> clientesPreRegistrados;

    public Sistema(int id){
        this.id = id;
        this.clientesPreRegistrados = new HashSet<>();
    }

    public int getId() { return id; }

    public boolean registrarCliente(TipoCliente tipo, String idCliente, String nome){
        switch (tipo) {
            case PROFESSOR:
                if (!ValidaDados.validaCPF(idCliente)) throw new IllegalArgumentException();
                clientesPreRegistrados.add(new ClienteProfessor(idCliente, nome));
                return true;
            case ALUNO:
                if (!ValidaDados.validaCPF(idCliente)) throw new IllegalArgumentException();
                clientesPreRegistrados.add(new ClienteAluno(idCliente, nome));
                return true;
            case EMPRESA:
                clientesPreRegistrados.add(new ClienteEmpresa(idCliente, nome));
                return true;
            default:
                throw new IllegalArgumentException();
        }
    }

    // vincula uma placa a um cliente pre registrado
    public boolean registrarPlacaCliente(String idCliente, String placa){
        if(placaJaExiste(placa)) throw new PlacaJaRegistradaException(placa);
        PreRegistrado cliente = procuraClienteId(idCliente);
        if(cliente == null) throw new ClienteNaoEncontradoException(idCliente);
        if(!ValidaDados.validaPlaca(placa)) throw new PlacaInvalidaException(placa);

        return cliente.registrarVeiculo(placa);
    }

    // remove uma placa de um cliente pre registrado
    public boolean removerPlacaCliente(String idCliente, String placa){
        PreRegistrado cliente = procuraClienteId(idCliente);
        if(cliente == null) throw new ClienteNaoEncontradoException(idCliente);
        if(!ValidaDados.validaPlaca(placa)) throw new PlacaInvalidaException(placa);

        return cliente.removeVeiculo(placa);
    }

    // verifica se uma placa ja esta registrada
    public boolean placaJaExiste(String placa){
        return clientesPreRegistrados.stream()
            .map(PreRegistrado::getPlacas)
            .flatMap(List::stream)
            .anyMatch(p -> p.equals(placa));
    }

    // retorna uma referencia a um cliente pre registrado a partir de uma placa (string)
    public PreRegistrado procuraClientesPreRegistrados(String placa){
        return clientesPreRegistrados.stream()
                                     .filter(c -> c.getPlacas().stream()
                                                               .anyMatch(p -> p.equals(placa)))
                                     .findAny()
                                     .orElse(null);
    }

    // retorna uma referencia a um cliente pre registrado a partir de um id
    public PreRegistrado procuraClienteId(String idCliente){
        return clientesPreRegistrados.stream()
            .filter(c -> c.equals(idCliente))
            .findFirst()
            .orElse(null);
    }
}
