package controller;

import java.util.*;
import coords.RegressaoLinear;
import view.MainFrame;
import view.TabelaModel;
import med.Medicao;
import med.MedicaoValidator;

public class SistemaController {
    private MedicaoValidator dao;
    private TabelaModel tableModel;
//    private Filtro filtroAtual;
    private RegressaoLinear regressaoAtual;
    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;
    private MainFrame view;
}
