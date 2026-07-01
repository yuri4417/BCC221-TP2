package controller;

import java.util.*;
import coords.RegressaoLinear;
import view.TabelaModel;
import med.Medicao;
import med.MedicaoValidator;
import view.MainFrame;

public class SistemaController {
    private MedicaoValidator dao;
    private TabelaModel tableModel;
//    private Filtro filtroAtual;
    private RegressaoLinear regressaoAtual;
    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;
    private MainFrame view;
}
