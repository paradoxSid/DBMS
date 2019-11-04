package com.sid;

import java.util.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;

public class ConnectToDB {
    private static String log = "ConnectToDb: ";
    MongoDatabase database;
    MongoCollection<Document> collectionFaculty;
    MongoCollection<Document> collectionDepartment;
    MongoCollection<Document> collectionLeaves;
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
        collectionLeaves = database.getCollection("Leaves");
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
                            .append("leaves", 5)
                            .append("extra", new Document());
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

	public List<Document> findAllLeaves(String id) {
		print("findAllLeaves");
        return collectionLeaves.find(Filters.eq("f_id", id)).sort(new Document("Request Date", 1)).into(new ArrayList<Document>());
	}
}
