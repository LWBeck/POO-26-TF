package sistema;

import java.util.HashMap;

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
    private final HashMap<String, PreRegistrado> clientesPreRegistrados;
    private final HashMap<String, PreRegistrado> placasRegistradas;

    public Sistema(int id){
        this.id = id;
        this.clientesPreRegistrados = new HashMap<>();
        this.placasRegistradas = new HashMap<>();
    }

    public int getId() { return id; }

    public boolean registrarCliente(TipoCliente tipo, String idCliente, String nome){
        switch (tipo) {
            case PROFESSOR -> 
                {if (!ValidaDados.validaCPF(idCliente)) throw new IllegalArgumentException();
                clientesPreRegistrados.put(idCliente, new ClienteProfessor(idCliente, nome));
                return true;}
            case ALUNO ->
                {if (!ValidaDados.validaCPF(idCliente)) throw new IllegalArgumentException();
                clientesPreRegistrados.put(idCliente, new ClienteAluno(idCliente, nome));
                return true;}
            case EMPRESA ->
                {clientesPreRegistrados.put(idCliente, new ClienteEmpresa(idCliente, nome));
                return true;}
            default ->
                throw new IllegalArgumentException();
        }
    }

    // vincula uma placa a um cliente pre registrado
    public boolean registrarPlacaCliente(String idCliente, String placa){
        if(placaJaExiste(placa)) throw new PlacaJaRegistradaException(placa);
        PreRegistrado cliente = procuraClienteId(idCliente);
        if(cliente == null) throw new ClienteNaoEncontradoException(idCliente);
        if(!ValidaDados.validaPlaca(placa)) throw new PlacaInvalidaException(placa);
        placasRegistradas.put(placa, cliente);
        return cliente.registrarVeiculo(placa);
    }

    // remove uma placa de um cliente pre registrado
    public boolean removerPlacaCliente(String idCliente, String placa){
        PreRegistrado cliente = procuraClienteId(idCliente);
        if(cliente == null) throw new ClienteNaoEncontradoException(idCliente);
        if(!ValidaDados.validaPlaca(placa)) throw new PlacaInvalidaException(placa);
        placasRegistradas.remove(placa);
        return cliente.removeVeiculo(placa);
    }

    // verifica se uma placa ja esta registrada
    public boolean placaJaExiste(String placa){
        return placasRegistradas.containsKey(placa);
    }

    // retorna uma referencia a um cliente pre registrado a partir de uma placa (string)
    public PreRegistrado procuraClientesPorPlaca(String placa){
        return placasRegistradas.get(placa);
    }

    // retorna uma referencia a um cliente pre registrado a partir de um id
    public PreRegistrado procuraClienteId(String idCliente){
        return clientesPreRegistrados.get(idCliente);
    }
}
