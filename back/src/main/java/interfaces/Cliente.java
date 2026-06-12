package interfaces;

import ph.Ticket;

public abstract class Cliente {
    public abstract String getId(); // id nesse caso pode ser placa, cpf ou cnpj
    public boolean equals(Cliente outroCliente){
        return this.getId().equals(outroCliente.getId());
    }
    public abstract double calculaValor(Ticket ticket);
}
