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
import javax.swing.BoxLayout;
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
    JButton searchButton;
    JButton logoutButton;
    JButton lastDept = null;
    JButton lastCCF = null;
    JButton dept = new JButton("Departments");
    JButton hod = new JButton("HOD");
    JButton deleteDept = new JButton("Delete");

    JButton ccfPosition = new JButton("Position");
    JButton ccfName = new JButton("Name");
    JButton ccfId = new JButton("Id");
    JButton ccfEdit = new JButton("Edit");

    public AdminPage() {
        page = new JPanel();
        layout = new SpringLayout();
        page.setLayout(layout);

        searchButton = new JButton();
        try {
            searchButton.setIcon(new ImageIcon(ImageIO.read(new File("src/R/drawable/search.png")).getScaledInstance(15,
                    15, java.awt.Image.SCALE_SMOOTH)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        searchButton.setText("Search");

        addDepartmentButton = new JButton("Add department");
        logoutButton = new JButton("Log Out");
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setForeground(Color.RED);

        allFaculties = db.findAllFaculty();
        distributeFaculties(allFaculties);
        if (depts.isEmpty()) {
            addCCF("Director");
        }
        lastCCF = addDepartmentButton;
        setCCF();
        setDepartmentNames(1);

        layout.putConstraint(SpringLayout.WEST, addDepartmentButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addDepartmentButton, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, searchButton, 5, SpringLayout.EAST, addDepartmentButton);
        layout.putConstraint(SpringLayout.NORTH, searchButton, 5, SpringLayout.NORTH, page);

        layout.putConstraint(SpringLayout.WEST, logoutButton, 5, SpringLayout.EAST, searchButton);
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

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                QueryPage queryPage = new QueryPage();
                setActivity(queryPage.page);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });

        page.add(addDepartmentButton);
        page.add(searchButton);
        page.add(logoutButton);
    }

    private void setCCF() {
        ccfPosition.setOpaque(false);
        ccfPosition.setContentAreaFilled(false);
        ccfPosition.setBorderPainted(false);
        ccfPosition.setFont(new Font(dept.getFont().getName(), Font.BOLD, dept.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, ccfPosition, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, ccfPosition, 5, SpringLayout.SOUTH, addDepartmentButton);
        page.add(ccfPosition);

        ccfName.setOpaque(false);
        ccfName.setContentAreaFilled(false);
        ccfName.setBorderPainted(false);
        ccfName.setFont(new Font(ccfName.getFont().getName(), Font.BOLD, ccfName.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, ccfName, 5, SpringLayout.EAST, ccfPosition);
        layout.putConstraint(SpringLayout.NORTH, ccfName, 5, SpringLayout.SOUTH, addDepartmentButton);
        page.add(ccfName);

        ccfId.setOpaque(false);
        ccfId.setContentAreaFilled(false);
        ccfId.setBorderPainted(false);
        ccfId.setFont(new Font(ccfId.getFont().getName(), Font.BOLD, ccfId.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, ccfId, 5, SpringLayout.EAST, ccfName);
        layout.putConstraint(SpringLayout.NORTH, ccfId, 5, SpringLayout.SOUTH, addDepartmentButton);
        page.add(ccfId);

        ccfEdit.setOpaque(false);
        ccfEdit.setContentAreaFilled(false);
        ccfEdit.setBorderPainted(false);
        ccfEdit.setFont(new Font(ccfEdit.getFont().getName(), Font.BOLD, ccfEdit.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, ccfEdit, 5, SpringLayout.EAST, ccfId);
        layout.putConstraint(SpringLayout.NORTH, ccfEdit, 5, SpringLayout.SOUTH, addDepartmentButton);
        page.add(ccfEdit);

        lastCCF = ccfPosition;
        for (Document doc : faculties.get("CCF")) {

            JButton position = new JButton(doc.getString("position"));
            JButton name = new JButton(doc.getString("name"));
            JButton id = new JButton(doc.getString("f_id"));
            JButton edit = new JButton();
            try {
                edit = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png")).getScaledInstance(15,
                        15, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            position.setOpaque(false);
            position.setContentAreaFilled(false);
            position.setBorderPainted(false);
            position.setForeground(Color.BLUE);

            name.setOpaque(false);
            name.setContentAreaFilled(false);
            name.setBorderPainted(false);
            name.setForeground(Color.BLUE);

            id.setOpaque(false);
            id.setContentAreaFilled(false);
            id.setBorderPainted(false);
            id.setForeground(Color.BLUE);

            edit.setOpaque(false);
            edit.setContentAreaFilled(false);
            edit.setBorderPainted(false);
            edit.setForeground(Color.BLUE);
            edit.setActionCommand(doc.getString("position"));
            edit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTextField fac_id = new JTextField(5);

                    JPanel newfac = new JPanel();
                    newfac.setLayout(new BoxLayout(newfac, BoxLayout.PAGE_AXIS));
                    newfac.add(new JLabel("Faculty Id: *"));
                    newfac.add(fac_id);

                    int result = JOptionPane.showConfirmDialog(page, newfac, "Please enter the followings",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String hod_id = fac_id.getText();

                        Document temp_dept = null;
                        for (Document doc : depts) {
                            if (doc.getString("d_id").equals("CCF")) {
                                temp_dept = doc;
                                break;
                            }
                        }

                        Document newHodDocument = null;
                        boolean flag = true;
                        for (Document doc : allFaculties) {
                            if (doc.getString("f_id").equals(hod_id)) {
                                if (doc.getString("position").equals("HOD") || doc.getString("position").equals("Dean")
                                        || doc.getString("position").equals("Associative Dean")
                                        || doc.getString("position").equals("Director")) {
                                    flag = false;
                                    break;
                                }
                                newHodDocument = doc;
                                break;
                            }
                        }

                        String hod_faculty = null;
                        if (newHodDocument != null) {
                            Document tempDoc = ((Document) temp_dept.get(e.getActionCommand()));
                            hod_faculty = tempDoc.getString(String.valueOf(tempDoc.size() - 1));
                            for (Document doc : faculties.get("CCF")) {
                                if (doc.getString("f_id").equals(hod_faculty)) {
                                    allFaculties.remove(doc);
                                    allFaculties.remove(newHodDocument);
                                    faculties.get("CCF").remove(doc);
                                    faculties.get("CCF").remove(newHodDocument);
                                    doc.put("position", "Faculty");
                                    doc.put("d_id", newHodDocument.getString("d_id"));
                                    db.upsertFaculty(doc);
                                    newHodDocument.put("position", e.getActionCommand());
                                    newHodDocument.put("d_id", "CCF");
                                    db.upsertFaculty(newHodDocument);
                                    allFaculties.add(doc);
                                    allFaculties.add(newHodDocument);
                                    faculties.get("CCF").add(doc);
                                    faculties.get("CCF").add(newHodDocument);
                                    break;
                                }
                            }
                            tempDoc.put(String.valueOf(tempDoc.size()), fac_id.getText());
                            temp_dept.put(e.getActionCommand(), tempDoc);
                            db.upsertDept(temp_dept);
                            logoutButton.doClick();
                            LoginPage.loginButton.doClick();
                        } else if (!flag)
                            JOptionPane.showMessageDialog(page, "Entered faculty is alredy having some position.");
                        else
                            JOptionPane.showMessageDialog(page,
                                    "No faculty with id " + fac_id.getText() + " found in any department.");
                    }

                }

            });

            layout.putConstraint(SpringLayout.WEST, position, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, position, 5, SpringLayout.SOUTH, lastCCF);

            layout.putConstraint(SpringLayout.WEST, name, 5, SpringLayout.EAST, ccfPosition);
            layout.putConstraint(SpringLayout.NORTH, name, 5, SpringLayout.SOUTH, lastCCF);

            layout.putConstraint(SpringLayout.WEST, id, 8, SpringLayout.EAST, ccfName);
            layout.putConstraint(SpringLayout.NORTH, id, 5, SpringLayout.SOUTH, lastCCF);

            layout.putConstraint(SpringLayout.WEST, edit, 8, SpringLayout.EAST, ccfId);
            layout.putConstraint(SpringLayout.NORTH, edit, 5, SpringLayout.SOUTH, lastCCF);

            page.add(position);
            page.add(name);
            page.add(id);
            page.add(edit);
            lastCCF = position;
        }
    }

    private void addCCF(String position) {
        JTextField name = new JTextField(5);
        JTextField id = new JTextField(5);
        JTextField pwd = new JTextField(5);

        JPanel newDept = new JPanel();
        newDept.setLayout(new BoxLayout(newDept, BoxLayout.PAGE_AXIS));
        newDept.add(new JLabel("Name: *"));
        newDept.add(name);
        newDept.add(new JLabel("ID: *"));
        newDept.add(id);
        newDept.add(new JLabel("Password: *"));
        newDept.add(pwd);
        int result = JOptionPane.showConfirmDialog(page, newDept, "Enter Details of " + position,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (name.getText().isEmpty() || id.getText().isEmpty() || pwd.getText().isEmpty())
                JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
            else {
                Document insertedFac = db.addNewFaculty(id.getText(), pwd.getText(), name.getText(), "CCF", position);
                if (allFaculties == null) {
                    allFaculties = new ArrayList<>();
                    distributeFaculties(allFaculties);
                }
                allFaculties.add(insertedFac);
                if (faculties.get("CCF") == null)
                    faculties.put("CCF", new ArrayList<Document>());
                faculties.get("CCF").add(insertedFac);
                if (position.equals("Director")) {
                    Document insertedDept = db.addNewDepartment("CCF", "Director", id.getText());
                    depts.add(insertedDept);
                    addCCF("Dean");
                }
                if (position.equals("Dean")) {
                    Document d = depts.get(0);
                    depts.remove(d);
                    d.append("Dean", new Document("0", id.getText()));
                    db.upsertDept(d);
                    depts.add(d);
                    addCCF("Associative Dean");
                }
                if (position.equals("Associative Dean")) {
                    Document d = depts.get(0);
                    depts.remove(d);
                    d.append("Associative Dean", new Document("0", id.getText()));
                    db.upsertDept(d);
                    depts.add(d);
                }
            }
        }

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
            layout.putConstraint(SpringLayout.NORTH, dept, 5, SpringLayout.SOUTH, lastCCF);
            page.add(dept);

            hod.setOpaque(false);
            hod.setContentAreaFilled(false);
            hod.setBorderPainted(false);
            hod.setFont(new Font(hod.getFont().getName(), Font.BOLD, hod.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, hod, 5, SpringLayout.EAST, dept);
            layout.putConstraint(SpringLayout.NORTH, hod, 5, SpringLayout.SOUTH, lastCCF);
            page.add(hod);

            deleteDept.setOpaque(false);
            deleteDept.setContentAreaFilled(false);
            deleteDept.setBorderPainted(false);
            deleteDept.setFont(new Font(deleteDept.getFont().getName(), Font.BOLD, deleteDept.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, deleteDept, 5, SpringLayout.EAST, hod);
            layout.putConstraint(SpringLayout.NORTH, deleteDept, 5, SpringLayout.SOUTH, lastCCF);
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
            hodName.setActionCommand(depts.get(i).getString("d_id"));

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
                    JTextField fac_id = new JTextField(5);

                    JPanel newfac = new JPanel();
                    newfac.setLayout(new BoxLayout(newfac, BoxLayout.PAGE_AXIS));
                    newfac.add(new JLabel("Faculty Id: *"));
                    newfac.add(fac_id);

                    int result = JOptionPane.showConfirmDialog(page, newfac, "Please enter the followings",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String hod_id = fac_id.getText();

                        Document temp_dept = null;
                        for (Document doc : depts) {
                            if (doc.getString("d_id").equals(e.getActionCommand())) {
                                temp_dept = doc;
                                break;
                            }
                        }

                        Document newHodDocument = null;
                        for (Document doc : faculties.get(e.getActionCommand())) {
                            if (doc.getString("f_id").equals(hod_id)) {
                                newHodDocument = doc;
                                break;
                            }
                        }

                        String hod_faculty = null;
                        if (newHodDocument != null) {
                            // allFaculties.remove(temp);
                            Document tempDoc = ((Document) temp_dept.get("hod"));
                            hod_faculty = tempDoc.getString(String.valueOf(tempDoc.size() - 1));
                            boolean flag = true;
                            if (hod_faculty.equals(hod_id))
                                flag = false;
                            if (flag) {
                                for (Document doc : faculties.get(e.getActionCommand())) {
                                    if (doc.getString("f_id").equals(hod_faculty)) {
                                        allFaculties.remove(doc);
                                        allFaculties.remove(newHodDocument);
                                        faculties.get(e.getActionCommand()).remove(doc);
                                        faculties.get(e.getActionCommand()).remove(newHodDocument);
                                        doc.put("position", "Faculty");
                                        db.upsertFaculty(doc);
                                        newHodDocument.put("position", "HOD");
                                        db.upsertFaculty(newHodDocument);
                                        allFaculties.add(doc);
                                        allFaculties.add(newHodDocument);
                                        faculties.get(e.getActionCommand()).add(doc);
                                        faculties.get(e.getActionCommand()).add(newHodDocument);
                                        break;
                                    }
                                }
                                tempDoc.put(String.valueOf(tempDoc.size()), fac_id.getText());
                                temp_dept.put("hod", tempDoc);
                                db.upsertDept(temp_dept);
                                logoutButton.doClick();
                                LoginPage.loginButton.doClick();
                            } else
                                JOptionPane.showMessageDialog(page, "Entered faculty is alredy having some position.");
                        } else
                            JOptionPane.showMessageDialog(page, "No faculty with id " + fac_id.getText() + " found in "
                                    + e.getActionCommand() + ".");
                    }
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