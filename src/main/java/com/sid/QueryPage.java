package com.sid;

import static com.sid.ActivityMain.db;
import static com.sid.ActivityMain.setActivity;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bson.Document;

public class QueryPage {
    public JPanel page = new JPanel();
    SpringLayout layout = new SpringLayout();
    List<Document> result;
    JButton lastFac = null;
    public static Document Info;
    JRadioButton last = null;
    JButton facultyName, deptName, fac_id, facPosition;
    JButton backButton;
    List <JButton> buttons = new ArrayList<>();

    public QueryPage() {
    page.setLayout(layout);

        final JTextField search_text = new JTextField(18);
        layout.putConstraint(SpringLayout.WEST, search_text, 5, SpringLayout.WEST, page);
        layout.putConstraint(SpringLayout.NORTH, search_text, 5, SpringLayout.NORTH, page);
        page.add(search_text);

        JButton SearchButton = new JButton("Search");
        layout.putConstraint(SpringLayout.WEST, SearchButton, 5, SpringLayout.EAST, search_text);
        layout.putConstraint(SpringLayout.NORTH, SearchButton, 5, SpringLayout.NORTH, page);
        page.add(SearchButton);

        backButton = new JButton("Back");
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.RED);
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.EAST, SearchButton);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, page);
        page.add(backButton);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActivity(null);
            }

        });




        final JRadioButton Faculty_wise = new JRadioButton("Faculty");
        Faculty_wise.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        layout.putConstraint(SpringLayout.WEST, Faculty_wise, 5, SpringLayout.WEST, search_text);
        layout.putConstraint(SpringLayout.NORTH, Faculty_wise, 5, SpringLayout.SOUTH, search_text);
        page.add(Faculty_wise);
        bg.add(Faculty_wise);

        final JRadioButton Department_wise = new JRadioButton("Department");
        layout.putConstraint(SpringLayout.WEST, Department_wise, 5, SpringLayout.EAST, Faculty_wise);
        layout.putConstraint(SpringLayout.NORTH, Department_wise, 5, SpringLayout.SOUTH, search_text);
        page.add(Department_wise);
        bg.add(Department_wise);
        last = Department_wise;


        SearchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (search_text.getText().isEmpty() || (!Faculty_wise.isSelected() && !Department_wise.isSelected())) {
                    JOptionPane.showMessageDialog(page, "Fields cannot be empty");
                    return;
                }
                if(!buttons.isEmpty())
                    removeLastSearchResult();
                if (Department_wise.isSelected()) {
                    String dep_Name = search_text.getText();
                    Document filter = new Document("d_id", dep_Name);
                    result = db.findFaculties(filter); // result have all faculty
                    setfaculty(0);

                    page.revalidate();
                    
                }

                if (Faculty_wise.isSelected()) {
                    
                    String fac_Name = search_text.getText();
                    Document filter = new Document("name", fac_Name);
                    result = db.findFaculties(filter); // result have all faculty
                    setfaculty(0);
                    if(buttons.isEmpty()){
                        JOptionPane.showMessageDialog(page, "No Faculty Found. Check Your Inputs.");
                            return;
                    }

                    page.revalidate();
                }
            }
        });
    }

    private void removeLastSearchResult() {
        for (JButton jButton : buttons) {
            page.remove(jButton);
        }
        buttons.clear();
        lastFac = facultyName;
        
    }

    private void setfaculty(int index) {
        if (lastFac == null) {
            facultyName = new JButton("Faculty Name");
            facultyName.setOpaque(false);
            facultyName.setContentAreaFilled(false);
            facultyName.setBorderPainted(false);
            facultyName
                    .setFont(new Font(facultyName.getFont().getName(), Font.BOLD, facultyName.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, facultyName, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, facultyName, 5, SpringLayout.SOUTH, last);
            page.add(facultyName);

            deptName = new JButton("Department");
            deptName.setOpaque(false);
            deptName.setContentAreaFilled(false);
            deptName.setBorderPainted(false);
            deptName.setFont(new Font(deptName.getFont().getName(), Font.BOLD, deptName.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, deptName, 5, SpringLayout.EAST, facultyName);
            layout.putConstraint(SpringLayout.NORTH, deptName, 5, SpringLayout.SOUTH, last);
            page.add(deptName);

            fac_id = new JButton("Faculty Id");
            fac_id.setOpaque(false);
            fac_id.setContentAreaFilled(false);
            fac_id.setBorderPainted(false);
            fac_id.setFont(new Font(fac_id.getFont().getName(), Font.BOLD, fac_id.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, fac_id, 5, SpringLayout.EAST, deptName);
            layout.putConstraint(SpringLayout.NORTH, fac_id, 5, SpringLayout.SOUTH, last);
            page.add(fac_id);
            lastFac = facultyName;
            
            facPosition = new JButton("Postions");
            facPosition.setOpaque(false);
            facPosition.setContentAreaFilled(false);
            facPosition.setBorderPainted(false);
            facPosition
                    .setFont(new Font(facPosition.getFont().getName(), Font.BOLD, facPosition.getFont().getSize() + 5));
            layout.putConstraint(SpringLayout.WEST, facPosition, 5, SpringLayout.EAST, fac_id);
            layout.putConstraint(SpringLayout.NORTH, facPosition, 5, SpringLayout.SOUTH, last);
            page.add(facPosition);

            facultyName.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    
                }
            });


        }
        int i = index;
        for (; i < result.size(); i++) {
            Document temp = result.get(i);

            JButton facName = new JButton(temp.getString("name"));
            facName.setOpaque(false);
            facName.setContentAreaFilled(false);
            facName.setBorderPainted(false);
            facName.setForeground(Color.BLUE);
            facName.setActionCommand(temp.getString("name"));

            JButton depId = new JButton(temp.getString("d_id"));
            depId.setOpaque(false);
            depId.setContentAreaFilled(false);
            depId.setBorderPainted(false);
            depId.setForeground(Color.BLUE);
            depId.setActionCommand(temp.getString("d_id"));

            JButton facId = new JButton(temp.getString("f_id"));
            facId.setOpaque(false);
            facId.setContentAreaFilled(false);
            facId.setBorderPainted(false);
            facId.setForeground(Color.BLUE);
            facId.setActionCommand(temp.getString("f_id"));

            JButton facpos = new JButton(temp.getString("position"));
            facpos.setOpaque(false);
            facpos.setContentAreaFilled(false);
            facpos.setBorderPainted(false);
            facpos.setForeground(Color.BLUE);
            facpos.setActionCommand(temp.getString("f_id"));

            layout.putConstraint(SpringLayout.WEST, facName, 5, SpringLayout.WEST, page);
            layout.putConstraint(SpringLayout.NORTH, facName, 5, SpringLayout.SOUTH, lastFac);

            layout.putConstraint(SpringLayout.WEST, depId, 5, SpringLayout.EAST, facultyName);
            layout.putConstraint(SpringLayout.NORTH, depId, 5, SpringLayout.SOUTH, lastFac);

            layout.putConstraint(SpringLayout.WEST, facId, 5, SpringLayout.EAST, deptName);
            layout.putConstraint(SpringLayout.NORTH, facId, 5, SpringLayout.SOUTH, lastFac);

            layout.putConstraint(SpringLayout.WEST, facpos, 5, SpringLayout.EAST, fac_id);
            layout.putConstraint(SpringLayout.NORTH, facpos, 5, SpringLayout.SOUTH, lastFac);

            lastFac = facName;
            buttons.add(facName);
            buttons.add(depId);
            buttons.add(facId);
            buttons.add(facpos);
            page.add(facName);
            page.add(depId);
            page.add(facId);
            page.add(facpos);
        }
        page.revalidate();
        page.repaint();
    }
}
