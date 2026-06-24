package sistema;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;

import interfaces.Cliente;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteEmpresa;
import tiposClientes.ClienteProfessor;

public class GerenciaArquivos {
    private static final String FICHEIRO_CLIENTES = "clientes.csv";
    private static final String FICHEIRO_REGISTOS = "registos.csv";
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // ---------------------- GESTÃO DE CLIENTES ----------------------

    public static void guardarClientes(HashMap<String, Cliente> clientes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHEIRO_CLIENTES))) {
            writer.println("TIPO,ID,NOME,DADO_EXTRA");
            for (Cliente cliente : clientes.values()) {
                if (cliente instanceof ClienteAluno clienteAluno) {
                    writer.println("ALUNO," + cliente.getId() + "," + cliente.getNome() + "," + clienteAluno.getSaldo());
                } else if (cliente instanceof ClienteEmpresa clienteEmpresa) {
                    writer.println("EMPRESA," + cliente.getId() + "," + cliente.getNome() + "," + clienteEmpresa.estaEmDebito());
                } else if (cliente instanceof ClienteProfessor) {
                    writer.println("PROFESSOR," + cliente.getId() + "," + cliente.getNome() + ",0");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar clientes: " + e.getMessage());
        }
    }

    public static void carregarClientes(HashMap<String, Cliente> clientes) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHEIRO_CLIENTES))) {
            String linha;
            reader.readLine(); 
            
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length < 3) continue;
                
                String tipo = dados[0];
                String id = dados[1];
                String nome = dados[2];

                switch (tipo) {
                    case "ALUNO":
                        ClienteAluno aluno = new ClienteAluno(id, nome);
                        if (dados.length > 3) aluno.adicionarSaldo(Double.parseDouble(dados[3]));
                        clientes.put(id, aluno);
                        break;
                    case "EMPRESA":
                        ClienteEmpresa empresa = new ClienteEmpresa(id, nome);
                        if (dados.length > 3) empresa.setDebito(Boolean.parseBoolean(dados[3]));
                        clientes.put(id, empresa);
                        break;
                    case "PROFESSOR":
                        clientes.put(id, new ClienteProfessor(id, nome));
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro de clientes não encontrado. Será criado um novo na próxima gravação.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    // ---------------------- GESTÃO DE REGISTOS (TICKETS) ----------------------

    public static void guardarRegistos(HashSet<Ticket> registos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHEIRO_REGISTOS))) {
            writer.println("ID_CLIENTE,PLACA,ENTRADA,SAIDA,VALOR_TOTAL,NOME_DESCONTO,VALOR_DESCONTO,VALOR_PAGO");
            for (Ticket t : registos) {
                if (t.getHSaida() != null) {
                    writer.println(t.getIdCliente() + "," +
                            t.getPlaca() + "," +
                            t.getHEntrada().format(FORMATADOR) + "," +
                            t.getHSaida().format(FORMATADOR) + "," +
                            t.getValorTotalDevido() + "," +
                            t.getNomeDesconto() + "," +
                            t.getValorDesconto() + "," +
                            t.getValor());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar registos: " + e.getMessage());
        }
    }

    public static void carregarRegistos(HashSet<Ticket> registos, Sistema sistema) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHEIRO_REGISTOS))) {
            String linha;
            reader.readLine(); // Salta o cabeçalho
            
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length < 7) continue;
                
                String idCliente = dados[0];
                Cliente cliente = sistema.procuraClienteId(idCliente);
                String placa = dados[1];
                LocalDateTime entrada = LocalDateTime.parse(dados[2], FORMATADOR);
                LocalDateTime saida = LocalDateTime.parse(dados[3], FORMATADOR);
                Double valorTotal = Double.parseDouble(dados[4]);
                String nomeDesconto = dados[5];
                Double valorDesconto = Double.parseDouble(dados[6]);
                Double valorPago = Double.parseDouble(dados[7]);

                Ticket ticketHistorico = new Ticket(cliente, placa, entrada, saida, valorTotal, nomeDesconto, valorDesconto, valorPago);
                registos.add(ticketHistorico);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro de registos não encontrado. Será criado um novo na próxima gravação.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar registos: " + e.getMessage());
        }
    }
}