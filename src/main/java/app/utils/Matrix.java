package app.utils;

/**
 * A matrix class that represents a two-dimensional array of doubles.
 * Internally stored as a one-dimensional array.
 */
public class Matrix extends RawVector<Matrix> {

    private final int cols; // Width
    private final int rows; // Height

    /**
     * Constructs a new matrix with the specified number of rows and columns.
     *
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public Matrix(int rows, int cols) {
        super(new double[rows * cols]);
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Constructs a new matrix with the specified data.
     *
     * @param data the data for the matrix
     */
    public Matrix(double[][] data) {
        super(new double[data.length * data[0].length]);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                //set(i, j, data[i][j]);
                this.components[i * data[i].length + j] = data[i][j];
            }
        }
        this.rows = data.length;
        this.cols = data[0].length;
    }
    
    /**
     * Get the number of rows in the matrix.
     *
     * @return the number of rows
     */
    public int rows() {
        return rows;
    }

    /**
     * Get the number of columns in the matrix.
     *
     * @return the number of columns
     */
    public int cols() {
        return cols;
    }

    /**
     * Get the value at the specified row and column.
     *
     * @param row the row index
     * @param col the column index
     * @return the value at the specified row and column
     */
    public double get(int row, int col) {
        if (row < 0 || row >= rows() || col < 0 || col >= cols())
            throw new IndexOutOfBoundsException("Index out of bounds: " + row + ", " + col);
        return components[row * cols() + col];
    }

    /**
     * Set the value at the specified row and column.
     *
     * @param row the row index
     * @param col the column index
     * @param value the value to set
     */
    public void set(int row, int col, double value) {
        if (row < 0 || row >= rows() || col < 0 || col >= cols())
            throw new IndexOutOfBoundsException("Index out of bounds: " + row + ", " + col);
        components[row * cols() + col] = value;
    }

    /**
     * Multiply this matrix by another matrix.
     * 
     * @param other the other matrix
     * @return the result of the multiplication
     */
    public Matrix mmul(Matrix other) {
        if (this.cols() != other.rows())
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication");
        Matrix result = new Matrix(this.rows(), other.cols());
        for (int i = 0; i < this.rows(); i++) {
            for (int j = 0; j < other.cols(); j++) {
                double sum = 0;
                for (int k = 0; k < this.cols(); k++) {
                    sum += this.get(i, k) * other.get(k, j);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    /**
     * Get a row of the matrix as a vector.
     * 
     * @param row the row index
     * @return the vector representing the row
     */
    public Vector getRow(int row) {
        if (row < 0 || row >= rows())
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        double[] rowData = new double[cols()];
        for (int j = 0; j < cols(); j++) {
            rowData[j] = get(row, j);
        }
        return new Vector(rowData);
    }

    public Vector3 getRow3(int row) {
        if (row < 0 || row >= rows())
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        double[] rowData = new double[cols()];
        for (int j = 0; j < cols(); j++) {
            rowData[j] = get(row, j);
        }
        return new Vector3(rowData);
    }

    /**
     * Set a row of the matrix from a vector.
     * @param col the row index
     * @param vector the vector to set
     */
    public void setRow(int row, RawVector<?> vector) {
        if (row < 0 || row >= rows())
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        if (vector.size() != cols())
            throw new IllegalArgumentException("Vector size does not match matrix column size");
        for (int j = 0; j < cols(); j++) {
            set(row, j, vector.get(j));
        }
    }

    /**
     * Get a column of the matrix as a vector.
     * 
     * @param col the column index
     * @return the vector representing the column
     */
    public Vector getCol(int col) {
        if (col < 0 || col >= cols())
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        double[] colData = new double[rows()];
        for (int i = 0; i < rows(); i++) {
            colData[i] = get(i, col);
        }
        return new Vector(colData);
    }

    /**
     * Set a column of the matrix from a vector.
     * 
     * @param col the column index
     * @param vector the vector to set
     */
    public void setCol(int col, RawVector<?> vector) {
        if (col < 0 || col >= cols())
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        if (vector.size() != rows())
            throw new IllegalArgumentException("Vector size does not match matrix row size");
        for (int i = 0; i < rows(); i++) {
            set(i, col, vector.get(i));
        }
    }

    /**
     * Merge with another matrix, column-wise.
     * 
     * @param other the other matrix
     * @return a new matrix that is the result of merging this matrix with the other
     */
    public Matrix mergeCols(Matrix other) {
        if (this.rows() != other.rows())
            throw new IllegalArgumentException("Matrix dimensions do not match for merging");
        Matrix result = new Matrix(this.rows(), this.cols() + other.cols());
        for (int i = 0; i < this.rows(); i++) {
            for (int j = 0; j < this.cols(); j++) {
                result.set(i, j, this.get(i, j));
            }
            for (int j = 0; j < other.cols(); j++) {
                result.set(i, this.cols() + j, other.get(i, j));
            }
        }
        return result;
    }

    /**
     * Merge with another matrix, row-wise.
     * 
     * @param other the other matrix
     * @return a new matrix that is the result of merging this matrix with the other
     */
    public Matrix mergeRows(Matrix other) {
        if (this.cols() != other.cols())
            throw new IllegalArgumentException("Matrix dimensions do not match for merging");
        Matrix result = new Matrix(this.rows() + other.rows(), this.cols());
        for (int i = 0; i < this.rows(); i++) {
            for (int j = 0; j < this.cols(); j++) {
                result.set(i, j, this.get(i, j));
            }
        }
        for (int i = 0; i < other.rows(); i++) {
            for (int j = 0; j < other.cols(); j++) {
                result.set(this.rows() + i, j, other.get(i, j));
            }
        }
        return result;
    }

    /**
     * Clone the matrix.
     * 
     * @return a new matrix with the same data
     */
    @Override
    public Matrix clone() {
        Matrix clone = new Matrix(rows(), cols());
        System.arraycopy(components, 0, clone.components, 0, components.length);
        return clone;
    }

    /**
     * Get the string representation of the matrix.
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                if(j > 0)
                    sb.append(" ");
                sb.append(get(i, j));
            }
            if(i < rows() - 1)
                sb.append("; ");
        }
        return sb.toString();
    }

}
