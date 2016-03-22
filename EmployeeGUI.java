import UML.Device;
import UML.DeviceDBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Author:  Peter Zorzonello
 * File:    EmployeeGUI.java
 * Version: 2.2
 * Date:    5/1/15
 *
 * This class is the GUI for IT employees to use. From here that can edit devices and list devices.
 * Eventually the functionality will include adding new devices and search for devices based on if they  are checked in
 * or out, the dates they were checked out, who has them, ect.
 */
public class EmployeeGUI {
    private JTabbedPane tabbedPane1;
    public JPanel panel1;
    private JPanel mainPanel;
    private JPanel deviceIO;
    private JButton button1;
    private JTextField serialNumber;
    private JTextField osText;
    private JButton updateButton;
    private JTextField employeeEmail;
    private JRadioButton checkingOutRadioButton;
    private JRadioButton checkingInRadioButton;
    private JTextField pleaseEnterNATextField;
    private JTextField employeeName;
    private JScrollPane scroll1;
    private JTable table1;
    private JPanel deviceAdder;
    private JTextField addNum;
    private JTextField addName;
    private JComboBox comboBox1;
    private JTextField addEmail;
    private JRadioButton checkingInRadioButton1;
    private JRadioButton checkingOutRadioButton2;
    private JButton addButton;
    private JTextField addOs;
    private JButton logOutButton;
    private JComboBox comboBox2;
    private JTable table2;
    private JButton searchButton;
    private JTextField valueTextField;
    private JScrollPane scroll2;
    public JFrame frame;


