package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;
import static com.sid.AdminPage.faculties;
import static com.sid.AdminPage.allFaculties;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bson.Document;

public class DepartmentPage {
    public JPanel page;
    SpringLayout layout;
    JButton addFacultyButton;
    JButton backButton;
    JButton lastFac = null;
    JButton facultyName = new JButton("Faculty Name");
    JButton facultyId = new JButton("Faculty ID");
    JButton deleteFac = new JButton("Delete");
    JButton editFac = new JButton("Edit");
    String deptName;

    public DepartmentPage(String d) {
        page = new JPanel();
        layout = new SpringLayout();
        page.setLayout(layout);
        deptName = d;

        addFacultyButton = new JButton("Add Faculty");
        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);

        setFacultyNames(0);

        layout.putConstraint(SpringLayout.WEST, addFacultyButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addFacultyButton, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, addFacultyButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);

        addFacultyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField facName = new JTextField(5);
                JTextField facId = new JTextField(5);
                JTextField password = new JTextField(5);

                JPanel newFac = new JPanel();
                newFac.setLayout(new GridLayout(6, 1));
                newFac.add(new JLabel("Faculty Name: *"));
                newFac.add(facName);
                newFac.add(new JLabel("Faculty ID: *"));
                newFac.add(facId);
                newFac.add(new JLabel("Password: *"));
                newFac.add(password);

