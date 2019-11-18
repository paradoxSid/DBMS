package com.sid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import java.sql.Date;

public class ConnectToPostgres {
    private static String log = "ConnectToPostgres: ";
    Connection c = null;
    Statement stmt = null;

    private void print(String s) {
        System.out.println(log + s);
    }

    public ConnectToPostgres() {
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/leaves", "postgres", "four");
            c.setAutoCommit(false);
            print("Connected");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public HashMap<String, Document> getAllRoutes() throws SQLException {
        print("getAllRoutes");

        stmt = c.createStatement();
        HashMap<String, Document> routes = new HashMap<>();

        ResultSet rs = stmt.executeQuery("SELECT * FROM leaveRoutes ORDER BY hno ASC;");
        while (rs.next()) {
            Document doc = new Document().append("hno", rs.getInt("hno")).append("applicant", rs.getString("applicant"))
                    .append("auth1", rs.getString("auth1")).append("auth2", rs.getString("auth2"));
            routes.put(rs.getString("applicant"), doc);
        }

        stmt.close();
        return routes;
    }

    public Document addNewRoute(int hno, int oldHno, String applicant, String auth1, String auth2) throws SQLException {
        print("addNewRoute");

        stmt = c.createStatement();
        Document route = new Document().append("hno", hno).append("applicant", applicant).append("auth1", auth1)
                .append("auth2", auth2);

        if (hno < oldHno)
            stmt.executeUpdate(
                    "UPDATE leaveRoutes SET hno = hno + 1 WHERE hno > " + (hno - 1) + " AND hno < " + oldHno + ";");
        else if (hno > oldHno)
            stmt.executeUpdate(
                    "UPDATE leaveRoutes SET hno = hno - 1 WHERE hno > " + oldHno + " AND hno < " + (hno + 1) + ";");

        stmt.executeUpdate("INSERT INTO leaveRoutes(hno, applicant, auth1, auth2) VALUES(" + hno + ", '" + applicant
                + "', '" + auth1 + "', '" + auth2 + "');");

        stmt.close();
        c.commit();
        return route;
    }

    public Document editRoute(int hno, int oldHno, String applicant, String auth1, String auth2) throws SQLException {
        print("editRoute");

        stmt = c.createStatement();
        Document route = new Document().append("hno", hno).append("applicant", applicant).append("auth1", auth1)
                .append("auth2", auth2);
        // "UPDATE allLeaves SET application_status = 'newLeaves', notifyUser = true,
        // borrowleaves = "
        // + ltb + " WHERE l_id = " + oldLID + ";"
        if (hno < oldHno)
            stmt.executeUpdate(
                    "UPDATE leaveRoutes SET hno = hno + 1 WHERE hno > " + (hno - 1) + " AND hno < " + oldHno + ";");
        else if (hno > oldHno)
            stmt.executeUpdate(
                    "UPDATE leaveRoutes SET hno = hno - 1 WHERE hno > " + oldHno + " AND hno < " + (hno + 1) + ";");
        stmt.executeUpdate("UPDATE leaveRoutes SET hno = " + hno + ", auth1 = '" + auth1 + "', auth2 = '" + auth2
                + "' WHERE applicant = '" + applicant + "';");

        stmt.close();
        c.commit();
        return route;
    }

    public void disableNotification(int lId) throws SQLException {
        print("disableNotification");
        stmt = c.createStatement();
        stmt.executeUpdate("UPDATE allLeaves SET notifyUser = false WHERE l_id = " + lId + ";");
        c.commit();
    }

    public HashMap<String, List<Document>> getAllLeavesToApprove(String position, String f_id, String d_id)
            throws SQLException {
        print("getAllLeavesToApprove");
        stmt = c.createStatement();
        HashMap<String, List<Document>> leavesToApprove = new HashMap<>();
        ResultSet rs1 = stmt.executeQuery("SELECT applicant FROM leaveRoutes WHERE auth1 = '" + position + "'");

        List<Document> auth1 = new ArrayList<>();
        while (rs1.next()) {
            Statement stmt1 = c.createStatement();
            ResultSet rs;
            if (!d_id.equals("CCF"))
                rs = stmt1.executeQuery(
                        "SELECT n.l_id, borrowleaves, application_date, n.f_id, d_id, from_date, to_date, commentsfac "
                                + "FROM newleaves as n, allleaves as a WHERE n.l_id = a.l_id AND applicant = '"
                                + rs1.getString("applicant") + "' AND d_id = '" + d_id + "';");
            else
                rs = stmt1.executeQuery(
                        "SELECT n.l_id, borrowleaves, application_date, n.f_id, d_id, from_date, to_date, commentsfac "
                                + "FROM newleaves as n, allleaves as a WHERE n.l_id = a.l_id AND applicant = '"
                                + rs1.getString("applicant") + "';");
            while (rs.next()) {
                Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                        .append("borrowleaves", String.valueOf(rs.getInt("borrowleaves")))
                        .append("application_date", String.valueOf(rs.getObject("application_date")))
                        .append("f_id", String.valueOf(rs.getInt("f_id")))
                        .append("d_id", String.valueOf(rs.getString("d_id")))
                        .append("from_date", String.valueOf(rs.getObject("from_date")))
                        .append("to_date", String.valueOf(rs.getObject("to_date")))
                        .append("commentsfac", String.valueOf(rs.getString("commentsfac")));
                System.out.println(doc);
                auth1.add(doc);
            }
            leavesToApprove.put("auth1", auth1);
            stmt1.close();
        }

        ResultSet rs2 = stmt.executeQuery("SELECT applicant FROM leaveRoutes WHERE auth2 = '" + position + "'");
        List<Document> auth2 = new ArrayList<>();
        while (rs2.next()) {
            Statement stmt1 = c.createStatement();
            ResultSet rs;
            if (!d_id.equals("CCF"))
                rs = stmt1.executeQuery(
                        "SELECT n.l_id, borrowleaves, application_date, n.f_id, d_id, from_date, to_date, commentsfac, auth1, auth1id, auth1comments, auth1responsetime "
                                + "FROM approved1leaves as n, allleaves as a WHERE n.l_id = a.l_id AND applicant = '"
                                + rs2.getString("applicant") + "' AND d_id = '" + d_id + "';");
            else
                rs = stmt1.executeQuery(
                        "SELECT n.l_id, borrowleaves, application_date, n.f_id, d_id, from_date, to_date, commentsfac, auth1, auth1id, auth1comments, auth1responsetime "
                                + "FROM approved1leaves as n, allleaves as a WHERE n.l_id = a.l_id AND applicant = '"
                                + rs2.getString("applicant") + "';");
            while (rs.next()) {
                Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                        .append("borrowleaves", String.valueOf(rs.getInt("borrowleaves")))
                        .append("application_date", String.valueOf(rs.getObject("application_date")))
                        .append("f_id", String.valueOf(rs.getInt("f_id")))
                        .append("d_id", String.valueOf(rs.getString("d_id")))
                        .append("from_date", String.valueOf(rs.getObject("from_date")))
                        .append("to_date", String.valueOf(rs.getObject("to_date")))
                        .append("commentsfac", String.valueOf(rs.getString("commentsfac")))
                        .append("auth1", String.valueOf(rs.getString("auth1")))
                        .append("auth1id", String.valueOf(rs.getInt("auth1id")))
                        .append("auth1comments", String.valueOf(rs.getString("auth1comments")))
                        .append("auth1responsetime", String.valueOf(rs.getObject("auth1responsetime")));
                System.out.println(doc);
                auth2.add(doc);
            }
            leavesToApprove.put("auth2", auth2);
            stmt1.close();
        }
        stmt.close();

        if (leavesToApprove.isEmpty())
            return null;
        else
            return leavesToApprove;
    }

    public int createNewLeaveEntry(int fId, String dId, Date date, Date date2, String comments, int bLeaves,
            String applicant) throws SQLException {
        print("createNewLeaveEntry");
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT count(l_id) FROM allLeaves;");
        rs.next();
        int count = rs.getInt("count") + 1;

        String sql1 = "INSERT INTO allLeaves(l_id, f_id, applicant, application_status, notifyUser, borrowleaves) VALUES ("
                + count + ", " + fId + ", '" + applicant + "', 'newLeaves', false, " + bLeaves + ");";
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
        return count;
    }

    public int editLeaveEntry(int fId, String dId, Date date, Date date2, String comments, int oldLID, int ltb)
            throws SQLException {
        print("editLeaveEntry");
        stmt = c.createStatement();

        stmt.executeUpdate("UPDATE allLeaves SET application_status = 'newLeaves', notifyUser = true, borrowleaves = "
                + ltb + " WHERE l_id = " + oldLID + ";");
        c.commit();

        String sql2 = "INSERT INTO newLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac) VALUES (?,?,?,?,?,?,?);";
        PreparedStatement pstmt = c.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, oldLID);
        pstmt.setObject(2, new Date(new java.util.Date().getTime()));
        pstmt.setInt(3, fId);
        pstmt.setString(4, dId);
        pstmt.setObject(5, date);
        pstmt.setObject(6, date2);
        pstmt.setString(7, comments);
        pstmt.executeUpdate();

        stmt.executeUpdate("DELETE FROM rejectedLeaves WHERE l_id = " + oldLID + ";");

        stmt.close();
        c.commit();
        return oldLID;
    }

