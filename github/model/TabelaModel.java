package ufop.poo.tp2.model;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class TabelaModel extends AbstractTableModel {
    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;
    private String[] colunas;
    private boolean excluirOutliers;
    private double limiteOutlierPercentual;
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public TabelaModel() {
        this.dadosOriginais = new ArrayList<>();
        this.dadosFiltrados = new ArrayList<>();
        this.colunas = new String[]{
            "Timestamp", "Cidade", "Latitude", "Longitude", 
            "Temperatura (°C)", "Consumo (kWh)", 
            "Consumo Previsto (kWh)", "Resíduo Percentual (%)"
        };
        this.excluirOutliers = false;
        this.limiteOutlierPercentual = 10.0;
    }
    
    @Override
    public int getRowCount() {
        return dadosFiltrados.size();
    }
    
    @Override
    public int getColumnCount() {
        return colunas.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Medicao m = dadosFiltrados.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return m.getTimestamp().format(FORMATTER);
            case 1: return m.getCidade();
            case 2: return m.getLatitude();
            case 3: return m.getLongitude();
            case 4: return m.getTemperatura();
            case 5: return m.getConsumoKwh();
            case 6: return m.getConsumoPrevisto();
            case 7: return m.getResiduoPercentual();
            default: return null;
        }
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Medicao m = dadosFiltrados.get(rowIndex);
        
        try {
            switch (columnIndex) {
                case 4: // Temperatura
                    m.setTemperatura((Double) value);
                    break;
                case 5: // Consumo
                    m.setConsumoKwh((Double) value);
                    break;
                default:
                    return;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            // Tratar erro
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4 || columnIndex == 5;
    }
    
    public Medicao getMedicaoAt(int row) {
        if (row >= 0 && row < dadosFiltrados.size()) {
            return dadosFiltrados.get(row);
        }
        return null;
    }
    
    public void setDadosOriginais(List<Medicao> dados) {
        this.dadosOriginais = new ArrayList<>(dados);
        atualizarFiltrosComOutliers();
    }
    
    public void setDadosFiltrados(List<Medicao> dados) {
        this.dadosFiltrados = new ArrayList<>(dados);
        fireTableDataChanged();
    }
    
    public void atualizarFiltrosComOutliers() {
        if (excluirOutliers) {
            dadosFiltrados = filtrarOutliers(dadosOriginais);
        } else {
            dadosFiltrados = new ArrayList<>(dadosOriginais);
        }
        fireTableDataChanged();
    }
    
    private List<Medicao> filtrarOutliers(List<Medicao> dados) {
        List<Medicao> resultado = new ArrayList<>();
        for (Medicao m : dados) {
            if (Math.abs(m.getResiduoPercentual()) <= limiteOutlierPercentual) {
                resultado.add(m);
            }
        }
        return resultado;
    }
    
    public void adicionarMedicao(Medicao m) {
        dadosOriginais.add(m);
        atualizarFiltrosComOutliers();
    }
    
    public void removerMedicoes(int[] linhas) {
        Arrays.sort(linhas);
        for (int i = linhas.length - 1; i >= 0; i--) {
            int idx = linhas[i];
            if (idx >= 0 && idx < dadosFiltrados.size()) {
                Medicao m = dadosFiltrados.get(idx);
                dadosOriginais.remove(m);
            }
        }
        atualizarFiltrosComOutliers();
    }
    
    public void setExcluirOutliers(boolean excluir) {
        this.excluirOutliers = excluir;
        atualizarFiltrosComOutliers();
    }
    
    public void setLimiteOutlier(double limite) {
        this.limiteOutlierPercentual = limite;
        atualizarFiltrosComOutliers();
    }
    
    public List<Medicao> getDadosFiltrados() {
        return dadosFiltrados;
    }
    
    public List<Medicao> getDadosOriginais() {
        return dadosOriginais;
    }
}