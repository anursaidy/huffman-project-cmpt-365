import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;


public class GUI extends JPanel implements ActionListener {
    private BufferedImage image;
    private JLabel huffmanLabel;
    private JLabel predictorLabel;


    private JLabel label;
    private JFrame frame;
    private JPanel panel;
    private JButton fileButton;
    private JFrame imageFrame;
    private ImagePanel imagePanel;

    private double[][] testMatrix =  {
            {89, 78, 76, 75, 70, 82, 81, 82},
            {122, 95, 86, 80, 80, 76, 74, 81},
            {184, 153, 126, 106, 85, 76, 71, 75},
            {221, 205, 180, 146, 97, 71, 68, 67},
            {225, 222, 217, 194, 144, 95, 78, 82},
            {228, 225, 227, 220, 193, 146, 110, 108},
            {223, 224, 225, 224, 220, 197, 156, 120},
            {217, 219, 219, 224, 230, 220, 197, 151}};

    public GUI() {

        imageFrame = null;
        imagePanel = null;
        frame = new JFrame();
        label = new JLabel("Project 3");
        frame.setTitle("Project 3");
        fileButton = new JButton("Open File");

        fileButton.addActionListener(this);

        JButton dctRowButton = new JButton("Row First DCT");
        dctRowButton.addActionListener(e->{
            processDCT(true);
        });

        JButton dctColButton = new JButton("Col First DCT");
        dctColButton.addActionListener(e->{
            processDCT(false);
        });

        JButton huffmanButton= new JButton("Huffman");
        huffmanButton.addActionListener(e->{
            boolean predictor = false;
            processHuffman(predictor);

        });

        JButton huffmanJpegButton= new JButton("Huffman+Predictor");
        huffmanJpegButton.addActionListener(e->{
            boolean predictor = true;
            processHuffman(predictor);

        });




        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        //Panel
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200));
        panel.setLayout(new GridLayout(0, 1));

        panel.add(label);
        panel.add(fileButton);
        panel.add(dctRowButton);
        panel.add(dctColButton);
        panel.add(huffmanButton);
        panel.add(huffmanJpegButton);
        panel.add(exitButton);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileButton) {
            openFile();
        }
    }


    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();

        //Returns 1 if cancelled, 0 if opened file
        int fileRes = fileChooser.showOpenDialog(null);
        // System.out.println(fileRes);

        //File Open Success
        if (fileRes == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            File inputFile = new File(path);


            if (path.toLowerCase().endsWith(".bmp")) {
                setUpBMP(inputFile);

                panel.revalidate();
                panel.repaint();
            }
        }
    }

    private void setUpBMP(File inputFile) {
        //Reference https://docs.oracle.com/javase/tutorial/2d/images/index.html

        try {
            image = ImageIO.read(inputFile);



            if (image.getType() != BufferedImage.TYPE_INT_RGB  && image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                System.out.println("Image is not 24-bit RGB");
                throw new IllegalArgumentException("Image is not 24-bit RGB");
            }

            displayBMP(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayBMP(BufferedImage image){
        if (imageFrame == null) {
            imageFrame = new JFrame("BMP Image");
            imageFrame.setBackground(Color.BLACK);
            imageFrame.setResizable(false);
            imagePanel = new ImagePanel(image);

           // imageFrame.add(imagePanel);
            JPanel contentPanel = new JPanel(new BorderLayout());

            contentPanel.add(imagePanel, BorderLayout.CENTER);
            JPanel compressionInfoPanel = new JPanel();
            compressionInfoPanel.setLayout(new BoxLayout(compressionInfoPanel, BoxLayout.Y_AXIS));
            JLabel titleLabel = new JLabel("Compression Ratios:");
            
            compressionInfoPanel.add(titleLabel);

            huffmanLabel = new JLabel("Huffman Coding: N/A");
            compressionInfoPanel.add(huffmanLabel);
            huffmanLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

             predictorLabel = new JLabel("Predictor + Huffman: N/A");
            compressionInfoPanel.add(predictorLabel);
            predictorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(compressionInfoPanel, BorderLayout.EAST);

            imageFrame.add(contentPanel);
            imageFrame.pack();
            imageFrame.setVisible(true);

        } else {

            imagePanel.replaceImage(image);
            huffmanLabel.setText(String.format("Huffman Coding: N/A"));
            predictorLabel.setText(String.format("Predictor + Huffman Coding: N/A"));
            imageFrame.pack();
        }

        //If they close the window set imageFrame to null
        imageFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                imageFrame = null;
            }
        });
    }

    private void processDCT(boolean rowFirst){
        double[][] inputMatrix= processInputMatrix();

        if(inputMatrix !=null) {
            DCT dct = new DCT(inputMatrix, rowFirst);
            int[][] transformedMatrix = dct.calculateDCT();


            displayMatrix(transformedMatrix);
        }


    }

    private double[][] processInputMatrix(){
        String testInput = JOptionPane.showInputDialog(frame, "Would you like to use the test input?");

        if(testInput==null){
            return null;
        }

        if (testInput != null && testInput.equalsIgnoreCase("yes") || testInput.equalsIgnoreCase("y")) {
            return testMatrix;
        }
        else {
            String input = JOptionPane.showInputDialog(frame, "Enter the matrix size (N, between 2 and 8):");
            int N = Integer.parseInt(input);
            if (N < 2 || N > 8) {
                throw new NumberFormatException("Matrix size must be between 2 and 8.");
            } else {
                return createMatrix(N);
            }
        }
    }

    //Reference: Java oracle
    private double[][] createMatrix(int N){
        String[] colNames = new String[N];
        for (int i = 0; i < N; i++) {
            colNames[i] = "Col " + (i + 1);
        }

        Object[][] data = new Object[N][N];

        JTable table = new JTable(data, colNames);
        JScrollPane scrollPane = new JScrollPane(table);

        int option = JOptionPane.showConfirmDialog(frame, scrollPane, "Input Matrix Values", JOptionPane.OK_CANCEL_OPTION);


        double[][] inputMatrix = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Object value = table.getValueAt(i, j);
                if(value==null){
                    value=0.0;
                }
                if(Double.parseDouble(value.toString()) < 0 || Double.parseDouble(value.toString()) >255){
                    throw new NumberFormatException("Value must be in between 0 and 255");
                }
                inputMatrix[i][j] = Double.parseDouble(value.toString());
            }
        }
        return inputMatrix;
    }


    private void displayMatrix(int[][] matrix) {

        String[] colNames = new String[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            colNames[i] = "Col. " + i;
        }

        Object[][] data = new Object[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                data[i][j] = matrix[i][j];
            }
        }

        JTable jTable = new JTable(data, colNames);
        jTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(jTable);


        JFrame matrixFrame = new JFrame("DCT Matrix");
        matrixFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        matrixFrame.add(scrollPane);

        matrixFrame.pack();
        matrixFrame.setVisible(true);

    }




    private void processHuffman(boolean predictor) {
        if (image == null) {
            JOptionPane.showMessageDialog(null, "Please open a BMP image first");
        }
        else {
            //Regular Huffman Compression Ratio
            if (predictor == false) {
                Huffman huffman = new Huffman(this.image, predictor); // Without predictor
                double huffmanRatio = huffman.compressionRatio();
                huffmanLabel.setText(String.format("Huffman Coding: %.2f", huffmanRatio));
            } else {
                //Predictor + Huffman Compression Ratio
                Huffman huffmanPredictor = new Huffman(this.image, predictor);
                double predictorRatio = huffmanPredictor.compressionRatio();
                predictorLabel.setText(String.format("Predictor + Huffman: %.2f", predictorRatio));
            }


        }

    }



}



