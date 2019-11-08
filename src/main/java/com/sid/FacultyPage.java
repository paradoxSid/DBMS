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
    public static Document facDoc;
    JButton logoutButton;

    public FacultyPage(Document doc) {
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        facDoc = doc;

        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
        JButton profileButton = new JButton("Profile");
        line.add(profileButton);
        
        JButton leaveButton = new JButton("Leave");
        line.add(leaveButton);

        logoutButton = new JButton("Logout");
        line.add(logoutButton);
        page.add(line);

        JPanel pro = setUpFacultyProfile();
        page.add(pro);

        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // LeavePage leavePage = new LeavePage(facDoc);
                // setActivity(leavePage.page);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                db.facultyDoc = null;
                setActivity(null);
            }
        });
        
    }

    JPanel setUpFacultyProfile(){
        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(facDoc.getString("name"));
        name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize() + 10));
        profile.add(name);

        JButton editButton = null;
        try {
            editButton = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png"))
                    .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        editButton.setOpaque(false);
        editButton.setContentAreaFilled(false);
        editButton.setBorderPainted(false);
        editButton.setForeground(Color.BLUE);
        profile.add(editButton);

        JTextArea department = new JTextArea(facDoc.getString("position") + ", " + facDoc.getString("d_id") + " department");
        department.setEditable(false);
        department.setCursor(null);
        department.setOpaque(false);
        department.setFocusable(false);
        department.setFont(new Font(department.getFont().getName(), Font.BOLD, department.getFont().getSize()));
        profile.add(department);

        setExtras(profile);
        
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createJOptionPane();
            }
        });
        return profile;
    }

    void createJOptionPane() {
        JTextArea textAboutMe = new JTextArea(5, 50);
        JScrollPane aboutMe = new JScrollPane(textAboutMe);
        JTextArea textSkillset = new JTextArea(5, 50);
        JScrollPane skillset = new JScrollPane(textSkillset);
        JTextArea textOffice = new JTextArea(1, 50);
        JScrollPane office = new JScrollPane(textOffice);
        JTextField phone = new JTextField(5);
        JTextField email = new JTextField(5);
        JTextField birth = new JTextField(5);
        JTextArea textAdd = new JTextArea(1, 50);
        JScrollPane address = new JScrollPane(textAdd);
        JTextField gender = new JTextField(5);
        JPanel editFac = new JPanel();

        Document extrasDoc = (Document) facDoc.get("extra");
        textAboutMe.setText(extrasDoc.getString("About Me"));
        textSkillset.setText(extrasDoc.getString("Skill Set"));
        textOffice.setText(((Document)extrasDoc.get("Work")).getString("Office"));
        phone.setText(((Document)extrasDoc.get("Personal")).getString("Mobile Phone"));
        email.setText(((Document)extrasDoc.get("Personal")).getString("Email"));
        birth.setText(((Document)extrasDoc.get("Personal")).getString("Birth Date"));
        textAdd.setText(((Document)extrasDoc.get("Personal")).getString("Address"));
        gender.setText(((Document)extrasDoc.get("Personal")).getString("Gender"));

        editFac.setLayout(new BoxLayout(editFac, BoxLayout.Y_AXIS));
        editFac.add(new JLabel("About Me: *", JLabel.LEFT));
        editFac.add(aboutMe);
        editFac.add(new JLabel("Skill Set: *", JLabel.LEFT));
        editFac.add(skillset);
        editFac.add(new JLabel("Office address: *", JLabel.LEFT));
        editFac.add(office);
        editFac.add(new JLabel("Moblie Phone: *", JLabel.LEFT));
        editFac.add(phone);
        editFac.add(new JLabel("Email: *", JLabel.LEFT));
        editFac.add(email);
        editFac.add(new JLabel("Birthday: *", JLabel.LEFT));
        editFac.add(birth);
        editFac.add(new JLabel("Home Address: *", JLabel.LEFT));
        editFac.add(address);
        editFac.add(new JLabel("Gender: *", JLabel.LEFT));
        editFac.add(gender);

        int result = JOptionPane.showConfirmDialog(page, editFac, "Please enter the followings",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (textAboutMe.getText().isEmpty() || textSkillset.getText().isEmpty() || textOffice.getText().isEmpty()
                    || phone.getText().isEmpty() || email.getText().isEmpty() || birth.getText().isEmpty()
                    || textAdd.getText().isEmpty() || gender.getText().isEmpty())
                JOptionPane.showMessageDialog(page, "All the fields must be Filled. Try Again");
            else {
                Document extras = (Document) facDoc.get("extra");
                extras.put("About Me", textAboutMe.getText());
                extras.put("Skill Set", textSkillset.getText());
                ((Document)extras.get("Work")).put("Office", textOffice.getText());
                ((Document)extras.get("Personal")).put("Mobile Phone", phone.getText());
                ((Document)extras.get("Personal")).put("Email", email.getText());
                ((Document)extras.get("Personal")).put("Birth Date", birth.getText());
                ((Document)extras.get("Personal")).put("Address", textAdd.getText());
                ((Document)extras.get("Personal")).put("Gender", gender.getText());
                facDoc.put("extra", extras);
                db.upsertFaculty(facDoc);
                logoutButton.doClick();
                loginButton.doClick();
            }
        }
    }

    void setExtras(JPanel profile) {
        Document extras = (Document) facDoc.get("extra");

        JLabel headingAboutMe = new JLabel("About Me");
        headingAboutMe.setFont(
                new Font(headingAboutMe.getFont().getName(), Font.BOLD, headingAboutMe.getFont().getSize() + 5));
        profile.add(headingAboutMe);

        JTextArea detailsAboutMe = new JTextArea(extras.getString("About Me"));
        detailsAboutMe.setEditable(false);
        detailsAboutMe.setCursor(null);
        detailsAboutMe.setOpaque(false);
        detailsAboutMe.setFocusable(false);
        detailsAboutMe.setLineWrap(true);
        detailsAboutMe.setWrapStyleWord(true);
        detailsAboutMe.setColumns(150);
        profile.add(detailsAboutMe);
        
        JLabel headingSkillSet = new JLabel("Skill Set");
        headingSkillSet.setFont(
                new Font(headingSkillSet.getFont().getName(), Font.BOLD, headingSkillSet.getFont().getSize() + 5));
        profile.add(headingSkillSet);

        JTextArea detailsSkillSet = new JTextArea(extras.getString("Skill Set"));
        detailsSkillSet.setEditable(false);
        detailsSkillSet.setCursor(null);
        detailsSkillSet.setOpaque(false);
        detailsSkillSet.setFocusable(false);
        detailsSkillSet.setLineWrap(true);
        detailsSkillSet.setWrapStyleWord(true);
        detailsSkillSet.setColumns(150);
        profile.add(detailsSkillSet);
        
        JTextArea headingWork = new JTextArea("Work");
        headingWork.setEditable(false);
        headingWork.setCursor(null);
        headingWork.setOpaque(false);
        headingWork.setFocusable(false);
        headingWork.setLineWrap(true);
        headingWork.setWrapStyleWord(true);
        headingWork.setColumns(150);
        headingWork.setFont(new Font(headingWork.getFont().getName(), Font.BOLD, headingWork.getFont().getSize() + 5));
        profile.add(headingWork);

        Document workDoc = (Document) extras.get("Work");
        List<String> workKeys = new ArrayList<>(workDoc.keySet());
        for (String key : workKeys) {
            JLabel work1 = new JLabel(key, JLabel.CENTER);
            work1.setForeground(Color.LIGHT_GRAY);
            profile.add(work1);

            JTextArea work2 = new JTextArea(workDoc.getString(key));
            work2.setEditable(false);
            work2.setCursor(null);
            work2.setOpaque(false);
            work2.setFocusable(false);
            work2.setLineWrap(true);
            work2.setWrapStyleWord(true);
            work2.setColumns(150);
            profile.add(work2);
        }

        JTextArea headingPersonal = new JTextArea("Personal");
        headingPersonal.setEditable(false);
        headingPersonal.setCursor(null);
        headingPersonal.setOpaque(false);
        headingPersonal.setFocusable(false);
        headingPersonal.setLineWrap(true);
        headingPersonal.setWrapStyleWord(true);
        headingPersonal.setColumns(150);
        headingPersonal.setFont(
                new Font(headingPersonal.getFont().getName(), Font.BOLD, headingPersonal.getFont().getSize() + 5));
        profile.add(headingPersonal);

        Document personalDoc = (Document) extras.get("Personal");
        List<String> personalKeys = new ArrayList<>(personalDoc.keySet());
        for (String key : personalKeys) {
            JLabel personal1 = new JLabel(key, JLabel.CENTER);
            personal1.setForeground(Color.LIGHT_GRAY);
            profile.add(personal1);

            JTextArea personal2 = new JTextArea(personalDoc.getString(key));
            personal2.setEditable(false);
            personal2.setCursor(null);
            personal2.setOpaque(false);
            personal2.setFocusable(false);
            personal2.setLineWrap(true);
            personal2.setWrapStyleWord(true);
            personal2.setColumns(150);
            profile.add(personal2);
        }
    }
}
