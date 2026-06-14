
import sistema.Estacionamento;
import utilities.ValidaDados;
import interfaces.Cliente;
import interfaces.PreRegistrado;
import tiposClientes.ClienteAluno;


public class App {
    public static void main(String[] args) {
        Estacionamento est = new Estacionamento(0,"estacionamento");
        ClienteAluno a = new ClienteAluno("66908494063","alice");
        a.registrarVeiculo("ABC1234");
        est.estacionaVeiculo("ABC1234", "01-01-2024 08:00");
        est.estacionaVeiculo("DXM2039", "01-01-2024 08:00");
        System.out.println("Veículos estacionados.");
    }
    
}
