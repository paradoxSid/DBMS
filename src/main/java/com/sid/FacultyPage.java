package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;
import static com.sid.LoginPage.loginButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bson.Document;

public class FacultyPage {
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    Document facDoc;
    JTextArea lastLabel = null;
    JButton logoutButton;

    public FacultyPage(Document doc) {
        page.setLayout(layout);
        facDoc = doc;

        JButton leaveButton = new JButton("Leave Application / Stauts");
        JButton addButton = new JButton("Add Field");
        JLabel name = new JLabel(doc.getString("name"));
        JTextArea department = new JTextArea(doc.getString("position") + ", " + doc.getString("d_id") + " department");
        department.setEditable(false);
        department.setCursor(null);
        department.setOpaque(false);
        department.setFocusable(false);
        lastLabel = department;

        setExtras();

        logoutButton = new JButton("Logout");

        name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize() + 10));
        department.setFont(new Font(department.getFont().getName(), Font.BOLD, department.getFont().getSize()));

        layout.putConstraint(SpringLayout.WEST, leaveButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, leaveButton, 5, SpringLayout.NORTH, page);
        
        layout.putConstraint(SpringLayout.WEST, addButton, 5, SpringLayout.EAST, leaveButton);
        layout.putConstraint(SpringLayout.NORTH, addButton, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, name, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, name, 5, SpringLayout.SOUTH, leaveButton);

        layout.putConstraint(SpringLayout.WEST, department, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, department, 5, SpringLayout.SOUTH, name);

        layout.putConstraint(SpringLayout.WEST, logoutButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, logoutButton, 5, SpringLayout.SOUTH, lastLabel);

        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // LeavePage leavePage = new LeavePage(facDoc);
                // setActivity(leavePage.page);
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createJOptionPane("", "");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });
        page.add(leaveButton);
        page.add(addButton);
        page.add(name);
        page.add(department);
        page.add(logoutButton);
    }

    void createJOptionPane(String sHeading, String sDetails) {
        JTextField newHeading = new JTextField(5);
        JTextArea newDetails = new JTextArea(15, 50);

        newHeading.setText(sHeading);
        newDetails.setText(sDetails);

        JScrollPane scrollPane = new JScrollPane(newDetails);

        JPanel newFac = new JPanel();
        newFac.setLayout(new BoxLayout(newFac, BoxLayout.Y_AXIS));
        newFac.add(new JLabel("Heading: *", JLabel.LEFT));
        newFac.add(newHeading);
        newFac.add(new JLabel("Details: *", JLabel.LEFT));
        newFac.add(scrollPane);

        int result = JOptionPane.showConfirmDialog(page, newFac, "Please enter the followings",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (newHeading.getText().isEmpty() || newDetails.getText().isEmpty())
                JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
            else {
                Document extras = (Document) facDoc.get("extra");
                extras.remove(sHeading);
                extras.put(newHeading.getText(), newDetails.getText());
                facDoc.put("extra", extras);
                db.upsertFaculty(facDoc);
                logoutButton.doClick();
                loginButton.doClick();
            }
        }
    }

    void setExtras() {
        Document extras = (Document) facDoc.get("extra");
        List<String> keys = new ArrayList<>(extras.keySet());
        for (String st : keys) {
            JLabel heading = new JLabel(st, JLabel.CENTER);
            JButton edit = null;
            JButton delete = null;
            try {
                edit = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png")).getScaledInstance(15,
                        15, java.awt.Image.SCALE_SMOOTH)));
                delete = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/delete.png"))
                        .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            JTextArea details = new JTextArea(extras.getString(st));
            heading.setFont(new Font(heading.getFont().getName(), Font.BOLD, heading.getFont().getSize() + 5));
            edit.setOpaque(false);
            edit.setContentAreaFilled(false);
            edit.setBorderPainted(false);
            edit.setForeground(Color.BLUE);
            edit.setActionCommand(st + "@#" + extras.getString(st));
            delete.setOpaque(false);
            delete.setContentAreaFilled(false);
            delete.setBorderPainted(false);
            delete.setForeground(Color.BLUE);
            delete.setActionCommand(st);
            details.setEditable(false);
            details.setCursor(null);
            details.setOpaque(false);
            details.setFocusable(false);
            details.setLineWrap(true);
            details.setWrapStyleWord(true);
            details.setColumns(150);

            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(page,
                            "Are you sure you want to delete " + e.getActionCommand() + "?", "WARNING",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        Document extras = (Document) facDoc.get("extra");
                        extras.remove(e.getActionCommand());
                        facDoc.put("extra", extras);
                        db.upsertFaculty(facDoc);
                        logoutButton.doClick();
                        loginButton.doClick();
                    }
                }
            });
            edit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split("@#");
                    createJOptionPane(s[0], s[1]);
                }
            });

            layout.putConstraint(SpringLayout.WEST, heading, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, heading, 5, SpringLayout.SOUTH, lastLabel);
            layout.putConstraint(SpringLayout.WEST, edit, 5, SpringLayout.EAST, heading);
            layout.putConstraint(SpringLayout.NORTH, edit, 5, SpringLayout.SOUTH, lastLabel);
            layout.putConstraint(SpringLayout.WEST, delete, 5, SpringLayout.EAST, edit);
            layout.putConstraint(SpringLayout.NORTH, delete, 5, SpringLayout.SOUTH, lastLabel);
            layout.putConstraint(SpringLayout.WEST, details, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, details, 5, SpringLayout.SOUTH, heading);
            lastLabel = details;
            page.add(heading);
            page.add(edit);
            page.add(delete);
            page.add(details);
        }
    }
}