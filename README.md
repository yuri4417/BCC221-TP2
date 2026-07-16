# Sistema de Previsão de Consumo Energético

## Sobre o Projeto
Este projeto é um sistema desenvolvido em Java para auxiliar gestores de energia na análise da relação entre a temperatura externa e o consumo de energia (em kWh). Utilizando **Regressão Linear** (y = β0 + β1.x), a aplicação permite identificar o impacto da temperatura no consumo e prever gastos futuros.

O principal diferencial do sistema é a capacidade de aplicar **filtros dinâmicos**
e recalcular matematicamente a regressão em tempo real apenas sobre os dados de interesse,
permitindo análises segmentadas e detecção visual de outliers.

## Tecnologias Utilizadas
* **Linguagem:** Java
* **Interface Gráfica:** Java Swing
* **Biblioteca de Look and Feel:** Flatlaf
* **Geração de Gráficos da Regressão Linear:** Graphics2D (pacote AWT)
* **Estruturação de Dados:** Arquivos TSV 

## Funcionalidades

### 1. Manipulação de Dados
* Carregamento de medições via arquivo `.tsv` utilizando `JFileChooser`.
* Filtração automática de medições inválidas ao carregar arquivos/registrar novas medições.
* Exportação de relatórios `.tsv` contendo os dados visíveis na tabela.

### 2. Interface Interativa (Swing)
* **Funções separadas por abas:** "Medições", "Filtros" e "Regressão e Previsão".
* **Tabela Dinâmica:** Implementação de `AbstractTableModel` e `TableCellRenderer` para exibição e edição dos dados,
com destaque visual para medições outliers.

### 3. Filtros Dinâmicos Simultâneos
* **Intervalo de Tempo:** Filtragem por data e hora de início/fim.
* **Intervalo de Temperatura:** Filtragem por temperatura mínima e máxima.
* **Raio de Coordenadas:** Filtragem por distância em km a partir de uma latitude e longitude específicas, utilizando a **Fórmula de Haversine**.

### 4. Regressão e Previsão
* Atualização automática dos coeficientes **β0 (intercepto)** e **β1 (inclinação)** a cada filtro aplicado.
* Cálculo do **R² (Coeficiente de Determinação)**, representado visualmente por uma `JProgressBar` colorida (Azul, Amarelo ou Vermelho).
* Cálculo do resíduo percentual para cada medição.
* **Gestão de Outliers:** Controle via `JSlider` para definir a porcentagem de corte e um `JToggleButton` para alternar entre apenas destacar ou excluir completamente as anomalias do recálculo.

### 5. Gráfico 2D
* Gráfico renderizado via `paintComponent` mostrando a relação Temperatura x Consumo.
* Plotagem dinâmica dos pontos reais e da reta de regressão linear sobreposta.
* Atualização em tempo real conforme os filtros são aplicados.

## Como Executar

### Pré-requisitos
* Java Development Kit (JDK) instalado.

### Passos
Para compilar o código diretamente via terminal, utilize os seguintes comandos:
1. Crie uma pasta para armazenar os arquivos compilados
   ```bash
   mkdir -p bin # Linux/Mac
   mkdir bin    # Windows
2. Compile os arquivos .java:
   ```bash
   # Linux/Mac
   javac -d bin -cp "lib/*" $(find src -name "*.java") 
   # Windows, PowerShell
   javac -d bin -cp "lib/*" (gci src -Recurse -Filter *.java).FullName 
3. Copie a pasta de imagens para a pasta bin (Opcional, mas pular este passo implica no não-carregamento dos ícones da interface):
   ```bash
   # Linux/Mac
   cp -r src/images bin/
   # Windows
   xcopy src\images bin\images /E /I /Y
4. Execute o programa:
   ```bash
   # Linux/Mac
   java -cp "bin:lib/*" Main
   # Windows
   java -cp "bin;lib\*" Main