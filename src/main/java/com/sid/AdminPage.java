package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.depts;
import static com.sid.ActivityMain.setActivity;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bson.Document;

public class AdminPage {
    public JPanel page;
    SpringLayout layout;
    public static List<Document> allFaculties;
    public static HashMap<String, List<Document>> faculties;
    JButton addDepartmentButton;
    JButton logoutButton;
    JButton lastDept = null;
    JButton dept = new JButton("Departments");
    JButton hod = new JButton("HOD");
    JButton deleteDept = new JButton("Delete");

    public AdminPage() {
        page = new JPanel();
        layout = new SpringLayout();
        page.setLayout(layout);

        addDepartmentButton = new JButton("Add department");
        logoutButton = new JButton("Log Out");
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setForeground(Color.RED);

        setDepartmentNames(0);
        allFaculties = db.findAllFaculty();
        distributeFaculties(allFaculties);

        layout.putConstraint(SpringLayout.WEST, addDepartmentButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addDepartmentButton, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, logoutButton, 5, SpringLayout.EAST, addDepartmentButton);
        layout.putConstraint(SpringLayout.NORTH, logoutButton, 5, SpringLayout.NORTH, page);

        addDepartmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField deptName = new JTextField(5);
                JTextField hod = new JTextField(5);

                JPanel newDept = new JPanel();
                newDept.setLayout(new GridLayout(4, 1));
                newDept.add(new JLabel("Department Name: *"));
                newDept.add(deptName);
                newDept.add(new JLabel("HOD ID: *"));
                newDept.add(hod);

                int result = JOptionPane.showConfirmDialog(page, newDept, "Please enter the followings",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String newDeptHod = hod.getText();
                    Document temp = null;
                    for (Document doc : allFaculties) {
                        if (doc.getString("f_id").equals(newDeptHod))
                            temp = doc;
                    }
                    if (deptName.getText().isEmpty() || newDeptHod.isEmpty())
                        JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
                    else if (temp == null) {
                        JTextField facName = new JTextField(5);
                        JTextField password = new JTextField(5);

                        JPanel newFac = new JPanel();
                        newFac.setLayout(new GridLayout(4, 1));
                        newFac.add(new JLabel("Faculty Name: *"));
                        newFac.add(facName);
                        newFac.add(new JLabel("Password: *"));
                        newFac.add(password);

                        int result1 = JOptionPane.showConfirmDialog(page, newFac, "Please enter the followings",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (result1 == JOptionPane.OK_OPTION) {
                            if (facName.getText().isEmpty() || password.getText().isEmpty())
                                JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
                            else {
                                Document insertedFac = db.addNewFaculty(hod.getText(), password.getText(),
                                        facName.getText(), deptName.getText(), "HOD");
                                if (faculties.get(deptName.getText()) == null)
                                    faculties.put(deptName.getText(), new ArrayList<Document>());
                                faculties.get(deptName.getText()).add(insertedFac);
                                allFaculties.add(insertedFac);

                                Document insertedDept = db.addNewDepartment(deptName.getText(), hod.getText());
                                depts.add(insertedDept);
                                setDepartmentNames(depts.size() - 1);
                            }
                        }
                    } else if (temp.getString("position").equals("Faculty")) {
                        allFaculties.remove(temp);
                        temp.put("position", "HOD");
                        temp.put("d_id", deptName.getText());
                        db.upsertFaculty(temp);
                        allFaculties.add(temp);
                        distributeFaculties(allFaculties);

                        Document inserted = db.addNewDepartment(deptName.getText(), hod.getText());
                        depts.add(inserted);
                        setDepartmentNames(depts.size() - 1);
                    } else
                        JOptionPane.showMessageDialog(page, "Invalid HOD. Try Again");
                }
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });

