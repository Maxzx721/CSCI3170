import java.sql.*;
import java.util.Scanner;

public class App {
    final static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db19?autoReconnect=true&useSSL=false";
    final static String dbUsername = "Group19";
    final static String dbPassword = "CSCI3170";

    final static String[] tableNames = {"category", "manufacturer", "part", "salesperson", "transaction"};

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
        in.close();

        switch (choice) {
            case 1:
                administrator(conn);

            case 2:
                salesPerson(conn);

            case 3:
                manager(conn);

            default:
                return;
        }
        
    }

    public static void administrator(Connection conn) throws SQLException {
        
        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for administrator menu-----");
        System.out.println("What kinds of operation would you like to perorm?");
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
                    conn.prepareStatement("CREATE TABLE category (cID INTEGER NOT NULL, cName VARCHAR(20) NOT NULL, PRIMARY KEY (cid))"),
                    conn.prepareStatement("CREATE TABLE manufacturer (mID INTEGER NOT NULL, mName VARCHAR(20) NOT NULL, mAddress VARCHAR(50) NOT NULL, mPhoneNumber INTEGER NOT NULL, PRIMARY KEY (mID))"),
                    conn.prepareStatement("CREATE TABLE part (pID INTEGER NOT NULL, pName VARCHAR(20) NOT NULL, pPrice INTEGER NOT NULL, mID INTEGER NOT NULL, cID INTEGER NOT NULL, pWarrantyPeriod INTEGER NOT NULL, pAvailableQuantity INTEGER NOT NULL, PRIMARY KEY (pID))"),
                    conn.prepareStatement("CREATE TABLE salesperson (sID INTEGER NOT NULL, sName VARCHAR(20) NOT NULL, sAddress VARCHAR(50) NOT NULL, sPhoneNumber INTEGER NOT NULL, sExperience INTEGER NOT NULL, PRIMARY KEY (sID))"),
                    conn.prepareStatement("CREATE TABLE transaction (tID INTEGER NOT NULL, pID INTEGER NOT NULL, sID INTEGER NOT NULL, integer INTEGER NOT NULL, tDate DATE NOT NULL, PRIMARY KEY (tID))")
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
                System.out.print("\nType in the Source Data Folder Path: ");
                String path = in.nextLine();

                in.close();
                System.out.println("Processing...Done! Data is inputted to the database!");
                break;

            case 4:
                System.out.print("Whih table would you like to show: ");
                String table = in.nextLine();
                
                stmt = conn.prepareStatement("SELECT * FROM " + table);
                printShell(stmt);
                in.close();
                break;
        
            default:
                in.close();
                menu(conn);
        }

    }

    public static void salesPerson(Connection conn) throws SQLException {

        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for salesperson menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Search for parts");
        System.out.println("2. Sell a part");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");

        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:
                System.out.println("Choose the Search criterion:\n1. Part Name\n2. Manufacturer Name");
                System.out.print("Choose the search criterion: ");
                choice = in.nextInt(); 
                System.out.print("Choose the search criterion: ");
                choice = in.nextInt();
                System.out.print("Type in the Search Keyword: ");
                String keyword = in.nextLine();
                System.out.println("Choose ordering:\n1. By price, ascending order\n2. By price, descending order");
                System.out.print("Choose the search criterion: ");
                choice = in.nextInt();
                
                break;
            
            case 2:
                System.out.print("Enter The Part ID: ");
                choice = in.nextInt();
                System.out.print("Enter The Salesperon ID: ");
                int choice1 = in.nextInt();

                System.out.println("");
                break;
        
            default:
                
                menu(conn);
        }

    }
    
    public static void manager(Connection conn) throws SQLException {

        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for manager menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. List all salespersons");
        System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
        System.out.println("3. Show the total sales value of each manufacturer");
        System.out.println("4. Show the N most popular part");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");

        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:
                System.out.println("Choose ordering:\n1. By ascending order\n2. By descending order");
                System.out.print("Choose the list ordering: ");
                choice = in.nextInt();

                stmt = conn.prepareStatement("SELECT sID AS ID, sNAME AS Name, sPhoneNumber AS Mobile Phone, sExperience AS Year of Experience FROM salesperson ORDER BY sExperience " + ((choice == 1) ? "ASC" : "DESC"));
                printShell(stmt);
                break;
            
            case 2:
                System.out.print("Type in the lower bound for years of experience: ");
                String y1 = in.nextLine();
                System.out.print("Type in the upper bound for years of experience: ");
                String y2 = in.nextLine();
                System.out.println("Transaction Record:");

                stmt = conn.prepareStatement("SELECT sID AS ID, sNAME AS Name, sExperience AS Years of Experience, count(TEMP.tempCount) AS Number of Transaction FROM (SELECT COUNT(*) AS tempCount FROM transaction GROUP BY sID) TEMP, salesperson S WHERE TEMP.sID = S.sID AND S.sExperience >= " + y1 + " AND S.sExperience <= " + y2 + " ORDER BY S.sID DESC");
                printShell(stmt);
                System.out.println("End of Query");
                break;
        
            case 3:
                stmt = conn.prepareStatement("SELECT mID AS Manufacture ID, mName AS Manufacturer Name, SUM(pPrice) AS Total Sales Value FROM manufacturer NATURAL JOIN part NATURAL JOIN transaction Group by mID ORDER BY Total Sales Value DESC");
                printShell(stmt);
                System.out.println("End of Query");
                break;
        
            case 4:
                System.out.print("Type in the number of parts: ");
                String N = in.nextLine();
                
                stmt = conn.prepareStatement("SELECT mID AS Manufacturer ID, mName AS Manufacturer Name, count(TEMP.tempCount) AS Number of Transaction FROM (SELECT COUNT(*) AS tempCount FROM transaction NATURAL JOIN part GROUP BY pID) TEMP, manufacturer M WHERE TEMP.mID = M.mID ORDER BY Number of Transaction DESC LIMIT " + N);
                printShell(stmt);
                System.out.println("End of Query");
                break;
        
            default:
                menu(conn);
        }
    }

    // printShell method is used for printing query result to the shell
    public static void printShell(PreparedStatement stmt) throws SQLException {
        rs = stmt.executeQuery();
        rsmd = rs.getMetaData();
        while (rs.next()) {
            for(int i = 1; i < rsmd.getColumnCount(); i++) {
                System.out.print("| " + rs.getString(i) + " ");
            }
            System.out.println("|");
        }
    }

}
