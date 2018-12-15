/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Refine;

import edu.wisc.cs.will.FOPC.Term;
import edu.wisc.cs.will.ILP.SingleClauseNode;
import java.util.HashMap;

/**
 *
 * @author Rodrigo Azevedo
 */
public class RefineTreeNode {
        private RefineNode node;
        private RefineTreeNode leftBranch;
        private RefineTreeNode rightBranch;
        
        public RefineTreeNode() {
            this.node = null;
            this.leftBranch = null;
            this.rightBranch = null;
        }
        
        public RefineNode getNode() {
            return node;
        }
        
        public void setNode(RefineNode node) {
            this.node = node;
        }
        
        public RefineTreeNode getLeftBranch() {
            return leftBranch;
        }
        
        public void setLeftBranch(RefineTreeNode node) {
            leftBranch = node;
        }
        
        public RefineTreeNode getRightBranch() {
            return rightBranch;
        }
        
        public void setRightBranch(RefineTreeNode node) {
            rightBranch = node;
        }
}