        page.add(addDepartmentButton);
        page.add(logoutButton);
    }

    public void distributeFaculties(List<Document> list) {
        faculties = new HashMap<>();
        for (Document doc : list) {
            List<Document> temp = faculties.get(doc.getString("d_id"));
            if (temp == null)
                temp = new ArrayList<>();
            temp.add(doc);
            faculties.put(doc.getString("d_id"), temp);
        }
    }

    public void setDepartmentNames(int index) {
        if (lastDept == null) {
            dept.setOpaque(false);
            dept.setContentAreaFilled(false);
            dept.setBorderPainted(false);
            dept.setFont(new Font(dept.getFont().getName(), Font.BOLD, dept.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, dept, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, dept, 5, SpringLayout.SOUTH, addDepartmentButton);
            page.add(dept);

            hod.setOpaque(false);
            hod.setContentAreaFilled(false);
            hod.setBorderPainted(false);
            hod.setFont(new Font(hod.getFont().getName(), Font.BOLD, hod.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, hod, 5, SpringLayout.EAST, dept);
            layout.putConstraint(SpringLayout.NORTH, hod, 5, SpringLayout.SOUTH, addDepartmentButton);
            page.add(hod);

            deleteDept.setOpaque(false);
            deleteDept.setContentAreaFilled(false);
            deleteDept.setBorderPainted(false);
            deleteDept.setFont(new Font(deleteDept.getFont().getName(), Font.BOLD, deleteDept.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, deleteDept, 5, SpringLayout.EAST, hod);
            layout.putConstraint(SpringLayout.NORTH, deleteDept, 5, SpringLayout.SOUTH, addDepartmentButton);
            page.add(deleteDept);

            lastDept = dept;
        }
        int i = index;
        for (; i < depts.size(); i++) {
            JButton deptName = new JButton(depts.get(i).getString("d_id"));
            Document allhod = (Document) depts.get(i).get("hod");
            JButton hodName = new JButton(allhod.getString(String.valueOf(allhod.size() - 1)));
            JButton removeDept = null;
            try {
                removeDept = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/delete.png"))
                        .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            deptName.setOpaque(false);
            deptName.setContentAreaFilled(false);
            deptName.setBorderPainted(false);
            deptName.setForeground(Color.BLUE);
            deptName.setActionCommand(depts.get(i).getString("d_id"));

            hodName.setOpaque(false);
            hodName.setContentAreaFilled(false);
            hodName.setBorderPainted(false);
            hodName.setForeground(Color.BLUE);
            hodName.setActionCommand(allhod.getString(String.valueOf(allhod.size() - 1)));

            removeDept.setOpaque(false);
            removeDept.setContentAreaFilled(false);
            removeDept.setBorderPainted(false);
            removeDept.setForeground(Color.BLUE);
            removeDept.setActionCommand(depts.get(i).getString("d_id"));

            deptName.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DepartmentPage dPage = new DepartmentPage(e.getActionCommand());
                    setActivity(dPage.page);
                }
            });

            hodName.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(page, e.getActionCommand());
                }
            });

            removeDept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(page,
                            "Are you sure you want to delete " + e.getActionCommand() + " department?", "WARNING",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (faculties.get(e.getActionCommand()) == null
                                || faculties.get(e.getActionCommand()).size() == 0) {
                            Document temp = null;
                            for (Document doc : depts) {
                                if (doc.getString("d_id").equals(e.getActionCommand())) {
                                    temp = doc;
                                    break;
                                }
                            }
                            depts.remove(temp);
                            db.deleteDepartment(e.getActionCommand());
                            // TODO: Change this method
                            logoutButton.doClick();
                            LoginPage.loginButton.doClick();
                        } else {
                            JOptionPane.showMessageDialog(page, "Unable to delete " + e.getActionCommand()
                                    + " department. Please delete all the faculties in it first.");
                        }
                    } else {

                    }
                }
            });

            layout.putConstraint(SpringLayout.WEST, deptName, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, deptName, 5, SpringLayout.SOUTH, lastDept);

            layout.putConstraint(SpringLayout.WEST, hodName, 5, SpringLayout.EAST, dept);
            layout.putConstraint(SpringLayout.NORTH, hodName, 5, SpringLayout.SOUTH, lastDept);

            layout.putConstraint(SpringLayout.WEST, removeDept, 5, SpringLayout.EAST, hod);
            layout.putConstraint(SpringLayout.NORTH, removeDept, 5, SpringLayout.SOUTH, lastDept);

            lastDept = deptName;
            page.add(deptName);
            page.add(hodName);
            page.add(removeDept);
        }
        page.revalidate();
    }
}