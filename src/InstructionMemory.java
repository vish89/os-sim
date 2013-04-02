/**
 * An instruction memory for simulation purposes.
 * To be used in conjunction with the simulation library.
 * @author oabahuss
 */
public class InstructionMemory {
    private int ownerPid;
    private int instruction;
    private int operand;
    
    /**
     * A constructor. Use setMemory to set it up.
     */
    public InstructionMemory(){
        this.instruction = 1;
        this.operand = 0;
        this.ownerPid = 0;
    }
    
    /**
     * Unlocks the memory if the given pid is the same as the ownerPid.
     * @param pid 
     */
    public boolean unlock(int pid) {
        if (this.ownerPid == pid){
            this.ownerPid = 0;
            return true;
        }
        else{
            return false;
        }
    }
    
    public void setOwner(int pid){
        this.ownerPid = pid;
    }
    /**
     * Returns the current instruction in the memory.
     * @return 0 for an empty instruction.
     * 1 for a CPU bound instruction.
     * 2 for Read data from memory specified by operand.
     * 3 for Write data on memory specified by operand.
     * 4 for lock data memory specified by operand.
     * 5 for unlock data memory specified by operand.
     * 6 for IO channel specified by operand.
     * 7 for a fork, creates a child process.
     */
    public int getInstruction(){
        return this.instruction;
    }
    
    public int getOperand(){
        return this.operand;
    }
    
    /**
     * Sets the memory to the parameters if no one occupies the memory.
     * @param pid The pid of the new owner.
     * @param instruction
     * @param operand
     * @return True if no one occupies the memory.
     */
    public boolean setMemory(int pid, int instruction, int operand){
        if (this.ownerPid == 0){ //Checks if no one owns the memory.
            this.operand = operand;
            this.instruction = instruction;
            this.ownerPid = pid;
            return true;
        }
        else { //Memory is already occupied!
            return false;
        }
    }
    
    /**
     * Checks if the no one occupies the memory.
     * @return True if no one is occupying the memory.
     */
    public boolean isEmpty(){
        if (this.ownerPid == 0){
            return true;
        }
        else{
            return false;
        }
    }

    public int getOwner() {
        return this.ownerPid;
    }
    
}
