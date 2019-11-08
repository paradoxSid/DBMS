package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bson.Document;

public class LeavePage {
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    Document facDoc;
    List<Document> leaves;
    JTextArea lastLabel = null;
    JButton addButton, backButton;

    public LeavePage(Document doc) {
        page.setLayout(layout);
        facDoc = doc;

        leaves = db.findAllLeaves(doc.getString("f_id"));

        addButton = new JButton("Apply for leave");
        layout.putConstraint(SpringLayout.WEST, addButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addButton, 5, SpringLayout.NORTH, page);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createJOptionPane("", "");
            }
        });
        page.add(addButton);

        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, addButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });
        page.add(backButton);
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
            }
        }
    }
}