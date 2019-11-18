package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.leavesDb;
import static com.sid.ActivityMain.setActivity;

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
import java.util.Calendar;
import java.util.HashMap;
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
import org.jdesktop.swingx.JXDatePicker;

public class LeavePage {
    public JPanel page = new JPanel() {
        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(5000, 5000);
        }
    };
    SpringLayout layout = new SpringLayout();
    List<Document> leaves;
    public static HashMap<String, List<Document>> pendingLeaveRequest;
    Document facDoc;
    JButton addButton;
    static JButton backButton;
    public static JButton refreshButton, approveButton;
    public static JButton authenticate = new JButton();
    JButton NOLButton = new JButton();

    JButton dateAppliedHead = new JButton("Date Of Application");
    JButton fIdHead = new JButton("f_id");
    JButton fromDateHead = new JButton("From (yyyy-MM-dd)");
    JButton toDateHead = new JButton("To (yyyy-MM-dd)");
    JButton commentsHead = new JButton("Comments");
    JButton statusHead = new JButton("Status");
    JButton editHead = new JButton("Edit");
    JButton withDrawHead = new JButton("Withdraw");
    JButton lastLeave;
    boolean canApplyForNew = true;

    public LeavePage(Document doc) {
        page.setLayout(layout);
        facDoc = doc;

        try {
            leaves = leavesDb.getAllLeaves(Integer.parseInt(facDoc.getString("f_id")));
        } catch (NumberFormatException | SQLException e1) {
            e1.printStackTrace();
        }

        addButton = new JButton("Apply for leave");
        layout.putConstraint(SpringLayout.WEST, addButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addButton, 5, SpringLayout.NORTH, page);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (canApplyForNew) {
                    createJOptionPane("", "", "", null);
                    refreshButton.doClick();
                } else
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Another Leave application is in process.",
                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        page.add(addButton);

        refreshButton = new JButton("Refresh");
        layout.putConstraint(SpringLayout.WEST, refreshButton, 5, SpringLayout.EAST, addButton);
        layout.putConstraint(SpringLayout.NORTH, refreshButton, 5, SpringLayout.NORTH, page);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshButton.setVisible(false);
                backButton.setVisible(false);
                FacultyPage.refreshButton.setVisible(false);
                FacultyPage.leaveButton.setVisible(false);

                backButton.doClick();
                FacultyPage.refreshButton.doClick();
                FacultyPage.leaveButton.doClick();

                backButton.setVisible(true);
                FacultyPage.refreshButton.setVisible(true);
                FacultyPage.leaveButton.setVisible(true);
                refreshButton.setVisible(true);
            }
        });
        page.add(refreshButton);

        JButton leftButton = refreshButton;

        try {
            pendingLeaveRequest = leavesDb.getAllLeavesToApprove(facDoc.getString("position"), facDoc.getString("f_id"),
                    facDoc.getString("d_id"));
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (pendingLeaveRequest != null) {
            int totalRequest = 0;
            if (pendingLeaveRequest.containsKey("auth1"))
                totalRequest += pendingLeaveRequest.get("auth1").size();
            if (pendingLeaveRequest.containsKey("auth2"))
                totalRequest += pendingLeaveRequest.get("auth2").size();
            approveButton = new JButton("Pending Leave Requests (" + totalRequest + ")");
            layout.putConstraint(SpringLayout.WEST, approveButton, 5, SpringLayout.EAST, refreshButton);
            layout.putConstraint(SpringLayout.NORTH, approveButton, 5, SpringLayout.NORTH, page);
            approveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ApproveLeavePage approveLeavePage = new ApproveLeavePage(facDoc);
                    setActivity(approveLeavePage.page);
                }
            });
            page.add(approveButton);
            leftButton = approveButton;
        }

        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, leftButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActivity(null);
            }
        });
        page.add(backButton);

        NOLButton.setText("Number of leaves left this year: " + facDoc.getInteger("leaves"));
        NOLButton.setOpaque(false);
        NOLButton.setContentAreaFilled(false);
        NOLButton.setBorderPainted(false);
        NOLButton.setForeground(Color.BLUE);
        layout.putConstraint(SpringLayout.WEST, NOLButton, 5, SpringLayout.EAST, backButton);
        layout.putConstraint(SpringLayout.NORTH, NOLButton, 5, SpringLayout.NORTH, page);
        page.add(NOLButton);

        authenticate.setText("Authenticate");
        authenticate.setOpaque(false);
        authenticate.setContentAreaFilled(false);
        authenticate.setBorderPainted(false);
        authenticate.setForeground(Color.BLUE);
        layout.putConstraint(SpringLayout.WEST, authenticate, 5, SpringLayout.EAST, NOLButton);
        layout.putConstraint(SpringLayout.NORTH, authenticate, 5, SpringLayout.NORTH, page);

        setUpLeaves(0);
    }

    void setUpLeaves(int index) {
        if (lastLeave == null) {
            dateAppliedHead.setOpaque(false);
            dateAppliedHead.setContentAreaFilled(false);
            dateAppliedHead.setBorderPainted(false);
            dateAppliedHead.setFont(
                    new Font(dateAppliedHead.getFont().getName(), Font.BOLD, dateAppliedHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, dateAppliedHead, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, dateAppliedHead, 5, SpringLayout.SOUTH, addButton);
            page.add(dateAppliedHead);

            fIdHead.setOpaque(false);
            fIdHead.setContentAreaFilled(false);
            fIdHead.setBorderPainted(false);
            fIdHead.setFont(new Font(fIdHead.getFont().getName(), Font.BOLD, fIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fIdHead, 5, SpringLayout.EAST, dateAppliedHead);
            layout.putConstraint(SpringLayout.NORTH, fIdHead, 5, SpringLayout.SOUTH, addButton);
            page.add(fIdHead);

            fromDateHead.setOpaque(false);
            fromDateHead.setContentAreaFilled(false);
            fromDateHead.setBorderPainted(false);
            fromDateHead.setFont(
                    new Font(fromDateHead.getFont().getName(), Font.BOLD, fromDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fromDateHead, 5, SpringLayout.EAST, fIdHead);
            layout.putConstraint(SpringLayout.NORTH, fromDateHead, 5, SpringLayout.SOUTH, addButton);
            page.add(fromDateHead);

            toDateHead.setOpaque(false);
            toDateHead.setContentAreaFilled(false);
            toDateHead.setBorderPainted(false);
            toDateHead.setFont(new Font(toDateHead.getFont().getName(), Font.BOLD, toDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, toDateHead, 5, SpringLayout.EAST, fromDateHead);
            layout.putConstraint(SpringLayout.NORTH, toDateHead, 5, SpringLayout.SOUTH, addButton);
            page.add(toDateHead);

            commentsHead.setOpaque(false);
            commentsHead.setContentAreaFilled(false);
            commentsHead.setBorderPainted(false);
            commentsHead.setFont(
                    new Font(commentsHead.getFont().getName(), Font.BOLD, commentsHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, commentsHead, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, commentsHead, 5, SpringLayout.SOUTH, addButton);
            page.add(commentsHead);

            statusHead.setOpaque(false);
            statusHead.setContentAreaFilled(false);
            statusHead.setBorderPainted(false);
            statusHead.setFont(new Font(statusHead.getFont().getName(), Font.BOLD, statusHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, statusHead, 5, SpringLayout.EAST, commentsHead);
            layout.putConstraint(SpringLayout.NORTH, statusHead, 5, SpringLayout.SOUTH, addButton);
            page.add(statusHead);

            editHead.setOpaque(false);
            editHead.setContentAreaFilled(false);
            editHead.setBorderPainted(false);
            editHead.setFont(new Font(editHead.getFont().getName(), Font.BOLD, editHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, editHead, 75, SpringLayout.EAST, statusHead);
            layout.putConstraint(SpringLayout.NORTH, editHead, 5, SpringLayout.SOUTH, addButton);
            page.add(editHead);

            withDrawHead.setOpaque(false);
            withDrawHead.setContentAreaFilled(false);
            withDrawHead.setBorderPainted(false);
            withDrawHead.setFont(
                    new Font(withDrawHead.getFont().getName(), Font.BOLD, withDrawHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, withDrawHead, 5, SpringLayout.EAST, editHead);
            layout.putConstraint(SpringLayout.NORTH, withDrawHead, 5, SpringLayout.SOUTH, addButton);
            page.add(withDrawHead);

            lastLeave = dateAppliedHead;
        }
        int i = index;
        for (; i < leaves.size(); i++) {
            if (leaves.get(i).getBoolean("notifyUser")) {
                if (leaves.get(i).getString("status").equals("rejectedLeaves")) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame,
                            "Your leave has been Rejected by " + leaves.get(i).getString("auth"), "Infomation",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (leaves.get(i).getString("status").equals("approvedLeaves")) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Your leave has been Approved", "Infomation",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (leaves.get(i).getString("status").equals("approved1Leaves")) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame,
                            "Your leave has been Approved by "
                                    + ActivityMain.routes.get(leaves.get(i).getString("applicant")).getString("auth1"),
                            "Infomation", JOptionPane.INFORMATION_MESSAGE);
                }
                try {
                    leavesDb.disableNotification(Integer.parseInt(leaves.get(i).getString("l_id")));
                } catch (NumberFormatException | SQLException e1) {
                    e1.printStackTrace();
                }
            }
            JButton dateApplied = new JButton(leaves.get(i).getString("application_date"));
            dateApplied.setOpaque(false);
            dateApplied.setContentAreaFilled(false);
            dateApplied.setBorderPainted(false);
            dateApplied.setForeground(Color.BLUE);
            dateApplied.setActionCommand(leaves.get(i).getString("l_id"));
            dateApplied.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, e.getActionCommand());
                }
            });
            layout.putConstraint(SpringLayout.WEST, dateApplied, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, dateApplied, 5, SpringLayout.SOUTH, lastLeave);
            page.add(dateApplied);

            JButton fId = new JButton(leaves.get(i).getString("f_id"));
            fId.setOpaque(false);
            fId.setContentAreaFilled(false);
            fId.setBorderPainted(false);
            fId.setForeground(Color.BLUE);
            fId.setActionCommand(leaves.get(i).getString("l_id"));
            fId.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, e.getActionCommand());
                }
            });
            layout.putConstraint(SpringLayout.WEST, fId, 5, SpringLayout.EAST, dateAppliedHead);
            layout.putConstraint(SpringLayout.NORTH, fId, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fId);

            JButton fromDate = new JButton(leaves.get(i).getString("from_date"));
            fromDate.setOpaque(false);
            fromDate.setContentAreaFilled(false);
            fromDate.setBorderPainted(false);
            fromDate.setForeground(Color.BLUE);
            fromDate.setActionCommand(leaves.get(i).getString("l_id"));
            fromDate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, e.getActionCommand());
                }
            });
            layout.putConstraint(SpringLayout.WEST, fromDate, 5, SpringLayout.EAST, fIdHead);
            layout.putConstraint(SpringLayout.NORTH, fromDate, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fromDate);

            JButton toDate = new JButton(leaves.get(i).getString("to_date"));
            toDate.setOpaque(false);
            toDate.setContentAreaFilled(false);
            toDate.setBorderPainted(false);
            toDate.setForeground(Color.BLUE);
            toDate.setActionCommand(leaves.get(i).getString("l_id"));
            toDate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, e.getActionCommand());
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
                    if (leaves.get(i).containsKey("commentsfac")) {
                        commentsView.add(new JLabel("Applicant Comments: "));
                        commentsView.add(new JLabel(leaves.get(i).getString("commentsfac")));
                    }
                    if (leaves.get(i).containsKey("authcomments")) {
                        commentsView.add(new JLabel(leaves.get(i).getString("auth") + " Comments: "));
                        commentsView.add(new JLabel(leaves.get(i).getString("authcomments")));
                    }
                    if (leaves.get(i).containsKey("auth1comments")) {
                        commentsView.add(new JLabel(leaves.get(i).getString("auth1") + " Comments: "));
                        commentsView.add(new JLabel(leaves.get(i).getString("auth1comments")));
                    }
                    if (leaves.get(i).containsKey("auth2comments")) {
                        commentsView.add(new JLabel(leaves.get(i).getString("auth2") + " Comments: "));
                        commentsView.add(new JLabel(leaves.get(i).getString("auth2comments")));
                    }
                    JOptionPane.showMessageDialog(ActivityMain.mainFrame, commentsView, "Comments",
                            JOptionPane.PLAIN_MESSAGE);
                }
            });
            layout.putConstraint(SpringLayout.WEST, comments, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, comments, 5, SpringLayout.SOUTH, lastLeave);
            page.add(comments);

            JButton status = new JButton();
            if (leaves.get(i).getString("status").equals("rejectedLeaves")) {
                status.setText("Rejected By " + leaves.get(i).getString("auth"));
            } else if (leaves.get(i).getString("status").equals("approvedLeaves")) {
                status.setText("Approved");
            } else if (leaves.get(i).getString("status").equals("approved1Leaves")) {
                status.setText(
                        "Wating for " + ActivityMain.routes.get(leaves.get(i).getString("applicant")).getString("auth2")
                                + " Approval");
                canApplyForNew = false;
            } else if (leaves.get(i).getString("status").equals("newLeaves")) {
                status.setText(
                        "Wating for " + ActivityMain.routes.get(leaves.get(i).getString("applicant")).getString("auth1")
                                + " Approval");
                canApplyForNew = false;
            }
            status.setSize(1, 10);
            status.setOpaque(false);
            status.setContentAreaFilled(false);
            status.setBorderPainted(false);
            status.setForeground(Color.BLUE);
            layout.putConstraint(SpringLayout.WEST, status, 5, SpringLayout.EAST, commentsHead);
            layout.putConstraint(SpringLayout.NORTH, status, 5, SpringLayout.SOUTH, lastLeave);
            page.add(status);

            JButton edit = new JButton();
            try {
                edit.setIcon(new ImageIcon(ImageIO.read(new File("src/R/drawable/edit.png")).getScaledInstance(15, 15,
                        java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            edit.setOpaque(false);
            edit.setContentAreaFilled(false);
            edit.setBorderPainted(false);
            edit.setForeground(Color.BLUE);
            edit.setActionCommand(i + "");
            edit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Document levDoc = leaves.get(Integer.parseInt(e.getActionCommand()));
                    if (!levDoc.getString("status").equals("rejectedLeaves")) {
                        JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Unable to edit.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    createJOptionPane(levDoc.getString("from_date"), levDoc.getString("to_date"),
                            levDoc.getString("commentsfac"), levDoc.getString("l_id"));
                    refreshButton.setVisible(false);
                    refreshButton.doClick();
                    refreshButton.setVisible(true);
                }
            });
            layout.putConstraint(SpringLayout.WEST, edit, 75, SpringLayout.EAST, statusHead);
            layout.putConstraint(SpringLayout.NORTH, edit, 5, SpringLayout.SOUTH, lastLeave);
            page.add(edit);

            JButton withDraw = new JButton();
            try {
                withDraw.setIcon(new ImageIcon(ImageIO.read(new File("src/R/drawable/delete.png")).getScaledInstance(15,
                        15, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            withDraw.setOpaque(false);
            withDraw.setContentAreaFilled(false);
            withDraw.setBorderPainted(false);
            withDraw.setForeground(Color.BLUE);
            withDraw.setActionCommand(i + "");
            withDraw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Document levDoc = leaves.get(Integer.parseInt(e.getActionCommand()));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    if (levDoc.getString("status").equals("rejectedLeaves")) {
                        JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Unable to withdraw.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int result = JOptionPane.showConfirmDialog(ActivityMain.mainFrame,
                            "Are you sure you want to withdraw the application.", "Delete", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            leavesDb.redeemLeaves(Integer.parseInt(levDoc.getString("l_id")),
                                    levDoc.getString("status"));
                            if (levDoc.getString("status").equals("approvedLeaves")) {
                                int nod = (int) numberOfDays(df.parse(levDoc.getString("from_date")),
                                        df.parse(levDoc.getString("to_date")));
                                facDoc.put("leaves", nod + facDoc.getInteger("leaves"));
                                db.upsertFaculty(facDoc);
                            }
                            FacultyPage.leaveButton.setVisible(false);
                            backButton.doClick();
                            FacultyPage.leaveButton.doClick();
                            FacultyPage.leaveButton.setVisible(true);
                        } catch (ParseException | NumberFormatException | SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            layout.putConstraint(SpringLayout.WEST, withDraw, 5, SpringLayout.EAST, editHead);
            layout.putConstraint(SpringLayout.NORTH, withDraw, 5, SpringLayout.SOUTH, lastLeave);
            page.add(withDraw);

            lastLeave = dateApplied;
        }
        page.revalidate();
    }

    void createJOptionPane(String fDate, String tDate, String c, String oldLID) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JXDatePicker newFromDate = new JXDatePicker();
        newFromDate.setDate(Calendar.getInstance().getTime());
        newFromDate.setFormats(df);
        JXDatePicker newToDate = new JXDatePicker();
        newToDate.setDate(Calendar.getInstance().getTime());
        newToDate.setFormats(df);
        JTextArea newComments = new JTextArea(15, 50);
        JScrollPane scrollPane = new JScrollPane(newComments);

        if (oldLID != null) {
            try {
                newFromDate.setDate(df.parse(fDate));
                newToDate.setDate(df.parse(tDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newComments.setText(c);
        }

        JPanel newFac = new JPanel();
        newFac.setLayout(new BoxLayout(newFac, BoxLayout.Y_AXIS));
        newFac.add(new JLabel("From: *", JLabel.LEFT));
        newFac.add(newFromDate);
        newFac.add(new JLabel("To: *", JLabel.LEFT));
        newFac.add(newToDate);
        newFac.add(new JLabel("Comments: ", JLabel.LEFT));
        newFac.add(scrollPane);

        int result = JOptionPane.showConfirmDialog(ActivityMain.mainFrame, newFac, "Please enter the followings",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (newFromDate.getDate().compareTo(new java.util.Date()) < 0
                    || newToDate.getDate().compareTo(newFromDate.getDate()) < 0)
                JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Invalid Dates. Try Again");
            else {
                try {
                    int nod = (int) numberOfDays(newFromDate.getDate(), newToDate.getDate());
                    if (nod <= facDoc.getInteger("leaves")) {
                        if (oldLID == null)
                            leavesDb.createNewLeaveEntry(Integer.parseInt(facDoc.getString("f_id")),
                                    facDoc.getString("d_id"), Date.valueOf(df.format(newFromDate.getDate())),
                                    Date.valueOf(df.format(newToDate.getDate())), newComments.getText(), 0,
                                    facDoc.getString("position"));
                        else
                            leavesDb.editLeaveEntry(Integer.parseInt(facDoc.getString("f_id")),
                                    facDoc.getString("d_id"), Date.valueOf(df.format(newFromDate.getDate())),
                                    Date.valueOf(df.format(newToDate.getDate())), newComments.getText(),
                                    Integer.parseInt(oldLID), 0);

                        leaves = leavesDb.getAllLeaves(Integer.parseInt(facDoc.getString("f_id")));
                        // setUpLeaves(leaves.size() - 1);
                        JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Request Created");
                    } else {
                        int ltb = 0;
                        int lany = 15;
                        if (facDoc.getInteger("leaves") < 0) {
                            ltb = nod;
                            lany += facDoc.getInteger("leaves");
                        } else
                            ltb = nod - facDoc.getInteger("leaves");
                        if (ltb <= lany) {
                            int result1 = JOptionPane
                                    .showConfirmDialog(ActivityMain.mainFrame,
                                            "You do not have enough leaves available, would you like to borrow " + ltb
                                                    + " leaves form next year.",
                                            "Borrow Leaves", JOptionPane.YES_NO_OPTION);
                            if (result1 == JOptionPane.YES_OPTION) {
                                if (oldLID == null)
                                    leavesDb.createNewLeaveEntry(Integer.parseInt(facDoc.getString("f_id")),
                                            facDoc.getString("d_id"), Date.valueOf(df.format(newFromDate.getDate())),
                                            Date.valueOf(df.format(newToDate.getDate())), newComments.getText(), ltb,
                                            facDoc.getString("postition"));
                                else
                                    leavesDb.editLeaveEntry(Integer.parseInt(facDoc.getString("f_id")),
                                            facDoc.getString("d_id"), Date.valueOf(df.format(newFromDate.getDate())),
                                            Date.valueOf(df.format(newToDate.getDate())), newComments.getText(),
                                            Integer.parseInt(oldLID), ltb);

                                leaves = leavesDb.getAllLeaves(Integer.parseInt(facDoc.getString("f_id")));
                                // setUpLeaves(leaves.size() - 1);
                                JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Request Created");
                            }
                        } else
                            JOptionPane.showMessageDialog(ActivityMain.mainFrame, "Leave duration exceeds your limit.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                }
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