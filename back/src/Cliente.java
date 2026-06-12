public abstract class Cliente {
    private boolean bloqueado;

    
    public abstract String getId(); // id nesse caso pode ser placa, cpf ou cnpj
    public abstract double calculaValor(Ticket ticket);
    public abstract boolean bloqueia(boolean bloqueado);
    public abstract boolean getBloqueado();
}
