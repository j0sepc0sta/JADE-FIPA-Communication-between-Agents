package factory;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientAgent extends Agent {
    private JFrame frame;
    private JTextField clientField, contactField, emailField, addressField;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private List<String[]> data_client;
    File file = new File("clients.csv");
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ClientAgent");
        sd.setName(getLocalName()+"ClientAgent");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] existingAgents = DFService.search(this, dfd);
            if (existingAgents != null && existingAgents.length > 0) {
                System.out.println("Client Agent already registered.");
            } else {
                DFService.register(this, dfd);
                System.out.println("Client Agent registered successfully.");
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            showGui();
            loadData();
        });
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        String clientName = msg.getContent();
                        boolean clientExists = false;
                        for (String[] rowData : data_client) {
                            if (rowData[1].equals(clientName)) {
                                clientExists = true;
                                break;
                            }
                        }
                        ACLMessage reply = msg.createReply();
                        if (clientExists) {
                            reply.setPerformative(ACLMessage.CONFIRM);
                            reply.setContent("Client exists");
                        } else {
                            reply.setPerformative(ACLMessage.FAILURE);
                            reply.setContent("Client doesn't exist");
                        }
                        send(reply);
                    }
                } else { block();}
            }
        });
    }
    public ClientAgent() {
        frame = new JFrame("Client Agent");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                addBehaviour(new OneShotBehaviour() {
                    public void action() {
                        myAgent.doDelete();
                    }
                });
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2,0,10));
        inputPanel.add(new JLabel("Client Name:"));
        clientField = new JTextField(50);
        clientField.setPreferredSize(new Dimension(500, 20));
        inputPanel.add(clientField);
        inputPanel.add(new JLabel("Contact:"));
        contactField = new JTextField(50);
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("E-mail:"));
        emailField = new JTextField(50);
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField(50);
        inputPanel.add(addressField);
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String clientName = this.clientField.getText();
            String contact = contactField.getText();
            String email = emailField.getText();
            String address = addressField.getText();
            boolean clientExists = false;
            for (String[] rowData : data_client) {
                if (rowData[1].equals(clientName)) {
                    clientExists = true;
                    break;
                }
            }
            if (clientExists) {
                JOptionPane.showMessageDialog(frame, "Client Already Exists.", "Duplicate Client", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "New Client added");
                String[] rowData = {"", clientName, contact, email, address};
                model.addRow(rowData);
                data_client.add(rowData);
                for (int i = 0; i < model.getRowCount(); i++) {
                    model.setValueAt(i + 1, i, 0);}
                saveData();
                clearFields();
            }
        });
        inputPanel.add(addButton);
        panel.add(inputPanel, BorderLayout.NORTH);
        model = new DefaultTableModel(new String[]{"Client Number","Client Name", "Contact","Email", "Address"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton clearAllButton = new JButton("Clear all");
        clearAllButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear all rows from the table?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                model.setRowCount(0);
                data_client.clear();
                saveData();
            }
        });
        buttonPanel.add(clearAllButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(row, row);
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem deleteMenuItem = new JMenuItem("Delete");
                    deleteMenuItem.addActionListener(event -> {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow != -1) {
                            model.removeRow(selectedRow);
                            saveData();
                        }
                    });
                    popupMenu.add(deleteMenuItem);
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });
    }
    private void loadData() {
        data_client = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    data_client.add(values);
                    model.addRow(values);
                    // Loop through the rows of the table model and add sequential numbers to the first column
                    //for (int i = 0; i < model.getRowCount(); i++) {
                    //    model.setValueAt(i + 1, i, 0); // Set sequential number (starting from 1) to the first column (index 0)
                    //}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveData() {
        File file = new File("clients.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        writer.write(value.toString());
                    }
                    if (j < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        frame.setLocation(centerX - frame.getWidth(), centerY - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                JOptionPane.showMessageDialog(frame, "Client Agent Control Panel Terminated");
            }
        });
    }
    private void clearFields() {
        clientField.setText("");
        contactField.setText("");
        addressField.setText("");
        emailField.setText("");
    }

}