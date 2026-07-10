package view;
import med.Medicao;

import javax.swing.table.AbstractTableModel;
import java.io.BufferedWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabelaModel extends AbstractTableModel {
    private List<Medicao> dados;
    private List<Medicao> dadosFiltrados;
    private String[] colunas;
    private boolean showOutliers;
    private double limiteOutlier;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private double porcentagemLimiteVerde = 0.3;

    public TabelaModel() {
        this.dados = new ArrayList<>();
        this.dadosFiltrados = new ArrayList<>();
//      this.checkBox = new HashSet<>();
        this.showOutliers = true;
        this.limiteOutlier = 5;
        this.colunas = new String[]{
                "Data/Hora",
                "Cidade",
                "Latitude",
                "Longitude",
                "Temperatura",
                "Consumo (kWh)",
                "Consumo Previsto",
                "Resíduo (%)"
        };
    }

    public double getPorcentagemLimiteVerde() {
        return porcentagemLimiteVerde;
    }

    public void setPorcentagemLimiteVerde(double porcentagem) {
        this.porcentagemLimiteVerde = porcentagem;
    }
    @Override
    public int getRowCount() {
        if (dadosFiltrados != null)
            return dadosFiltrados.size();
        return 0;
    }
    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
            return getValueAt(0, columnIndex).getClass();
        }
        return Object.class;
    }
    public List<Medicao> getMedicoes() {
        return this.dados;
    }

    //retorna um valor da tabela de uma determinada coordenada (linha, coluna)
    @Override
    public Object getValueAt(int row, int col) {
        Medicao m = dadosFiltrados.get(row);
        return switch (col) {
            case 0 -> m.getTimeStamp().format(FORMATTER);
            case 1 -> m.getCidade();
            case 2 -> m.getCoordenadas().getLatitude();
            case 3 -> m.getCoordenadas().getLongitude();
            case 4 -> m.getTemperatura();
            case 5 -> m.getConsumoKwh();
            case 6 -> m.getConsumoPrevisto();
            case 7 -> m.getResiduoPercentual();
            default -> null;
        };
    }

    public double getLimiteOutlier() {
        return limiteOutlier;
    }

    public boolean getShowOutliers() { return showOutliers;}

    public void setShowOutliers(boolean show) {
        if (this.showOutliers == show)
            return;
        this.showOutliers = show;
        atualizarOutliers();
    }

    public void setLimiteOutlier(double limite) {
        if (this.limiteOutlier == limite)
            return;
        this.limiteOutlier = limite;
        atualizarOutliers();
    }

    public void setDados(List<Medicao> dados) {
        this.dados = dados;
    }
    public void setDadosFiltrados(List<Medicao> dados) {
        this.dadosFiltrados = dados; }
    //atribui valor a uma certa coordenada da tabela (linha, coluna)
    @Override
    public void setValueAt(Object value, int row, int col) {
        Medicao m = dadosFiltrados.get(row);
        if (col == 4)
            m.setTemperatura((Double) value);
        else if (col == 5)
            m.setConsumoKwh((Double) value);

    }
    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 4 || col == 5;
    }

    @Override
    public String getColumnName(int col) {
        if (col >= 0 && col < colunas.length)
            return colunas[col];
        else
            return "";
    }

    public DateTimeFormatter getFormatter() { return FORMATTER; }

    public Medicao getMedicao(int row) {
        if (row >= 0 && row < dadosFiltrados.size())
            return dadosFiltrados.get(row);
        return null;
    }
    public List<Medicao> getDados(){ return dados; }

    public List<Medicao> getDadosFiltrados() {return dadosFiltrados;}

    public void atualizarOutliers() {
        dadosFiltrados = (showOutliers) ? new ArrayList<>(dados) : filtrarOutliers(dados);

        fireTableDataChanged();
    }

    private List<Medicao> filtrarOutliers(List<Medicao> listaDados) {
        List<Medicao> temp = new ArrayList<>();
        for (var m : listaDados)
            if (Math.abs(m.getResiduoPercentual()) <= limiteOutlier)
                temp.add(m);

        return temp;
    }

    public void adicionarMedicao(Medicao m) {
        if (dados != null) {
            dados.add(m);
            atualizarOutliers();
        }
    }
    public void removerMedicoes(int[] linhas) {
        List<Medicao> iraRemover = new ArrayList<>();
        for (int i = 0; i < linhas.length; i++) {
            int indice = linhas[i];
            if (indice >= 0 && indice< dadosFiltrados.size()) {
                Medicao m = dadosFiltrados.get(indice);
                iraRemover.add(m);
            }
        }
        if (dados != null) {
            dados.removeAll(iraRemover);
        }
        atualizarOutliers();
        fireTableDataChanged();
    }
}