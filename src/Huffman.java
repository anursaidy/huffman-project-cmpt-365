import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;

public class Huffman {
    BufferedImage image;
    Color[] colorData;
    ArrayList<HuffmanNode> huffmanNodeList = new ArrayList<HuffmanNode>();
    HashMap<Integer, String> codes = new HashMap<Integer, String>();
    boolean predictor = false;

    public Huffman(BufferedImage image, boolean predictor) {
        this.image = image;
        this.colorData = new Color[image.getWidth() * image.getHeight()];
        this.predictor = predictor;
        if(this.predictor==false){
            getPixelData();
        }
        else{
            predictor();
        }
        huffmanTree();
        huffmanCodes();
        compressionRatio();
    }


  public class HuffmanNode{
       private int data; //our rgb val
       private int frequency;
       private HuffmanNode leftChild;
       private HuffmanNode rightChild;

      //Huffman Node has a left and right node and each node has their frequency
      public HuffmanNode(int data, int frequency) {
          this.data = data;
          this.frequency = frequency;
          this.leftChild = null;
          this.rightChild = null;
      }
      public HuffmanNode(int frequency, HuffmanNode leftChild, HuffmanNode rightChild) {//internal node
          this.data = 0; //not needed
          this.frequency = frequency;
          this.leftChild = leftChild;
          this.rightChild = rightChild;
      }
  }


    private void getPixelData(){
        int index=0;
        for (int i =0; i<image.getHeight();i++){
            for(int j=0;j<image.getWidth();j++){
                Color c = new Color(image.getRGB(j, i));

                colorData[index]=c;
                index++;
            }
        }
    }

    //Directly on pixels
    private HashMap<Integer, Integer> getFrequencies(){
        HashMap<Integer, Integer> freqHashMap = new HashMap<Integer,Integer>();

        for(int i=0;i< colorData.length;i++){
            //Gets the color's rgb as a single integer i.e. because int =red << 16|green<< 8|blue;
            //(Hash merge referenced from w3schools): RGB as a single integer as key and val if the key exists incremement it
            freqHashMap.merge(colorData[i].getRGB(), 1, Integer::sum);
        }
        return freqHashMap;
    }


    private void huffmanTree(){
        //Steps of huffman tree: Get the nodes frequencies and first sort in ascending order of frequencies
        setUpHuffmanNodes();

        //Two smallest frequencies of the whole list and add them together to make a new node
        //Since ascending order first 2 elements in list is always the 2 smallest

        while(this.huffmanNodeList.size() >1){
            //have to resort nodes after adding a new internal one
            sortHuffmanNodes();

                HuffmanNode leftChild = this.huffmanNodeList.get(0);
                HuffmanNode rightChild = this.huffmanNodeList.get(1);

                int sumFrequency = leftChild.frequency + rightChild.frequency;

                //now they are not part of the set we are looking at
                this.huffmanNodeList.remove(0);
                this.huffmanNodeList.remove(0);

                //now make a new node with the combined sum
                HuffmanNode internalNode = new HuffmanNode(sumFrequency, leftChild, rightChild);
                this.huffmanNodeList.add(internalNode);
            }


    }


    private void setUpHuffmanNodes(){
        //Make a nodelist of their frequencies
        HashMap<Integer, Integer> freqHashMap = getFrequencies();

        //Reference geeksforgeeks traversing a hashmap
        for(Map.Entry<Integer,Integer>frequency: freqHashMap.entrySet()){
            HuffmanNode huffmanNode = new HuffmanNode(frequency.getKey(), frequency.getValue());
            this.huffmanNodeList.add(huffmanNode);
        }
        sortHuffmanNodes();
    }

    private void sortHuffmanNodes(){
        //have to use comparator since custom data type
        Collections.sort(this.huffmanNodeList, new Comparator<HuffmanNode>() {
            @Override
            public int compare(HuffmanNode h1, HuffmanNode h2) {
                return h1.frequency-h2.frequency;
            }
        });
    }


    private void huffmanCodes(){
        int rootIndex = this.huffmanNodeList.size() - 1;
        HuffmanNode root = this.huffmanNodeList.get(rootIndex);

        //Store rgb value with its code
        HashMap<Integer, String> codeHashMap = new HashMap<Integer, String>();

        Stack<HuffmanNode> huffmanNodeStack = new Stack<HuffmanNode>();
        huffmanNodeStack.push(root);

        Stack<String> codeStack = new Stack<>();
        codeStack.push("");
        while(!huffmanNodeStack.empty()) {

                HuffmanNode huffmanNode = huffmanNodeStack.pop();
                String code = codeStack.pop();

                //Node is a leaf we reached end, save the code
                if (huffmanNode.leftChild == null && huffmanNode.rightChild ==null) {
                    codeHashMap.put(huffmanNode.data, code);
                }
                 if (huffmanNode.leftChild != null) {
                    huffmanNodeStack.push(huffmanNode.leftChild); //Visit left child
                    codeStack.push(code + "0");
                }
                 if(huffmanNode.rightChild != null){
                    huffmanNodeStack.push(huffmanNode.rightChild);//Visit right child
                    codeStack.push(code + "1");
                }
        }
        codes=codeHashMap;
    }

    //Jpeg Lossless Predictor
    private void predictor(){
        int index=0;
        for (int i =0; i<image.getHeight();i++){
            for(int j=0;j<image.getWidth();j++){
                Color x = new Color(image.getRGB(j, i));

                int a = (j > 0) ?  new Color(image.getRGB(j-1, i)).getRGB():0;
                int b =(i > 0)? new Color(image.getRGB(j, i-1)).getRGB():0;
                int c = (i>0 && j>0)?new Color(image.getRGB(j-1, i-1)).getRGB():0;


                int predicted = a+b-c;

                int residual = x.getRGB() -predicted;


                colorData[index]= new Color(residual);
                index++;
            }
        }
    }

    public double compressionRatio(){
        int uncompressedSize = image.getWidth() * image.getHeight() *24;

        HashMap<Integer, Integer> pixelFrequencies = getFrequencies();
        int compressedSize = 0;
        for (Map.Entry<Integer, Integer> entry : pixelFrequencies.entrySet()) {
            int rgb = entry.getKey();
            int frequency = entry.getValue();

            String code = codes.get(rgb);
            int codeLength = code.length();

            // Average code length formula
            compressedSize += frequency * codeLength;
        }


        double compressionRatio = (double) uncompressedSize / compressedSize;

        return compressionRatio;

    }









}
