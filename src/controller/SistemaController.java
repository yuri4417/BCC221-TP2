package controller;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import javax.swing.*;


import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import coords.Coordenada;
import coords.RegressaoLinear;
import view.GraficoPanel;
import view.MainFrame;
import view.TabelaModel;
import med.Medicao;
import med.MedicaoValidator;
import static med.MedicaoValidator.erros;
import view.GraficoPanel;


public class SistemaController {
    public SistemaController(){}

    public boolean carregarTSV(String caminhoArquivo, TabelaModel t, GraficoPanel graficoPanel) {
        String linha;
        System.out.println("Tentando carregar arquivo");
        MedicaoValidator.resetarErros();
        int medicoesValidas = 0;
        int numeroLinha = 0;
        List<String> linhasErros = new ArrayList<>();

        //Abre o arquivo para leitura
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            ArrayList<Medicao> vec = new ArrayList<>();
            br.readLine();
            numeroLinha++;
            while ((linha = br.readLine()) != null) {
                String[] camposLinha = linha.split("\t");
                //Se linha contém menos de 6 elementos, quantidade está inválida
                if (camposLinha.length < 6) {
                    numeroLinha++;
                    linhasErros.add(numeroLinha +": Campos insuficientes.");
                    continue;
                }
                try {
                    Medicao temp = new Medicao();
                    //timestamp,cidade,latitude,longitude,temperatura,consumoKwh
                    //Realiza o parsing da linha, preenchendo a nova medição lida
                    LocalDateTime time = LocalDateTime.parse(camposLinha[0], t.getFormatter());
                    temp.setTimeStamp(time);
                    temp.setCidade(camposLinha[1]);
                    temp.setCoordenadas(new Coordenada(Double.parseDouble(camposLinha[2]), Double.parseDouble(camposLinha[3])));
                    temp.setTemperatura(Double.parseDouble(camposLinha[4]));
                    temp.setConsumoKwh(Double.parseDouble(camposLinha[5]));
                    boolean linhaValida = true;
                    StringBuilder qualErro = new StringBuilder();

                    //Realiza todas as validações da medição lida
                    System.out.printf("Lendo linha: cidade %s %f %n", temp.getCidade(), temp.getTemperatura());
                    if (!MedicaoValidator.validarCoordenada(temp.getCoordenadas())) {
                        linhaValida = false;
                        qualErro.append("Coordenada fora do limite. ");
                    }
                    if(!MedicaoValidator.validarTemperatura(temp.getTemperatura())){
                        linhaValida = false;
                        qualErro.append("Temperatura fora do limite. ");
                    }
                    if(!MedicaoValidator.validarConsumo(temp.getConsumoKwh())){
                        linhaValida = false;
                        qualErro.append("Consumo negativo. ");
                    }
                    if (linhaValida){
                        vec.add(temp);
                        medicoesValidas++;
                    }
                    else{
                        MedicaoValidator.erros.incrementarErrosTotais();
                        linhasErros.add(numeroLinha+": "+ qualErro.toString().trim());
                    }
                } catch(NumberFormatException | java.time.format.DateTimeParseException e) {
                    MedicaoValidator.erros.incrementarErrosTotais();
                    linhasErros.add(numeroLinha+": Formato de dados inválido.");
                }
                numeroLinha++;
            }
            //Adiciona os dados válidos
            t.setDados(vec);
            t.atualizarOutliers();
            if (graficoPanel != null) {
                RegressaoLinear regressao = new RegressaoLinear();
                regressao.setDados(t.getDadosFiltrados());
                graficoPanel.atualizarDados(t.getDadosFiltrados(), regressao, t.getLimiteOutlier(), t.getPorcentagemLimiteVerde());
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

        //Caso tenham erros, exibe o JOptionPane com os erros encontrados
        if(!linhasErros.isEmpty()){
            JPanel painelErros = new JPanel(new BorderLayout(0,10));
            JLabel errosEncontrados = new JLabel("Detectados " + linhasErros.size() + " ERRO(S)", SwingConstants.CENTER);

            JList<String> listaErros = new JList<>(linhasErros.toArray(new String[0]));
            listaErros.setBackground(UIManager.getColor("Panel.background"));
            listaErros.setForeground(UIManager.getColor("Label.foreground"));
            listaErros.setFocusable(false);
            listaErros.setSelectionModel(new DefaultListSelectionModel() {
                @Override
                public void setSelectionInterval(int index0, int index1) {
                    super.setSelectionInterval(-1, -1);
                }
            });
            listaErros.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JScrollPane scroll = new JScrollPane(listaErros);
            scroll.setPreferredSize(new Dimension(450,250));
            scroll.getViewport().setBackground(UIManager.getColor("Panel.background"));
            scroll.setBorder(BorderFactory.createEmptyBorder());

            painelErros.add(errosEncontrados,BorderLayout.NORTH);
            painelErros.add(scroll,BorderLayout.CENTER);
            JOptionPane.showMessageDialog(null,painelErros,"Relatório de Erros", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("Arquivo TSV importado com sucesso!");
        return true;

    }
    //Transfere os dados do programa para um arquivo
    public void exportarTSV(String caminhoArquivo, TabelaModel t) throws IOException {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.getName().toLowerCase().endsWith(".tsv")) {
            arquivo = new File(arquivo.getAbsolutePath() + ".tsv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh");
            writer.newLine();
            //pega os dados filtrados para transferir ao arquivo
            for (var m : t.getDadosFiltrados()) {
                writer.write(String.format(Locale.ROOT,"%s\t%s\t%.4f\t%.4f\t%.1f\t%.0f%n",
                        m.getTimeStamp().format(t.getFormatter()),
                        m.getCidade(),
                        m.getCoordenadas().getLatitude(),
                        m.getCoordenadas().getLongitude(),
                        m.getTemperatura(),
                        m.getConsumoKwh()));
            }
        }
    }

    //Faz o parse, trocando eventuais vírgulas por pontos
    public static Double parseDouble(String texto) throws NumberFormatException {
        if (texto == null || texto.trim().isEmpty())
            return null;
        
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    //Estiliza o calendário para se adaptar ao padrão do sistema
    public static void aplicarTemaCalendario(DatePickerSettings dateSettings) {
        Color corFundo = UIManager.getColor("Panel.background");
        Color corTexto = UIManager.getColor("Label.foreground");
        Color corFundoSelecionado = UIManager.getColor("Component.focusColor");

        // cores da borda e menu
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, corFundo);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, corFundo);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, corFundo);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, corFundo);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundCalendarPanelLabelsOnHover, corFundoSelecionado);
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, corTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.TextTodayLabel, corTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.TextClearLabel, corTexto);

        // cores internas do calendario
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, corFundo);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, corFundoSelecionado);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, corTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, corTexto);
        dateSettings.setColorBackgroundWeekdayLabels(corFundo, true);


        Color corFundoTexto = UIManager.getColor("TextField.background");
        Color corTextoTexto = UIManager.getColor("TextField.foreground");

        dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, corFundoTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundInvalidDate, corFundoTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundVetoedDate, corFundoTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, corTextoTexto);
        dateSettings.setColor(DatePickerSettings.DateArea.DatePickerTextInvalidDate, Color.RED);
    }

    public static DatePickerSettings getConfigsData() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dateSettings.setFormatForDatesBeforeCommonEra("uuuu-MM-dd");

        aplicarTemaCalendario(dateSettings);
        return dateSettings;
    }

    public static TimePickerSettings getConfigsTempo() {
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.setAllowKeyboardEditing(true);
        java.time.format.DateTimeFormatter formatoTempo = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        timeSettings.setFormatForDisplayTime(formatoTempo);
        timeSettings.setFormatForMenuTimes(formatoTempo);

        Color corFundoTexto = UIManager.getColor("TextField.background");
        Color corTextoTexto = UIManager.getColor("TextField.foreground");

        timeSettings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TextFieldBackgroundValidTime, corFundoTexto);
        timeSettings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TextFieldBackgroundInvalidTime, corFundoTexto);
        timeSettings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TextFieldBackgroundVetoedTime, corFundoTexto);

        timeSettings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TimePickerTextValidTime, corTextoTexto);
        timeSettings.setColor(com.github.lgooddatepicker.components.TimePickerSettings.TimeArea.TimePickerTextInvalidTime, Color.RED);
        return timeSettings;
    }
}
