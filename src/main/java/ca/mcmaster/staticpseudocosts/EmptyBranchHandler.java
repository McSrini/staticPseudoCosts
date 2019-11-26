/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.ZERO;
import ilog.concert.IloException;
import ilog.cplex.IloCplex.BranchCallback;

/**
 *
 * @author tamvadss
 */
public class EmptyBranchHandler extends BranchCallback{
 
    protected void main() throws IloException {
        //if (getNbranches() > ZERO) {      
            //System.out.println( "Number of reamining nodes = " +  getNremainingNodes64());
        //}
    }
    
}
