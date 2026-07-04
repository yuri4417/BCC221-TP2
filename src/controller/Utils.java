package controller;

import coords.Coordenada;
import med.Medicao;
import med.MedicaoValidator;
import view.TabelaModel;
import static med.MedicaoValidator.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
//TODO: Mudar exportacao e importacao para classe SistemaController
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
                temp.setTimeStamp(time);
                temp.setCidade(camposLinha[1]);
                temp.setCoordenadas(new Coordenada(Double.parseDouble(camposLinha[2]), Double.parseDouble(camposLinha[3])));
                temp.setTemperatura(Double.parseDouble(camposLinha[4]));
                temp.setConsumoKwh(Double.parseDouble(camposLinha[5]));
                System.out.printf("Lendo linha: cidade %s %f %n", temp.getCidade(), temp.getTemperatura());
                if (validaMedicao(temp))
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
        if (MedicaoValidator.erros.getErrosTotais() > 0)
            JOptionPane.showMessageDialog(null, "Erro ao processar os dados do arquivo. " + erros.getErrosTotais() +
                    " Medições com campos inválidos ignorados.", "Erro", JOptionPane.ERROR_MESSAGE);
        System.out.println("OK");
        return true;

    }

    public static boolean exportarTSV(TabelaModel t) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar dados para TSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos TSV (*.tsv)", "tsv"));

        fileChooser.setAcceptAllFileFilterUsed(false);

        int selecionado = fileChooser.showSaveDialog(null);
        if (selecionado == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            String filePath = arquivo.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".tsv"))
                arquivo = new File(filePath + ".tsv");
            else
                return false;

            if (arquivo != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                    writer.write("timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh");
                    writer.newLine();
                    for (var m : t.getDados()) {
                        writer.write(String.format("%s\t%s\t%.4f\t%.4f\t%.1f\t%.0f%n", m.getTimeStamp().format(t.getFormatter()), m.getCidade(),
                                m.getCoordenadas().getLatitude(), m.getCoordenadas().getLongitude(), m.getTemperatura(), m.getConsumoKwh()));
                    }
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo:\n" + e.getMessage(),"Erro de Exportação", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        else
            return false;
        JOptionPane.showMessageDialog(null, "Arquivo exportado com sucesso!","Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    public static ImageIcon carregaIcon(String filePath, int largura, int altura) {
        URL iconPath = Utils.class.getResource(filePath);
        if (iconPath != null) {
            try {
                Image img = ImageIO.read(iconPath);
                Image imgScaled = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                return new ImageIcon(imgScaled);
            }
            catch (IOException e) {
                System.err.println("Erro ao ler o arquivo de imagem: " + filePath);
                e.printStackTrace();
            }
        }
        else {
            System.err.println("Aviso: Ícone não encontrado no caminho: " + filePath);
        }
        return null;
    }
}