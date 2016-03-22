import UML.UserDBManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Author:      Peter Zorzonello
 * File:        logInGUI.java
 * Version:     1.0
 * Last Update: 4/28/15
 *
 * This is a GUI for the user to log in
 */
public class LogInGUI {
    public JPanel mainPanel1;
    private JTextField userIdText;
    private JTextField userPassTest;
    private JButton loginBtn;
    public JFrame frame;
    public static String employee;



    public LogInGUI() throws SQLException {

        final UserDBManager userDB = new UserDBManager();
        userDB.createUsersTable();

        userIdText.setName("User ID");
        userPassTest.setName("Password");

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Validator.IsPresent(userIdText) && Validator.IsPresent(userPassTest)){
                    String id = userIdText.getText();
                    String pass = userPassTest.getText();

                    try {
                        if(userDB.authUser(id,pass)){
                            frame.dispose();
                            employee = id;
                            toEmployee();
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"Incorrect Credentials", "Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }


                }

            }
        });
    }

    private static void toLoginGUI() throws SQLException {
        LogInGUI gui2 = new LogInGUI();
        gui2.frame = new JFrame("logInGUI");
        gui2.frame.setContentPane(gui2.mainPanel1);
        gui2.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui2.frame.pack();

        //make the GUI not resizable and spawn in the center of the screen
        gui2.frame.setLocationRelativeTo(null);
        gui2.frame.setResizable(false);
        gui2.frame.setVisible(true);
    }

    private void toEmployee() throws SQLException{

        EmployeeGUI gui1 = new EmployeeGUI();
        gui1.frame = new JFrame("EmployeeGUI");
        gui1.frame.setContentPane(gui1.panel1);

        //keep the minimize and close buttons off the frame
        gui1.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gui1.frame.setUndecorated(true);
        gui1.frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        //make the frame visible and not resizable
        gui1.frame.pack();
        gui1.frame.setLocationRelativeTo(null);
        gui1.frame.setResizable(false);
        gui1.frame.setVisible(true);


    }

    public static void main(String[] args) throws SQLException {
        toLoginGUI();
    }


}
