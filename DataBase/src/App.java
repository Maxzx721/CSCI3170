import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.io.*;

public class App {
    final static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db19?autoReconnect=true&useSSL=false";
    final static String dbUsername = "Group19";
    final static String dbPassword = "CSCI3170";

    final static String[] tableNames = {"category", "manufacturer", "part", "salesperson", "transaction"};
    final static String[] datapath = {"/category.txt", "/manufacturer.txt", "/part.txt", "/salesperson.txt", "/transaction.txt"};
    
    public static PreparedStatement stmt;
    public static ResultSet rs;
    public static ResultSetMetaData rsmd;

    public static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
        }catch(Exception x) { 
            System.err.println("Unable to load the driver class!"); 
        }
        Connection conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);

        System.out.println("Welcome to sales system!");
        menu(conn);

    }

    public static void menu(Connection conn) throws SQLException {
        
        System.out.println("\n-----Main menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                administrator(conn);
                break;

            case 2:
                salesPerson(conn);
                break;

            case 3:
                manager(conn);
                break;

            default:
                in.close();
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                conn.close();
                System.exit(0);
        }
        
    }

    public static void administrator(Connection conn) throws SQLException {
    
        while(true) {
            System.out.println("\n-----Operations for administrator menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Create all tables");
            System.out.println("2. Delate all tables");
            System.out.println("3. Load from datafile");
            System.out.println("4. Show content of a table");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = in.nextInt();
            switch (choice) {
                case 1:
                    PreparedStatement[] stmts = {
                        conn.prepareStatement("CREATE TABLE category (cID INTEGER NOT NULL, cName VARCHAR(20) NOT NULL, PRIMARY KEY (cID))"),
                        conn.prepareStatement("CREATE TABLE manufacturer (mID INTEGER NOT NULL, mName VARCHAR(20) NOT NULL, mAddress VARCHAR(50) NOT NULL, mPhoneNumber INTEGER NOT NULL, PRIMARY KEY (mID))"),
                        conn.prepareStatement("CREATE TABLE part (pID INTEGER NOT NULL, pName VARCHAR(20) NOT NULL, pPrice INTEGER NOT NULL, mID INTEGER NOT NULL, cID INTEGER NOT NULL, pWarrantyPeriod INTEGER NOT NULL, pAvailableQuantity INTEGER NOT NULL, PRIMARY KEY (pID))"),
                        conn.prepareStatement("CREATE TABLE salesperson (sID INTEGER NOT NULL, sName VARCHAR(20) NOT NULL, sAddress VARCHAR(50) NOT NULL, sPhoneNumber INTEGER NOT NULL, sExperience INTEGER NOT NULL, PRIMARY KEY (sID))"),
                        conn.prepareStatement("CREATE TABLE transaction (tID INTEGER NOT NULL, pID INTEGER NOT NULL, sID INTEGER NOT NULL, tDate DATE NOT NULL, PRIMARY KEY (tID))")
                    };
                    for (int i = 0; i < stmts.length; i++)
                        stmts[i].execute();
                    System.out.println("Processing...Done! Database is initialized!");
                    menu(conn);
                    
                case 2:
                    for (int i = 0; i < tableNames.length; i++) {
                        stmt = conn.prepareStatement("DROP TABLE " + tableNames[i]);
                        stmt.execute();
                    }
                    System.out.println("Processing...Done! Database is removed!"); 
                    menu(conn);

                case 3:
                    BufferedReader reader;
                    FileReader fr;
                    System.out.print("\nType in the Source Data Folder Path: ");
                    String path = in.next();
                    try {
                        for (int i = 0; i < datapath.length; i++) {
                            String filepath = path + datapath[i];
                            fr = new FileReader(filepath);
                            reader = new BufferedReader(fr);
                            String data = reader.readLine();
                            while (data != null) {
                                String[] splited = data.split("	");
                                switch (i) {
                                    case 0:
                                        stmt = conn.prepareStatement("INSERT INTO category (cID, cName) VALUES (?, ?)");
                                        stmt.setInt(1, Integer.parseInt(splited[0]));
                                        stmt.setString(2, splited[1]); 
                                        stmt.execute();
                                        break;

                                    case 1:
                                        stmt = conn.prepareStatement("INSERT INTO manufacturer (mID, mName, mAddress, mPhoneNumber) VALUES (?, ?, ?, ?)");
                                        for(int j = 0; j < 4; j++)
                                            if (j != 0 || j != 3)
                                                stmt.setString(j + 1, splited[j]); 
                                            else 
                                                stmt.setInt(j + 1, Integer.parseInt(splited[3]));
                                        stmt.execute();
                                        break;

                                    case 2:
                                        stmt = conn.prepareStatement("INSERT INTO part (pID, pName, pPrice, mID, cID, pWarrantyPeriod, pAvailableQuantity) VALUES (?, ?, ?, ?, ?, ?, ?)");
                                        for (int j = 0; j < 7; j++) 
                                            if (j == 1) 
                                                stmt.setString(j + 1, splited[j]); 
                                            else
                                                stmt.setInt(j + 1, Integer.parseInt(splited[j]));
                                        stmt.execute();
                                        break;

                                    case 3:
                                        stmt = conn.prepareStatement("INSERT INTO salesperson (sID, sName, sAddress, sPhoneNumber, sExperience) VALUES (?, ?, ?, ?, ?)");
                                        for (int j = 0; j < 5; j++) 
                                            if (j == 1 || j == 2) 
                                                stmt.setString(j + 1, splited[j]); 
                                            else 
                                                stmt.setInt(j + 1, Integer.parseInt(splited[j]));
                                        stmt.execute();
                                        break;

                                    case 4:
                                        stmt = conn.prepareStatement("INSERT INTO transaction (tID, pID, sID, tDate) VALUES (?, ?, ?, ?)");
                                        SimpleDateFormat format = new SimpleDateFormat( "DD/MM/YYYY" ); 
                                        java.util.Date tDate = format.parse(splited[3]);
                                        java.sql.Date sqlDate = new java.sql.Date(tDate.getTime());
                                        for (int j = 0; j<4;j++) 
                                            if (j == 3)
                                                stmt.setDate(j+1, sqlDate);
                                            else 
                                                stmt.setInt(j+1, Integer.parseInt(splited[j]));                           
                                        stmt.execute();
                                        break;
                                        
                                    default:
                                        break;
                                } 
                                data = reader.readLine();
                            }
                        }
                    }catch(Exception e) {
                        System.out.println(e);
                    }
                    System.out.println("Processing...Done! Data is inputted to the database!");
                    menu(conn);

                case 4:
                    System.out.print("Which table would you like to show: ");
                    String table = in.next();
                    System.out.println("Content of table category:");
                    if (table.equals("transaction")) 
                        stmt = conn.prepareStatement("SELECT tID, pID, sID, DATE_FORMAT(tDate, '%d/%m/%Y') AS tData FROM " + table);
                    else 
                        stmt = conn.prepareStatement("SELECT * FROM " + table);
                    printShell(stmt);
                    menu(conn);
                
                default:
                    menu(conn);
            }
        }
    }   

    public static void salesPerson(Connection conn) throws SQLException {

        while(true) {
            System.out.println("\n-----Operations for salesperson menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. Search for parts");
            System.out.println("2. Sell a part");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = in.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Choose the Search criterion:\n1. Part Name\n2. Manufacturer Name");
                    System.out.print("Choose the search criterion: ");
                    choice = in.nextInt();
                    in.nextLine();
                    System.out.print("Type in the Search Keyword: ");
                    String keyword = in.nextLine();
                    System.out.print("Choose ordering:\n1. By price, ascending order\n2. By price, descending order\nChoose the Search criterion: ");
                    int order = in.nextInt();
                    
                    stmt = conn.prepareStatement("SELECT pID AS ID, pName AS Name, mName AS Manufacturer, cName AS Category, pAvailableQuantity AS Quantity, pWarrantyPeriod AS Warranty, pPrice AS Price FROM manufacturer NATURAL JOIN part NATURAL JOIN category WHERE " + ((choice == 1) ? "pName" : "mName") + " = '" + keyword + "' ORDER BY pPrice " + ((order == 1) ? "ASC" : "DESC"));
                    printShell(stmt);
                    System.out.println("End of Query");
                    menu(conn);
                
                case 2:
                    System.out.print("Enter The Part ID: ");
                    String pid = in.next();
                    System.out.print("Enter The Salesperon ID: ");
                    String sid = in.next();

                    stmt = conn.prepareStatement("SELECT pAvailableQuantity, pName FROM part WHERE pID =" + pid);
                    rs = stmt.executeQuery();
                    rsmd = rs.getMetaData();
                    int rquantity = 0;
                    String pname = "";
                    while (rs.next()) {
                        rquantity = Integer.parseInt(rs.getString(1));
                        pname = rs.getString(2);
                    }
                    if(rquantity <= 0) {
                        System.out.println("Opps ! the item has been sold out");
                    }else {
                        stmt = conn.prepareStatement("SELECT * FROM transaction");
                        rs = stmt.executeQuery();
                        rsmd = rs.getMetaData();
                        int tid = 1;
                        while (rs.next()) {
                            tid++;
                        }
                        stmt = conn.prepareStatement("INSERT INTO transaction VALUES(?, ?, ?, curdate())");
                        stmt.setInt(1, tid);
                        stmt.setInt(2, Integer.parseInt(pid));
                        stmt.setInt(3, Integer.parseInt(sid));
                        stmt.execute();
                        rquantity = rquantity - 1;
                        stmt = conn.prepareStatement("Update part SET pAvailableQuantity = " + rquantity + " WHERE pID = " + pid);
                        stmt.execute();
                        System.out.println("Product: " + pname + "(id " + pid + ") Remaining Quantity: " + rquantity);
                    }
                    menu(conn);
            
                default:
                    menu(conn);
            }
        }

    }
    
    public static void manager(Connection conn) throws SQLException {

        while(true) {
            System.out.println("\n-----Operations for manager menu-----");
            System.out.println("What kinds of operation would you like to perform?");
            System.out.println("1. List all salespersons");
            System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
            System.out.println("3. Show the total sales value of each manufacturer");
            System.out.println("4. Show the N most popular part");
            System.out.println("5. Return to the main menu");
            System.out.print("Enter Your Choice: ");
            int choice = in.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Choose ordering:\n1. By ascending order\n2. By descending order");
                    System.out.print("Choose the list ordering: ");
                    choice = in.nextInt();
                    stmt = conn.prepareStatement("SELECT sID AS ID, sName AS Name, sPhoneNumber AS Mobile_Phone, sExperience AS Years_of_Experience FROM salesperson ORDER BY Years_of_Experience " + ((choice == 1) ? "ASC" : "DESC"));
                    printShell(stmt);
                    menu(conn);
                
                case 2:
                    System.out.print("Type in the lower bound for years of experience: ");
                    String y1 = in.next();
                    System.out.print("Type in the upper bound for years of experience: ");
                    String y2 = in.next();

                    stmt = conn.prepareStatement("SELECT S.sID AS ID, sName AS Name, sExperience AS Years_of_Experience, TEMP.tempCount AS Number_of_Transaction FROM (SELECT COUNT(*) AS tempCount, sID FROM transaction GROUP BY sID) AS TEMP, salesperson S WHERE TEMP.sID = S.sID AND S.sExperience >= " + y1 + " AND S.sExperience <= " + y2 + " ORDER BY ID DESC");
                    System.out.println("Transaction Record:");
                    printShell(stmt);
                    System.out.println("End of Query");
                    menu(conn);
            
                case 3:
                    stmt = conn.prepareStatement("SELECT mID AS Manufacture_ID, mName AS Manufacturer_Name, SUM(pPrice) AS Total_Sales_Value FROM manufacturer NATURAL JOIN part NATURAL JOIN transaction Group by mID ORDER BY Total_Sales_Value DESC");
                    printShell(stmt);
                    System.out.println("End of Query");
                    menu(conn);
            
                case 4:
                    System.out.print("Type in the number of parts: ");
                    String N = in.next();
                    stmt = conn.prepareStatement("SELECT pID AS Part_ID, pName AS Part_Name, COUNT(*) AS No_of_Transaction FROM transaction NATURAL JOIN part GROUP BY pID ORDER BY No_of_Transaction DESC LIMIT " + N);
                    printShell(stmt);
                    System.out.println("End of Query");
                    menu(conn);
            
                default:
                    menu(conn);
            }
        }
    }

    // printShell method is used for printing query result to the shell
    public static void printShell(PreparedStatement stmt) throws SQLException {
        rs = stmt.executeQuery();
        rsmd = rs.getMetaData();
        for(int i = 1; i <= rsmd.getColumnCount(); i++)
            System.out.print("| " + rsmd.getColumnLabel(i) + " ");
        System.out.println("|");
        while (rs.next()) {
            for(int i = 1; i <= rsmd.getColumnCount(); i++) 
                System.out.print("| " + rs.getString(i) + " ");
            System.out.println("|");
        }
    }

}
