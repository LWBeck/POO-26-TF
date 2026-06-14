package interfaces;

import sistema.Ticket;

public interface Cliente {
    public String getId(); // id nesse caso pode ser placa, cpf ou cnpj
    public Double calculaValor(Ticket ticket);
    public boolean equals(Cliente outroCliente);
    public boolean equals(String outroId);
}
