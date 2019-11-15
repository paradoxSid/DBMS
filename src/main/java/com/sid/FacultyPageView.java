package com.sid;

import static com.sid.ActivityMain.depts;
import static com.sid.ActivityMain.setActivity;

import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.bson.Document;

public class FacultyPageView {
    public JPanel page = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(5000, 5000);
        }
    };
    SpringLayout layout;
    public static Document facDoc;
    public JButton backButton;
    final DateTimeFormatter dfoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    JTextArea lastArea;

    public FacultyPageView(Document doc) {
        layout = new SpringLayout();
        page.setLayout(layout);
        facDoc = doc;

        backButton = new JButton("Back");
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActivity(null);
            }
        });
        page.add(backButton);

        setUpFacultyProfile();
    }

    void setUpFacultyProfile() {
        JLabel name = new JLabel(facDoc.getString("name"));
        name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize() + 10));
        layout.putConstraint(SpringLayout.WEST, name, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, name, 5, SpringLayout.SOUTH, backButton);
        page.add(name);

        JTextArea department = new JTextArea(
                facDoc.getString("position") + ", " + facDoc.getString("d_id") + " department");
        department.setEditable(false);
        department.setCursor(null);
        department.setOpaque(false);
        department.setFocusable(false);
        department.setFont(new Font(department.getFont().getName(), Font.BOLD, department.getFont().getSize()));
        layout.putConstraint(SpringLayout.WEST, department, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, department, 5, SpringLayout.SOUTH, name);
        page.add(department);

        Document extras = (Document) facDoc.get("extra");
        JLabel headingAboutMe = new JLabel("About");
        headingAboutMe.setFont(
                new Font(headingAboutMe.getFont().getName(), Font.BOLD, headingAboutMe.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, headingAboutMe, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, headingAboutMe, 5, SpringLayout.SOUTH, department);
        page.add(headingAboutMe);

        JTextArea detailsAboutMe = new JTextArea(extras.getString("About Me"));
        detailsAboutMe.setEditable(false);
        detailsAboutMe.setCursor(null);
        detailsAboutMe.setOpaque(false);
        detailsAboutMe.setFocusable(false);
        detailsAboutMe.setLineWrap(true);
        detailsAboutMe.setWrapStyleWord(true);
        detailsAboutMe.setColumns(150);
        layout.putConstraint(SpringLayout.WEST, detailsAboutMe, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, detailsAboutMe, 5, SpringLayout.SOUTH, headingAboutMe);
        page.add(detailsAboutMe);

        JLabel headingSkillSet = new JLabel("Skill Set");
        headingSkillSet.setFont(
                new Font(headingSkillSet.getFont().getName(), Font.BOLD, headingSkillSet.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, headingSkillSet, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, headingSkillSet, 5, SpringLayout.SOUTH, detailsAboutMe);
        page.add(headingSkillSet);

        JTextArea detailsSkillSet = new JTextArea(extras.getString("Skill Set"));
        detailsSkillSet.setEditable(false);
        detailsSkillSet.setCursor(null);
        detailsSkillSet.setOpaque(false);
        detailsSkillSet.setFocusable(false);
        detailsSkillSet.setLineWrap(true);
        detailsSkillSet.setWrapStyleWord(true);
        detailsSkillSet.setColumns(150);
        layout.putConstraint(SpringLayout.WEST, detailsSkillSet, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, detailsSkillSet, 5, SpringLayout.SOUTH, headingSkillSet);
        page.add(detailsSkillSet);

        JTextArea headingWork = new JTextArea("Work");
        headingWork.setEditable(false);
        headingWork.setCursor(null);
        headingWork.setOpaque(false);
        headingWork.setFocusable(false);
        headingWork.setLineWrap(true);
        headingWork.setWrapStyleWord(true);
        headingWork.setColumns(150);
        headingWork.setFont(new Font(headingWork.getFont().getName(), Font.BOLD, headingWork.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, headingWork, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, headingWork, 5, SpringLayout.SOUTH, detailsSkillSet);
        page.add(headingWork);

        lastArea = headingWork;
        Document workDoc = (Document) extras.get("Work");
        List<String> workKeys = new ArrayList<>(workDoc.keySet());
        for (String key : workKeys) {
            JLabel work1 = new JLabel(key, JLabel.CENTER);
            work1.setForeground(Color.LIGHT_GRAY);
            layout.putConstraint(SpringLayout.WEST, work1, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, work1, 5, SpringLayout.SOUTH, lastArea);
            page.add(work1);

            JTextArea work2 = new JTextArea();
            if (key.equals("Date of joining"))
                work2.setText(df.format((Date) workDoc.get("Date of joining")));
            else if (key.equals("Department"))
                work2.setText(facDoc.getString("d_id"));
            else
                work2.setText(workDoc.getString(key));
            work2.setEditable(false);
            work2.setCursor(null);
            work2.setOpaque(false);
            work2.setFocusable(false);
            work2.setLineWrap(true);
            work2.setWrapStyleWord(true);
            work2.setColumns(150);
            layout.putConstraint(SpringLayout.WEST, work2, 5, SpringLayout.EAST, work1);
            layout.putConstraint(SpringLayout.NORTH, work2, 5, SpringLayout.SOUTH, lastArea);

            if (!facDoc.getString("d_id").equals("CCF"))
                if (key.equals("HOD ID")) {
                    for (Document doc : depts) {
                        if (doc.getString("d_id").equals(facDoc.getString("d_id"))) {
                            Document allhod = (Document) doc.get("hod");
                            work2.setText(allhod.getString(String.valueOf(allhod.size() - 1)));
                        }
                    }
                }

            page.add(work2);
            lastArea = work2;
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
        layout.putConstraint(SpringLayout.WEST, headingPersonal, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, headingPersonal, 5, SpringLayout.SOUTH, lastArea);
        page.add(headingPersonal);
        lastArea = headingPersonal;

        Document personalDoc = (Document) extras.get("Personal");
        List<String> personalKeys = new ArrayList<>(personalDoc.keySet());
        for (String key : personalKeys) {
            JLabel personal1 = new JLabel(key, JLabel.CENTER);
            personal1.setForeground(Color.LIGHT_GRAY);
            layout.putConstraint(SpringLayout.WEST, personal1, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, personal1, 5, SpringLayout.SOUTH, lastArea);
            page.add(personal1);

            JTextArea personal2 = new JTextArea(personalDoc.getString(key));
            personal2.setEditable(false);
            personal2.setCursor(null);
            personal2.setOpaque(false);
            personal2.setFocusable(false);
            personal2.setLineWrap(true);
            personal2.setWrapStyleWord(true);
            personal2.setColumns(150);
            layout.putConstraint(SpringLayout.WEST, personal2, 5, SpringLayout.EAST, personal1);
            layout.putConstraint(SpringLayout.NORTH, personal2, 5, SpringLayout.SOUTH, lastArea);
            page.add(personal2);
            lastArea = personal2;
        }
        setExtras(0);
    }

    void setExtras(int index) {
        Document extras = (Document) facDoc.get("extra");
        List<String> keys = new ArrayList<>(extras.keySet());
        keys.remove("About Me");
        keys.remove("Skill Set");
        keys.remove("Work");
        keys.remove("Personal");
        for (String key : keys) {
            JLabel heading = new JLabel(key);
            heading.setCursor(null);
            heading.setOpaque(false);
            heading.setFocusable(false);
            heading.setFont(new Font(heading.getFont().getName(), Font.BOLD, heading.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, heading, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, heading, 5, SpringLayout.SOUTH, lastArea);
            page.add(heading);

            JTextArea details = new JTextArea(extras.getString(key));
            details.setEditable(false);
            details.setCursor(null);
            details.setOpaque(false);
            details.setFocusable(false);
            details.setLineWrap(true);
            details.setWrapStyleWord(true);
            details.setColumns(150);
            layout.putConstraint(SpringLayout.WEST, details, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, details, 5, SpringLayout.SOUTH, heading);
            page.add(details);
            lastArea = details;
        }
    }
}
