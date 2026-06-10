public abstract class Cliente {
    private String[] placa;
    private boolean bloqueado;

    public abstract String getId(); // id nesse caso pode ser placa, cpf ou cnpj
    public abstract double calculaValor(Ticket ticket);
}
