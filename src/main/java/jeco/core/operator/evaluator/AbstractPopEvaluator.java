/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeco.core.operator.evaluator;

import java.util.ArrayList;

/**
 *
 * @author José Luis Risco Martín
 */
public abstract class AbstractPopEvaluator {

	/**
	 * Matrix with data of type double
	 */
    protected ArrayList<double[]> dataTable;

    /**
     * to be implemented by each problem, evaluates the expression in position idxExpr
     * @param idxExpr position of expression
     */
    public abstract void evaluateExpression(int idxExpr);

    /**
     * Sets matrix of data
     * @param dataTable matrix of data
     */
    public void setDataTable(ArrayList<double[]> dataTable) {
        this.dataTable = dataTable;
    }

    /**
     * Returns matrix of data
     * @return matrix of data
     */
    public ArrayList<double[]> getDataTable() {
        return dataTable;
    }
}
