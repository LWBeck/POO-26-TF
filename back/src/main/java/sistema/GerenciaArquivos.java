package utilities;

import interfaces.PreRegistrado;
import sistema.Ticket;
import tiposClientes.ClienteAluno;
import tiposClientes.ClienteEmpresa;
import tiposClientes.ClienteProfessor;
import tiposClientes.ClienteAvulso;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;

public class GerenciaArquivos {
    private static final String FICHEIRO_CLIENTES = "clientes.csv";
    private static final String FICHEIRO_REGISTOS = "registos.csv";
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // ---------------------- GESTÃO DE CLIENTES ----------------------

    public static void guardarClientes(HashMap<String, PreRegistrado> clientes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHEIRO_CLIENTES))) {
            writer.println("TIPO,ID,NOME,DADO_EXTRA");
            for (PreRegistrado cliente : clientes.values()) {
                if (cliente instanceof ClienteAluno) {
                    writer.println("ALUNO," + cliente.getId() + "," + cliente.getNome() + "," + ((ClienteAluno) cliente).getSaldo());
                } else if (cliente instanceof ClienteEmpresa) {
                    writer.println("EMPRESA," + cliente.getId() + "," + cliente.getNome() + "," + ((ClienteEmpresa) cliente).estaEmDebito());
                } else if (cliente instanceof ClienteProfessor) {
                    writer.println("PROFESSOR," + cliente.getId() + "," + cliente.getNome() + ",0");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar clientes: " + e.getMessage());
        }
    }

    public static void carregarClientes(HashMap<String, PreRegistrado> clientes) {
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
            writer.println("ID_CLIENTE,ENTRADA,SAIDA,VALOR_TOTAL,NOME_DESCONTO,VALOR_DESCONTO,VALOR_PAGO");
            for (Ticket t : registos) {
                if (t.getHSaida() != null) {
                    writer.println(t.getIdCliente() + "," +
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

    public static void carregarRegistos(HashSet<Ticket> registos) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FICHEIRO_REGISTOS))) {
            String linha;
            reader.readLine(); // Salta o cabeçalho
            
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length < 7) continue;
                
                String idCliente = dados[0];
                LocalDateTime entrada = LocalDateTime.parse(dados[1], FORMATADOR);
                LocalDateTime saida = LocalDateTime.parse(dados[2], FORMATADOR);
                Double valorTotal = Double.parseDouble(dados[3]);
                String nomeDesconto = dados[4];
                Double valorDesconto = Double.parseDouble(dados[5]);
                Double valorPago = Double.parseDouble(dados[6]);

                Ticket ticketHistorico = new Ticket(new ClienteAvulso(idCliente), entrada, saida, valorTotal, nomeDesconto, valorDesconto, valorPago);
                registos.add(ticketHistorico);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro de registos não encontrado. Será criado um novo na próxima gravação.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar registos: " + e.getMessage());
        }
    }
}