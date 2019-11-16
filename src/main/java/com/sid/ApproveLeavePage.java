package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.leavesDb;
import static com.sid.ActivityMain.setActivity;
import static com.sid.LeavePage.pendingLeaveRequest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.swing.SpringLayout;

import org.bson.Document;

public class ApproveLeavePage {
    public JPanel page = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(5000, 5000);
        }
    };
    SpringLayout layout = new SpringLayout();
    Document facDoc;
    JButton refreshButton, backButton;

    JButton lIdHead = new JButton("l_id");
    JButton dateAppliedHead = new JButton("Date Of Application");
    JButton fIdHead = new JButton("f_id");
    JButton fromDateHead = new JButton("From (yyyy-MM-dd)");
    JButton toDateHead = new JButton("To (yyyy-MM-dd)");
    JButton commentsHead = new JButton("Comments");
    JButton borrowHead = new JButton("Borrow");
    JButton availabilityHead = new JButton("Availability");
    JButton lastLeave;

    public ApproveLeavePage(Document doc) {
        page.setLayout(layout);
        facDoc = doc;

        refreshButton = new JButton("Refresh");
        layout.putConstraint(SpringLayout.WEST, refreshButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, refreshButton, 5, SpringLayout.NORTH, page);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backButton.doClick();
                LeavePage.refreshButton.doClick();
                LeavePage.approveButton.doClick();
            }
        });
        page.add(refreshButton);

        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, refreshButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActivity(null);
            }
        });
        page.add(backButton);

        setUpLeaves(0);
    }

    void setUpLeaves(int index) {
        if (lastLeave == null) {
            JLabel pendingLeaves = new JLabel("New Leaves");
            pendingLeaves.setForeground(Color.BLUE);
            pendingLeaves.setFont(
                    new Font(pendingLeaves.getFont().getName(), Font.BOLD, pendingLeaves.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, pendingLeaves, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, pendingLeaves, 5, SpringLayout.SOUTH, refreshButton);
            page.add(pendingLeaves);

            lIdHead.setOpaque(false);
            lIdHead.setContentAreaFilled(false);
            lIdHead.setBorderPainted(false);
            lIdHead.setFont(new Font(lIdHead.getFont().getName(), Font.BOLD, lIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, lIdHead, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, lIdHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(lIdHead);

            dateAppliedHead.setOpaque(false);
            dateAppliedHead.setContentAreaFilled(false);
            dateAppliedHead.setBorderPainted(false);
            dateAppliedHead.setFont(
                    new Font(dateAppliedHead.getFont().getName(), Font.BOLD, dateAppliedHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, dateAppliedHead, 5, SpringLayout.EAST, lIdHead);
            layout.putConstraint(SpringLayout.NORTH, dateAppliedHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(dateAppliedHead);

            fIdHead.setOpaque(false);
            fIdHead.setContentAreaFilled(false);
            fIdHead.setBorderPainted(false);
            fIdHead.setFont(new Font(fIdHead.getFont().getName(), Font.BOLD, fIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fIdHead, 5, SpringLayout.EAST, dateAppliedHead);
            layout.putConstraint(SpringLayout.NORTH, fIdHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(fIdHead);

            fromDateHead.setOpaque(false);
            fromDateHead.setContentAreaFilled(false);
            fromDateHead.setBorderPainted(false);
            fromDateHead.setFont(
                    new Font(fromDateHead.getFont().getName(), Font.BOLD, fromDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fromDateHead, 5, SpringLayout.EAST, fIdHead);
            layout.putConstraint(SpringLayout.NORTH, fromDateHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(fromDateHead);

            toDateHead.setOpaque(false);
            toDateHead.setContentAreaFilled(false);
            toDateHead.setBorderPainted(false);
            toDateHead.setFont(new Font(toDateHead.getFont().getName(), Font.BOLD, toDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, toDateHead, 5, SpringLayout.EAST, fromDateHead);
            layout.putConstraint(SpringLayout.NORTH, toDateHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(toDateHead);

            commentsHead.setOpaque(false);
            commentsHead.setContentAreaFilled(false);
            commentsHead.setBorderPainted(false);
            commentsHead.setFont(
                    new Font(commentsHead.getFont().getName(), Font.BOLD, commentsHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, commentsHead, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, commentsHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(commentsHead);

            borrowHead.setOpaque(false);
            borrowHead.setContentAreaFilled(false);
            borrowHead.setBorderPainted(false);
            borrowHead.setFont(new Font(borrowHead.getFont().getName(), Font.BOLD, borrowHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, borrowHead, 5, SpringLayout.EAST, commentsHead);
            layout.putConstraint(SpringLayout.NORTH, borrowHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(borrowHead);

            availabilityHead.setOpaque(false);
            availabilityHead.setContentAreaFilled(false);
            availabilityHead.setBorderPainted(false);
            availabilityHead.setFont(new Font(availabilityHead.getFont().getName(), Font.BOLD,
                    availabilityHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, availabilityHead, 5, SpringLayout.EAST, borrowHead);
            layout.putConstraint(SpringLayout.NORTH, availabilityHead, 5, SpringLayout.SOUTH, pendingLeaves);
            page.add(availabilityHead);

            lastLeave = dateAppliedHead;
        }
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<String> keySet = new ArrayList<>(pendingLeaveRequest.keySet());
        for (String key : keySet) {
            final List<Document> docList = pendingLeaveRequest.get(key);
            for (int i = 0; i < docList.size(); i++) {
                int nod = 0;
                try {
                    nod = (int) numberOfDays(df.parse(docList.get(i).getString("from_date")),
                            df.parse(docList.get(i).getString("to_date")));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                JButton lId = new JButton(docList.get(i).getString("l_id"));
                lId.setOpaque(false);
                lId.setContentAreaFilled(false);
                lId.setBorderPainted(false);
                lId.setForeground(Color.BLUE);
                lId.setActionCommand(docList.get(i).getString("l_id") + " " + docList.get(i).getString("f_id") + " "
                        + nod + " " + key);
                lId.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ");
                        createJOptionPane(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3]);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, lId, 5, SpringLayout.WEST, page);
                layout.putConstraint(SpringLayout.NORTH, lId, 5, SpringLayout.SOUTH, lastLeave);
                page.add(lId);

                JButton dateApplied = new JButton(docList.get(i).getString("application_date"));
                dateApplied.setOpaque(false);
                dateApplied.setContentAreaFilled(false);
                dateApplied.setBorderPainted(false);
                dateApplied.setForeground(Color.BLUE);
                dateApplied.setActionCommand(docList.get(i).getString("l_id") + " " + docList.get(i).getString("f_id")
                        + " " + nod + " " + key);
                dateApplied.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ");
                        createJOptionPane(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3]);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, dateApplied, 5, SpringLayout.EAST, lIdHead);
                layout.putConstraint(SpringLayout.NORTH, dateApplied, 5, SpringLayout.SOUTH, lastLeave);
                page.add(dateApplied);

                JButton fId = new JButton(docList.get(i).getString("f_id"));
                fId.setOpaque(false);
                fId.setContentAreaFilled(false);
                fId.setBorderPainted(false);
                fId.setForeground(Color.BLUE);
                fId.setActionCommand(docList.get(i).getString("l_id") + " " + docList.get(i).getString("f_id") + " "
                        + nod + " " + key);
                fId.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ");
                        createJOptionPane(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3]);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, fId, 5, SpringLayout.EAST, dateAppliedHead);
                layout.putConstraint(SpringLayout.NORTH, fId, 5, SpringLayout.SOUTH, lastLeave);
                page.add(fId);

                JButton fromDate = new JButton(docList.get(i).getString("from_date"));
                fromDate.setOpaque(false);
                fromDate.setContentAreaFilled(false);
                fromDate.setBorderPainted(false);
                fromDate.setForeground(Color.BLUE);
                fromDate.setActionCommand(docList.get(i).getString("l_id") + " " + docList.get(i).getString("f_id")
                        + " " + nod + " " + key);
                fromDate.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ");
                        createJOptionPane(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3]);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, fromDate, 5, SpringLayout.EAST, fIdHead);
                layout.putConstraint(SpringLayout.NORTH, fromDate, 5, SpringLayout.SOUTH, lastLeave);
                page.add(fromDate);

                JButton toDate = new JButton(docList.get(i).getString("to_date"));
                toDate.setOpaque(false);
                toDate.setContentAreaFilled(false);
                toDate.setBorderPainted(false);
                toDate.setForeground(Color.BLUE);
                toDate.setActionCommand(docList.get(i).getString("l_id") + " " + docList.get(i).getString("f_id") + " "
                        + nod + " " + key);
                toDate.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String[] s = e.getActionCommand().split(" ");
                        createJOptionPane(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3]);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, toDate, 5, SpringLayout.EAST, fromDateHead);
                layout.putConstraint(SpringLayout.NORTH, toDate, 5, SpringLayout.SOUTH, lastLeave);
                page.add(toDate);

                JButton comments = new JButton("View");
                comments.setOpaque(false);
                comments.setContentAreaFilled(false);
                comments.setBorderPainted(false);
                comments.setForeground(Color.BLUE);
                comments.setActionCommand(i + "");
                comments.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int i = Integer.parseInt(e.getActionCommand());
                        JPanel commentsView = new JPanel();
                        commentsView.setLayout(new BoxLayout(commentsView, BoxLayout.PAGE_AXIS));
                        if (docList.get(i).containsKey("commentsfac")) {
                            commentsView.add(new JLabel("Comments Fac: "));
                            commentsView.add(new JLabel(docList.get(i).getString("commentsfac")));
                            commentsView.add(new JLabel(" "));
                        }
                        if (docList.get(i).containsKey("auth1comments")) {
                            commentsView.add(new JLabel(docList.get(i).getString("auth1") + " Comments: "));
                            commentsView.add(new JLabel(docList.get(i).getString("auth1comments")));
                            commentsView.add(new JLabel(" "));
                        }
                        JOptionPane.showMessageDialog(ActivityMain.mainFrame, commentsView, "Comments",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                });
                layout.putConstraint(SpringLayout.WEST, comments, 5, SpringLayout.EAST, toDateHead);
                layout.putConstraint(SpringLayout.NORTH, comments, 5, SpringLayout.SOUTH, lastLeave);
                page.add(comments);

                JButton borrow = new JButton(docList.get(i).getString("borrowleaves"));
                borrow.setOpaque(false);
                borrow.setContentAreaFilled(false);
                borrow.setBorderPainted(false);
                borrow.setForeground(Color.RED);
                layout.putConstraint(SpringLayout.WEST, borrow, 5, SpringLayout.EAST, commentsHead);
                layout.putConstraint(SpringLayout.NORTH, borrow, 5, SpringLayout.SOUTH, lastLeave);
                page.add(borrow);

                JButton availability = new JButton();
                try {
                    availability.setIcon(new ImageIcon(ImageIO.read(new File("src/R/drawable/calender.png"))
                            .getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                availability.setOpaque(false);
                availability.setContentAreaFilled(false);
                availability.setBorderPainted(false);
                availability.setForeground(Color.BLUE);
                availability.setActionCommand(i + "");
                availability.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Document levDoc = docList.get(Integer.parseInt(e.getActionCommand()));
                        try {
                            int[] alreadyOnLeave = getData(levDoc, df);
                            java.util.Date fDate = df.parse(levDoc.getString("from_date"));
                            JPanel p = new JPanel();
                            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                            for (int num : alreadyOnLeave) {
                                p.add(new JLabel(df.format(fDate) + ": " + num));
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(fDate);
                                cal.add(Calendar.DATE, 1);
                                fDate = cal.getTime();
                            }
                            JOptionPane.showMessageDialog(ActivityMain.mainFrame, p, "Faculties on leaves each day",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (ParseException | SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                layout.putConstraint(SpringLayout.WEST, availability, 5, SpringLayout.EAST, borrowHead);
                layout.putConstraint(SpringLayout.NORTH, availability, 5, SpringLayout.SOUTH, lastLeave);
                page.add(availability);

                lastLeave = dateApplied;
            }
        }
        try {
            setUpOldLeaves();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        page.revalidate();
    }

    private void setUpOldLeaves() throws SQLException, ParseException {
        JLabel oldLeaves = new JLabel("Approved Leaves");
        oldLeaves.setForeground(Color.BLUE);
        oldLeaves.setFont(new Font(oldLeaves.getFont().getName(), Font.BOLD, oldLeaves.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, oldLeaves, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, oldLeaves, 5, SpringLayout.SOUTH, lastLeave);
        page.add(oldLeaves);

        JButton lIdHead1 = new JButton("l_id");
        lIdHead1.setOpaque(false);
        lIdHead1.setContentAreaFilled(false);
        lIdHead1.setBorderPainted(false);
        lIdHead1.setFont(new Font(lIdHead1.getFont().getName(), Font.BOLD, lIdHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, lIdHead1, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, lIdHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(lIdHead1);

        JButton dateAppliedHead1 = new JButton("Date of Application");
        dateAppliedHead1.setOpaque(false);
        dateAppliedHead1.setContentAreaFilled(false);
        dateAppliedHead1.setBorderPainted(false);
        dateAppliedHead1.setFont(
                new Font(dateAppliedHead1.getFont().getName(), Font.BOLD, dateAppliedHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, dateAppliedHead1, 5, SpringLayout.EAST, lIdHead);
        layout.putConstraint(SpringLayout.NORTH, dateAppliedHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(dateAppliedHead1);

        JButton fIdHead1 = new JButton("f_id");
        fIdHead1.setOpaque(false);
        fIdHead1.setContentAreaFilled(false);
        fIdHead1.setBorderPainted(false);
        fIdHead1.setFont(new Font(fIdHead1.getFont().getName(), Font.BOLD, fIdHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, fIdHead1, 5, SpringLayout.EAST, dateAppliedHead);
        layout.putConstraint(SpringLayout.NORTH, fIdHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(fIdHead1);

        JButton fromDateHead1 = new JButton("From (yyyy-MM-dd)");
        fromDateHead1.setOpaque(false);
        fromDateHead1.setContentAreaFilled(false);
        fromDateHead1.setBorderPainted(false);
        fromDateHead1
                .setFont(new Font(fromDateHead1.getFont().getName(), Font.BOLD, fromDateHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, fromDateHead1, 5, SpringLayout.EAST, fIdHead);
        layout.putConstraint(SpringLayout.NORTH, fromDateHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(fromDateHead1);

        JButton toDateHead1 = new JButton("To (yyyy-MM-dd)");
        toDateHead1.setOpaque(false);
        toDateHead1.setContentAreaFilled(false);
        toDateHead1.setBorderPainted(false);
        toDateHead1.setFont(new Font(toDateHead1.getFont().getName(), Font.BOLD, toDateHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, toDateHead1, 5, SpringLayout.EAST, fromDateHead);
        layout.putConstraint(SpringLayout.NORTH, toDateHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(toDateHead1);

        JButton commentsHead1 = new JButton("Comments");
        commentsHead1.setOpaque(false);
        commentsHead1.setContentAreaFilled(false);
        commentsHead1.setBorderPainted(false);
        commentsHead1
                .setFont(new Font(commentsHead1.getFont().getName(), Font.BOLD, commentsHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, commentsHead1, 5, SpringLayout.EAST, toDateHead);
        layout.putConstraint(SpringLayout.NORTH, commentsHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(commentsHead1);

        JButton borrowHead1 = new JButton("Borrow");
        borrowHead1.setOpaque(false);
        borrowHead1.setContentAreaFilled(false);
        borrowHead1.setBorderPainted(false);
        borrowHead1.setFont(new Font(borrowHead1.getFont().getName(), Font.BOLD, borrowHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, borrowHead1, 5, SpringLayout.EAST, commentsHead);
        layout.putConstraint(SpringLayout.NORTH, borrowHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(borrowHead1);

        JButton approvedByHODHead1 = new JButton("Approved 1");
        approvedByHODHead1.setOpaque(false);
        approvedByHODHead1.setContentAreaFilled(false);
        approvedByHODHead1.setBorderPainted(false);
        approvedByHODHead1.setFont(new Font(approvedByHODHead1.getFont().getName(), Font.BOLD,
                approvedByHODHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, approvedByHODHead1, 5, SpringLayout.EAST, borrowHead1);
        layout.putConstraint(SpringLayout.NORTH, approvedByHODHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(approvedByHODHead1);

        JButton approvedByDeanHead1 = new JButton("Approved 2");
        approvedByDeanHead1.setOpaque(false);
        approvedByDeanHead1.setContentAreaFilled(false);
        approvedByDeanHead1.setBorderPainted(false);
        approvedByDeanHead1.setFont(new Font(approvedByDeanHead1.getFont().getName(), Font.BOLD,
                approvedByDeanHead1.getFont().getSize() + 5));
        layout.putConstraint(SpringLayout.WEST, approvedByDeanHead1, 5, SpringLayout.EAST, approvedByHODHead1);
        layout.putConstraint(SpringLayout.NORTH, approvedByDeanHead1, 5, SpringLayout.SOUTH, oldLeaves);
        page.add(approvedByDeanHead1);

        lastLeave = dateAppliedHead1;
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final List<Document> approvedLeaveRequest = leavesDb.getAllApprovedLeaves(facDoc.getString("d_id"),
                new Date(new java.util.Date().getTime()), new Date(df.parse("2030-12-31").getTime()));
        for (int i = 0; i < approvedLeaveRequest.size(); i++) {
            JButton lId = new JButton(approvedLeaveRequest.get(i).getString("l_id"));
            lId.setOpaque(false);
            lId.setContentAreaFilled(false);
            lId.setBorderPainted(false);
            lId.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, lId, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, lId, 5, SpringLayout.SOUTH, lastLeave);
            page.add(lId);

            JButton dateApplied = new JButton(approvedLeaveRequest.get(i).getString("application_date"));
            dateApplied.setOpaque(false);
            dateApplied.setContentAreaFilled(false);
            dateApplied.setBorderPainted(false);
            dateApplied.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, dateApplied, 5, SpringLayout.EAST, lIdHead1);
            layout.putConstraint(SpringLayout.NORTH, dateApplied, 5, SpringLayout.SOUTH, lastLeave);
            page.add(dateApplied);

            JButton fId = new JButton(approvedLeaveRequest.get(i).getString("f_id"));
            fId.setOpaque(false);
            fId.setContentAreaFilled(false);
            fId.setBorderPainted(false);
            fId.setForeground(Color.BLUE);
            fId.setActionCommand(approvedLeaveRequest.get(i).getString("f_id"));
            fId.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FacultyPageView facultyPageView = new FacultyPageView(
                            db.findFaculties(new Document("f_id", e.getActionCommand())).get(0));
                    setActivity(facultyPageView.page);
                }
            });
            layout.putConstraint(SpringLayout.WEST, fId, 5, SpringLayout.EAST, dateAppliedHead1);
            layout.putConstraint(SpringLayout.NORTH, fId, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fId);

            JButton fromDate = new JButton(approvedLeaveRequest.get(i).getString("from_date"));
            fromDate.setOpaque(false);
            fromDate.setContentAreaFilled(false);
            fromDate.setBorderPainted(false);
            fromDate.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, fromDate, 5, SpringLayout.EAST, fIdHead1);
            layout.putConstraint(SpringLayout.NORTH, fromDate, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fromDate);

            JButton toDate = new JButton(approvedLeaveRequest.get(i).getString("to_date"));
            toDate.setOpaque(false);
            toDate.setContentAreaFilled(false);
            toDate.setBorderPainted(false);
            toDate.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, toDate, 5, SpringLayout.EAST, fromDateHead1);
            layout.putConstraint(SpringLayout.NORTH, toDate, 5, SpringLayout.SOUTH, lastLeave);
            page.add(toDate);

            JButton comments = new JButton("View");
            comments.setOpaque(false);
            comments.setContentAreaFilled(false);
            comments.setBorderPainted(false);
            comments.setForeground(Color.BLUE);
            comments.setActionCommand(i + "");
            comments.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int i = Integer.parseInt(e.getActionCommand());
                    JPanel commentsView = new JPanel();
                    commentsView.setLayout(new BoxLayout(commentsView, BoxLayout.PAGE_AXIS));
                    if (approvedLeaveRequest.get(i).containsKey("commentsfac")) {
                        commentsView.add(new JLabel("Applicant Comments: "));
                        commentsView.add(new JLabel(approvedLeaveRequest.get(i).getString("commentsfac")));
                        commentsView.add(new JLabel(" "));
                    }
                    if (approvedLeaveRequest.get(i).containsKey("auth1comments")) {
                        commentsView.add(new JLabel(approvedLeaveRequest.get(i).getString("auth1") + " Comments: "));
                        commentsView.add(new JLabel(approvedLeaveRequest.get(i).getString("auth1comments")));
                        commentsView.add(new JLabel(" "));
                    }
                    if (approvedLeaveRequest.get(i).containsKey("auth2comments")) {
                        commentsView.add(new JLabel(approvedLeaveRequest.get(i).getString("auth2") + " Comments: "));
                        commentsView.add(new JLabel(approvedLeaveRequest.get(i).getString("auth2comments")));
                        commentsView.add(new JLabel(" "));
                    }
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, commentsView, "Comments",
                            JOptionPane.PLAIN_MESSAGE);
                }
            });
            layout.putConstraint(SpringLayout.WEST, comments, 5, SpringLayout.EAST, toDateHead1);
            layout.putConstraint(SpringLayout.NORTH, comments, 5, SpringLayout.SOUTH, lastLeave);
            page.add(comments);

            JButton borrow = new JButton(approvedLeaveRequest.get(i).getString("borrowleaves"));
            borrow.setOpaque(false);
            borrow.setContentAreaFilled(false);
            borrow.setBorderPainted(false);
            borrow.setForeground(Color.RED);
            layout.putConstraint(SpringLayout.WEST, borrow, 5, SpringLayout.EAST, commentsHead1);
            layout.putConstraint(SpringLayout.NORTH, borrow, 5, SpringLayout.SOUTH, lastLeave);
            page.add(borrow);

            JButton approvedByHOD = new JButton(approvedLeaveRequest.get(i).getString("auth1id"));
            approvedByHOD.setOpaque(false);
            approvedByHOD.setContentAreaFilled(false);
            approvedByHOD.setBorderPainted(false);
            approvedByHOD.setForeground(Color.BLUE);
            approvedByHOD.setActionCommand(approvedLeaveRequest.get(i).getString("auth1id"));
            approvedByHOD.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FacultyPageView facultyPageView = new FacultyPageView(
                            db.findFaculties(new Document("f_id", e.getActionCommand())).get(0));
                    setActivity(facultyPageView.page);
                }
            });
            layout.putConstraint(SpringLayout.WEST, approvedByHOD, 5, SpringLayout.EAST, borrowHead1);
            layout.putConstraint(SpringLayout.NORTH, approvedByHOD, 5, SpringLayout.SOUTH, lastLeave);
            page.add(approvedByHOD);

            JButton approvedByDean = new JButton(approvedLeaveRequest.get(i).getString("auth2id"));
            approvedByDean.setOpaque(false);
            approvedByDean.setContentAreaFilled(false);
            approvedByDean.setBorderPainted(false);
            approvedByDean.setForeground(Color.BLUE);
            approvedByDean.setActionCommand(approvedLeaveRequest.get(i).getString("auth2id"));
            approvedByDean.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FacultyPageView facultyPageView = new FacultyPageView(
                            db.findFaculties(new Document("f_id", e.getActionCommand())).get(0));
                    setActivity(facultyPageView.page);
                }
            });
            layout.putConstraint(SpringLayout.WEST, approvedByDean, 5, SpringLayout.EAST, approvedByHODHead1);
            layout.putConstraint(SpringLayout.NORTH, approvedByDean, 5, SpringLayout.SOUTH, lastLeave);
            page.add(approvedByDean);

            lastLeave = dateApplied;
        }
        page.revalidate();
    }

    protected int[] getData(Document levDoc, DateFormat df) throws ParseException, SQLException {
        List<Document> approvedLeaves = null;
        java.util.Date fDate = df.parse(levDoc.getString("from_date"));
        java.util.Date tDate = df.parse(levDoc.getString("to_date"));
        int nod = (int) (tDate.getTime() - fDate.getTime()) / 86400000 + 1;
        int[] result = new int[nod];
        approvedLeaves = leavesDb.getAllApprovedLeaves(levDoc.getString("d_id"), new Date(fDate.getTime()),
                new Date(tDate.getTime()));
        for (int i = 0; i < result.length; i++) {
            for (Document doc : approvedLeaves) {
                if (df.parse(doc.getString("from_date")).compareTo(fDate) <= 0
                        && df.parse(doc.getString("to_date")).compareTo(fDate) >= 0)
                    result[i]++;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(fDate);
            cal.add(Calendar.DATE, 1);
            fDate = cal.getTime();
        }
        return result;
    }

    void createJOptionPane(int lId, String fId, int nod, String auth) {
        JTextArea newComments = new JTextArea(15, 50);
        JScrollPane scrollPane = new JScrollPane(newComments);

        JPanel newFac = new JPanel();
        newFac.setLayout(new BoxLayout(newFac, BoxLayout.Y_AXIS));
        newFac.add(new JLabel("Comments: ", JLabel.LEFT));
        newFac.add(scrollPane);

        Object[] options = { "Approve", "Reject", "Keep on Hold" };
        int result = JOptionPane.showOptionDialog(ActivityMain.mainFrame, newFac, "Please enter the followings",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (auth.equals("auth1"))
                    leavesDb.auth1Response(lId, true, facDoc.getString("position"),
                            Integer.parseInt(facDoc.getString("f_id")), newComments.getText());
                else if (auth.equals("auth2")) {
                    leavesDb.auth2Response(lId, true, facDoc.getString("position"),
                            Integer.parseInt(facDoc.getString("f_id")), newComments.getText());
                    List<Document> toChangeLeaves = db.findFaculties(new Document("f_id", fId));
                    toChangeLeaves.get(0).put("leaves", toChangeLeaves.get(0).getInteger("leaves") - nod);
                    db.upsertFaculty(toChangeLeaves.get(0));
                }
                JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Approved");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        } else if (result == JOptionPane.NO_OPTION) {
            try {
                if (auth.equals("auth1"))
                    leavesDb.auth1Response(lId, false, facDoc.getString("position"),
                            Integer.parseInt(facDoc.getString("f_id")), newComments.getText());
                if (facDoc.getString("position").equals("Dean"))
                    leavesDb.auth2Response(lId, false, facDoc.getString("position"),
                            Integer.parseInt(facDoc.getString("f_id")), newComments.getText());
                JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Rejected");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static long numberOfDays(java.util.Date start, java.util.Date end) {
        int workingDays = 0;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        while (!c1.after(c2)) {
            int day = c1.get(Calendar.DAY_OF_WEEK);
            if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
                workingDays++;
            c1.add(Calendar.DATE, 1);
        }
        System.out.println(workingDays);
        return workingDays;
    }
}