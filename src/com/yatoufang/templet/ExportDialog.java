package com.yatoufang.templet;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: hse
 * @Date: 2021/1/25
 */
public class ExportDialog extends DialogWrapper {

    private final String content;
    private final String fileName;

    public ExportDialog(@Nullable Project project, String content, String fileName) {
        super(project);
        this.content = content;
        this.fileName = fileName;
        init();
        setTitle("Export File");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JBScrollPane scrollPane = new JBScrollPane();
        JTextArea textArea = new JBTextArea();
        textArea.setText(this.content);
        scrollPane.setViewportView(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 700));
        return scrollPane;
    }

//    @Override
//    protected @Nullable JComponent createNorthPanel() {
//        JPanel jPanel = new JPanel();
//        JButton markdown = new JButton("MarkDown");
//        JButton word = new JButton("Word");
//
//        markdown.addActionListener(e -> {
//
//        });
//        word.addActionListener(e -> {
//
//        });
//
//        jPanel.add(markdown);
//        jPanel.add(word);
//        return jPanel;
//    }

    @NotNull
    @Override
    protected JPanel createButtonsPanel(@NotNull List<? extends JButton> buttons) {
        JPanel jPanel = new JPanel();
        JButton copyButton = new JButton("Copy");
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Close");
        jPanel.add(copyButton);
        jPanel.add(saveButton);
        jPanel.add(cancelButton);

        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(content);
            clipboard.setContents(selection, null);
            dispose();
        });

        saveButton.addActionListener(e -> saveFile());
        cancelButton.addActionListener(e -> dispose());
        return jPanel;
    }

    private void saveFile(){
        UIManager.put("FileChooser.cancelButtonText", "Cancel");
        UIManager.put("FileChooser.openButtonText", "Save");
        String userDir = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser(userDir + "/" + "Desktop");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select path");
        int flag = fileChooser.showOpenDialog(null);
        if (flag == JFileChooser.APPROVE_OPTION) {
            String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(absolutePath + "\\" +fileName + ".md");
            try {
                FileWriter fileWriter = new FileWriter(file,false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(content);
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        dispose();
    }


}