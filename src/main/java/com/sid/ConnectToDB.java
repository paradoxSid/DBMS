package com.sid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

public class ConnectToDB {
    private static String log = "ConnectToDb: ";
    MongoDatabase database;
    MongoCollection<Document> collectionFaculty;
    MongoCollection<Document> collectionDepartment;
    public Document facultyDoc;

    private void print(String s) {
        System.out.println(log + s);
    }

    public ConnectToDB() {
        // MongoCredential credential;
        // credential = MongoCredential.createCredential("sid", "FacultyProject",
        // "sid".toCharArray());
        // print("Connected user: " + userName + "\nTo dateabase: " + dbName);
        MongoClientURI uri = new MongoClientURI("mongodb://sid:sid@localhost/?authSource=FacultyProject");
        MongoClient mongo = new MongoClient(uri);
        // Accessing the database
        database = mongo.getDatabase("FacultyProject");

        // Accessing the collections
        collectionFaculty = database.getCollection("Faculty");
        collectionDepartment = database.getCollection("Department");

        Calendar date = Calendar.getInstance();
        if(date.get(Calendar.MONTH) == 0 && date.get(Calendar.DATE) == 1)
            resetLeaves(date.get(Calendar.YEAR));
    }

    public boolean checkValidUser(String id, String pwd) {
        List<Document> foundDocument = collectionFaculty.find(Filters.eq("f_id", id)).into(new ArrayList<Document>());
        if (foundDocument.size() == 1 && foundDocument.get(0).getString("pwd").equals(pwd.toString())) {
            facultyDoc = foundDocument.get(0);
            print("Login success, User: " + id);
            return true;
        } else if (foundDocument.size() == 0) {
            facultyDoc = null;
            print("Incorrect user id, User: " + id);
        } else {
            facultyDoc = null;
            print("Incorrect Password, User: " + id);
        }
        if (id.equals("admin") && pwd.equals("admin")) {
            facultyDoc = null;
            return true;
        }
        return false;
    }

    public List<Document> findAllDepartments() {
        print("findAllDepartments");
        return collectionDepartment.find().into(new ArrayList<Document>());
    }

    public List<Document> findAllFaculty() {
        print("findAllFaculty");
        return collectionFaculty.find().into(new ArrayList<Document>());
    }

    public Document addNewDepartment(String name, String hod) {
        print("addNewDepartment");
        Document insert = new Document().append("d_id", name);
        if (!hod.isEmpty())
            insert.append("hod", new Document("0", hod));
        else
            insert.append("hod", new Document());
        collectionDepartment.insertOne(insert);
        List<Document> inserted = collectionDepartment.find(insert).into(new ArrayList<Document>());
        return inserted.get(0);
    }

	public Document addNewDepartment(String name, String position, String id) {
        print("addNewDepartment CCF");
        Document insert = new Document().append("d_id", name).append(position, new Document("0", id));
        collectionDepartment.insertOne(insert);
        List<Document> inserted = collectionDepartment.find(insert).into(new ArrayList<Document>());
        return inserted.get(0);
	}

    public void upsertDept(Document doc) {
        print("upsertDept");
        collectionDepartment.replaceOne(Filters.eq("d_id", doc.getString("d_id")), doc);
    }

    public void deleteDepartment(String id) {
        print("deleteDepartment");
        collectionDepartment.deleteOne(new Document().append("d_id", id));
    }

    public Document addNewFaculty(String id, String pwd, String name, String dName, String position) {
        print("addNewFaculty");
        Document insert = new Document().append("f_id", id)
                            .append("pwd", pwd)
                            .append("name", name)
                            .append("d_id", dName)
                            .append("position", position)
                            .append("leaves", 15)
                            .append("year", Calendar.getInstance().get(Calendar.YEAR));
        Document extra = new Document().append("About Me", "")
                            .append("Skill Set", "");
        BasicDBObject work  =  new BasicDBObject().append("EmployeeID", id)
                            .append("Department", "")
                            .append("HOD ID", "")
                            .append("Title", "")
                            .append("Office", "")
                            .append("Date of joining", new Date())
                            .append("Employee Status", "Active");
        extra.append("Work", work);
        Document personal  =  new Document().append("Mobile Phone", "")
                            .append("Email", "")
                            .append("Birth Date", "")
                            .append("Age", "")
                            .append("Address", "")
                            .append("Gender", "");
        extra.append("Personal", personal);
        insert.append("extra", extra);
        collectionFaculty.insertOne(insert);
        List<Document> inserted = collectionFaculty.find(insert).into(new ArrayList<Document>());
        return inserted.get(0);
    }

    public void deleteFaculty(String id) {
        print("deleteFaculty");
        collectionFaculty.deleteOne(new Document().append("f_id", id));
    }

    public void upsertFaculty(Document doc) {
        print("upsertFaculty");
        collectionFaculty.replaceOne(Filters.eq("f_id", doc.getString("f_id")), doc);
    }

    public List<Document> findFaculties(Document filter) {
        print("findFaculties");
        return collectionFaculty.find(filter).into(new ArrayList<Document>());
    }

	private void resetLeaves(int year) {
        print("resetLeaves");
        Document filter = new Document().append("leaves", new Document("$gte", 0)).append("year",
                new Document("$ne", year));
        Document updateDoc = new Document().append("$set", new Document("leaves", 15).append("year", year));
        collectionFaculty.updateMany(filter , updateDoc);

        Document filter1 = new Document().append("leaves", new Document("$lt", 0)).append("year",
                new Document("$ne", year));
        Document updateDoc1 = new Document().append("$inc", new Document("leaves", 15)).append("$set",
                new Document("year", year));
        collectionFaculty.updateMany(filter1, updateDoc1);
	}

}
