
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * 
 */
public class Simulator {
    //Fixed params during simulation
    private ArrayList<Process> processesList;
    private ArrayList<InstructionMemory> instructionMemory;
    private ArrayList<IOChannel> ioList;
    private ArrayList<DataMemory> dataMemory;
    private CPU cpu;
    private int schedulingPolicy;
    
    //Run-time simulation attributes
    private int clock; // The simulation clock.
    private ArrayList<String> textLog; //what happened in whatever clock step.
    private PriorityQueue<Process> readyProcesses; //Queue must be sorted for scheduling.
    
    //Run-time simulation param
    private int clockSpeed;
    
    /**
     * A class constructor.
     * @param clockSpeed The speed of the simulation, 0 if its manual stepping.
     * @param clock The current clock at which the simulation begins at.
     * @param schedulingPolicy The chosen scheduling policy (FIFO,RR,...) TODO.
     */
    public Simulator(int clockSpeed, int clock, int schedulingPolicy){
        this.clock = clock;
        this.clockSpeed = clockSpeed;
        this.schedulingPolicy = schedulingPolicy;
        this.processesList = new ArrayList();
        this.dataMemory  = new ArrayList();
        this.instructionMemory = new ArrayList();
        this.textLog = new ArrayList();
        this.ioList = new ArrayList();
        this.readyProcesses = new PriorityQueue();
        this.cpu = new CPU();
        
        StringBuilder sb = new StringBuilder();
        sb.append(0);
        sb.append(": ");
        sb.append("Starting simulation...");
        this.textLog.add(sb.toString());
        
        this.jumpClock(clock);
        
    }

    /**
     * A method that steps the simulation clock up to the param clock.
     * @param clock the time you want to jump the simulation to.
     */
    private void jumpClock(int clock) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Kills a specified process, and its children *evil laugh*.
     * AND TRACK DEM DOWN!
     * @param processId The process ID of the process to be killed
     */
    public void killProcess(int processId){
        ArrayList<Integer> processesKilled = new ArrayList(); //for log only
        for(int i=0; i<this.processesList.size(); i++){
            if((this.processesList.get(i).getId() == processId)||
                    (this.processesList.get(i).getParentId() == processId)){
                processesKilled.add(this.processesList.get(i).getId());
                this.processesList.remove(i);
            }
        }
        
        //TRACK DEM DOWN!
        for(int i=0;i<processesKilled.size();i++){
            int pid = processesKilled.get(i);
            for (int j=0; j<this.dataMemory.size();j++){
                this.unlockDataMemory(pid, j); //Unlocks memory locked by the dead process.
            }
            for (int j=0; j<this.instructionMemory.size();j++){
                this.unlockInstructionMemory(pid, j); //Unlocks memory locked by the dead process.
            }
            StringBuilder sb = new StringBuilder();
            sb.append(clock);
            sb.append(": ");
            sb.append("Killed process ");
            sb.append(pid);
            this.textLog.add(sb.toString());
        }
    }
    
    /**
     * Unlocks a data memory if the given pid is the locker. Then modifies 
     * processes' states accordingly.
     * @param pid The claiming locker's process ID.
     * @param memAddress The data memory address.
     */
    public void unlockDataMemory(int pid, int memAddress){
        int next;
        next = this.dataMemory.get(memAddress).unlock(pid);
        if (next > 0){
            StringBuilder sb = new StringBuilder();
            sb.append(clock);
            sb.append(": ");
            sb.append("Unlocked data memory #");
            sb.append(memAddress);
            this.textLog.add(sb.toString());
            this.unblock(next);
        }
        
    }

    /**
     * Changes the process status to ready (1).
     * @param pid The process Id of the process to be unblocked.
     */
    public void unblock(int pid) {
        for (int j=0; j<this.processesList.size();j++){
            if (pid == this.processesList.get(j).getId()){
                StringBuilder sb = new StringBuilder();
                sb.append(clock);
                sb.append(": ");
                sb.append("Unlocked process ");
                sb.append(pid);
                this.textLog.add(sb.toString());
                this.processesList.get(j).ready();
            }
        }
    }
    
    /**
     * Frees an instruction memory address from an occupying Process.
     * @param pid The address' locker.
     * @param memAddress 
     */
    public void unlockInstructionMemory(int pid, int memAddress) {
        if(this.instructionMemory.get(memAddress).unlock(pid)){
            StringBuilder sb = new StringBuilder();
            sb.append(clock);
            sb.append(": ");
            sb.append("Unlocked instruction memory #");
            sb.append(memAddress);
            this.textLog.add(sb.toString());
        }
    }
    
    /**
     * Fetches the next instruction and updates PC if it was successful.
     */
    private void fetchInstruction(){
        int instructionAddr = this.readyProcesses.peek().getNextInstruction();
        InstructionMemory instruction = this.instructionMemory.get(instructionAddr);
        boolean success = this.cpu.execute(instruction, this.dataMemory,this.ioList);
        if (success) {
            this.readyProcesses.peek().incrementPc();
            StringBuilder sb = new StringBuilder();
            sb.append(clock);
            sb.append(": ");
            sb.append("Successfully executed instruction memory #");
            sb.append(instructionAddr);
            this.textLog.add(sb.toString());
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(clock);
            sb.append(": ");
            sb.append("Failed executing instruction memory #");
            sb.append(instructionAddr);
            this.textLog.add(sb.toString());
            //Punish the process if needed. Busy-waiting otherwise.
        }
    }
    
}
