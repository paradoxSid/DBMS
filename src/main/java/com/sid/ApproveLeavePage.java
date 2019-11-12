package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.leavesDb;
import static com.sid.ActivityMain.setActivity;
import static com.sid.LeavePage.pendingLeaveRequest;

import java.awt.Color;
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
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    Document facDoc;
    JButton refreshButton, backButton;

    JButton lIdHead = new JButton("l_id");
    JButton dateAppliedHead = new JButton("Date Of Application");
    JButton fIdHead = new JButton("f_id");
    JButton fromDateHead = new JButton("From (yyyy-MM-dd)");
    JButton toDateHead = new JButton("To (yyyy-MM-dd)");
    JButton reasonHead = new JButton("Reason");
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
            lIdHead.setOpaque(false);
            lIdHead.setContentAreaFilled(false);
            lIdHead.setBorderPainted(false);
            lIdHead.setFont(new Font(lIdHead.getFont().getName(), Font.BOLD, lIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, lIdHead, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, lIdHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(lIdHead);

            dateAppliedHead.setOpaque(false);
            dateAppliedHead.setContentAreaFilled(false);
            dateAppliedHead.setBorderPainted(false);
            dateAppliedHead.setFont(
                    new Font(dateAppliedHead.getFont().getName(), Font.BOLD, dateAppliedHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, dateAppliedHead, 5, SpringLayout.EAST, lIdHead);
            layout.putConstraint(SpringLayout.NORTH, dateAppliedHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(dateAppliedHead);

            fIdHead.setOpaque(false);
            fIdHead.setContentAreaFilled(false);
            fIdHead.setBorderPainted(false);
            fIdHead.setFont(new Font(fIdHead.getFont().getName(), Font.BOLD, fIdHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fIdHead, 5, SpringLayout.EAST, dateAppliedHead);
            layout.putConstraint(SpringLayout.NORTH, fIdHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(fIdHead);

            fromDateHead.setOpaque(false);
            fromDateHead.setContentAreaFilled(false);
            fromDateHead.setBorderPainted(false);
            fromDateHead.setFont(
                    new Font(fromDateHead.getFont().getName(), Font.BOLD, fromDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fromDateHead, 5, SpringLayout.EAST, fIdHead);
            layout.putConstraint(SpringLayout.NORTH, fromDateHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(fromDateHead);

            toDateHead.setOpaque(false);
            toDateHead.setContentAreaFilled(false);
            toDateHead.setBorderPainted(false);
            toDateHead.setFont(new Font(toDateHead.getFont().getName(), Font.BOLD, toDateHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, toDateHead, 5, SpringLayout.EAST, fromDateHead);
            layout.putConstraint(SpringLayout.NORTH, toDateHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(toDateHead);

            reasonHead.setOpaque(false);
            reasonHead.setContentAreaFilled(false);
            reasonHead.setBorderPainted(false);
            reasonHead.setFont(new Font(reasonHead.getFont().getName(), Font.BOLD, reasonHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, reasonHead, 5, SpringLayout.EAST, toDateHead);
            layout.putConstraint(SpringLayout.NORTH, reasonHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(reasonHead);

            borrowHead.setOpaque(false);
            borrowHead.setContentAreaFilled(false);
            borrowHead.setBorderPainted(false);
            borrowHead.setFont(new Font(borrowHead.getFont().getName(), Font.BOLD, borrowHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, borrowHead, 5, SpringLayout.EAST, reasonHead);
            layout.putConstraint(SpringLayout.NORTH, borrowHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(borrowHead);

            availabilityHead.setOpaque(false);
            availabilityHead.setContentAreaFilled(false);
            availabilityHead.setBorderPainted(false);
            availabilityHead.setFont(new Font(availabilityHead.getFont().getName(), Font.BOLD,
                    availabilityHead.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, availabilityHead, 5, SpringLayout.EAST, borrowHead);
            layout.putConstraint(SpringLayout.NORTH, availabilityHead, 5, SpringLayout.SOUTH, refreshButton);
            page.add(availabilityHead);

            lastLeave = dateAppliedHead;
        }
        int i = index;
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
            layout.putConstraint(SpringLayout.WEST, dateApplied, 5, SpringLayout.EAST, lIdHead);
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

            JButton borrow = new JButton(pendingLeaveRequest.get(i).getString("borrowleaves"));
            borrow.setOpaque(false);
            borrow.setContentAreaFilled(false);
            borrow.setBorderPainted(false);
            borrow.setForeground(Color.RED);
            layout.putConstraint(SpringLayout.WEST, borrow, 5, SpringLayout.EAST, reasonHead);
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
                    Document levDoc = pendingLeaveRequest.get(Integer.parseInt(e.getActionCommand()));
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
                        JOptionPane.showMessageDialog(page, p, "Faculties on leaves each day", JOptionPane.INFORMATION_MESSAGE);
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
                if (facDoc.getString("position").equals("HOD")) {
                    leavesDb.hodResponse(lId, true, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                    // TODO: Comments this lines after adding dean
                    leavesDb.deanResponse(lId, true, Integer.parseInt(facDoc.getString("f_id")), newReason.getText());
                    List<Document> toAddTheLeave = db.findFaculties(new Document("f_id", String.valueOf(fId)));
                    toAddTheLeave.get(0).put("leaves", toAddTheLeave.get(0).getInteger("leaves") - nod);
                    db.upsertFaculty(toAddTheLeave.get(0));
                    //
                } else if (facDoc.getString("position").equals("Dean")) {
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