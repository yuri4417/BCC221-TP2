package controller;

import coords.Coordenada;
import med.Medicao;
import view.MainFrame;
import view.TabelaModel;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Utils {
    public static boolean carregarTSV(String caminhoArquivo, TabelaModel t) {
        String linha;
        System.out.println("Tentando carregar arquivo");
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            ArrayList<Medicao> vec = new ArrayList<>();
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] camposLinha = linha.split("\t");
                if (camposLinha.length < 6)
                    continue;
                Medicao temp = new Medicao();
                //timestamp,cidade,latitude,longitude,temperatura,consumoKwh
                LocalDateTime time = LocalDateTime.parse(camposLinha[0], t.getFormatter());
                temp.setDateTime(time);
                temp.setCidade(camposLinha[1]);
                temp.setCoordenadas(new Coordenada(Double.parseDouble(camposLinha[2]), Double.parseDouble(camposLinha[3])));
                temp.setTemperatura(Double.parseDouble(camposLinha[4]));
                temp.setConsumoKwh(Double.parseDouble(camposLinha[5]));
                System.out.printf("Lendo linha: cidade %s %f", temp.getCidade(), temp.getTemperatura());
                vec.add(temp);
            }
            t.setDados(vec);
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Erro ao processar os dados do arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        System.out.println("OK");
        return true;

    }
    public static boolean exportarTSV(String caminhoArquivo, TabelaModel t){
//        TODO: Permitir escolha do local para exportar os arquivos (com carinho)
        try (FileWriter fw = new FileWriter(caminhoArquivo); BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write("timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh");
            writer.newLine();
            var vec = t.getDados();
            for (var m : vec) {
                writer.write(m.toString());
                writer.newLine();
            }
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Erro ao processar os dados do arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
