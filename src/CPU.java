
import java.util.ArrayList;

/**
 * A CPU simulator to be used in conjunction with the whole simulation package.
 * @author oabahuss
 */
class CPU {
    Simulator simulator;
    int currentProcessId;
    int address;
    int instruction;
    int operand;
    
    /**
     * Constructs a CPU that interacts with the passed simulator.
     * @param sim 
     */
    public CPU(Simulator sim){
        this.simulator = sim;
    }
    
    /**
     * Executes an instruction
     * @param address The address of the instruction.
     * @param instructionMemory The memory cell to be executed.
     * @return True if the instruction was executed successfully.
     */
    public boolean execute(int address, InstructionMemory instructionMemory) {
        this.instruction = instructionMemory.getInstruction();
        this.operand = instructionMemory.getOperand();
        this.currentProcessId = instructionMemory.getOwner();
        
        switch(this.instruction){
            case 1: return true; //A CPU instruction, always succeeds.
            case 2: return this.read();
            case 3: return this.write();
            case 4: return this.lock();
            case 5: return this.unlock();
            case 6: return this.io();
            case 7: return this.fork();
            default: return false;
        }
    }

    /**
     * Reads from a data memory
     * @return true if it succeeds
     */
    private boolean read() {
        DataMemory data = this.simulator.getData(this.operand);
        if (data.read(this.currentProcessId)){
            StringBuilder sb = new StringBuilder();
            sb.append("Sucessfully read memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return true;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed reading memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return false;
        }
    }
    
    /**
     * Writes on a data memory
     * @return true if it succeeds
     */
    private boolean write() {
        DataMemory data = this.simulator.getData(this.operand);
        if (data.write(this.currentProcessId)){
            StringBuilder sb = new StringBuilder();
            sb.append("Sucessfully wrote on memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return true;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed writing on memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return false;
        }
    }

    /**
     * Locks a data memory
     * @return true if it succeeds
     */
    private boolean lock() {
        DataMemory data = this.simulator.getData(this.operand);
        if (data.lock(this.currentProcessId)){
            StringBuilder sb = new StringBuilder();
            sb.append("Sucessfully locked memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return true;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed locking memory ");
            sb.append(operand);
            this.simulator.addLog(sb.toString());
            return false;
        }
    }
    
    /**
     * Tells the simulator to unlock a data memory
     * @return true if it succeeds
     */
    private boolean unlock() {
        this.simulator.unlockDataMemory(this.currentProcessId, operand);
        return true; //bro... unlocking always succeeds.
    }
    
    /**
     * Tells the simulator to add the process to an io channel.
     * @return True if it succeeds
     */
    private boolean io() {        
        this.simulator.addToIo(this.currentProcessId, this.operand);
        return true; 
    }

    /**
     * Tells the simulator to fork a process. 
     * @return True.
     * @deprecated 
     */
    private boolean fork() {
        this.simulator.forkProcess(this.currentProcessId);
        return true;
    }
    
}
