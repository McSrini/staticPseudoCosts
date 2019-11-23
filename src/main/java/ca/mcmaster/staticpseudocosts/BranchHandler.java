/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.*; 
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex.BranchCallback;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class BranchHandler extends BranchCallback {
    
    private Collection<IloNumVar> variableList ;
     
    public BranchHandler ( Collection<IloNumVar> vars) {
        variableList= vars;
    }
 
    protected void main() throws IloException {
        if (getNbranches() > ZERO) {
         
            for (IloNumVar var : variableList ){
                
                // force Cplex to compute the up and down pseudo cost for every variable
                
                double downPseudoCost =  getDownPseudoCost(  var);
                 
                double upPseudoCost =   getUpPseudoCost(  var);         

                //branch on the suggested var                 

            }

            //done
            abort ();
        }
    }
    
}
