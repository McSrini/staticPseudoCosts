/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.ONE;
import ilog.concert.IloException;
import ilog.cplex.IloCplex.NodeCallback;

/**
 *
 * @author tamvadss
 */
public class NodeHandler extends NodeCallback {
 
    protected void main() throws IloException {
        if (getNremainingNodes64()> ONE){
            abort();
        }
    }
    
}
