/*
 * Dylan Thompson
 * Course: COP 3503C, CS2
 * Section: 00110
 * Date: 06/23/2025
 */

/*
 * Class Description:
 * Makes a 2-3-4 tree that stores any integer (including negatives I hope)
 * Able to add, delete, and search values (integers) within the tree
 */
public class TwoFourTree { // 2-3-4 tree that stores any integer (including negatives I hope)
    // Useful constants to determine the direction of child nodes with respect to the parent
    private static final int LEFT = 0;
    private static final int CENTER_LEFT = 1;
    private static final int CENTER = 2;
    private static final int CENTER_RIGHT = 3;
    private static final int RIGHT = 4;

    /* 
     * Class Description:
     * Defines each node within the tree as well as
     * methods that help with node operations
     */
    private class TwoFourTreeItem { 
        int values = 1;
        int value1 = 0;                             // always exists.
        int value2 = 0;                             // exists iff the node is a 3-node or 4-node.
        int value3 = 0;                             // exists iff the node is a 4-node.
        boolean isLeaf = true;
        
        TwoFourTreeItem parent = null;              // parent exists iff the node is not root.
        TwoFourTreeItem leftChild = null;           // left and right child exist iff the note is a non-leaf.
        TwoFourTreeItem rightChild = null;          
        TwoFourTreeItem centerChild = null;         // center child exists iff the node is a non-leaf 3-node.
        TwoFourTreeItem centerLeftChild = null;     // center-left and center-right children exist iff the node is a non-leaf 4-node.
        TwoFourTreeItem centerRightChild = null;

        public boolean isTwoNode() {
            return (this.values == 1) ? true : false;
        } 

        public boolean isThreeNode() {
            return (this.values == 2) ? true : false;
        }

        public boolean isFourNode() {
            return (this.values == 3) ? true : false;
        }

        public boolean isRoot() {
            return (this.parent == null) ? true : false;
        }

        public TwoFourTreeItem(int value1) { // Constructor for a 2-node. Accepts any integer
            this.value1 = value1;
        } 

        public TwoFourTreeItem(int value1, int value2) { // Constructor for a 3-node. Accepts any integer and sorts from least to greatest
            if(value1 < value2) {
                this.value1 = value1;
                this.value2 = value2;
            }
            else {
                this.value1 = value2;
                this.value2 = value1;
            }
            this.values = 2;
        }

        public TwoFourTreeItem(int value1, int value2, int value3) { // Constructor for a 4-node. Accepts any integer and sorts from least to greatest
            int[] vals = { value1, value2, value3 };
            for(int i = 0; i < 3; i++) {
                for(int j = i + 1; j < 3; j++) {
                    if(vals[i] > vals[j]) {
                        int a = vals[i];
                        vals[i] = vals[j];
                        vals[j] = a;
                    }
                }
            }
            this.value1 = vals[0];
            this.value2 = vals[1];
            this.value3 = vals[2];
            this.values = 3;
        }

        private void printIndents(int indent) {
            for(int i = 0; i < indent; i++) System.out.printf("  ");
        }

        public void printInOrder(int indent) {
            if(!isLeaf) leftChild.printInOrder(indent + 1);
            printIndents(indent);
            System.out.printf("%d\n", value1);
            if(isThreeNode()) {
                if(!isLeaf) centerChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
            } else if(isFourNode()) {
                if(!isLeaf) centerLeftChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
                if(!isLeaf) centerRightChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value3);
            }
            if(!isLeaf) rightChild.printInOrder(indent + 1);
        }

        /*
         * Takes in the integer value of a child node then returns which child that must be from
         * Cannot accept leaf nodes
         */
        public int childHasValue(int value) {
            if(value < this.value1) return LEFT;
            else if(this.isThreeNode() && value < this.value2) return CENTER;
            else if(this.isFourNode()) {
                if(value < this.value2) return CENTER_LEFT;
                else if(value < this.value3) return CENTER_RIGHT;
            }
            return RIGHT;
        }

