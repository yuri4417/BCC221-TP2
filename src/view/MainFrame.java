package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame {
    public static void main(String[] args){
        //Tela total
        JFrame frame = new JFrame("BCC 221 - POO | Sistema de Previsão de Consumo Energético");

        // Barra de Menus
        JMenuBar barra = new JMenuBar();

        // Menu de Arquivo, Filtros
        JMenu arquivo = new JMenu("Arquivo");
        JMenu filtros = new JMenu("Filtros");

        // Cria no menu Arquivo os itens Carregar TSV, Exportar TSV
        JMenuItem carregaTSV = new JMenuItem("Carregar TSV");
        JMenuItem exportaTSV = new JMenuItem("Exportar TSV");

        // Cria no menu Filtros os itens Intervalo de Tempo, Intervalo de Temperatura, Raio a partir da Coordenada
        JMenuItem intervaloTempo = new JMenuItem("Intervalo de Tempo");
        JMenuItem intervaloTemperatura = new JMenuItem("Intervalo de Temperatura");
        JMenuItem raioCoordenada = new JMenuItem("Raio a partir da Coordenada");

        carregaTSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Importar TSV");
            }
        });

        exportaTSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exportar TSV");
            }
        });

        intervaloTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Filtro de tempo");
            }
        });
        intervaloTemperatura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Filtro de temperatura");
            }
        });
        raioCoordenada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Filtro de coordenada");
            }
        });

        arquivo.add(carregaTSV);
        arquivo.add(exportaTSV);
        filtros.add(intervaloTempo);
        filtros.add(intervaloTemperatura);
        filtros.add(raioCoordenada);

        barra.add(arquivo);
        barra.add(filtros);
        frame.setJMenuBar(barra);

        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) (tela.width/1.25), (int) (tela.height/1.25));
        frame.setLocation(tela.width/2, tela.height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
