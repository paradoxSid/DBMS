package com.sid;

import static com.sid.ActivityMain.leavesDb;
import static com.sid.ActivityMain.routes;
import static com.sid.ActivityMain.setActivity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.bson.Document;

public class RoutePage {
    public JPanel page;
    SpringLayout layout;
    JButton addRouteButton;
    JButton backButton;
    JButton lastRoute = null;
    JButton hnoHead = new JButton("H.No.");
    JButton applicantHead = new JButton("Applicant");
    JButton auth1Head = new JButton("Authentication 1");
    JButton auth2Head = new JButton("Authentication 2");
    JButton editRouteHead = new JButton("Edit");
    String[] allPositions;

    public RoutePage() {
        page = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(5000, 5000);
            }
        };
        layout = new SpringLayout();
        page.setLayout(layout);

        allPositions = new String[ActivityMain.depts.get(0).size() + 1];
        int i = 0;
        allPositions[i++] = "Select";
        for (String s : ActivityMain.depts.get(0).keySet()) {
            if (s.equals("_id") || s.equals("d_id"))
                continue;
            allPositions[i++] = s;
        }
        allPositions[i++] = "HOD";
        allPositions[i++] = "Faculty";

        addRouteButton = new JButton("Add Route");
        addRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // JFormattedTextField hno = new JFormattedTextField(new
                // NumberFormatter(NumberFormat.getInstance()));
                JComboBox<String> hno = new JComboBox<>();
                hno.addItem("Select");
                for (int j = 1; j < allPositions.length; j++)
                    hno.addItem(j + "");
                JComboBox<String> newApplicant = new JComboBox<>(allPositions);
                JComboBox<String> newAuth1 = new JComboBox<>(allPositions);
                JComboBox<String> newAuth2 = new JComboBox<>(allPositions);

                newApplicant.setSelectedItem("Select");
                newAuth1.setSelectedItem("Select");
                newAuth2.setSelectedItem("Select");

                JPanel newRoute = new JPanel();
                newRoute.setLayout(new GridLayout(8, 1));
                newRoute.add(new JLabel("H.No.: *"));
                newRoute.add(hno);
                newRoute.add(new JLabel("Applicant: *"));
                newRoute.add(newApplicant);
                newRoute.add(new JLabel("Authentication 1: *"));
                newRoute.add(newAuth1);
                newRoute.add(new JLabel("Authentication 2: *"));
                newRoute.add(newAuth2);

                int result = JOptionPane.showConfirmDialog(ActivityMain.mainFrame, newRoute,
                        "Please enter the followings", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    if (hno.getSelectedItem().equals("Select")
                            || ((String) newApplicant.getSelectedItem()).equals("Select")
                            || ((String) newAuth1.getSelectedItem()).equals("Select")
                            || ((String) newAuth2.getSelectedItem()).equals("Select"))
                        JOptionPane.showMessageDialog(ActivityMain.mainFrame,
                                "All the fields must be Filled. Try Again");
                    else {
                        if (routes.containsKey((String) newApplicant.getSelectedItem()))
                            JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Applicant already exist.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        else {
                            Document inserted = null;
                            try {
                                inserted = leavesDb.addNewRoute(Integer.parseInt((String) hno.getSelectedItem()),
                                        (String) newApplicant.getSelectedItem(), (String) newAuth1.getSelectedItem(),
                                        (String) newAuth2.getSelectedItem());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            routes.put((String) newApplicant.getSelectedItem(), inserted);
                            // setRoutes(newApplicant.getText());
                        }
                    }
                }
            }
        });
        layout.putConstraint(SpringLayout.WEST, addRouteButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addRouteButton, 5, SpringLayout.NORTH, page);
        page.add(addRouteButton);

        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActivity(null);
            }
        });
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, addRouteButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        page.add(backButton);

        setRoutes(0);
    }

    public void setRoutes(int index) {
        if (lastRoute == null) {
            hnoHead.setOpaque(false);
            hnoHead.setContentAreaFilled(false);
            hnoHead.setBorderPainted(false);
            hnoHead.setFont(new Font(hnoHead.getFont().getName(), Font.BOLD, hnoHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, hnoHead, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, hnoHead, 5, SpringLayout.SOUTH, addRouteButton);
            page.add(hnoHead);

            applicantHead.setOpaque(false);
            applicantHead.setContentAreaFilled(false);
            applicantHead.setBorderPainted(false);
            applicantHead.setFont(
                    new Font(applicantHead.getFont().getName(), Font.BOLD, applicantHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, applicantHead, 5, SpringLayout.EAST, hnoHead);
            layout.putConstraint(SpringLayout.NORTH, applicantHead, 5, SpringLayout.SOUTH, addRouteButton);
            page.add(applicantHead);

            auth1Head.setOpaque(false);
            auth1Head.setContentAreaFilled(false);
            auth1Head.setBorderPainted(false);
            auth1Head.setFont(new Font(auth1Head.getFont().getName(), Font.BOLD, auth1Head.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, auth1Head, 5, SpringLayout.EAST, applicantHead);
            layout.putConstraint(SpringLayout.NORTH, auth1Head, 5, SpringLayout.SOUTH, addRouteButton);
            page.add(auth1Head);

            auth2Head.setOpaque(false);
            auth2Head.setContentAreaFilled(false);
            auth2Head.setBorderPainted(false);
            auth2Head.setFont(new Font(auth2Head.getFont().getName(), Font.BOLD, auth2Head.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, auth2Head, 5, SpringLayout.EAST, auth1Head);
            layout.putConstraint(SpringLayout.NORTH, auth2Head, 5, SpringLayout.SOUTH, addRouteButton);
            page.add(auth2Head);

            editRouteHead.setOpaque(false);
            editRouteHead.setContentAreaFilled(false);
            editRouteHead.setBorderPainted(false);
            editRouteHead.setFont(
                    new Font(editRouteHead.getFont().getName(), Font.BOLD, editRouteHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, editRouteHead, 5, SpringLayout.EAST, auth2Head);
            layout.putConstraint(SpringLayout.NORTH, editRouteHead, 5, SpringLayout.SOUTH, addRouteButton);
            page.add(editRouteHead);

            lastRoute = applicantHead;
        }
        List<String> keySet = new ArrayList<>(routes.keySet());
        for (String key : keySet) {
            Document temp = routes.get(key);

            JButton hNum = new JButton(temp.getInteger("hno") + "");
            hNum.setOpaque(false);
            hNum.setContentAreaFilled(false);
            hNum.setBorderPainted(false);
            hNum.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, hNum, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, hNum, 5, SpringLayout.SOUTH, lastRoute);
            page.add(hNum);

            JButton applicant = new JButton(temp.getString("applicant"));
            applicant.setOpaque(false);
            applicant.setContentAreaFilled(false);
            applicant.setBorderPainted(false);
            applicant.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, applicant, 5, SpringLayout.EAST, hNum);
            layout.putConstraint(SpringLayout.NORTH, applicant, 5, SpringLayout.SOUTH, lastRoute);
            page.add(applicant);

            JButton auth1 = new JButton(temp.getString("auth1"));
            auth1.setOpaque(false);
            auth1.setContentAreaFilled(false);
            auth1.setBorderPainted(false);
            auth1.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, auth1, 5, SpringLayout.EAST, applicantHead);
            layout.putConstraint(SpringLayout.NORTH, auth1, 5, SpringLayout.SOUTH, lastRoute);
            page.add(auth1);

            JButton auth2 = new JButton(temp.getString("auth2"));
            auth2.setOpaque(false);
            auth2.setContentAreaFilled(false);
            auth2.setBorderPainted(false);
            auth2.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, auth2, 5, SpringLayout.EAST, auth1Head);
            layout.putConstraint(SpringLayout.NORTH, auth2, 5, SpringLayout.SOUTH, lastRoute);
            page.add(auth2);

            JButton editRoute = null;
            try {
                editRoute = new JButton(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png"))
                        .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            editRoute.setOpaque(false);
            editRoute.setContentAreaFilled(false);
            editRoute.setBorderPainted(false);
            editRoute.setForeground(Color.BLUE);
            editRoute.setActionCommand(temp.getInteger("hno") + "!" + temp.getString("applicant") + "!"
                    + temp.getString("auth1") + "!" + temp.getString("auth2"));
            editRoute.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split("!");
                    String[] appli = { "Select", s[1] };

                    JComboBox<String> hno = new JComboBox<>();
                    hno.addItem("Select");
                    for (int j = 1; j < allPositions.length; j++)
                        hno.addItem(j + "");
                    JComboBox<String> newApplicant = new JComboBox<>(appli);
                    JComboBox<String> newAuth1 = new JComboBox<>(allPositions);
                    JComboBox<String> newAuth2 = new JComboBox<>(allPositions);

                    hno.setSelectedItem(s[0]);
                    newApplicant.setSelectedItem(s[1]);
                    newAuth1.setSelectedItem(s[2]);
                    newAuth2.setSelectedItem(s[3]);

                    JPanel newRoute = new JPanel();
                    newRoute.setLayout(new GridLayout(8, 1));
                    newRoute.add(new JLabel("H.No.: *"));
                    newRoute.add(hno);
                    newRoute.add(new JLabel("Applicant: *"));
                    newRoute.add(newApplicant);
                    newRoute.add(new JLabel("Authentication 1: *"));
                    newRoute.add(newAuth1);
                    newRoute.add(new JLabel("Authentication 2: *"));
                    newRoute.add(newAuth2);

                    int result = JOptionPane.showConfirmDialog(ActivityMain.mainFrame, newRoute,
                            "Please enter the followings", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        if (hno.getSelectedItem().equals("Select")
                                || ((String) newApplicant.getSelectedItem()).equals("Select")
                                || ((String) newAuth1.getSelectedItem()).equals("Select")
                                || ((String) newAuth2.getSelectedItem()).equals("Select"))
                            JOptionPane.showMessageDialog(ActivityMain.mainFrame,
                                    "All the fields must be Filled. Try Again");
                        else {
                            Document inserted = null;
                            try {
                                inserted = leavesDb.editRoute(Integer.parseInt((String) hno.getSelectedItem()),
                                        (String) newApplicant.getSelectedItem(), (String) newAuth1.getSelectedItem(),
                                        (String) newAuth2.getSelectedItem());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            routes.put((String) newApplicant.getSelectedItem(), inserted);
                            // setRoutes(newApplicant.getText());
                        }
                    }
                }
            });
            layout.putConstraint(SpringLayout.WEST, editRoute, 5, SpringLayout.EAST, auth2Head);
            layout.putConstraint(SpringLayout.NORTH, editRoute, 5, SpringLayout.SOUTH, lastRoute);
            page.add(editRoute);

            lastRoute = applicant;
        }
        page.revalidate();
    }
}