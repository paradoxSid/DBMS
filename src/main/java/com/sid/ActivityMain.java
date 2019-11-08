package com.sid;

import java.awt.GridLayout;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ActivityMain {
    static JFrame mainFrame;
    static ConnectToDB db;
    static Stack<JPanel> stActivities;

    public static void main(String[] args) {
        mainFrame = new JFrame("Faculty Portal");
        mainFrame.setSize(600, 800);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(1, 1));

        stActivities = new Stack<>();

        db = new ConnectToDB();
        LoginPage loginPage = new LoginPage();
        setActivity(loginPage.page);

        mainFrame.setVisible(true);
    }

    public static void setActivity(JPanel nActivity) {
        if (!stActivities.empty())
            mainFrame.remove(stActivities.peek());
        if (nActivity != null) {
            mainFrame.add(nActivity);
            stActivities.push(nActivity);
        } else {
            stActivities.pop();
            mainFrame.add(stActivities.peek());
        }
        mainFrame.revalidate();
        mainFrame.repaint();
    }
}