/**
 * A class for simulating a process within the whole OS simulation library.
 * @author oabahuss && fredQin
 */
public class Process {
    private int id;
    private int priority;
    private int state; //1 for ready, 2 for blocked, 3 for running.
    private int startingAddress;
    private int length;
    private int parentId;
    private int pc; 
    
    
    /**
     * A constructor for a brand new process. State begins at 1, and pc = 0. 
     * id is null, and startingAddress is null. To be set later by simulator.
     * @param id
     * @param priority
     * @param startingAddress Memory starting address.
     * @param length 
     */
    public Process(int priority, int length){
        this.id = 0;
        this.priority = priority;
        this.state = state;
        this.startingAddress = 0;
        this.length = length;
        this.parentId = 0;
        this.state = 1;
        this.pc = 0;
    }
    
    /**
     * A constructor for a brand new process. State begins at 1, and pc = 0.
     * @param id
     * @param priority
     * @param startingAddress Memory starting address.
     * @param length 
     * @deprecated 
     */
    public Process(int id, int priority, int startingAddress, int length){
        this.id = id;
        this.priority = priority;
        this.state = state;
        this.startingAddress = startingAddress;
        this.length = length;
        this.parentId = 0;
        this.state = 1;
        this.pc = 0;
    }
    
    /**
     * Creates a copy of the parent, id = null.
     * @param parent The creating parent process.
     */
    public Process(Process parent){
        this.priority = parent.getPriority();
        this.parentId = parent.getId();
        this.startingAddress = parent.getStartingAddress();
        this.length = parent.getLength();
        this.pc = parent.getPc();
        
        
        this.id = 0;
        this.state = 1;
    }
    
    /**
     * @return The memory address of the next instruction.
     */
    public int getNextInstruction(){
        return (this.startingAddress + this.pc);
    }
    
    /**
     * Increments the PC by one, resets if it was at the last instruction.
     */
    public void incrementPc(){
        this.pc = (this.pc +1) %this.length;
    }
    
    public int getId() {
        return this.id;
    }
    public int getPriority() {
        return this.priority;
    }

    public int getStartingAddress() {
        return this.startingAddress;
    }

    public int getLength() {
        return this.length;
    }

    public int getPc() {
        return this.pc;
    }

    public int getParentId() {
        return this.parentId;
    }
    
    public int getState(){
        return this.state;
    }

    public void block() {
        this.state = 2;
    }
    
    public void ready() {
        this.state = 1;
    }
    
    public void run() {
        this.state = 3;
    }
    
    public void setStartingAddress(int newAddress){
        this.startingAddress = newAddress;
    }
    
    public void setId(int newId){
        this.id = newId;
    }
}
