package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class LoginPage {
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    public static JButton loginButton;
    public LoginPage() {
        page.setLayout(layout);

        JLabel namelabel = new JLabel("User ID: ", JLabel.RIGHT);
        JLabel passwordLabel = new JLabel("Password: ", JLabel.RIGHT);
        final JTextField userText = new JTextField(6);
        final JPasswordField passwordText = new JPasswordField(6);
        loginButton = new JButton("Login");

        layout.putConstraint(SpringLayout.WEST, namelabel, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, namelabel, 5, SpringLayout.NORTH, page);
        layout.putConstraint(SpringLayout.WEST, userText, 5, SpringLayout.EAST, passwordLabel);
        layout.putConstraint(SpringLayout.NORTH, userText, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, passwordLabel, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, namelabel);
        layout.putConstraint(SpringLayout.WEST, passwordText, 5, SpringLayout.EAST, passwordLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordText, 5, SpringLayout.SOUTH, userText);

        layout.putConstraint(SpringLayout.WEST, loginButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, loginButton, 5, SpringLayout.SOUTH, passwordLabel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ActivityMain.db.checkValidUser(userText.getText(), new String(passwordText.getPassword()))) {
                    if (userText.getText().equals("admin")){
                        AdminPage adminPage = new AdminPage();
                        setActivity(adminPage.page);
                    }
                    else{
                        FacultyPage facultyPage = new FacultyPage(db.facultyDoc);
                        setActivity(facultyPage.page);
                    }
                } else {
                    JOptionPane.showMessageDialog(page, "Incorrect login credentials i.e. ID or password!");
                }
            }
        });
        page.add(namelabel);
        page.add(userText);
        page.add(passwordLabel);
        page.add(passwordText);
        page.add(loginButton);
        defaultLogin("1111", "1111", userText, passwordText);
    }
    void defaultLogin(String id, String pwd, JTextField userText, JPasswordField passwordText) {
        userText.setText(id);
        passwordText.setText(pwd);
    }
}