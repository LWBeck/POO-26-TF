
import sistema.Estacionamento;
import utilities.ValidaDados;


public class App {
    public static void main(String[] args) {
        Estacionamento est = new Estacionamento(0,"estacionamento");
        System.out.println(ValidaDados.validaCNPJ("12345678000195"));
        System.out.println(ValidaDados.validaCNPJ("73443460000168"));
        System.out.println(ValidaDados.validaCNPJ("12345678910112"));
    }
    
}
