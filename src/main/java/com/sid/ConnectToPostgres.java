package com.sid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import java.sql.Date;

public class ConnectToPostgres {
    Connection c = null;
    Statement stmt = null;

    public ConnectToPostgres() {
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/leaves", "postgres", "admin");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        // try {
        // createNewLeaveEntry(0, 0, new Date(new java.util.Date().getTime()),
        // new Date(new java.util.Date().getTime()), " ");
        // createNewLeaveEntry(0, 0, new Date(new java.util.Date().getTime()),
        // new Date(new java.util.Date().getTime()), " ");
        // createNewLeaveEntry(0, 0, new Date(new java.util.Date().getTime()),
        // new Date(new java.util.Date().getTime()), " ");
        // hodResponse(1, true, 1, " ");
        // hodResponse(2, true, 1, " ");
        // hodResponse(3, false, 1, " ");
        // deanResponse(1, true, 1, " ");
        // deanResponse(2, false, 1, " ");
        // System.out.println("Records created successfully");
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }
    }

    public void createNewLeaveEntry(int fId, String dId, Date date, Date date2, String comments) throws SQLException {
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT count(l_id) FROM allLeaves;");
        rs.next();
        int count = rs.getInt("count") + 1;

        String sql1 = "INSERT INTO allLeaves(l_id, f_id, application_status) VALUES (" + count + ", " + fId
                + ", 'newLeaves');";
        stmt.executeUpdate(sql1);
        c.commit();

        String sql2 = "INSERT INTO newLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac) VALUES (?,?,?,?,?,?,?);";
        PreparedStatement pstmt = c.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, count);
        pstmt.setObject(2, new Date(new java.util.Date().getTime()));
        pstmt.setInt(3, fId);
        pstmt.setString(4, dId);
        pstmt.setObject(5, date);
        pstmt.setObject(6, date2);
        pstmt.setString(7, comments);
        pstmt.executeUpdate();

        stmt.close();
        c.commit();
    }

    public void hodResponse(int lId, boolean accepted, int hodId, String comments) throws SQLException {
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM newLeaves WHERE l_id = " + lId + ";");
        rs.next();

        if (accepted) {
            String sql1 = "INSERT INTO approved1Leaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
                    + " auth1, auth1id, auth1comments, auth1ResponseTime)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, rs.getInt("l_id"));
            pstmt.setObject(2, rs.getDate("application_date"));
            pstmt.setInt(3, rs.getInt("f_id"));
            pstmt.setString(4, rs.getString("d_id"));
            pstmt.setObject(5, rs.getDate("from_date"));
            pstmt.setObject(6, rs.getDate("to_date"));
            pstmt.setString(7, rs.getString("commentsFac"));
            pstmt.setString(8, "HOD");
            pstmt.setInt(9, hodId);
            pstmt.setString(10, comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate("UPDATE allLeaves SET application_status = 'approved1Leaves' WHERE l_id = " + lId + ";");

            stmt.executeUpdate("DELETE FROM newLeaves WHERE l_id = " + lId + ";");
        } else {
            String sql1 = "INSERT INTO rejectedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
                    + " auth, authId, authComments, authResponseTime)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, rs.getInt("l_id"));
            pstmt.setObject(2, rs.getDate("application_date"));
            pstmt.setInt(3, rs.getInt("f_id"));
            pstmt.setString(4, rs.getString("d_id"));
            pstmt.setObject(5, rs.getDate("from_date"));
            pstmt.setObject(6, rs.getDate("to_date"));
            pstmt.setString(7, rs.getString("commentsFac"));
            pstmt.setString(8, "HOD");
            pstmt.setInt(9, hodId);
            pstmt.setString(10, comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate("UPDATE allLeaves SET application_status = 'rejectedLeaves' WHERE l_id = " + lId + ";");

            stmt.executeUpdate("DELETE FROM newLeaves WHERE l_id = " + lId + ";");
        }

        stmt.close();
        c.commit();
    }

    public void deanResponse(int lId, boolean accepted, int deanId, String comments) throws SQLException {
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM approved1Leaves WHERE l_id = " + lId + ";");
        rs.next();

        if (accepted) {
            String sql1 = "INSERT INTO approvedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
                    + " auth1, auth1id, auth1comments, auth1ResponseTime, "
                    + "auth2, auth2id, auth2comments, auth2ResponseTime)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, rs.getInt("l_id"));
            pstmt.setObject(2, rs.getDate("application_date"));
            pstmt.setInt(3, rs.getInt("f_id"));
            pstmt.setString(4, rs.getString("d_id"));
            pstmt.setObject(5, rs.getDate("from_date"));
            pstmt.setObject(6, rs.getDate("to_date"));
            pstmt.setString(7, rs.getString("commentsFac"));
            pstmt.setString(8, rs.getString("auth1"));
            pstmt.setInt(9, rs.getInt("auth1id"));
            pstmt.setString(10, rs.getString("auth1comments"));
            pstmt.setObject(11, rs.getObject("auth1ResponseTime"));
            pstmt.setString(12, "Dean");
            pstmt.setInt(13, deanId);
            pstmt.setString(14, comments);
            pstmt.setObject(15, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate("UPDATE allLeaves SET application_status = 'approvedLeaves' WHERE l_id = " + lId + ";");

            stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
        } else {
            String sql1 = "INSERT INTO rejectedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
                    + " auth, authId, authComments, authResponseTime)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, rs.getInt("l_id"));
            pstmt.setObject(2, rs.getDate("application_date"));
            pstmt.setInt(3, rs.getInt("f_id"));
            pstmt.setString(4, rs.getString("d_id"));
            pstmt.setObject(5, rs.getDate("from_date"));
            pstmt.setObject(6, rs.getDate("to_date"));
            pstmt.setString(7, rs.getString("commentsFac"));
            pstmt.setString(8, "Dean");
            pstmt.setInt(9, deanId);
            pstmt.setString(10, comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate("UPDATE allLeaves SET application_status = 'rejectedLeaves' WHERE l_id = " + lId + ";");

            stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
        }

        stmt.close();
        c.commit();
    }

    public List<Document> leavesVisibleToHOD(String dId) throws SQLException {
        List<Document> leavesDId = new ArrayList<>();
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM newLeaves WHERE d_id = '" + dId + "';");
        while (rs.next()) {
            Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                    .append("application_date", String.valueOf(rs.getObject("application_date")))
                    .append("f_id", String.valueOf(rs.getInt("f_id")))
                    .append("d_id", String.valueOf(rs.getString("d_id")))
                    .append("from_date", String.valueOf(rs.getObject("from_date")))
                    .append("to_date", String.valueOf(rs.getObject("to_date")))
                    .append("commentsfac", String.valueOf(rs.getString("commentsfac")));
            leavesDId.add(doc);
        }
        stmt.close();
        return leavesDId;
    }

    public List<Document> leavesVisibleToDean() throws SQLException {
        List<Document> leaveRequestsToDean = new ArrayList<>();
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM approved1leaves;");
        while (rs.next()) {
            Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                    .append("application_date", String.valueOf(rs.getObject("application_date")))
                    .append("f_id", String.valueOf(rs.getInt("f_id")))
                    .append("d_id", String.valueOf(rs.getString("d_id")))
                    .append("from_date", String.valueOf(rs.getObject("from_date")))
                    .append("to_date", String.valueOf(rs.getObject("to_date")))
                    .append("commentsfac", String.valueOf(rs.getString("commentsfac")))
                    .append("hod_id", String.valueOf(rs.getInt("auth1id")))
                    .append("commentshod", String.valueOf(rs.getString("auth1comments")))
                    .append("hodResponseTime", String.valueOf(rs.getObject("auth1responsetime")));
            leaveRequestsToDean.add(doc);
        }
        stmt.close();
        return leaveRequestsToDean;
    }

    public List<Document> getAllLeaves(int fId) throws SQLException {
        List<Document> leavesFid = new ArrayList<>();
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM allLeaves WHERE f_id = " + fId + ";");
        while (rs.next()) {
            int leaveId = rs.getInt("l_id");
            String status = rs.getString("application_status");
            Statement stmt1 = c.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM " + status + " WHERE l_id = " + leaveId + ";");
            while (rs1.next()) {
                Document doc = new Document().append("l_id", String.valueOf(rs1.getInt("l_id")))
                        .append("status", status)
                        .append("application_date", String.valueOf(rs1.getObject("application_date")))
                        .append("f_id", String.valueOf(rs1.getInt("f_id")))
                        .append("d_id", String.valueOf(rs1.getString("d_id")))
                        .append("from_date", String.valueOf(rs1.getObject("from_date")))
                        .append("to_date", String.valueOf(rs1.getObject("to_date")))
                        .append("commentsfac", String.valueOf(rs1.getString("commentsfac")));
                if (status.equals("rejectedLeaves")) {
                    doc.append("auth", String.valueOf(rs1.getString("auth")))
                            .append("authid", String.valueOf(rs1.getInt("authid")))
                            .append("authcomments", String.valueOf(rs1.getString("authcomments")))
                            .append("authresponsetime", String.valueOf(rs1.getObject("authresponsetime")));
                } else if (!status.equals("newLeaves")) {
                    doc.append("hod_id", String.valueOf(rs1.getInt("auth1id")))
                            .append("commentshod", String.valueOf(rs1.getString("auth1comments")))
                            .append("hodResponseTime", String.valueOf(rs1.getObject("auth1responsetime")));
                }
                if (status.equals("approvedleaves")) {
                    doc.append("dean_id", String.valueOf(rs1.getInt("auth2id")))
                            .append("commentsdean", String.valueOf(rs1.getString("auth2comments")))
                            .append("deanResponseTime", String.valueOf(rs1.getObject("auth2responsetime")));
                }
                leavesFid.add(doc);
            }
        }
        stmt.close();
        return leavesFid;
    }
}
