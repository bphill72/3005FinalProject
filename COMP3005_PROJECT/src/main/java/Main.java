import javax.xml.transform.Result;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

//Main Class
public class Main
{
    //Global variables used to access db
    static final String url = "jdbc:postgresql://localhost:5432/project_db";
    static final String user = "postgres";
    static final String password = "admin";

    private static Connection connection;

    //Main function to call methods
    public static void main(String[] args)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,user,password);
            
            //newMem();
            processBilling(1);
            

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

   //function to get user input for new member
   public static void newMem(){

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter first name:");
        String firstName = scanner.nextLine();
        
        System.out.println("Enter last name:");
        String lastName = scanner.nextLine();
        
        //set fee for new members
        int fee = 30;
        
        System.out.println("Enter registration date int this format (yyyy-mm-dd):");
        String regDateStr = scanner.next();
        Date regDate = Date.valueOf(regDateStr);
        
        System.out.println("Enter weight:");
        int weight = scanner.nextInt();
        
        System.out.println("Enter height:");
        int height = scanner.nextInt();
        
        System.out.println("Enter username:");
        String username = scanner.next();
        
        System.out.println("Enter password:");
        String password = scanner.next();
        
        // Call the function with data
        memberRegistration(firstName, lastName, fee, regDate, weight, height, username, password);

    }

    //function for adding student to table
    public static void memberRegistration(String first_name, String last_name, int fee, Date registration_date, int weight, int height, String user, String pass) {
        try {
            // statement sent to db to create new member
            PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name, last_name, fee, registration_date) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            // setting all the values
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setInt(3, fee);
            statement.setDate(4, registration_date);
            
            // execute the statement
            int rows = statement.executeUpdate();

            //error checking
            if (rows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            //retrieve the generated member ID
            ResultSet pKeys = statement.getGeneratedKeys();
            if (pKeys.next()) {
                int memberId = pKeys.getInt(1);
                
                // create profile for member
                PreparedStatement pStatement = connection.prepareStatement("INSERT INTO profiles (member_id, mem_weight, mem_height, user_name, user_pass) VALUES (?, ?, ?, ?, ?)");
                
                pStatement.setInt(1, memberId);
                pStatement.setInt(2, weight); 
                pStatement.setInt(3, height); 
                pStatement.setString(4, user);
                pStatement.setString(5, pass); 
                pStatement.executeUpdate();

                //generate a bill for the new member
                generateBill(memberId, fee, registration_date);
                
            } else {
                throw new SQLException("Creating member failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());  
        }
    }

    // Function to generate a bill for a new member
    private static void generateBill(int memberId, int amountOwed, Date regDate) {
        try {
            PreparedStatement billState = connection.prepareStatement("INSERT INTO billing (member_id, amount_owed, payment_date_due, payment_status) VALUES (?, ?, ?, ?)");
            billState.setInt(1, memberId);
            billState.setInt(2, amountOwed);
            //payment date set to 1 month
            billState.setDate(3, Date.valueOf(regDate.toLocalDate().plusMonths(1)));
            billState.setString(4, "Unpaid");
            billState.executeUpdate();
            
            System.out.println("Bill for member ID: " + memberId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //function for getting user info for bill
    public static void createBill() {
    
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter Member ID:");
        int memId = scanner.nextInt();
        
        System.out.println("Enter Amount Owed:");
        int fee = scanner.nextInt();
        
        System.out.println("Enter Payment Date Due in this format (YYYY-MM-DD):");
        String paymentDate = scanner.next();
        Date paymentDateDue = Date.valueOf(paymentDate);
        generateBill(memId, fee, paymentDateDue);
        
        scanner.close();
        
    }

    /*
    //function to update profile weight and height, based on profile id provided
    public static void updateProfile(Integer profile_id, Integer mem_weight, Integer mem_height, Integer goal_id, Integer goal_weight, Integer goal_reps, Integer goal_sets)
    {

        //try and catch for any errors
        try
        {
            Class.forName("org.postgresql.Driver");

            //connecting to db
            Connection connection = DriverManager.getConnection(url, user, password);

            //query for updating student email based on id
            String updateQuery = "UPDATE profiles SET mem_weight = ?, mem_height = ? WHERE profile_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, mem_weight);
            preparedStatement.setInt(2, mem_height);
            preparedStatement.setInt(3, profile_id);
            int updatedRows = preparedStatement.executeUpdate();

            String updateGoalQuery = "UPDATE goals SET goal_weight = ?, goal_reps = ?, goal_sets = ? WHERE profile_id = ? AND goal_id = ?";
            PreparedStatement goalStatement = connection.prepareStatement(updateGoalQuery);
            goalStatement.setInt(1, goal_weight);
            goalStatement.setInt(2, goal_reps);
            goalStatement.setInt(3, goal_sets);
            goalStatement.setInt(4, profile_id);
            goalStatement.setInt(5, goal_id);
            int updatedGoalRows = goalStatement.executeUpdate();

            //checking to see if profile and goal were updated
            if (updatedRows > 0 && updatedGoalRows > 0)
            {
                System.out.println("Profile and Goal updated successfully.");
            }

            else
            {
                System.out.println("Profile with ID " + profile_id + " or Goal with ID " + goal_id + " not found.");
            }
        }

        //catch any exception errors
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    */

    //function for displaying dashboard
    public static void dashboard(int memberId) {
        try {

            //statement to get name of member
            PreparedStatement nameState = connection.prepareStatement("SELECT first_name, last_name FROM members WHERE member_id = ?");
            nameState.setInt(1, memberId);
            ResultSet nameRS = nameState.executeQuery();
            
            String fname = "";
            String lname = "";
            if (nameRS.next()) {
                fname = nameRS.getString("first_name");
                lname = nameRS.getString("last_name");
            }
            
            System.out.println("Dashboard for Member: " + fname + " " + lname);
            System.out.println();

            //get goals from the goals table using the profile id of the member id
            PreparedStatement goalState = connection.prepareStatement("SELECT * FROM goals WHERE profile_id IN (SELECT profile_id FROM profiles WHERE member_id = ?)");
            goalState.setInt(1, memberId);
            ResultSet goalRS = goalState.executeQuery();
            
            //display exercise goals
            System.out.println("Exercise, Goals, and Current Achieved:");
            while (goalRS.next()) {
                String exercise = goalRS.getString("exercise");
                int goalWeight = goalRS.getInt("goal_weight");
                int goalReps = goalRS.getInt("goal_reps");
                int goalSets = goalRS.getInt("goal_sets");
                int currentWeight = goalRS.getInt("current_weight");
                int currentReps = goalRS.getInt("current_reps");
                int currentSets = goalRS.getInt("current_sets");
                
                System.out.println("Exercise: " + exercise);
                System.out.println("Goal Weight: " + goalWeight + " lbs");
                System.out.println("Goal Reps: " + goalReps);
                System.out.println("Goal Sets: " + goalSets);
                System.out.println("Current Weight: " + currentWeight + " lbs");
                System.out.println("Current Reps: " + currentReps);
                System.out.println("Current Sets: " + currentSets);
                System.out.println();
            }
            
            //health statistics
            PreparedStatement statsState = connection.prepareStatement("SELECT * FROM profiles WHERE member_id = ?");
            statsState.setInt(1, memberId);
            ResultSet statsRS = statsState.executeQuery();
            
            //display 
            System.out.println("Health Statistics:");
            while (statsRS.next()) {
                int weight = statsRS.getInt("mem_weight");
                int height = statsRS.getInt("mem_height");
                System.out.println("Weight: " + weight + " lbs");
                System.out.println("Height: " + height + " cm");
                System.out.println();
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    public static void equipmentMaintenance(int equipment_id) {
        try{
            Statement statement = connection.createStatement();
            String insertSQL = "UPDATE equipment SET last_maintained_date=? WHERE equipment_id=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                pstmt.setInt(2, equipment_id);
                pstmt.executeUpdate();
                System.out.println("Updated the equipment's last maintained by date");
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}
    }

    public static void trainerSchedule(int trainer_id) {
        try{
            Statement statement = connection.createStatement();
            String insertSQL = "SELECT * FROM trainers WHERE trainer_id=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, trainer_id);
                pstmt.executeQuery();
                System.out.println("Here are the trainer's availabilites: ");

                ResultSet results = pstmt.getResultSet();

                // Prints each attribute of a student seperated by a tab
                while (results.next()) {
                    System.out.print(results.getString("availability_id") + "\t");
                    System.out.print(results.getString("trainer_id") + "\t");
                    System.out.print(results.getString("start_time") + "\t");
                    System.out.print(results.getString("end_time") + "\t");
                    System.out.println(results.getString("week_day"));
                }
                results.close();

                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {}

        Scanner scanner = new Scanner(System.in);

        System.out.println("Select the date you wish to change your availability for:");
        System.out.println("[1] Sunday");
        System.out.println("[2] Monday");
        System.out.println("[3] Tuesday");
        System.out.println("[4] Wednesday");
        System.out.println("[5] Thursday");
        System.out.println("[6] Friday");
        System.out.println("[7] Saturday");
        int day = scanner.nextInt();

        String weekday = "";

        switch (day) {
            case 1:
                weekday = "Sunday";
                break;
            case 2:
                weekday = "Monday";
                break;
            case 3:
                weekday = "Tuesday";
                break;
            case 4:
                weekday = "Wednesday";
                break;
            case 5:
                weekday = "Thursday";
                break;
            case 6:
                weekday = "Friday";
                break;
            case 7:
                weekday = "Saturday";
                break;
        }
        updateTrainerSchedule(trainer_id, weekday);
    }

    public static void updateTrainerSchedule(int trainer_id, String weekday) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the new start time in the following format: (hh:mm)");
        String start = scanner.nextLine();

        System.out.println("Enter the new end time in the following format: (hh:mm)");
        String end = scanner.nextLine();

        try{
            Statement statement = connection.createStatement();
            String insertSQL = "UPDATE availability SET start_time=?, end_time=? WHERE trainer_id=? AND weekday=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setTime(1, new java.sql.Time(start));
                pstmt.setTime(2, new java.sql.Time(end));
                pstmt.setInt(3, trainer_id);
                pstmt.setString(4, weekday);
                pstmt.executeUpdate();
                System.out.println("Updated the trainer's availability");
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}
    }
    */



    //function for billing
    public static void processBilling(int memberId) {
        try {

            //get billing information
            PreparedStatement billingState = connection.prepareStatement("SELECT * FROM billing WHERE member_id = ?");
            billingState.setInt(1, memberId);
            ResultSet billingRS = billingState.executeQuery();
            
            //billing information
            System.out.println("Billing Information:");
            while (billingRS.next()) {
                int billingId = billingRS.getInt("billing_id");
                int amountOwed = billingRS.getInt("amount_owed");
                String paymentDateDue = billingRS.getString("payment_date_due");
                String paymentStatus = billingRS.getString("payment_status");
                
                System.out.println("Billing ID: " + billingId);
                System.out.println("Amount Owed: $" + amountOwed);
                System.out.println("Payment Date Due: " + paymentDateDue);
                System.out.println("Payment Status: " + paymentStatus);
                System.out.println();
            }

            boolean isBillPaid = billPaid(memberId);
            
            if (isBillPaid) {
                System.out.println("Bill already paid.");
                return;
            }
            
            //payment processing
            System.out.println("Payment processing");
            updatePayment(memberId);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // simulate updating payment 
    private static void updatePayment(int memberId) {
        try {
            PreparedStatement updateState = connection.prepareStatement("UPDATE billing SET payment_status = 'Paid' WHERE member_id = ?");
            updateState.setInt(1, memberId);
            updateState.executeUpdate();
            System.out.println("Payment processed successfully for member ID: " + memberId);
            } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //check if the bill is already paid
    private static boolean billPaid(int memberId) {
        try {
            PreparedStatement billState = connection.prepareStatement("SELECT payment_status FROM billing WHERE member_id = ?");
            billState.setInt(1, memberId);
            ResultSet billRS = billState.executeQuery();
            
            if (billRS.next()) {
                String paymentStatus = billRS.getString("payment_status");
                return paymentStatus.equals("Paid");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; 
    }

    /*
    //function to add a session
    public static void addSession(int roomId, int trainerId, Time startTime, Time endTime, String weekDay, int capacity)
    {
        try (Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "INSERT INTO sessions (room_id, trainer_id, start_time, end_time, week_day, capacity, current) VALUES (?, ?, ?, ?, ?, ?, 1)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, trainerId);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, endTime);
            preparedStatement.setString(5, weekDay);
            preparedStatement.setInt(6, capacity);
            preparedStatement.executeUpdate();
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //function to check if session that wants to be added collides with anything
    public static boolean checkSessions(Time startTime, Time endTime, String weekDay)
    {
        try(Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "SELECT COUNT(*) AS count FROM sessions WHERE week_day = ? AND ((start_time <= ? AND end_time >= ?) OR (start_time >= ? AND start_time < ?) OR (end_time > ? AND end_time <= ?))";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, weekDay);
            preparedStatement.setTime(2, startTime);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, startTime);
            preparedStatement.setTime(5, endTime);
            preparedStatement.setTime(6, endTime);
            preparedStatement.setTime(7, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }
    
    //funciton to print sessions not full
    public static void printSessionsNotFull()
    {
        try(Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "SELECT * FROM sessions WHERE current < capacity";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Sessions Not Full:");

            while (resultSet.next())
            {
                int sessionId = resultSet.getInt("session_id");
                int roomId = resultSet.getInt("room_id");
                int trainerId = resultSet.getInt("trainer_id");
                String startTime = resultSet.getTime("start_time").toString();
                String endTime = resultSet.getTime("end_time").toString();
                String weekDay = resultSet.getString("week_day");
                int capacity = resultSet.getInt("capacity");
                int current = resultSet.getInt("current");

                System.out.printf("Session ID: %d, Room ID: %d, Trainer ID: %d, Start Time: %s, End Time: %s, Week Day: %s, Capacity: %d, Current: %d%n",
                        sessionId, roomId, trainerId, startTime, endTime, weekDay, capacity, current);
            }
        }

        catch (SQLException e)

        {
            e.printStackTrace();
        }
    }
    
    //function to increment group sessions 
    public static void incrementSessionCurrent(int sessionId)
    {
        try (Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "UPDATE sessions SET current = current + 1 WHERE session_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sessionId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0)
            {
                System.out.println("Session incremented");
            }

            else
            {
                System.out.println("Failed to increment session");
            }
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
     */
    /*
    public static void updateSession(int sessionId, int roomId, int trainerId, Time startTime, Time endTime, String weekDay, int capacity, int current)
    {
        try(Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "UPDATE sessions SET room_id = ?, trainer_id = ?, start_time = ?, end_time = ?, week_day = ?, capacity = ?, current = ? WHERE session_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, trainerId);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, endTime);
            preparedStatement.setString(5, weekDay);
            preparedStatement.setInt(6, capacity);
            preparedStatement.setInt(7, current);
            preparedStatement.setInt(8, sessionId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0)
            {
                System.out.println("Schedule updated");
            }

            else
            {
                System.out.println("Schedule update failed");
            }
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    */

    /*
    public static void updateSessionRoom(int sessionId, int newRoomId, Time startTime, Time endTime, String weekDay)
    {
        if(isRoomAvailable(newRoomId, startTime, endTime, weekDay))
        {
            try(Connection connection = DriverManager.getConnection(url, user, password))
            {
                String query = "UPDATE sessions SET room_id = ? WHERE session_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, newRoomId);
                preparedStatement.setInt(2, sessionId);
                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0)
                {
                    System.out.println("Session room updated");
                }

                else
                {
                    System.out.println("Session room update failed");
                }
            }

            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            System.out.println("Room collides with time slot of another");
        }
    }

    private static boolean isRoomAvailable(int roomId, Time startTime, Time endTime, String weekDay)
    {
        try(Connection connection = DriverManager.getConnection(url, user, password))
        {
            String query = "SELECT COUNT(*) AS count FROM sessions WHERE room_id = ? AND week_day = ? AND ((start_time <= ? AND end_time >= ?) OR (start_time >= ? AND start_time < ?) OR (end_time > ? AND end_time <= ?))";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setString(2, weekDay);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, endTime);
            preparedStatement.setTime(5, startTime);
            preparedStatement.setTime(6, endTime);
            preparedStatement.setTime(7, startTime);
            preparedStatement.setTime(8, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                int count = resultSet.getInt("count");
                return count == 0;
            }

        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }
    */


    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while(option != 0) {
            System.out.println("Select one of the following options:");
            System.out.println("[1] Member functions");
            System.out.println("[2] Trainer functions");
            System.out.println("[3] Admin functions");
            System.out.println("[0] Quit");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    memberAccess();
                    break;
                case 2:
                    //trainerFuntions();
                    break;
                case 3:
                    //staffFuntions();
                    break;
            }
        }
    }

    public static void memberAccess() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while(option != 0) {
            System.out.println("Select one of the following options:");
            System.out.println("[1] Login");
            System.out.println("[2] Register");
            System.out.println("[0] Quit");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    login();
                    break;
                case 2:
                    newMem();
                    break;
            }
        }
    }

    public static void login() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter the member's login info in the following format:");
        System.out.println("username password");
        // Getting all attributes from the same line
        String username = scanner.next();
        String password = scanner.next();

        try{
            Statement statement = connection.createStatement();
            String insertSQL = "SELECT * FROM profiles WHERE user_name=? AND user_pass=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeQuery();

                ResultSet results = pstmt.getResultSet();

                // Gets the member id
                results.next();
                int member_id = results.getInt("member_id");
                int profile_id = results.getInt("profile_id");
                results.close();
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}

        memberFunctions(member_id, profile_id);
    }

    public static void memberFunctions(int member_id, int profile_id) {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while (option != 0) {
            System.out.println("Select one of the following options:");
            System.out.println("[1] Profile Management");
            System.out.println("[2] Dashboard Display");
            System.out.println("[3] Schedule Management");
            System.out.println("[0] Logout");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    newProfileInfo(profile_id);
                    break;
                case 2:
                    dashboard(memberId);
                    break;
                case 3:
                    //schedule management
                    break;
            }
        }
    }

    public static void newProfileInfo(int profile_id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the member's new weight and height in the following format:");
        System.out.println("weight height");
        int weight = scanner.nextInt();
        int height = scanner.nextInt();
        newGoals(rofile_id, weight, height);
    }

    public static void newGoals(int profile_id, int weight, int height) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the member's new weight, reps, and set goals in the following format:");
        System.out.println("weight reps sets");
        int g_weight = scanner.nextInt();
        int reps = scanner.nextInt();
        int set = scanner.nextInt();

        try{
            Statement statement = connection.createStatement();
            String insertSQL = "SELECT goal_id FROM goals WHERE profile_id=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, profile_id);
                pstmt.executeQuery();

                ResultSet results = pstmt.getResultSet();

                // Gets the goal id
                results.next();
                int goal_id = results.getInt("goal_id");
                results.close();
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}

        updateProfile(profile_id, weight, height, goal_id, g_weight, reps, sets);

    }

    public static void trainerFunctions() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while(option != 0) {
            System.out.println("Select one of the following options:");
            System.out.println("[1] Schedule Management");
            System.out.println("[2] Member Profile Viewing");
            System.out.println("[0] Return to Main Menu");
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Input the trainer's id:");
                    int trainer_id = scanner.nextInt();
                    trainerSchedule(trainer_id);
                    break;
                case 2:
                    System.out.println("Input the member's name in the following format:");
                    System.out.println("first_name last_name");
                    String fname = scanner.next();
                    String lname = scanner.next();

                    try{
                        Statement statement = connection.createStatement();
                        String insertSQL = "SELECT member_id FROM members WHERE first_name=? AND last_name=?";
                        // Creating a prepared statement for security
                        try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                            pstmt.setString(1, fname);
                            pstmt.setString(2, lname);
                            pstmt.executeQuery();

                            ResultSet results = pstmt.getResultSet();

                            // Gets the member id
                            results.next();
                            int member_id = results.getInt("member_id");
                            results.close();
                            pstmt.close();
                            statement.close();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    } catch (SQLException e) {}

                    dashboard(int member_id);
                    break;
            }
        }
    }
}
