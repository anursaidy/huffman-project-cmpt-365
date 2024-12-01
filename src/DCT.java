public class DCT {
    double[][] matrix;
    int N; //length of matrix
    boolean rowFirst=false;

    public DCT(double[][] matrix, boolean rowFirst){

    this.matrix = matrix;
    this.N= matrix.length;
    this.rowFirst = rowFirst;
    }

    private double a(int i){
        if(i==0){
            return Math.sqrt(1.0/N);
        }
        else{
            return Math.sqrt(2.0/N);
        }
    }

    private double[][] dct1DRows(double[][] inputMatrix){
        double F_uv;
        double[][] dctMatrixRows = new double[N][N];
        //Run 1D DCT on all rows
        for(int i=0; i< N;i++){
            for(int j=0; j<N;j++){
                F_uv =0;
                //dot product
                for(int k=0;k<N;k++) {
                    //iterate through columns of the row
                    //if rowFirst then the inputMatrix is the original matrix: if colFirst inputMatrix is the dctColMatrix
                    //switched out i with k for the 2i+1
                    F_uv += a(j) * inputMatrix[i][k] * Math.cos(((2 * k + 1) * j * Math.PI) / (2 * N));
                }
                dctMatrixRows[i][j] = F_uv;

            }

        }
        return dctMatrixRows;
    }

    private double[][] dct1DCols(double[][] inputMatrix) {

        double F_uv;
        double[][] dctMatrixCol = new double[N][N];

        //Run 1D DCT on all cols after the doing the rows and using its result
        for(int i=0; i< N;i++){
            for(int j=0; j<N;j++){
                F_uv =0.0;
                for(int k=0;k<N;k++) {
                    //iterate through columns of the row
                    //switched out j with k for the 2j+1
                    F_uv +=a(i)* inputMatrix[k][j] * Math.cos(((2 * k + 1) * i * Math.PI) / (2 * N));
                }
                dctMatrixCol[i][j] = F_uv;

            }
        }

        return dctMatrixCol;
    }

    public int[][] calculateDCT(){
        if(rowFirst==true) {
            double[][] dctRowsMatrix = dct1DRows(matrix);
            double[][] dctColsMatrix = dct1DCols(dctRowsMatrix);
            return roundToInteger(dctColsMatrix);
        }
        else{
            double[][] dctColsMatrix = dct1DCols(matrix);
            double[][] dctRowsMatrix = dct1DRows(dctColsMatrix);

            return roundToInteger(dctRowsMatrix);
        }


    }

    private int[][] roundToInteger(double[][] matrix){
        int[][] intMatrix = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                intMatrix[i][j] = (int) Math.round(matrix[i][j]);
            }
        }
        return intMatrix;
    }



}
