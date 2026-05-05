package app;

import enums.ReportType;
import exception.MissionParsingException;
import facade.MissionAnalysisFacade;
import reporter.Report;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public class MainFrame extends JFrame {
    private final MissionAnalysisFacade facade;
    private JTextArea outputArea;
    private JLabel fileLabel;
    private JComboBox<ReportType> reportTypeCombo;
    private Path selectedFile;

    public MainFrame() {
        this.facade = new MissionAnalysisFacade();
        initComponents();
    }

    private void initComponents() {
        setTitle("📊 Анализатор Миссий - Токийский Магический Колледж");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        // Верхняя панель управления
        JPanel topPanel = createTopPanel();

        // Центральная панель с отчетом
        JPanel centerPanel = createCenterPanel();

        // Нижняя панель статуса
        JPanel bottomPanel = createBottomPanel();

        // Добавляем панели
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Управление"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Метка файла
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("📁 Файл:"), gbc);

        // Кнопка выбора файла
        gbc.gridx = 1;
        JButton selectButton = new JButton("Выбрать файл");
        selectButton.addActionListener(e -> selectFile());
        panel.add(selectButton, gbc);

        // Метка с именем файла
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        fileLabel = new JLabel("Файл не выбран");
        fileLabel.setFont(new Font(fileLabel.getFont().getName(), Font.ITALIC, 12));
        panel.add(fileLabel, gbc);

        // Метка типа отчета
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        panel.add(new JLabel("📄 Тип отчета:"), gbc);

        // ComboBox выбора типа отчета
        gbc.gridx = 4;
        reportTypeCombo = new JComboBox<>(ReportType.values());
        reportTypeCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(reportTypeCombo, gbc);

        // Кнопка генерации отчета
        gbc.gridx = 5;
        JButton generateButton = new JButton("📋 Сформировать отчет");
        generateButton.setBackground(new Color(70, 130, 180));
        generateButton.setForeground(Color.WHITE);
        generateButton.setOpaque(true);
        generateButton.setBorderPainted(false);
        generateButton.addActionListener(e -> generateReport());
        panel.add(generateButton, gbc);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Метка
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("📝 Результат анализа:"));
        panel.add(labelPanel, BorderLayout.NORTH);

        // Текстовая область для отчета
        outputArea = new JTextArea();
        outputArea.setFont(new Font("PT Mono", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(new Color(250, 250, 250));
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel statusLabel = new JLabel("Готов к работе");
        statusLabel.setName("statusLabel");
        panel.add(statusLabel);

        return panel;
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Documents/mephi/Java/laba_2/missions");
        fileChooser.setDialogTitle("Выберите файл миссии");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;  // Показываем директории
                }

                String fileName = f.getName();
                // Проверяем расширения
                if (fileName.toLowerCase().endsWith(".json") ||
                        fileName.toLowerCase().endsWith(".xml") ||
                        fileName.toLowerCase().endsWith(".txt") ||
                        fileName.toLowerCase().endsWith(".yaml") ||
                        fileName.toLowerCase().endsWith(".yml")) {
                    return true;
                }

                // Проверяем файлы без расширения
                return !fileName.contains(".");
            }

            @Override
            public String getDescription() {
                return "Файлы миссий (JSON, XML, TXT, YAML, YML, без расширения)";
            }
        });
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile().toPath();
            fileLabel.setText(selectedFile.getFileName().toString());
            fileLabel.setForeground(Color.BLACK);
            outputArea.setText("");
            updateStatus("Файл выбран: " + selectedFile.getFileName());
        }
    }

    private void generateReport() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Сначала выберите файл миссии!",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Показываем индикатор загрузки
            outputArea.setText("⏳ Обработка миссии...\nПожалуйста, подождите.");
            updateStatus("Обработка файла...");

            // Получаем выбранный тип отчета
            ReportType reportType = (ReportType) reportTypeCombo.getSelectedItem();
            if (reportType == null) {
                reportType = ReportType.DETAILED;
            }

            // Обрабатываем через Facade
            Report report = facade.processMission(selectedFile, reportType);

            // Выводим результат
            outputArea.setText(report.getContent());
            updateStatus("Отчет сформирован: " + report.getType());

        } catch (MissionParsingException e) {
            String errorMsg = "❌ Ошибка парсинга:\n" + e.getMessage();
            outputArea.setText(errorMsg);
            updateStatus("Ошибка парсинга");
            JOptionPane.showMessageDialog(this,
                    errorMsg,
                    "Ошибка парсинга",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            String errorMsg = "❌ Неожиданная ошибка:\n" + e.getMessage();
            outputArea.setText(errorMsg);
            updateStatus("Ошибка обработки");
            JOptionPane.showMessageDialog(this,
                    errorMsg,
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateStatus(String message) {
        JLabel statusLabel = (JLabel) ((JPanel) getContentPane()
                .getComponent(2)).getComponent(0);
        statusLabel.setText(message);
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }
}