    public void auth1Response(int lId, boolean accepted, String auth1position, int auth1Id, String auth1comments)
            throws SQLException {
        print("auth1Response");
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
            pstmt.setString(8, auth1position);
            pstmt.setInt(9, auth1Id);
            pstmt.setString(10, auth1comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate(
                    "UPDATE allLeaves SET application_status = 'approved1Leaves', notifyUser = true WHERE l_id = " + lId
                            + ";");

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
            pstmt.setString(8, auth1position);
            pstmt.setInt(9, auth1Id);
            pstmt.setString(10, auth1comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate(
                    "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = true WHERE l_id = " + lId
                            + ";");

            stmt.executeUpdate("DELETE FROM newLeaves WHERE l_id = " + lId + ";");
        }

        stmt.close();
        c.commit();
    }

    public void auth2Response(int lId, boolean accepted, String auth2position, int auth2Id, String auth2comments)
            throws SQLException {
        print("auth2Response");
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
            pstmt.setString(12, auth2position);
            pstmt.setInt(13, auth2Id);
            pstmt.setString(14, auth2comments);
            pstmt.setObject(15, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate(
                    "UPDATE allLeaves SET application_status = 'approvedLeaves', notifyUser = true WHERE l_id = " + lId
                            + ";");

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
            pstmt.setString(8, auth2position);
            pstmt.setInt(9, auth2Id);
            pstmt.setString(10, auth2comments);
            pstmt.setObject(11, new Date(new java.util.Date().getTime()));
            pstmt.executeUpdate();

            stmt.executeUpdate(
                    "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = true WHERE l_id = " + lId
                            + ";");

            stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
        }

        stmt.close();
        c.commit();
    }

    /*
     * public void hodResponse(int lId, boolean accepted, int hodId, String
     * comments, String string) throws SQLException { print("hodResponse"); stmt =
     * c.createStatement();
     * 
     * ResultSet rs = stmt.executeQuery("SELECT * FROM newLeaves WHERE l_id = " +
     * lId + ";"); rs.next();
     * 
     * if (accepted) { String sql1 =
     * "INSERT INTO approved1Leaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth1, auth1id, auth1comments, auth1ResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, "HOD"); if (rs.getInt("f_id") == hodId) pstmt.setString(8,
     * "MySelf" + string); pstmt.setInt(9, hodId); pstmt.setString(10, comments);
     * pstmt.setObject(11, new Date(new java.util.Date().getTime()));
     * pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'approved1Leaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM newLeaves WHERE l_id = " + lId + ";"); } else
     * { String sql1 =
     * "INSERT INTO rejectedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth, authId, authComments, authResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, "HOD"); pstmt.setInt(9, hodId); pstmt.setString(10,
     * comments); pstmt.setObject(11, new Date(new java.util.Date().getTime()));
     * pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM newLeaves WHERE l_id = " + lId + ";"); }
     * 
     * stmt.close(); c.commit(); }
     * 
     * public void deanResponse(int lId, boolean accepted, int deanId, String
     * comments) throws SQLException { print("deanResponse"); stmt =
     * c.createStatement();
     * 
     * ResultSet rs =
     * stmt.executeQuery("SELECT * FROM approved1Leaves WHERE l_id = " + lId + ";");
     * rs.next();
     * 
     * if (accepted) { String sql1 =
     * "INSERT INTO approvedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth1, auth1id, auth1comments, auth1ResponseTime, " +
     * "auth2, auth2id, auth2comments, auth2ResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, rs.getString("auth1")); pstmt.setInt(9,
     * rs.getInt("auth1id")); pstmt.setString(10, rs.getString("auth1comments"));
     * pstmt.setObject(11, rs.getObject("auth1ResponseTime")); pstmt.setString(12,
     * "Dean"); if (rs.getInt("f_id") == deanId) pstmt.setString(12, "MySelfDean");
     * pstmt.setInt(13, deanId); pstmt.setString(14, comments); pstmt.setObject(15,
     * new Date(new java.util.Date().getTime())); pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'approvedLeaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
     * } else { String sql1 =
     * "INSERT INTO rejectedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth, authId, authComments, authResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, "Dean"); pstmt.setInt(9, deanId); pstmt.setString(10,
     * comments); pstmt.setObject(11, new Date(new java.util.Date().getTime()));
     * pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
     * }
     * 
     * stmt.close(); c.commit(); }
     * 
     * public void directorResponse(int lId, boolean accepted, int dId, String
     * comments) throws SQLException { print("directorResponse"); stmt =
     * c.createStatement();
     * 
     * ResultSet rs =
     * stmt.executeQuery("SELECT * FROM approved1Leaves WHERE l_id = " + lId + ";");
     * rs.next();
     * 
     * if (accepted) { String sql1 =
     * "INSERT INTO approvedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth1, auth1id, auth1comments, auth1ResponseTime, " +
     * "auth2, auth2id, auth2comments, auth2ResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, rs.getString("auth1")); pstmt.setInt(9,
     * rs.getInt("auth1id")); pstmt.setString(10, rs.getString("auth1comments"));
     * pstmt.setObject(11, rs.getObject("auth1ResponseTime")); pstmt.setString(12,
     * "Director"); if (rs.getInt("f_id") == dId) pstmt.setString(12, "MySelfHead");
     * pstmt.setInt(13, dId); pstmt.setString(14, comments); pstmt.setObject(15, new
     * Date(new java.util.Date().getTime())); pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'approvedLeaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
     * } else { String sql1 =
     * "INSERT INTO rejectedLeaves(l_id, application_date, f_id, d_id, from_date, to_date, commentsFac,"
     * + " auth, authId, authComments, authResponseTime)" +
     * " VALUES (?,?,?,?,?,?,?,?,?,?,?);"; PreparedStatement pstmt =
     * c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS); pstmt.setInt(1,
     * rs.getInt("l_id")); pstmt.setObject(2, rs.getDate("application_date"));
     * pstmt.setInt(3, rs.getInt("f_id")); pstmt.setString(4, rs.getString("d_id"));
     * pstmt.setObject(5, rs.getDate("from_date")); pstmt.setObject(6,
     * rs.getDate("to_date")); pstmt.setString(7, rs.getString("commentsFac"));
     * pstmt.setString(8, "Director"); pstmt.setInt(9, dId); pstmt.setString(10,
     * comments); pstmt.setObject(11, new Date(new java.util.Date().getTime()));
     * pstmt.executeUpdate();
     * 
     * stmt.executeUpdate(
     * "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = true WHERE l_id = "
     * + lId + ";");
     * 
     * stmt.executeUpdate("DELETE FROM approved1Leaves WHERE l_id = " + lId + ";");
     * }
     * 
     * stmt.close(); c.commit(); }
     */
    public void redeemLeaves(int lId, String tableName) throws SQLException {
        print("redeemLeaves");
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE l_id = " + lId + ";");
        rs.next();
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
        pstmt.setString(8, "Self");
        pstmt.setInt(9, rs.getInt("f_id"));
        pstmt.setString(10, "Self Redeem");
        pstmt.setObject(11, new Date(new java.util.Date().getTime()));
        pstmt.executeUpdate();

        stmt.executeUpdate(
                "UPDATE allLeaves SET application_status = 'rejectedLeaves', notifyUser = false WHERE l_id = " + lId
                        + ";");

        stmt.executeUpdate("DELETE FROM " + tableName + " WHERE l_id = " + lId + ";");
        stmt.close();
        c.commit();
    }

    /*
     * public List<Document> leavesVisibleToHOD(String dId) throws SQLException {
     * print("leavesVisibleToHOD"); List<Document> leavesDId = new ArrayList<>();
     * stmt = c.createStatement();
     * 
     * ResultSet rs = stmt.executeQuery("SELECT * FROM newLeaves WHERE d_id = '" +
     * dId + "';"); while (rs.next()) { Statement stmt1 = c.createStatement();
     * ResultSet rs1 = stmt1.executeQuery("SELECT * FROM allleaves WHERE l_id = " +
     * rs.getInt("l_id") + ";"); rs1.next(); Document doc = new
     * Document().append("l_id", String.valueOf(rs.getInt("l_id")))
     * .append("borrowleaves", String.valueOf(rs1.getInt("borrowleaves")))
     * .append("application_date", String.valueOf(rs.getObject("application_date")))
     * .append("f_id", String.valueOf(rs.getInt("f_id"))) .append("d_id",
     * String.valueOf(rs.getString("d_id"))) .append("from_date",
     * String.valueOf(rs.getObject("from_date"))) .append("to_date",
     * String.valueOf(rs.getObject("to_date"))) .append("commentsfac",
     * String.valueOf(rs.getString("commentsfac"))); leavesDId.add(doc);
     * stmt1.close(); } stmt.close(); Collections.sort(leavesDId,
     * leaveIdComparator); return leavesDId; }
     * 
     * public List<Document> leavesVisibleToDean() throws SQLException {
     * print("leavesVisibleToDean"); List<Document> leaveRequestsToDean = new
     * ArrayList<>(); stmt = c.createStatement();
     * 
     * ResultSet rs = stmt.
     * executeQuery("SELECT * FROM approved1leaves WHERE auth1 NOT LIKE 'MySelf%';"
     * ); while (rs.next()) { Statement stmt1 = c.createStatement(); ResultSet rs1 =
     * stmt1.executeQuery("SELECT * FROM allleaves WHERE l_id = " +
     * rs.getInt("l_id") + ";"); rs1.next(); Document doc = new
     * Document().append("l_id", String.valueOf(rs.getInt("l_id")))
     * .append("borrowleaves", String.valueOf(rs1.getInt("borrowleaves")))
     * .append("application_date", String.valueOf(rs.getObject("application_date")))
     * .append("f_id", String.valueOf(rs.getInt("f_id"))) .append("d_id",
     * String.valueOf(rs.getString("d_id"))) .append("from_date",
     * String.valueOf(rs.getObject("from_date"))) .append("to_date",
     * String.valueOf(rs.getObject("to_date"))) .append("commentsfac",
     * String.valueOf(rs.getString("commentsfac"))) .append("hod_id",
     * String.valueOf(rs.getInt("auth1id"))) .append("commentshod",
     * String.valueOf(rs.getString("auth1comments"))) .append("hodResponseTime",
     * String.valueOf(rs.getObject("auth1responsetime")));
     * leaveRequestsToDean.add(doc); stmt1.close(); } stmt.close();
     * Collections.sort(leaveRequestsToDean, leaveIdComparator); return
     * leaveRequestsToDean; }
     * 
     * public List<Document> leavesVisibleToDirector() throws SQLException {
     * print("leavesVisibleToDirector"); List<Document> leaveRequestsToDean = new
     * ArrayList<>(); stmt = c.createStatement();
     * 
     * ResultSet rs =
     * stmt.executeQuery("SELECT * FROM approved1leaves WHERE auth1 LIKE 'MySelf%';"
     * ); while (rs.next()) { Statement stmt1 = c.createStatement(); ResultSet rs1 =
     * stmt1.executeQuery("SELECT * FROM allleaves WHERE l_id = " +
     * rs.getInt("l_id") + ";"); rs1.next(); Document doc = new
     * Document().append("l_id", String.valueOf(rs.getInt("l_id")))
     * .append("borrowleaves", String.valueOf(rs1.getInt("borrowleaves")))
     * .append("application_date", String.valueOf(rs.getObject("application_date")))
     * .append("f_id", String.valueOf(rs.getInt("f_id"))) .append("d_id",
     * String.valueOf(rs.getString("d_id"))) .append("from_date",
     * String.valueOf(rs.getObject("from_date"))) .append("to_date",
     * String.valueOf(rs.getObject("to_date"))) .append("commentsfac",
     * String.valueOf(rs.getString("commentsfac"))) .append("hod_id",
     * String.valueOf(rs.getInt("auth1id"))) .append("commentshod",
     * String.valueOf(rs.getString("auth1comments"))) .append("hodResponseTime",
     * String.valueOf(rs.getObject("auth1responsetime")));
     * leaveRequestsToDean.add(doc); stmt1.close(); } stmt.close();
     * Collections.sort(leaveRequestsToDean, leaveIdComparator); return
     * leaveRequestsToDean; }
     */
    public List<Document> getAllApprovedLeaves(String dId, Date fDate, Date tDate) throws SQLException {
        if (dId.equals("CCF"))
            return getAllApprovedLeaves(fDate, tDate);
        print("getAllApprovedLeaves");
        List<Document> allApprovedLeaves = new ArrayList<>();
        stmt = c.createStatement();
        String sql1 = "SELECT * FROM approvedleaves WHERE d_id = '" + dId + "' AND from_date <= ? AND to_date >= ?;";
        PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
        pstmt.setObject(1, tDate);
        pstmt.setObject(2, fDate);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Statement stmt1 = c.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM allleaves WHERE l_id = " + rs.getInt("l_id") + ";");
            rs1.next();
            Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                    .append("borrowleaves", String.valueOf(rs1.getInt("borrowleaves")))
                    .append("application_date", String.valueOf(rs.getObject("application_date")))
                    .append("f_id", String.valueOf(rs.getInt("f_id")))
                    .append("d_id", String.valueOf(rs.getString("d_id")))
                    .append("from_date", String.valueOf(rs.getObject("from_date")))
                    .append("to_date", String.valueOf(rs.getObject("to_date")))
                    .append("commentsfac", String.valueOf(rs.getString("commentsfac")))
                    .append("auth1", String.valueOf(rs.getString("auth1")))
                    .append("auth1id", String.valueOf(rs.getInt("auth1id")))
                    .append("auth1comments", String.valueOf(rs.getString("auth1comments")))
                    .append("auth1responsetime", String.valueOf(rs.getObject("auth1responsetime")))
                    .append("auth2", String.valueOf(rs.getString("auth2")))
                    .append("auth2id", String.valueOf(rs.getInt("auth2id")))
                    .append("auth2comments", String.valueOf(rs.getString("auth2comments")))
                    .append("auth2responsetime", String.valueOf(rs.getObject("auth2responsetime")));
            allApprovedLeaves.add(doc);
        }
        stmt.close();
        Collections.sort(allApprovedLeaves, leaveIdComparator);
        return allApprovedLeaves;
    }

