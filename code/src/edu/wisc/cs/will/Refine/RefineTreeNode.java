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
        private RefineTreeNode leftNode;
        private RefineTreeNode rightNode;
        
        public RefineTreeNode() {
            this.node = null;
            this.leftNode = null;
            this.rightNode = null;
        }
        
        public RefineNode getNode() {
            return node;
        }
        
        public void setNode(RefineNode node) {
            this.node = node;
        }
        
        public RefineTreeNode getLeftNode() {
            return leftNode;
        }
        
        public void setLeftNode(RefineTreeNode node) {
            leftNode = node;
        }
        
        public RefineTreeNode getRightNode() {
            return rightNode;
        }
        
        public void setRightNode(RefineTreeNode node) {
            rightNode = node;
        }
}