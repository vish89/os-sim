
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author oabahuss && fredQin
 */
public class Simulator {
    //Fixed params during simulation
    private ArrayList<Process> processesList;
    private InstructionMemory[] instructionMemory;
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
        this.textLog = new ArrayList();
        this.ioList = new ArrayList();
        this.readyProcesses = new PriorityQueue();
        this.cpu = new CPU(this);
        
        this.instructionMemory = new InstructionMemory[1000];
        for (int i=0; i<1000; i++){
            this.instructionMemory[i] = new InstructionMemory();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(0);
        sb.append(": ");
        sb.append("Starting simulation...");
        this.textLog.add(sb.toString());
        
        this.jumpClock(clock);
        
    }

    /**
     * A method that steps the simulation clock up to the parameter clock.
     * @param clock the time you want to jump the simulation to.
     */
    private void jumpClock(int clock) {
        int i = clock;
        while(i>=0){
            this.incrementClock();
        }
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
            for (int j=0; j<this.instructionMemory.length; j++){
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
                sb.append("Unblocked process ");
                sb.append(pid);
                this.textLog.add(sb.toString());
                this.processesList.get(j).ready();
                this.readyProcesses.add(this.processesList.get(j));
            }
        }
    }
    
    /**
     * Frees an instruction memory address from an occupying Process.
     * @param pid The address' locker.
     * @param memAddress 
     */
    public void unlockInstructionMemory(int pid, int memAddress) {
        if(this.instructionMemory[memAddress].unlock(pid)){
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
        boolean success = this.cpu.execute(instructionAddr, this.instructionMemory[instructionAddr]);
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
    
    /**
     * Increments the clock and does everything necessary
     */
    public void incrementClock(){
        this.schedule();
        this.fetchInstruction();
        this.clock++;
        this.updateIo();
    }
    
    /**
     * Returns a process that matches the pid.
     * @param pid
     * @return Null if no processes are found. 
     */
    public Process getProcess(int pid){
        for(int i=0; i<this.processesList.size(); i++){
            if(this.processesList.get(i).getId() == pid){
                return this.processesList.get(i);
            }
        }
        return null;
    }
    
    /**
     * Schedules readyProcesses.
     */
    private void schedule() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    /**
     * Updates the IO with the new clock and sees if any processes has to be unlocked.
     */
    private void updateIo() {
        for (int j=0; j<this.ioList.size(); j++){
                Process pid = this.ioList.get(j).decrementTimeLeft();
                if (pid != null){
                    this.unblock(pid.getId());
                }
        }
    }

    public DataMemory getData(int operand) {
        return this.dataMemory.get(operand);
    }

    /**
     * Adds a process to wait on an IO channel and blocks it.
     * @param pid
     * @param ioChannel 
     */
    public void addToIo(int pid, int ioChannel) {
        this.ioList.get(ioChannel).addProcess(this.getProcess(pid));
        this.blockProcess(pid);
        StringBuilder sb = new StringBuilder();
        sb.append("Process ");
        sb.append(pid);
        sb.append(" is waiting on IO ");
        sb.append(ioChannel);
        this.addLog(sb.toString());
    }

    /**
     * Blocks a process
     * @param pid 
     */
    private void blockProcess(int pid) {
        for (int j=0; j<this.processesList.size();j++){
            if (pid == this.processesList.get(j).getId()){
                StringBuilder sb = new StringBuilder();
                sb.append(clock);
                sb.append(": ");
                sb.append("Blocked process ");
                sb.append(pid);
                this.textLog.add(sb.toString());
                this.processesList.get(j).block();
                this.readyProcesses.remove(this.processesList.get(j));
            }
        }
    }
    
    /**
     * Prints to log
     * @param str The string to be printed
     */
    public void addLog(String str){
        StringBuilder sb = new StringBuilder();
        sb.append(clock);
        sb.append(": ");
        sb.append(str);
        this.textLog.add(sb.toString());
    }

    void forkProcess(int currentProcessId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
