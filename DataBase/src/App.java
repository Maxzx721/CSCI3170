import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.io.*;

public class App {
    final static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db19?autoReconnect=true&useSSL=false";
    final static String dbUsername = "Group19";
    final static String dbPassword = "CSCI3170";

    final static String[] tableNames = {"category", "manufacturer", "part", "salesperson", "transaction"};
    final static String[] datapath = {"/category.txt","/manufacturer.txt","/part.txt","/salesperson.txt","/transaction.txt"};
    
    public static PreparedStatement stmt;
    public static ResultSet rs;
    public static ResultSetMetaData rsmd;

    public static void main(String[] args) throws Exception {
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
        } catch(Exception x) { 
            System.err.println("Unable to load the driver class!"); 
        }
        Connection conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);

        menu(conn);

        rs.close();
        stmt.close();
        conn.close();
    }

    public static void menu(Connection conn) throws SQLException {

        Scanner in = new Scanner(System.in);
        
        System.out.println("Welcome to sales system!\n");
        System.out.println("-----Main menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                administrator(conn,in);
                break;

            case 2:
                salesPerson(conn,in);
                break;

            case 3:
                manager(conn,in);
                break;

            default:
                System.exit(0);
        }
        
    }

    public static void administrator(Connection conn, Scanner in) throws SQLException {
        
        
        System.out.println("-----Operations for administrator menu-----");
        System.out.println("What kinds of operation would you like to perorm?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delate all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show content of a table");
        System.out.println("5. Return to the main menu");
    
        loop: while(true) {
            System.out.print("Enter Your Choice: ");
            int choice = in.nextInt();
            switch (choice) {
                case 1:
                    PreparedStatement[] stmts = {
                        conn.prepareStatement("CREATE TABLE category (cID INTEGER NOT NULL, cName VARCHAR(20) NOT NULL, PRIMARY KEY (cID))"),
                        conn.prepareStatement("CREATE TABLE manufacturer (mID INTEGER NOT NULL, mName VARCHAR(20) NOT NULL, mAddress VARCHAR(50) NOT NULL, mPhoneNumber INTEGER NOT NULL, PRIMARY KEY (mID))"),
                        conn.prepareStatement("CREATE TABLE part (pID INTEGER NOT NULL, pName VARCHAR(20) NOT NULL, pPrice INTEGER NOT NULL, mID INTEGER NOT NULL, cID INTEGER NOT NULL, pWarrantyPeriod INTEGER NOT NULL, pAvailableQuantity INTEGER NOT NULL, PRIMARY KEY (pID))"),
                        conn.prepareStatement("CREATE TABLE salesperson (sID INTEGER NOT NULL, sName VARCHAR(20) NOT NULL, sAddress VARCHAR(50) NOT NULL, sPhoneNumber INTEGER NOT NULL, sExperience INTEGER NOT NULL, PRIMARY KEY (sID))"),
                        conn.prepareStatement("CREATE TABLE transaction (tID INTEGER NOT NULL, pID INTEGER NOT NULL, sID INTEGER NOT NULL,tDate DATE NOT NULL, PRIMARY KEY (tID))")
                    };
                    for (int i = 0; i < stmts.length; i++) {
                        stmts[i].execute();
                    }
                    System.out.println("Processing...Done! Database is initialized!");
                    break;
                case 2:
                    for (int i = 0; i < tableNames.length; i++) {
                        stmt = conn.prepareStatement("DROP TABLE " + tableNames[i]);
                        stmt.execute();
                    
                    }
                    System.out.println("Processing...Done! Database is removed!"); 
                    break;
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
                            while(data != null){
                                String[] splited = data.split("	");
                                switch (i) {
                                case 0:
                                    stmt = conn.prepareStatement("INSERT INTO category (cID,cName) VALUES (?, ?)");
                                    stmt.setInt(1,Integer.parseInt(splited[0]));
                                    stmt.setString(2, splited[1]); 
                                    stmt.execute();
                                    break;

                                case 1:
                                    stmt = conn.prepareStatement("INSERT INTO manufacturer (mID,mName,mAddress,mPhoneNumber) VALUES (?, ?, ?, ?)");
                                    for(int j = 0; j<4;j++){
                                        if (j!=0||j!=3) {
                                            stmt.setString(j+1, splited[j]); 
                                        }else {
                                            stmt.setInt(j+1,Integer.parseInt(splited[3]));
                                        }
                                    }
                                    stmt.execute();
                                    break;

                                case 2:
                                    stmt = conn.prepareStatement("INSERT INTO part (pID,pName,pPrice,mID,cID,pWarrantyPeriod,pAvailableQuantity) VALUES (?, ?, ?, ?, ?, ?, ?)");
                                    for (int j = 0; j < 7; j++) {
                                        if (j == 1) {
                                            stmt.setString(j+1, splited[j]); 
                                        }else {
                                            stmt.setInt(j+1,Integer.parseInt(splited[j]));
                                        } 
                                    }
                                    stmt.execute();
                                    break;

                                case 3:
                                    stmt = conn.prepareStatement("INSERT INTO salesperson (sID,sName,sAddress,sPhoneNumber,sExperience) VALUES (?, ?, ?, ?, ?)");
                                    for (int j = 0; j < 5; j++) {
                                        if (j == 1 || j == 2) {
                                            stmt.setString(j+1, splited[j]); 
                                        }else {
                                            stmt.setInt(j+1,Integer.parseInt(splited[j]));
                                        }  
                                    }
                                    stmt.execute();
                                    break;

                                case 4:
                                    stmt = conn.prepareStatement("INSERT INTO transaction (tID,pID,sID,tDate) VALUES (?, ?, ?, ?)");
                                    SimpleDateFormat format = new SimpleDateFormat( "MM/dd/yyyy" ); 
                                    java.util.Date tDate = format.parse(splited[3]);
                                    java.sql.Date sqlDate = new java.sql.Date(tDate.getTime());
                                    for (int j = 0; j<4;j++) {
                                        if (j == 3) { 
                                            stmt.setDate(j+1, sqlDate);
                                        }else {
                                            stmt.setInt(j+1, Integer.parseInt(splited[j]));                           
                                        }
                                    }
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
                    break;

                case 4:
                    System.out.print("Which table would you like to show: ");
                    String table = in.next();
                    stmt = conn.prepareStatement("SELECT * FROM " + table);
                    printShell(stmt,"listAll");
                    break;

                case 5:
                    menu(conn);
                    break loop;
                
                default:
                    break;
            }
        }
    }   

    public static void salesPerson(Connection conn, Scanner in) throws SQLException {
        
        System.out.println("-----Operations for salesperson menu-----");
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
                String keyword=in.nextLine();
                System.out.println(keyword);
                System.out.print("Choose ordering:\n1. By price, ascending order\n2. By price, descending order\nChoose the Search criterion:");
                int order = in.nextInt();
                if(choice == 1){
                    stmt = conn.prepareStatement("SELECT pID,pName,mName,cName,pAvailableQuantity,pWarrantyPeriod,pPrice FROM manufacturer NATURAL JOIN part NATURAL JOIN category WHERE pName = '"+keyword+"' ORDER BY pPrice " + ((order == 1) ? "ASC" : "DESC"));
                }else if(choice == 2){
                    stmt = conn.prepareStatement("SELECT pID,pName,mName,cName,pAvailableQuantity,pWarrantyPeriod,pPrice FROM manufacturer NATURAL JOIN part NATURAL JOIN category WHERE mName = '"+keyword+"' ORDER BY pPrice " + ((order == 1) ? "ASC" : "DESC"));
                }
                printShell(stmt,"searchPart");
                System.out.println("End of Query");
                break;
            
            case 2:
                System.out.print("Enter The Part ID: ");
                choice = in.nextInt();
                System.out.println("Enter The Salesperon ID: ");
                int choice1 = in.nextInt();

                System.out.println("");
                break;
        
            default:
                
                menu(conn);
        }

    }
    
    public static void manager(Connection conn, Scanner in) throws SQLException {

        
        System.out.println("-----Operations for manager menu-----");
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
                stmt = conn.prepareStatement("SELECT sID,sName,sPhoneNumber,sExperience FROM salesperson ORDER BY sExperience " + ((choice == 1) ? "ASC" : "DESC"));
                printShell(stmt,"sorting");
                break;
            
            case 2:
                System.out.print("Type in the lower bound for years of experience: ");
                String y1 = in.next();
                System.out.print("Type in the upper bound for years of experience: ");
                String y2 = in.next();
                System.out.println("Transaction Record:");

                stmt = conn.prepareStatement("SELECT S.sID, sName, sExperience, TEMP.tempCount  FROM (SELECT COUNT(*) AS tempCount,sID FROM transaction GROUP BY sID) AS TEMP, salesperson S WHERE TEMP.sID = S.sID AND S.sExperience >= " + y1 + " AND S.sExperience <= " + y2 + " ORDER BY S.sID DESC");
                printShell(stmt,"Exp");
                System.out.println("End of Query");
                break;
        
            case 3:
                stmt = conn.prepareStatement("SELECT mID , mName, SUM(pPrice) AS psum FROM manufacturer NATURAL JOIN part NATURAL JOIN transaction Group by mID ORDER BY psum DESC");
                printShell(stmt,"salevalue");
                System.out.println("End of Query");
                break;
        
            case 4:
                System.out.print("Type in the number of parts: ");
                String N = in.next();
                //stmt = conn.prepareStatement("SELECT mID, mName, count(TEMP.tempCount) AS Number of Transaction FROM (SELECT COUNT(*) AS tempCount FROM transaction NATURAL JOIN part GROUP BY pID) TEMP, manufacturer M WHERE TEMP.mID = M.mID ORDER BY Number of Transaction DESC LIMIT " + N);
                stmt = conn.prepareStatement("SELECT pID,pName,COUNT(*) AS tempCount FROM transaction NATURAL JOIN part GROUP BY pID ORDER BY tempCount DESC LIMIT " + N);
                printShell(stmt,"mostP");
                System.out.println("End of Query");
                break;
        
            default:
                menu(conn);
        }
    }

    // printShell method is used for printing query result to the shell
    public static void printShell(PreparedStatement stmt, String mode) throws SQLException {
        rs = stmt.executeQuery();
        rsmd = rs.getMetaData();
        if(mode == "listAll"){
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.print("| " + rsmd.getColumnName(i) + " ");
            }
            System.out.println("|");
        }else if(mode == "sorting"){
            System.out.print("| ID ");System.out.print("| Name ");System.out.print("| Mobile Phone ");System.out.print("| Years of Experience ");System.out.println("|");
        }else if(mode =="Exp"){
            System.out.println("Transaction Record :");
            System.out.print("| ID ");System.out.print("| Name ");System.out.print("| Years of Experience ");System.out.print("| Number of Transaction ");System.out.println("|");
        }else if(mode == "salevalue"){
            System.out.print("| Manufacturer ID ");System.out.print("| Manufacturer Name ");System.out.print("| Total Sales Value ");System.out.println("|");
        }else if(mode == "mostP"){
            System.out.print("| Part ID ");System.out.print("| Part Name ");System.out.print("| No. of Transaction ");System.out.println("|");
        }else if(mode == "searchPart"){
            System.out.print("| ID ");System.out.print("| Name ");System.out.print("| Manufacturer ");System.out.print("| Category ");System.out.print("| Quantity ");System.out.print("| Warranty ");System.out.print("| Price ");System.out.println("|");
        }
        while (rs.next()) {
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.print("| " + rs.getString(i) + " ");
            }
            System.out.println("|");
        }
    }

}