                int result = JOptionPane.showConfirmDialog(page, newFac, "Please enter the followings",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    if (facName.getText().isEmpty() || facId.getText().isEmpty() || password.getText().isEmpty())
                        JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
                    else {
                        Document inserted = db.addNewFaculty(facId.getText(), password.getText(), facName.getText(),
                                deptName, "Faculty");
                        if (faculties.get(deptName) == null)
                            faculties.put(deptName, new ArrayList<Document>());
                        faculties.get(deptName).add(inserted);
                        allFaculties.add(inserted);
                        setFacultyNames(faculties.get(deptName).size() - 1);
                    }
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });

        page.add(addFacultyButton);
        page.add(backButton);
    }

    public void setFacultyNames(int index) {
        if (lastFac == null) {
            facultyName.setOpaque(false);
            facultyName.setContentAreaFilled(false);
            facultyName.setBorderPainted(false);
            facultyName
                    .setFont(new Font(facultyName.getFont().getName(), Font.BOLD, facultyName.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, facultyName, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, facultyName, 5, SpringLayout.SOUTH, addFacultyButton);
            page.add(facultyName);

            facultyId.setOpaque(false);
            facultyId.setContentAreaFilled(false);
            facultyId.setBorderPainted(false);
            facultyId.setFont(new Font(facultyId.getFont().getName(), Font.BOLD, facultyId.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, facultyId, 5, SpringLayout.EAST, facultyName);
            layout.putConstraint(SpringLayout.NORTH, facultyId, 5, SpringLayout.SOUTH, addFacultyButton);
            page.add(facultyId);

            deleteFac.setOpaque(false);
            deleteFac.setContentAreaFilled(false);
            deleteFac.setBorderPainted(false);
            deleteFac.setFont(new Font(deleteFac.getFont().getName(), Font.BOLD, deleteFac.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, deleteFac, 5, SpringLayout.EAST, facultyId);
            layout.putConstraint(SpringLayout.NORTH, deleteFac, 5, SpringLayout.SOUTH, addFacultyButton);
            page.add(deleteFac);

            editFac.setOpaque(false);
            editFac.setContentAreaFilled(false);
            editFac.setBorderPainted(false);
            editFac.setFont(new Font(editFac.getFont().getName(), Font.BOLD, editFac.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, editFac, 5, SpringLayout.EAST, deleteFac);
            layout.putConstraint(SpringLayout.NORTH, editFac, 5, SpringLayout.SOUTH, addFacultyButton);
            page.add(editFac);

            lastFac = facultyName;
        }
        int i = index;
        if (faculties.get(deptName) != null)
            for (; i < faculties.get(deptName).size(); i++) {
                Document temp = faculties.get(deptName).get(i);
                JButton facName = new JButton(temp.getString("name"));
                JButton facId = new JButton(temp.getString("f_id"));
                JButton removeFac = null;
                JButton facEdit = null;
                try {
                    facEdit = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png"))
                            .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));

                    removeFac= new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/delete.png"))
                            .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                facName.setOpaque(false);
                facName.setContentAreaFilled(false);
                facName.setBorderPainted(false);
                facName.setForeground(Color.BLUE);
                facName.setActionCommand(temp.getString("f_id"));

                facId.setOpaque(false);
                facId.setContentAreaFilled(false);
                facId.setBorderPainted(false);
                facId.setForeground(Color.BLUE);
                facId.setActionCommand(temp.getString("f_id"));

                removeFac.setOpaque(false);
                removeFac.setContentAreaFilled(false);
                removeFac.setBorderPainted(false);
                removeFac.setForeground(Color.BLUE);
                removeFac.setActionCommand(temp.getString("f_id"));

                facEdit.setOpaque(false);
                facEdit.setContentAreaFilled(false);
                facEdit.setBorderPainted(false);
                facEdit.setForeground(Color.BLUE);
                facEdit.setActionCommand(
                        temp.getString("f_id") + " " + temp.getString("d_id") + " " + temp.getString("name"));

                facName.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(page, e.getActionCommand());
                    }
                });

                facId.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(page, e.getActionCommand());
                    }
                });

                removeFac.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (JOptionPane.showConfirmDialog(page,
                                "Are you sure you want to delete " + e.getActionCommand() + " faculty?", "WARNING",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            Document temp = null;
                            for (Document doc : faculties.get(deptName)) {
                                if (doc.getString("f_id").equals(e.getActionCommand())) {
                                    temp = doc;
                                    break;
                                }
                            }
                            faculties.get(deptName).remove(temp);
                            db.deleteFaculty(e.getActionCommand());
                        }
                    }
                });

                facEdit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ", 3);
                        JTextField facName = new JTextField(5);
                        JTextField facDeptName = new JTextField(5);
                        JTextField password = new JTextField(5);
                        facName.setText(s[2]);
                        facDeptName.setText(s[1]);

                        JPanel newFac = new JPanel();
                        newFac.setLayout(new GridLayout(4, 1));
                        newFac.add(new JLabel("Faculty Name: *"));
                        newFac.add(facName);
                        newFac.add(new JLabel("Department: *"));
                        newFac.add(facDeptName);
                        newFac.add(new JLabel("Password: *"));
                        newFac.add(password);

                        int result = JOptionPane.showConfirmDialog(page, newFac, "Please enter the followings",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            if (facName.getText().isEmpty() || facDeptName.getText().isEmpty()
                                    || password.getText().isEmpty())
                                JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
                            else {
                                if (faculties.containsKey(facDeptName.getText())) {
                                    Document temp1 = null;
                                    for (Document doc : allFaculties) {
                                        if (doc.getString("f_id").equals(s[0]))
                                            temp1 = doc;
                                    }
                                    faculties.get(deptName).remove(temp1);
                                    allFaculties.remove(temp1);
                                    temp1.put("name", facName.getText());
                                    temp1.put("d_id", facDeptName.getText());
                                    temp1.put("pwd", password.getText());
                                    db.upsertFaculty(temp1);
                                    faculties.get(facDeptName.getText()).add(temp1);
                                    allFaculties.add(temp1);
                                    setFacultyNames(faculties.get(deptName).size() - 1);
                                } else {
                                    JOptionPane.showMessageDialog(page, "Department does not exist");
                                }
                            }
                        }
                    }
                });

                layout.putConstraint(SpringLayout.WEST, facName, 5, SpringLayout.WEST, page);
                layout.putConstraint(SpringLayout.NORTH, facName, 5, SpringLayout.SOUTH, lastFac);

                layout.putConstraint(SpringLayout.WEST, facId, 5, SpringLayout.EAST, facultyName);
                layout.putConstraint(SpringLayout.NORTH, facId, 5, SpringLayout.SOUTH, lastFac);

                layout.putConstraint(SpringLayout.WEST, removeFac, 5, SpringLayout.EAST, facultyId);
                layout.putConstraint(SpringLayout.NORTH, removeFac, 5, SpringLayout.SOUTH, lastFac);

                layout.putConstraint(SpringLayout.WEST, facEdit, 5, SpringLayout.EAST, deleteFac);
                layout.putConstraint(SpringLayout.NORTH, facEdit, 5, SpringLayout.SOUTH, lastFac);

                lastFac = facName;
                page.add(facName);
                page.add(facId);
                page.add(removeFac);
                page.add(facEdit);
            }
        page.revalidate();
    }
}