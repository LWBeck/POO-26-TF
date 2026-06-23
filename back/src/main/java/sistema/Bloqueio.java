package utilities;

import interfaces.PreRegistrado;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteEmpresa;

public class Bloqueio {
    
     //verifica se o cliente possui pendências financeiras e deve ser bloqueado.
     //retorna true se estiver bloqueado.
     
    public static boolean verificarBloqueio(PreRegistrado cliente) {
        if (cliente == null) return false;
        
        if (cliente instanceof ClienteAluno) {
            ClienteAluno aluno = (ClienteAluno) cliente;
            // Se o saldo for negativo, o aluno é bloqueado
            return aluno.getSaldo() < 0; 
        } 
        else if (cliente instanceof ClienteEmpresa) {
            ClienteEmpresa empresa = (ClienteEmpresa) cliente;
            // Bloqueia caso a empresa esteja marcada em débito
            return empresa.estaEmDebito();
        }
        
        // ClienteProfessor e ClienteAvulso não possuem regras de bloqueio direto nesta etapa
        return false;
    }
}