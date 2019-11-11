package com.sid;

import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.bson.Document;;

public class ActivityMain {
    static JFrame mainFrame;
    static ConnectToDB db;
    static ConnectToPostgres leavesDb;
    static Stack<JPanel> stActivities;
    static List<Document> depts;
    static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        mainFrame = new JFrame("Faculty Portal");
        mainFrame.setSize(600, 800);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(1, 1));

        stActivities = new Stack<>();

        db = new ConnectToDB();
        depts = db.findAllDepartments();
        leavesDb = new ConnectToPostgres();
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