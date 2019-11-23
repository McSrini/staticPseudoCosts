/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.ZERO;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 *
 * @author tamvadss
 */
public class EmptyBranchCallback extends IloCplex.BranchCallback {
 
    protected void main() throws IloException {
        if (getNbranches() > ZERO) {
             System.out.println (" leaf count  = "+ getNremainingNodes64()) ;
        }
    }
    
}
