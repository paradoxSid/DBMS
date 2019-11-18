package com.sid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.ibatis.jdbc.ScriptRunner;

public class CreatePostgres {
   public static void main(String args[]) {
      // To start the postgres server 'pg_ctl -D "C:\Program Files\PostgreSQL\12\data"
      // start'
      // To access psql from cmd in windows 'psql -U postgres postgres'
      Connection c = null;
      try {
         c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/leaves", "postgres", "four");
         System.out.println("Opened database successfully");

         ScriptRunner sr = new ScriptRunner(c);
         // Creating a reader object
         Reader reader = new BufferedReader(new FileReader("src/R/SQL/p_leaves_schema.sql"));
         // Running the script
         sr.runScript(reader);
         c.close();
      } catch (Exception e) {
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         System.exit(0);
      }
      System.out.println("Table created successfully");
   }
}