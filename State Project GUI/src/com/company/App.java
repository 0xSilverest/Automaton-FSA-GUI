package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends JFrame{
    private JPanel MainPanel;
    private JButton OpenFile;
    private JCheckBox e_NFAToNFACheckBox;
    private JCheckBox NFAToDFACheckBox;
    private JCheckBox minimizeDFACheckBox;
    private JPanel MenuPanel;
    private JButton suivantButton;
    private JTextArea consoleOut;
    private JButton clearButton;
    private JScrollPane scroll;
    private JPanel InitialState;
    private static File selectedPath;
    private static BufferedReader br;
    private static Graph Automaton;

    public App() throws Exception{
        add(MainPanel);
        setTitle("FSA");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(MainPanel);
        this.pack();

        OpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentLine;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileFilter(new FileNameExtensionFilter("gv file","gv"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("txt file","txt"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("dot file","dot"));
                int result = fileChooser.showOpenDialog(MainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedPath = fileChooser.getSelectedFile();
                    try{
                        br = new BufferedReader(new FileReader(selectedPath));
                        Automaton = new Graph();

                        String st;
                        String src = "";
                        String dest = "";
                        String weight = "";
                        Pattern srcPattern = Pattern.compile("\\w+|S|E");
                        Pattern destPattern = Pattern.compile("->\\s*\\w+|->\\s*E");
                        Pattern labelPattern = Pattern.compile("\"\\s*\\w+\\s*\"");
                        Pattern WeightPattern = Pattern.compile("\\w+");

                        while ((st = br.readLine()) != null) {
                            Matcher mSrc = srcPattern.matcher(st);
                            Matcher mDest = destPattern.matcher(st);
                            Matcher mWeight = labelPattern.matcher(st);
                            if(mSrc.find()){
                                src = mSrc.group();
                            }
                            if(mDest.find()){
                                Matcher dst = srcPattern.matcher(mDest.group());
                                if(dst.find()){
                                    dest = dst.group();
                                }
                                if(src.equals("S")){
                                    Automaton.addInitial(dest);
                                    continue;
                                }
                                if(dest.equals("E")){
                                    Automaton.addAcceptance(src);
                                    continue;
                                }
                            }
                            if(mWeight.find()){
                                Matcher weightMatch = WeightPattern.matcher(mWeight.group());
                                if(weightMatch.find()){
                                    weight = weightMatch.group();
                                    Automaton.addTransition(src, dest, weight.charAt(0));
                                }
                            }
                        }
                    } catch (Exception error){
                        error.printStackTrace();
                    }
                }
            }
        });

        suivantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                DisplayImage img = null;
                try {
                    img = new DisplayImage();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (e_NFAToNFACheckBox.isSelected()) {
                    Automaton.e_NFAtoNFA();
                    FileWriter writeFile = null;
                    try {
                        consoleOut.append("NFA :" + Automaton.toString());
                        writeFile = new FileWriter("NFA.dot");

                        writeFile.write(Automaton.Output());
                        writeFile.close();

                        Process p = Runtime.getRuntime().exec("dot -Tpng NFA.dot -o NFA.png");
                        p.waitFor();

                        img.Display("NFA.png");
                        p.destroyForcibly();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (NFAToDFACheckBox.isSelected()) {
                    Automaton.NFAtoDFA();
                    try {
                        consoleOut.append("\nDFA :" + Automaton.toString());
                        FileWriter DFA = new FileWriter("DFA.dot");

                        DFA.write(Automaton.Output());
                        DFA.close();

                        Process p = Runtime.getRuntime().exec("dot -Tpng DFA.dot -o DFA.png");
                        p.waitFor();
                        img.Display("DFA.png");
                        p.destroyForcibly();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (minimizeDFACheckBox.isSelected()) {
                    Automaton.minimize();
                    try {
                        consoleOut.append("\nDFA minimiser :" + Automaton.toString());
                        FileWriter min = new FileWriter("min.dot");

                        min.write(Automaton.Output());
                        min.close();

                        Process p = Runtime.getRuntime().exec("dot -Tpng min.dot -o min.png");
                        p.waitFor();
                        img.Display("min.png");
                        p.destroyForcibly();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Automaton = null;
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    consoleOut.getDocument().remove(0, consoleOut.getDocument().getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
