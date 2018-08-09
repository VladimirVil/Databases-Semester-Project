/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Beleg: Datenbank
 * @author Vilenchik Vladimir, Abu Bostan
 * @author s0556191   s0556010
 * @version 1.0
 * Datum: 05.01.2018
 */
public class SQLManagement {

    static String db_url = "jdbc:postgresql://db.f4.htw-berlin.de:5432/_s0556191__handel";
    static String username = "_s0556191__handel_generic";
    //static String username = "s0556191";
    /*
    Full privileges added on the server side:
        GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO _s0556191__handel_generic;
        GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO _s0556191__handel_generic;   */
    static String password = "12345678";
    
    static Statement stmt = null;
    static Connection dbcon = null;
    static ResultSet rs = null;
    static ResultSetMetaData meda = null;

    public static void main(String args[]) {

        try {
            Class.forName("org.postgresql.Driver");
            //Class.forName("com.postgresql.jdbc.Driver");
            
            

            // creating a connection to the databae 
            dbcon = DriverManager.getConnection(db_url, username, password);
            // creating a statement object for the queries 
            stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            menu();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Prints the whole table 
     * @throws SQLException
     */
    public static void provideTable(int variante) throws SQLException {
        rs = stmt.executeQuery("select * from filiale");
        meda = rs.getMetaData();

        int num_columns = meda.getColumnCount();
        //System.out.println("Anzahl Splaten: " + num_columns);

        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.println("FID           Standort        Strasse       Haus Nr.     PLZ           M.umsatz    Region");
        System.out.println("------------------------------------------------------------------------------------------");
        while (rs.next()) {
            for (int i = 1; i <= num_columns; i++) {
                System.out.printf("%-15s", rs.getString(i));
            }
            System.out.print("\n");
        }

        System.out.println("---------------------------------------------------------------------------------------");
        if (variante == 1) {
            menu();
        }
    }

    /**
     * Addition of an entry 
     *
     * @throws SQLException
     */
    public static void add_element() throws SQLException {
        Scanner fragen = new Scanner(System.in);

        System.out.print("\n Filial ID: ");
        while (fragen.hasNextInt() != true) {
            System.out.print(" *** Error: Only integers! \n");
            System.out.print("Filial ID: ");
            fragen.nextLine();
        }
        int filID = fragen.nextInt();
        fragen.nextLine();

        System.out.print("Standort: ");
        String standort = fragen.nextLine();

        System.out.print("Strasse: ");
        String strasse = fragen.nextLine();

        System.out.print("Haus Nummer: ");
        while (fragen.hasNextInt() != true) {
            System.out.print("  *** Error: Only integers! \n");
            System.out.print("Haus Nummer: ");
            fragen.nextLine();
        }
        int hausNum = fragen.nextInt();
        fragen.nextLine();

        System.out.print("PLZ: ");
        while (fragen.hasNextInt() != true) {
            System.out.print("  *** Error: Only integers! \n");
            System.out.print("PLZ: ");
            fragen.nextLine();
        }
        int plz = fragen.nextInt();
        fragen.nextLine();

        System.out.print("Monatsumsatz: ");
        while (fragen.hasNextInt() != true) {
            System.out.print("  *** Error: Only integers! \n");
            System.out.print("Monatsumsatz: ");
            fragen.nextLine();
        }
        int monatsUmsatz = fragen.nextInt();
        fragen.nextLine();

        System.out.print("Region: ");
        while (fragen.hasNextInt() != true) {
            System.out.print("  *** Error: Only integers! \n");
            System.out.print("Region: ");
            fragen.nextLine();
        }
        int region = fragen.nextInt();
        fragen.nextLine();

        try {
            String update = "INSERT INTO Filiale (FID, Standort, Strasse, Nr, PLZ, Monatsumsatz, zuRegion)" + "VALUES ("+filID+", '"+standort+"', '"+strasse+"', "+hausNum+", "+plz+", "+monatsUmsatz+", "+region+")";
            //stmt.executeUpdate("INSERT INTO filiale " + "VALUES (" + filID + ", '" + standort + "', '" + strasse + "', " + hausNum + ", '" + plz + "', '" + monatsUmsatz + "', '" + region + "')");
            stmt.executeUpdate(update);
            System.out.print("Done! \n");
        } catch (SQLException ex) {
            System.out.println("\n *** Error: You provided wrong data, please try again");
        }
        menu();
    }

    /**
     * Delete of an entry
     *
     * @throws SQLException
     */
    public static void delete_element() throws SQLException {
        provideTable(0);

        Scanner check = new Scanner(System.in);
        System.out.print("\n Which entry would you like to delete(please provide only the id of the filial)?: ");
        while (check.hasNextInt() != true) {
            System.out.print(" *** Error: Only integers\n");
            System.out.print("FIlial ID: ");
            check.nextLine();
        }
        int fid = check.nextInt();

        String query = "delete from filiale where fid = ?";
        PreparedStatement preparedStmt = dbcon.prepareStatement(query);
        preparedStmt.setInt(1, fid);

        // execute the preparedstatement
        try {
            preparedStmt.execute();
            System.out.print("Done! \n");
        }
        catch (SQLException e)
        {
            System.out.println("You cannot delete this entry, it is connected with other tables");
        }

        //System.out.print("Done! \n");
        menu();
    }

    /**
     * Navigation 
     *
     * @throws SQLException
     */
    public static void navigation() throws SQLException {

        Scanner nav = new Scanner(System.in);

        System.out.print("\n For navigation use: n (next), p (previous), q (quit): ");
        String nv = nav.nextLine();
        switch (nv) {
            case "n":
                rs.next();
                singleReadout();
                break;
            case "p":
                rs.previous();
                singleReadout();
                break;
            case "q":
                menu();
                break;
            default:
                System.out.print("\n *** Error! \n");
                navigation();
        }

    }

    private static void singleReadout() throws SQLException {
        if (rs.isBeforeFirst()) {
            System.out.println("\n *** Already was the beginning, the list moves to the end ");
            rs.last();
        }

        if (rs.isAfterLast()) {
            System.out.println("\n *** Already was the end, the list moves to the beginning");
            rs.first();
        }
        System.out.println("\n---------------------------------------------------------------------------------------");
        for (int i = 1; i <= 6; i++) {
            System.out.printf("%-15s", rs.getString(i));
        }
        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.print("\n");

        navigation();
    }

    private static void clear() throws SQLException {
        rs = stmt.executeQuery("select * from filiale");
        meda = rs.getMetaData();
    }
    
    
     /**
     * Menu
     * @throws SQLException 
     */
    public static void menu() throws SQLException{
        Scanner menu = new Scanner(System.in);
        System.out.println("\n +++ MENU Filiale +++");
        System.out.println("(1) Provide the full table (filiale) ");
        System.out.println("(2) Add an entry");
        System.out.println("(3) Delete en entry");
        System.out.println("(4) Show the table entry by entry");
        clear();
        System.out.print("Your choice: ");
        
        if(menu.hasNextInt()){
            int wahl = menu.nextInt();
        
            switch(wahl){
                case 1: provideTable(1); break;
                case 2: add_element(); break;
                case 3: delete_element(); break;
                case 4: navigation(); break;
                default: System.out.println("\n*** Error: Please choose from a menu"); menu();
            }
        }else{
            //in case no int 
            System.out.println("\n*** Error: Please pay attention");menu();
        }
    }

}
