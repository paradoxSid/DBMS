package com.sid;

import static com.sid.ActivityMain.leavesDb;
import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;
import static com.sid.LeavePage.pendingLeaveRequest;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.bson.Document;

public class ApproveLeavePage {
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    Document facDoc;
    JButton addButton, backButton;

    JButton lIdHead = new JButton("l_id");
    JButton dateAppliedHead = new JButton("Date Of Application");
    JButton fIdHead = new JButton("f_id");
    JButton fromDateHead = new JButton("From (yyyy-MM-dd)");
    JButton toDateHead = new JButton("To (yyyy-MM-dd)");
    JButton reasonHead = new JButton("Reason");
    JButton lastLeave;

    public ApproveLeavePage(Document doc) {
        page.setLayout(layout);
        facDoc = doc;

        addButton = new JButton("Apply for leave");
        layout.putConstraint(SpringLayout.WEST, addButton, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, addButton, 5, SpringLayout.NORTH, page);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // createJOptionPane();
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
                setActivity(null);
            }
        });
        page.add(backButton);

        setUpLeaves(0);
    }

    void setUpLeaves(int index) {
        if (lastLeave == null) {
            lIdHead.setOpaque(false);
            lIdHead.setContentAreaFilled(false);
            lIdHead.setBorderPainted(false);
            lIdHead.setFont(new Font(lIdHead.getFont().getName(), Font.BOLD, lIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, lIdHead, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, lIdHead, 5, SpringLayout.SOUTH, addButton);
            page.add(lIdHead);

            dateAppliedHead.setOpaque(false);
            dateAppliedHead.setContentAreaFilled(false);
            dateAppliedHead.setBorderPainted(false);
            dateAppliedHead.setFont(
                    new Font(dateAppliedHead.getFont().getName(), Font.BOLD, dateAppliedHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, dateAppliedHead, 5, SpringLayout.EAST, lIdHead);
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

            reasonHead.setOpaque(false);
            reasonHead.setContentAreaFilled(false);
            reasonHead.setBorderPainted(false);
            reasonHead.setFont(new Font(reasonHead.getFont().getName(), Font.BOLD, reasonHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, reasonHead, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, reasonHead, 5, SpringLayout.SOUTH, addButton);
            page.add(reasonHead);

            lastLeave = dateAppliedHead;
        }
        int i = index;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (; i < pendingLeaveRequest.size(); i++) {
            int nod = 0;
            try {
                nod = (int) (df.parse(pendingLeaveRequest.get(i).getString("to_date")).getTime()
                        - df.parse(pendingLeaveRequest.get(i).getString("from_date")).getTime()) / 86400000 + 1;
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            JButton lId = new JButton(pendingLeaveRequest.get(i).getString("l_id"));
            lId.setOpaque(false);
            lId.setContentAreaFilled(false);
            lId.setBorderPainted(false);
            lId.setForeground(Color.BLUE);
            lId.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            lId.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, lId, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, lId, 5, SpringLayout.SOUTH, lastLeave);
            page.add(lId);

            JButton dateApplied = new JButton(pendingLeaveRequest.get(i).getString("application_date"));
            dateApplied.setOpaque(false);
            dateApplied.setContentAreaFilled(false);
            dateApplied.setBorderPainted(false);
            dateApplied.setForeground(Color.BLUE);
            dateApplied.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            dateApplied.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, dateApplied, 5, SpringLayout.EAST, lId);
            layout.putConstraint(SpringLayout.NORTH, dateApplied, 5, SpringLayout.SOUTH, lastLeave);
            page.add(dateApplied);

            JButton fId = new JButton(pendingLeaveRequest.get(i).getString("f_id"));
            fId.setOpaque(false);
            fId.setContentAreaFilled(false);
            fId.setBorderPainted(false);
            fId.setForeground(Color.BLUE);
            fId.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            fId.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, fId, 5, SpringLayout.EAST, dateAppliedHead);
            layout.putConstraint(SpringLayout.NORTH, fId, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fId);

            JButton fromDate = new JButton(pendingLeaveRequest.get(i).getString("from_date"));
            fromDate.setOpaque(false);
            fromDate.setContentAreaFilled(false);
            fromDate.setBorderPainted(false);
            fromDate.setForeground(Color.BLUE);
            fromDate.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            fromDate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, fromDate, 5, SpringLayout.EAST, fIdHead);
            layout.putConstraint(SpringLayout.NORTH, fromDate, 5, SpringLayout.SOUTH, lastLeave);
            page.add(fromDate);

            JButton toDate = new JButton(pendingLeaveRequest.get(i).getString("to_date"));
            toDate.setOpaque(false);
            toDate.setContentAreaFilled(false);
            toDate.setBorderPainted(false);
            toDate.setForeground(Color.BLUE);
            toDate.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            toDate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, toDate, 5, SpringLayout.EAST, fromDateHead);
            layout.putConstraint(SpringLayout.NORTH, toDate, 5, SpringLayout.SOUTH, lastLeave);
            page.add(toDate);

            JButton reason = new JButton(pendingLeaveRequest.get(i).getString("commentsfac"));
            reason.setOpaque(false);
            reason.setContentAreaFilled(false);
            reason.setBorderPainted(false);
            reason.setForeground(Color.BLUE);
            reason.setActionCommand(pendingLeaveRequest.get(i).getString("l_id") + " "
                    + pendingLeaveRequest.get(i).getString("f_id") + " " + nod);
            reason.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String[] s = e.getActionCommand().split(" ");
                    createJOptionPane(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                }
            });
            layout.putConstraint(SpringLayout.WEST, reason, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, reason, 5, SpringLayout.SOUTH, lastLeave);
            page.add(reason);

            lastLeave = dateApplied;
        }
        page.revalidate();
    }

    void createJOptionPane(int lId, int fId, int nod) {
        JTextArea newReason = new JTextArea(15, 50);
        JScrollPane scrollPane = new JScrollPane(newReason);

        JPanel newFac = new JPanel();
        newFac.setLayout(new BoxLayout(newFac, BoxLayout.Y_AXIS));
        newFac.add(new JLabel("Reason: ", JLabel.LEFT));
        newFac.add(scrollPane);

        Object[] options = { "Approve", "Reject", "Keep on Hold" };
        int result = JOptionPane.showOptionDialog(page, newFac, "Please enter the followings",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (facDoc.getString("position").equals("HOD"))
                    leavesDb.hodResponse(lId, true, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                else if (facDoc.getString("position").equals("Dean")) {
                    leavesDb.deanResponse(lId, true, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                    List<Document> toAddTheLeave = db.findFaculties(new Document("f_id", String.valueOf(fId)));
                    toAddTheLeave.get(0).put("leaves", toAddTheLeave.get(0).getInteger("leaves") - nod);
                    db.upsertFaculty(toAddTheLeave.get(0));
                }
                JOptionPane.showMessageDialog(page, "Approved");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        } else if (result == JOptionPane.NO_OPTION) {
            try {
                if (facDoc.getString("position").equals("HOD"))
                    leavesDb.hodResponse(lId, false, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                if (facDoc.getString("position").equals("Dean"))
                    leavesDb.deanResponse(lId, false, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                JOptionPane.showMessageDialog(page, "Rejected");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}