        /*
         * Takes in another tree node and sets it to be the child of this node
         * Takes in an integer value from LEFT to RIGHT which determines where the child node belongs with respect to this node
         * Works with null pointers, and if child != null then it will set the child's parent pointer to be this node
         */
        public void setChild(TwoFourTreeItem child, int value) {
            if(value == LEFT) this.leftChild = child;
            else if(value == CENTER_LEFT) this.centerLeftChild = child;
            else if(value == CENTER) this.centerChild = child;
            else if(value == CENTER_RIGHT) this.centerRightChild = child;
            else this.rightChild = child;
            if(child != null) child.parent = this;
        }

        /*
         * Works in tandem with setChild, takes in 5 tree node objects and sets them as children of this node
         * Also updates the isLeaf status of this node using the isLeaf() method
         */
        public void setChildren(TwoFourTreeItem newLeft, TwoFourTreeItem newCenterLeft, TwoFourTreeItem newCenter, TwoFourTreeItem newCenterRight, TwoFourTreeItem newRight) {
            TwoFourTreeItem[] newChildren = { newLeft, newCenterLeft, newCenter, newCenterRight, newRight };
            for(int i = 0; i < 5; i++) {
                this.setChild(newChildren[i], i);
            }
            this.isLeaf();
        }
    
        public boolean isLeaf() { // Sets isLeaf to true or false and returns isLeaf
            return this.isLeaf = (this.leftChild == null && this.rightChild == null);
        } 
    }

    TwoFourTreeItem root = null;

    /*
     * Used in addValue, important for splitting 4-nodes into 3 separate 2-nodes
     * Will only accept 4-nodes and still maintains proper memory management after split (no lost nodes)
     */
    private void split(TwoFourTreeItem node) {
        if(!node.isFourNode()) return;
        TwoFourTreeItem newLeftNode = new TwoFourTreeItem(node.value1);
        TwoFourTreeItem newRightNode = new TwoFourTreeItem(node.value3);

        if(!node.isLeaf()) { // Responsible for maintaining references to children
            newLeftNode.setChildren(node.leftChild, null, null, null, node.centerLeftChild);
            newRightNode.setChildren(node.centerRightChild, null, null, null, node.rightChild);
        }

        TwoFourTreeItem parent = node.parent;
        int childValue = parent.childHasValue(node.value1);

        // Responsible for updating parent
        if(parent.isTwoNode()) {
            if(childValue == LEFT) {
                parent.value2 = parent.value1;
                parent.value1 = node.value2;
                parent.setChildren(newLeftNode, null, newRightNode, null, parent.rightChild);
            }
            else if(childValue == RIGHT) {
                parent.value2 = node.value2;
                parent.setChildren(parent.leftChild, null, newLeftNode, null, newRightNode);
            }
        }
        else if(parent.isThreeNode()) {
            if(childValue == LEFT) {
                parent.value3 = parent.value2;
                parent.value2 = parent.value1;
                parent.value1 = node.value2;
                parent.setChildren(newLeftNode, newRightNode, null, parent.centerChild, parent.rightChild);
            }
            else if(childValue == CENTER) {
                parent.value3 = parent.value2;
                parent.value2 = node.value2;
                parent.setChildren(parent.leftChild, newLeftNode, null, newRightNode, parent.rightChild);
            }
            else if(childValue == RIGHT) {
                parent.value3 = node.value2;
                parent.setChildren(parent.leftChild, parent.centerChild, null, newLeftNode, newRightNode);
            }
        }
        parent.values++;
    }

    /*
     * Takes in the integer value to be added to the tree
     * Inserts the integer into the tree and self balances while inserting by employing the split method
     */
    public boolean addValue(int value) {
        if(this.root == null) {
            this.root = new TwoFourTreeItem(value);
            return true;
        }

        if(this.root.isFourNode()) { // split root
            TwoFourTreeItem newRoot = new TwoFourTreeItem(this.root.value2);

            TwoFourTreeItem newLeftNode = new TwoFourTreeItem(this.root.value1);
            TwoFourTreeItem newRightNode = new TwoFourTreeItem(this.root.value3);

            if(!this.root.isLeaf()) {
                newLeftNode.setChildren(this.root.leftChild, null, null, null, this.root.centerLeftChild);
                newRightNode.setChildren(this.root.centerRightChild, null, null, null, this.root.rightChild);
            }
            newRoot.setChildren(newLeftNode, null, null, null, newRightNode);

            this.root = newRoot;
        }
        TwoFourTreeItem leaf = this.root;

        /*
         * Searches for appropriate leaf node
         * Always splits 4-nodes before traversing to them
         */
        while(leaf != null && !(leaf.isLeaf())) {
            if(this.root.isFourNode()) { // split root in the event it becomes a 4-node during traversal
                TwoFourTreeItem newRoot = new TwoFourTreeItem(this.root.value2);

                TwoFourTreeItem newLeftNode = new TwoFourTreeItem(this.root.value1);
                TwoFourTreeItem newRightNode = new TwoFourTreeItem(this.root.value3);

                if(!this.root.isLeaf()) {
                    newLeftNode.setChildren(this.root.leftChild, null, null, null, this.root.centerLeftChild);

                    newRightNode.setChildren(this.root.centerRightChild, null, null, null, this.root.rightChild);
                }
                newRoot.setChildren(newLeftNode, null, null, null, newRightNode);

                this.root = newRoot;
                leaf = this.root;
            }
            if(value < leaf.value1) {
                if(leaf.leftChild.isFourNode()) this.split(leaf.leftChild);
                else leaf = leaf.leftChild;
            }
            else if(!leaf.isTwoNode() && value < leaf.value2) {
                if(leaf.isThreeNode()) {
                    if(leaf.centerChild.isFourNode()) this.split(leaf.centerChild);
                    else leaf = leaf.centerChild;
                }
                else {
                    if(leaf.centerLeftChild.isFourNode()) this.split(leaf.centerLeftChild);
                    else leaf = leaf.centerLeftChild;
                }
            }
            else if(leaf.isFourNode() && value < leaf.value3) {
                if(leaf.centerRightChild.isFourNode()) this.split(leaf.centerRightChild);
                else leaf = leaf.centerRightChild;
            }
            else {
                if(leaf.rightChild.isFourNode()) this.split(leaf.rightChild);
                else leaf = leaf.rightChild;
            }
        }
        // Responsible for the actual addition of the value
        if(leaf != null) {
            if(leaf.isRoot()) {
                if(leaf.isTwoNode()) this.root = new TwoFourTreeItem(leaf.value1, value);
                else this.root = new TwoFourTreeItem(leaf.value1, leaf.value2, value);
            }
            else {
                if(leaf.isTwoNode()) {
                    if(value < leaf.value1) {
                        leaf.value2 = leaf.value1;
                        leaf.value1 = value;
                    }
                    else leaf.value2 = value;
                }
                else {
                    if(value < leaf.value1) {
                        leaf.value3 = leaf.value2;
                        leaf.value2 = leaf.value1;
                        leaf.value1 = value;
                    }
                    else if(value < leaf.value2) {
                        leaf.value3 = leaf.value2;
                        leaf.value2 = value;
                    }
                    else leaf.value3 = value;
            }
                leaf.values++;
            }
        }
        
        return (leaf != null); // Returns false if traversal led to null pointer (Not realistically possible, but helpful for debugging)
    }

    /*
     * Searches the tree for the specified value
     * Will loop till pointer is null or the value is found, returns false if null, otherwise true
     */
    public boolean hasValue(int value) {
        TwoFourTreeItem search = this.root;
        while(search != null && (search.value1 != value && (search.isTwoNode() || search.value2 != value) && (!search.isFourNode() || search.value3 != value))) {
            if(value < search.value1) search = search.leftChild;
            else if(!search.isTwoNode() && value < search.value2) {
                if(search.isThreeNode()) search = search.centerChild;
                else search = search.centerLeftChild;
            }
            else if(search.isFourNode() && value < search.value3) search = search.centerRightChild;
            else search = search.rightChild;
        }

        return (search != null);
    }

    /*
     * Used in deleteValue, important for merging 2-nodes into 4-nodes
     * It assumes parent isnt null
     * Also assumes child, and immediate siblings are 2-nodes
     * Otherwise the tree structure may break and nodes may be misplaced or lost
     */
    private void merge(TwoFourTreeItem child) {
        TwoFourTreeItem mergedNode = null;
        TwoFourTreeItem parent = child.parent;
        TwoFourTreeItem sibling = null;
        int childValue = parent.childHasValue(child.value1);

        if(parent.isTwoNode() && parent.isRoot()) { // Updates this.root
            mergedNode = new TwoFourTreeItem(parent.leftChild.value1, parent.value1, parent.rightChild.value1);
            if(!child.isLeaf()) mergedNode.setChildren(parent.leftChild.leftChild, parent.leftChild.rightChild, null, parent.rightChild.leftChild, parent.rightChild.rightChild);
            this.root = mergedNode;
        }
        else if(childValue == LEFT) {
            if(parent.isThreeNode()) {
                sibling = parent.centerChild;
                mergedNode = new TwoFourTreeItem(child.value1, parent.value1, sibling.value1);
                parent.setChildren(mergedNode, null, null, null, parent.rightChild);
            }
            else {
                sibling = parent.centerLeftChild;
                mergedNode = new TwoFourTreeItem(child.value1, parent.value1, sibling.value1);
                parent.setChildren(mergedNode, null, parent.centerRightChild, null, parent.rightChild);
            }
            parent.value1 = parent.value2;
            parent.value2 = parent.value3;
        }
        else if(childValue == RIGHT) { 
            if(parent.isThreeNode()) {
                sibling = parent.centerChild;
                mergedNode = new TwoFourTreeItem(child.value1, parent.value2, sibling.value1);
                parent.setChildren(parent.leftChild, null, null, null, mergedNode);
            }
            else {
                sibling = parent.centerRightChild;
                mergedNode = new TwoFourTreeItem(child.value1, parent.value3, sibling.value1);
                parent.setChildren(parent.leftChild, null, parent.centerLeftChild, null, mergedNode);
            }
        }
        else if(childValue == CENTER) {
            sibling = parent.rightChild;
            mergedNode = new TwoFourTreeItem(child.value1, parent.value2, sibling.value1);
            parent.setChildren(parent.leftChild, null, null, null, mergedNode);
        }
        else {
            if(childValue == CENTER_LEFT) sibling = parent.centerRightChild;
            else if(childValue == CENTER_RIGHT) sibling = parent.centerLeftChild;

            mergedNode = new TwoFourTreeItem(child.value1, parent.value2, sibling.value1);
            parent.value2 = parent.value3;
            parent.setChildren(parent.leftChild, null, mergedNode, null, parent.rightChild);
        }
        
        if(!child.isLeaf() && mergedNode != this.root) { // Updates child nodes if necessary
            if(childValue != CENTER_RIGHT && childValue != RIGHT) mergedNode.setChildren(child.leftChild, child.rightChild, null, sibling.leftChild, sibling.rightChild);
            else mergedNode.setChildren(sibling.leftChild, sibling.rightChild, null, child.leftChild, child.rightChild);
        }
        parent.values--;
    }

    /*
     * Used in deleteValue, important for turning 2-nodes into 3-nodes
     * It assumes parent isnt null
     * Also assumes child is a 2-node and there exists a valid non-2-node immediate sibling
     * Otherwise the tree structure may break and nodes may be misplaced or lost
     */
    private void rotate(TwoFourTreeItem child) {

        TwoFourTreeItem parent = child.parent;
        TwoFourTreeItem newNode = null;
        TwoFourTreeItem sibling = null;
        int childValue = parent.childHasValue(child.value1);

        if(childValue == LEFT) {
            if(parent.isTwoNode()) {
                sibling = parent.rightChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value1);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.setChildren(newNode, null, null, null, sibling);
            }
            else if(parent.isThreeNode()) {
                sibling = parent.centerChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value1);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.setChildren(newNode, null, sibling, null, parent.rightChild);
            }
            else {
                sibling = parent.centerLeftChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value1);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.setChildren(newNode, sibling, null, parent.centerRightChild, parent.rightChild);
            }
            
            parent.value1 = sibling.value1;
            sibling.value1 = sibling.value2;
            if(sibling.isThreeNode()) sibling.setChildren(sibling.centerChild, null, null, null, sibling.rightChild);
            else {
                sibling.value2 = sibling.value3;
                sibling.setChildren(sibling.centerLeftChild, null, sibling.centerRightChild, null, sibling.rightChild);
            }
        }
        else if(childValue == CENTER_LEFT) {
            if(!parent.leftChild.isTwoNode()) {
                sibling = parent.leftChild;
                newNode = new TwoFourTreeItem(parent.value1, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value1 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value1 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
            }
            else {
                sibling = parent.centerRightChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value2);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.value2 = sibling.value1;
                sibling.value1 = sibling.value2;
                if(sibling.isThreeNode() && !sibling.isLeaf()) sibling.setChildren(sibling.centerChild, null, null, null, sibling.rightChild);
                else if(sibling.isFourNode()) {
                    sibling.value2 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.centerLeftChild, null, sibling.centerRightChild, null, sibling.rightChild);
                }
            }
            parent.setChildren(parent.leftChild, newNode, null, parent.centerRightChild, parent.rightChild);
        }
        else if(childValue == CENTER_RIGHT) {
            if(!parent.centerLeftChild.isTwoNode()) {
                sibling = parent.centerLeftChild;
                newNode = new TwoFourTreeItem(parent.value2, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value2 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value2 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
            }
            else {
                sibling = parent.rightChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value3);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.value3 = sibling.value1;
                sibling.value1 = sibling.value2;
                if(sibling.isThreeNode() && !sibling.isLeaf()) sibling.setChildren(sibling.centerChild, null, null, null, sibling.rightChild);
                else if(sibling.isFourNode()) {
                    sibling.value2 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.centerLeftChild, null, sibling.centerRightChild, null, sibling.rightChild);
                }
            }
            parent.setChildren(parent.leftChild, parent.centerLeftChild, null, newNode, parent.rightChild);
        }
        else if(childValue == CENTER) {
            if(!parent.leftChild.isTwoNode()) {
                sibling = parent.leftChild;
                newNode = new TwoFourTreeItem(parent.value1, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value1 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value1 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
            }
            else {
                sibling = parent.rightChild;
                newNode = new TwoFourTreeItem(child.value1, parent.value2);
                if(!sibling.isLeaf()) newNode.setChildren(child.leftChild, null, child.rightChild, null, sibling.leftChild);
                parent.value2 = sibling.value1;
                sibling.value1 = sibling.value2;
                if(sibling.isThreeNode() && !sibling.isLeaf()) sibling.setChildren(sibling.centerChild, null, null, null, sibling.rightChild);
                else if(sibling.isFourNode()) {
                    sibling.value2 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.centerLeftChild, null, sibling.centerRightChild, null, sibling.rightChild);
                }
            }
            parent.setChildren(parent.leftChild, null, newNode, null, parent.rightChild);
        }
        else {
            if(parent.isTwoNode()) {
                sibling = parent.leftChild;
                newNode = new TwoFourTreeItem(parent.value1, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value1 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value1 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
                parent.setChildren(sibling, null, null, null, newNode);
            }
            else if(parent.isThreeNode()) {
                sibling = parent.centerChild;
                newNode = new TwoFourTreeItem(parent.value2, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value2 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value2 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
                parent.setChildren(parent.leftChild, null, sibling, null, newNode);
            }
            else {
                sibling = parent.centerRightChild;
                newNode = new TwoFourTreeItem(parent.value3, child.value1);
                if(!sibling.isLeaf()) newNode.setChildren(sibling.rightChild, null, child.leftChild, null, child.rightChild);
                if(sibling.isThreeNode()) {
                    parent.value3 = sibling.value2;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, null, null, sibling.centerChild);
                }
                else {
                    parent.value3 = sibling.value3;
                    if(!sibling.isLeaf()) sibling.setChildren(sibling.leftChild, null, sibling.centerLeftChild, null, sibling.centerRightChild);
                }
                parent.setChildren(parent.leftChild, parent.centerLeftChild, null, sibling, newNode);
            }
        }
        sibling.values--;
    }

    /*
     * Used in predecessor/successor method in the event that after merging
     * within those methods the value to delete/replace was pulled down into
     * another node. This will update toor pointer to point to the node with 
     * the correct value.
     * Assumes toor != null
     */
    private TwoFourTreeItem ensureToor(TwoFourTreeItem toor, int value) {
        if(value != toor.value1 && (toor.isTwoNode() || value != toor.value2) && (!toor.isFourNode() || value != toor.value3)) {
            if(value < toor.value1) toor = toor.leftChild;
            else if(!toor.isTwoNode() && value < toor.value2) {
                if(toor.isThreeNode()) toor = toor.centerChild;
                else toor = toor.centerLeftChild;
            }
            else if(toor.isFourNode() && value < toor.value3) toor = toor.centerRightChild;
            else toor = toor.rightChild; 
        }

        return toor;
    }

    /*
     * Used in deleteValue, important in the event value to delete is found
     * in an internal node. Will replace the value in the internal node with 
     * its predecessor and delete the predecessor from the leaf it pulled from, in essence deleting the value.
     * Returns true in order to end deleteValue method (since value has been deleted)
     * Assumes toor (node with value to be deleted) isnt null and has the value to delete
     * Otherwise could break tree
     */
    private boolean predecessor(TwoFourTreeItem toor, int value) {
        TwoFourTreeItem swap = null;
        while(swap == null || swap == toor) { // Goes to the immediate leftChild before following rightChild to leaf
            if(value == toor.value1) {
                if(toor.leftChild.isTwoNode()) merge(toor.leftChild);
                else swap = toor.leftChild;
            }
            else if(toor.isThreeNode() && value == toor.value2) {
                if(toor.centerChild.isTwoNode()) merge(toor.centerChild);
                else swap = toor.centerChild;
            }
            else if(toor.isFourNode() && value == toor.value2) {
                if(toor.centerLeftChild.isTwoNode()) merge(toor.centerLeftChild);
                else swap = toor.centerLeftChild;
            }
            else {
                if(toor.centerRightChild.isTwoNode()) merge(toor.centerRightChild);
                else swap = toor.centerRightChild;
            }
        }
        
        while(!swap.isLeaf()) { // Goes to the right most leaf child
            if(swap.rightChild.isTwoNode()) {
                if(swap.isTwoNode() && swap.leftChild.isTwoNode()) merge(swap.rightChild);
                else if(swap.isThreeNode() && swap.centerChild.isTwoNode()) merge(swap.rightChild);
                else if(swap.isFourNode() && swap.centerRightChild.isTwoNode()) merge(swap.rightChild);
                else rotate(swap.rightChild);
            }
            else swap = swap.rightChild;
            toor = ensureToor(toor, value);
        }

        // Below performs the actual value replacement and deletion
        if(value == toor.value1) {
            if(swap.isThreeNode()) toor.value1 = swap.value2;
            else toor.value1 = swap.value3;
        }
        else if(value == toor.value2) {
            if(swap.isThreeNode()) toor.value2 = swap.value2;
            else toor.value2 = swap.value3;
        }
        else {
            if(swap.isThreeNode()) toor.value3 = swap.value2;
            else toor.value3 = swap.value3;
        }
        swap.values--;
        
        return true;
    }

    /*
     * Used in deleteValue, important in the event value to delete is found
     * in an internal node. Will replace the value in the internal node with
     * its successor and delete the successor from the leaf it pulled from, in essence deleting the value.
     * Returns true in order to end deleteValue method (since value has been deleted)
     * Assumes toor (node with value to be deleted) isnt null and has the value to delete
     * Otherwise could break tree
     */
    private boolean successor(TwoFourTreeItem toor, int value) {
        TwoFourTreeItem swap = null;
        while(swap == null || swap == toor) { // Goes to the immediate rightChild before following leftChild to leaf
            if(toor.isThreeNode() && value == toor.value1) {
                if(toor.centerChild.isTwoNode()) merge(toor.centerChild);
                else swap = toor.centerChild;
            }
            else if(toor.isFourNode() && value == toor.value1) {
                if(toor.centerLeftChild.isTwoNode()) merge(toor.centerLeftChild);
                else swap = toor.centerLeftChild;
            }
            else if(toor.isFourNode() && value == toor.value2) {
                if(toor.centerRightChild.isTwoNode()) merge(toor.centerRightChild);
                else swap = toor.centerRightChild;
            }
            else {
                if(toor.rightChild.isTwoNode()) merge(toor.rightChild);
                else swap = toor.rightChild;
            }
        }
        
        while(!swap.isLeaf()) { // Goes to the left most leaf child
            if(swap.leftChild.isTwoNode()) {
                if(swap.isTwoNode() && swap.leftChild.isTwoNode()) merge(swap.leftChild);
                else if(swap.isThreeNode() && swap.centerChild.isTwoNode()) merge(swap.leftChild);
                else if(swap.isFourNode() && swap.centerLeftChild.isTwoNode()) merge(swap.leftChild);
                else rotate(swap.leftChild);
            }
            else swap = swap.leftChild;
            toor = ensureToor(toor, value);
        }

        // Below performs the actual value replacement and deletion
        if(value == toor.value1) toor.value1 = swap.value1;
        else if(value == toor.value2) toor.value2 = swap.value1;
        else toor.value3 = swap.value1;
        swap.value1 = swap.value2;
        swap.value2 = swap.value3;
        swap.values--;

        return true;
    }

    /*
     * Takes in an integer value to delete and searches for the value within the tree
     * Maintains balance by merging 2-nodes while searching
     * If value is found then employs merge(), rotate(), predecessor(), or successor()
     * depending on the circumstances in which it found the value, will return true if value is found
     * since it will have deleted it.
     * If value is never found then returns false
     */
    public boolean deleteValue(int value) {
        if(this.root == null) return false;
        
        // If root and both its children are 2-nodes they are merged
        if(!this.root.isLeaf() && this.root.isTwoNode() && (this.root.leftChild.isTwoNode() && this.root.rightChild.isTwoNode())) merge(this.root.leftChild);
        TwoFourTreeItem search = this.root;
        
        /*
         * Searches for value to delete
         * Always merges/rotates before entering 2-node
         * Returns true if value is found (aka value has been deleted),
         * returns false if value is never found
         */
        while(search != null) {
            if(search.value1 == value || (!search.isTwoNode() && search.value2 == value) || (search.isFourNode() && search.value3 == value)) {
                boolean deleted = false;
                if(!search.isLeaf()) {
                    if(search.value1 == value) {
                        if(search.isTwoNode()) {
                            if(!search.leftChild.isTwoNode()) deleted = predecessor(search, value);
                            else deleted = successor(search, value);
                        }
                        else if(!search.leftChild.isTwoNode()) deleted = predecessor(search, value);
                        else if(search.isThreeNode() && !search.centerChild.isTwoNode()) deleted = successor(search, value);
                        else if(search.isFourNode() && !search.centerLeftChild.isTwoNode()) deleted = successor(search, value);
                        else merge(search.leftChild);
                    }
                    else if(search.isThreeNode()) {
                        if(!search.centerChild.isTwoNode()) deleted = predecessor(search, value);
                        else if(!search.rightChild.isTwoNode()) deleted = successor(search, value);
                        else merge(search.centerChild);
                    }
                    else {
                        if(search.value2 == value) {
                            if(!search.centerLeftChild.isTwoNode()) deleted = predecessor(search, value);
                            else if(!search.centerRightChild.isTwoNode()) deleted = successor(search, value);
                            else merge(search.centerLeftChild);
                        }
                        else {
                            if(!search.centerRightChild.isTwoNode()) deleted = predecessor(search, value);
                            else if(!search.rightChild.isTwoNode()) deleted = successor(search, value);
                            else merge(search.rightChild);
                        }
                    }
                }
                else {
                    if(value == search.value1) {
                        search.value1 = search.value2;
                        search.value2 = search.value3;
                    }
                    else if(value == search.value2) search.value2 = search.value3;
                    search.values--;
                    deleted = true;
                }
                if(deleted) return true;
            }
            else if(value < search.value1) {
                if(search.leftChild != null && search.leftChild.isTwoNode()) {
                    if(search.isThreeNode() && search.centerChild.isTwoNode()) merge(search.leftChild);
                    else if(search.isFourNode() && search.centerLeftChild.isTwoNode()) merge(search.leftChild);
                    else if(search.isTwoNode() && search.rightChild.isTwoNode()) merge(search.leftChild);
                    else rotate(search.leftChild);
                }
                else search = search.leftChild;
            }
            else if(!search.isTwoNode() && value < search.value2) {
                if(search.isThreeNode()) {
                    if(search.centerChild != null && search.centerChild.isTwoNode()) {
                        if(search.leftChild.isTwoNode() && search.rightChild.isTwoNode()) merge(search.centerChild);
                        else rotate(search.centerChild);
                    }
                    else search = search.centerChild;
                }
                else {
                    if(search.centerLeftChild != null && search.centerLeftChild.isTwoNode()) {
                        if(search.leftChild.isTwoNode() && search.centerRightChild.isTwoNode()) merge(search.centerLeftChild);
                        else rotate(search.centerLeftChild);
                    }
                    else search = search.centerLeftChild;
                }
            }
            else if(search.isFourNode() && value < search.value3) {
                if(search.centerRightChild != null && search.centerRightChild.isTwoNode()) {
                    if(search.centerLeftChild.isTwoNode() && search.rightChild.isTwoNode()) merge(search.centerRightChild);
                    else rotate(search.centerRightChild);
                }
                else search = search.centerRightChild;
            }
            else {
                if(search.rightChild != null && search.rightChild.isTwoNode()) {
                    if(search.isThreeNode() && search.centerChild.isTwoNode()) merge(search.rightChild);
                    else if(search.isFourNode() && search.centerRightChild.isTwoNode()) merge(search.rightChild);
                    else if(search.isTwoNode() && search.leftChild.isTwoNode()) merge(search.rightChild);
                    else rotate(search.rightChild);
                }
                else search = search.rightChild;
            }
        }

        return false;
    }

    public void printInOrder() {
        if(root != null) root.printInOrder(0);
    }

    public TwoFourTree() {

    }
}