    public List<Document> getAllApprovedLeaves(Date fDate, Date tDate) throws SQLException {
        print("getAllApprovedLeaves");
        List<Document> allApprovedLeaves = new ArrayList<>();
        stmt = c.createStatement();
        String sql1 = "SELECT * FROM approvedleaves WHERE from_date <= ? AND to_date >= ?;";
        PreparedStatement pstmt = c.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
        pstmt.setObject(1, tDate);
        pstmt.setObject(2, fDate);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Statement stmt1 = c.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM allleaves WHERE l_id = " + rs.getInt("l_id") + ";");
            rs1.next();
            Document doc = new Document().append("l_id", String.valueOf(rs.getInt("l_id")))
                    .append("borrowleaves", String.valueOf(rs1.getInt("borrowleaves")))
                    .append("application_date", String.valueOf(rs.getObject("application_date")))
                    .append("f_id", String.valueOf(rs.getInt("f_id")))
                    .append("d_id", String.valueOf(rs.getString("d_id")))
                    .append("from_date", String.valueOf(rs.getObject("from_date")))
                    .append("to_date", String.valueOf(rs.getObject("to_date")))
                    .append("commentsfac", String.valueOf(rs.getString("commentsfac")))
                    .append("auth1", String.valueOf(rs.getString("auth1")))
                    .append("auth1id", String.valueOf(rs.getInt("auth1id")))
                    .append("auth1comments", String.valueOf(rs.getString("auth1comments")))
                    .append("auth1responsetime", String.valueOf(rs.getObject("auth1responsetime")))
                    .append("auth2", String.valueOf(rs.getString("auth2")))
                    .append("auth2id", String.valueOf(rs.getInt("auth2id")))
                    .append("auth2comments", String.valueOf(rs.getString("auth2comments")))
                    .append("auth2responsetime", String.valueOf(rs.getObject("auth2responsetime")));
            allApprovedLeaves.add(doc);
        }
        stmt.close();
        Collections.sort(allApprovedLeaves, leaveIdComparator);
        return allApprovedLeaves;
    }

    public List<Document> getAllLeaves(int fId) throws SQLException {
        print("getAllLeaves");
        List<Document> leavesFid = new ArrayList<>();
        stmt = c.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM allLeaves WHERE f_id = " + fId + ";");
        while (rs.next()) {
            int leaveId = rs.getInt("l_id");
            String applicant = rs.getString("applicant");
            String status = rs.getString("application_status");
            boolean notifyUser = rs.getBoolean("notifyUser");
            int borrowleaves = rs.getInt("borrowleaves");
            Statement stmt1 = c.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM " + status + " WHERE l_id = " + leaveId + ";");
            while (rs1.next()) {
                Document doc = new Document().append("l_id", String.valueOf(rs1.getInt("l_id")))
                        .append("notifyUser", notifyUser).append("borrowleaves", String.valueOf(borrowleaves))
                        .append("status", status).append("applicant", applicant)
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
                    doc.append("auth1", String.valueOf(rs1.getString("auth1")))
                            .append("auth1id", String.valueOf(rs1.getInt("auth1id")))
                            .append("auth1comments", String.valueOf(rs1.getString("auth1comments")))
                            .append("auth1responsetime", String.valueOf(rs1.getObject("auth1responsetime")));
                }
                if (status.equals("approvedLeaves")) {
                    doc.append("auth2", String.valueOf(rs1.getString("auth2")))
                            .append("auth2id", String.valueOf(rs1.getInt("auth2id")))
                            .append("auth2comments", String.valueOf(rs1.getString("auth2comments")))
                            .append("auth2responsetime", String.valueOf(rs1.getObject("auth2responsetime")));
                }
                leavesFid.add(doc);
            }
        }
        stmt.close();
        Collections.sort(leavesFid, leaveIdComparator1);
        return leavesFid;
    }

    public static Comparator<Document> leaveIdComparator = new Comparator<Document>() {
        public int compare(Document d1, Document d2) {
            return d1.getString("l_id").compareTo(d2.getString("l_id"));
        }
    };

    public static Comparator<Document> leaveIdComparator1 = new Comparator<Document>() {
        public int compare(Document d1, Document d2) {
            return d2.getString("l_id").compareTo(d1.getString("l_id"));
        }
    };

}
