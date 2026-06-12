package ph;

import interfaces.Cliente;

public class Veiculo{
    private final Cliente dono;
    private final String placa;

    public Veiculo(Cliente dono, String placa){
        this.dono = dono;
        this.placa = placa;
    }
    
    public boolean equals(Veiculo outroVeiculo){
        return (this.placa.equals(outroVeiculo.getPlaca()));
    }

    public boolean equals(String outraPlaca){
        return (this.placa.equals(outraPlaca));
    }

    public String getDono(){ return dono.getId(); }
    public String getPlaca(){ return this.placa; }
}