    public EmployeeGUI() throws SQLException {
        //sets up/connects to the db and the assets table
        final DeviceDBManager deviceDB = new DeviceDBManager();
        deviceDB.createAssetTable();

        //gives names to the texboxes so we can use the Validator API
        osText.setName("Device OS");
        employeeName.setName("Name of person getting device");
        employeeEmail.setName("Email");
        serialNumber.setName("Serial Number");

        addOs.setName("Device OS");
        addName.setName("Name of person getting device");
        addEmail.setName("Email");
        addNum.setName("Serial Number");


        //the update button's action listener. When clicked check
        //the required fields and preform the device update
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //make sure the required files are filled
                if(Validator.IsPresent(serialNumber) && Validator.IsPresent(employeeEmail) &&
                        Validator.IsPresent(employeeName) && Validator.IsPresent(osText)){
                    //get the text from the text boxes
                    String serNum = serialNumber.getText();
                    String osV = osText.getText();
                    String email = employeeEmail.getText();
                    String name = employeeName.getText();

                    try {
                        //if the device is preset we can update
                        if(deviceDB.deviceExists(serNum)){
                            //if the device is being checked in many of the fields can be and should be blank
                            if(checkingInRadioButton.isSelected()){
                                osV = "";
                                email ="";
                                name="";
                                String date = "";
                                String it = "";
                                try {
                                    Device d = deviceDB.getDevice(serNum);
                                    String type = d.getDeviceType();
                                    Device updatedDevice = new Device(serNum,type, osV, date, it, email, name, "in");
                                    deviceDB.editDevice(updatedDevice);

                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            //if the device is being checked out the values in the text boxes are important, keep them
                            else {
                                Device d = deviceDB.getDevice(serNum);
                                String type = d.getDeviceType();
                                Device updatedDevice = new Device(serNum,type, osV,
                                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString(),
                                        LogInGUI.employee, email, name,"out");
                                deviceDB.editDevice(updatedDevice);
                            }

                            //alert the user the device was successfully updated
                            //clear the fields so we can begin again
                            JOptionPane.showMessageDialog(null,"Device Updated", "alert", JOptionPane.INFORMATION_MESSAGE);
                            clearTextboxes();
                        }
                        //if the device is not in the db we cannot update it, tell the user
                        else {
                            JOptionPane.showMessageDialog(null,"Device does not exist", "Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        //action listener for the list all devices button
        //when clicked get the list of devices and populate a table
        button1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    table1 = createTable(deviceDB.listDevices());
                    table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    table1.getTableHeader().setReorderingAllowed(false);
                    scroll1.setViewportView(table1);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //action listener for the addButton
        //when this button is clicked add a device to the db
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String addANum, addAType, addAOs, addAName, addAEmail, addAIT, addADate;
                try {
                    if(Validator.IsPresent(addNum)){
                        if(!deviceDB.deviceExists(addNum.getText())){
                            if(checkingInRadioButton1.isSelected()){
                                addANum = addNum.getText();
                                addADate = "";
                                addAEmail = "";
                                addAIT = "";
                                addAName = "";
                                addAOs = "";
                                addAType = comboBox1.getSelectedItem().toString();

                                Device newDevice = new Device(addANum, addAType, addAOs,addADate, addAIT, addAEmail,
                                        addAName, "in");

                                deviceDB.addDevice(newDevice);
                                JOptionPane.showMessageDialog(null,"Device Updated", "alert", JOptionPane.INFORMATION_MESSAGE);
                                clearTextboxes2();
                            }
                            else {
                                if(Validator.IsPresent(addEmail) && Validator.IsPresent(addName) &&
                                        Validator.IsPresent(addOs)){
                                    addANum = addNum.getText();
                                    addADate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
                                    addAEmail = addEmail.getText();
                                    addAIT = LogInGUI.employee;
                                    addAName = addName.getText();
                                    addAOs = addOs.getText();
                                    addAType = comboBox1.getSelectedItem().toString();

                                    Device newDevice = new Device(addANum, addAType, addAOs,addADate, addAIT, addAEmail,
                                            addAName, "out");

                                    deviceDB.addDevice(newDevice);
                                    JOptionPane.showMessageDialog(null,"Device Updated", "alert", JOptionPane.INFORMATION_MESSAGE);
                                    clearTextboxes2();
                                }
                            }

                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Device Already Exists", "Error", JOptionPane.INFORMATION_MESSAGE);
                            clearTextboxes2();
                        }
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });

        //calls the logInGUI and disposes of this GUI
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                frame.dispose();
                try {

                    toLoginGUI();
                } catch (SQLException e1) {

                }

            }
        });

        //when the user presses search the program gets the user input from the GUI
        //and then calls the search function from DeviceDBManager and then makes
        //a table with the returned array list
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Validator.IsPresent(valueTextField)) {
                    String col = comboBox2.getSelectedItem().toString();
                    String val = valueTextField.getText().toString();

                    try {
                        ArrayList<Device> searchDevices = deviceDB.listDeviceByColVal(col, val);
                        if (searchDevices.size() == 0 || searchDevices == null) {
                            JOptionPane.showMessageDialog(null,"No Devices Found", "Error", JOptionPane.INFORMATION_MESSAGE);
                            scroll2.setViewportView(null);
                        } else {
                            table2 = createTable(searchDevices);
                            table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            table2.getTableHeader().setReorderingAllowed(false);
                            scroll2.setViewportView(table2);

                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * This function creates a table based off of the devices in an array list of devices
     *
     * @param devices an array list of devices
     * @return a table
     */
    public static JTable createTable(ArrayList<Device> devices)
    {
        String[] columnNames = {
                "Serial Number",
                "Device Type",
                "OS",
                "Date Out",
                "Employee Email",
                "Employee Name",
                "IT Person",
                "Status"
        };


        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        for (Device d : devices) {
            model.addRow(new String[]{d.getSerialNum().toString(), d.getDeviceType(),d.getOsVersion(),
                    d.getDateOut().toString(), d.getEmailOfRecipiant(), d.getRecipiantName(),
                    d.getItProfessional(), d.getStatus()});
            System.out.println(d.toString());
        }

        JTable table = new JTable(model);
        table.setModel(model);
        table.setFillsViewportHeight(true);

        return table;
    }

    /**
     * Clears the textboxes on the update device panel
     */
    private void clearTextboxes() {
        serialNumber.setText("");
        osText.setText("");
        employeeName.setText("");
        employeeEmail.setText("");
    }

    /**
     * Clears textboxes on the form
     */
    private void clearTextboxes2() {
        addEmail.setText("");
        addName.setText("");
        addNum.setText("");
        addOs.setText("");
    }


    /**
     * This function launches the logInGUI
     *
     * @throws SQLException
     */
    private void toLoginGUI() throws SQLException {

        LogInGUI gui3 = new LogInGUI();
        gui3.frame = new JFrame("logInGUI");

        gui3.frame.setContentPane(gui3.mainPanel1);
        gui3.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui3.frame.pack();

        //make the GUI not resizable and spawn in the center of the screen
        gui3.frame.setLocationRelativeTo(null);
        gui3.frame.setResizable(false);
        gui3.frame.setVisible(true);
    }



}
