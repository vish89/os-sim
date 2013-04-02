
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * An OS simulator!
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
    private Queue<Process> readyProcesses; //Queue must be sorted for scheduling.
    
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
        this.readyProcesses = new LinkedList<>();
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
    public void jumpClock(int clock) {
        int i = clock;
        while(i>0){
            this.incrementClock();
            i--;
        }
    }
    
    public Object[] getLog(){
        return textLog.toArray();
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
        if (this.readyProcesses.peek() == null){
            StringBuilder sb = new StringBuilder();
            sb.append("No processes to run...");
            this.addLog(sb.toString());
        }
        else {
            int instructionAddr = this.readyProcesses.peek().getNextInstruction();
            boolean success = this.cpu.execute(instructionAddr, this.instructionMemory[instructionAddr]);
            if (success) {
                this.readyProcesses.peek().incrementPc();
                StringBuilder sb = new StringBuilder();
                sb.append(clock);
                sb.append(": ");
                sb.append("Successfully executed instruction memory #");
                sb.append(instructionAddr);
                sb.append(" for process ");
                sb.append(this.readyProcesses.peek().getId());                       
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
     * Not implemented yet.
     */
    private void schedule() {
        this.readyProcesses.add(this.readyProcesses.poll()); //RR express :P
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
     * Prints to log prefixed by a timestamp of the simulator's clock.
     * @param str The string to be printed
     */
    public void addLog(String str){
        StringBuilder sb = new StringBuilder();
        sb.append(clock);
        sb.append(": ");
        sb.append(str);
        this.textLog.add(sb.toString());
    }

    /**
     * Creates a child process and adds it to the simulation.
     * TODO: Forking doesn't work yet.
     * @param pid The parent's process ID. 
     */
    public void forkProcess(int pid) {
        Process parent = this.getProcess(pid);
        Process child = new Process(parent);
        this.addProcess(child, null);
    }
    
    /**
     * Adds a new process to the list of processes, and allocates memory for it.
     * Faulty...
     * @param newProcess The new process.
     * @param instructions The process instructions.
     * @return true if the process was added successfully.
     */
    public boolean addProcess(Process newProcess, InstructionMemory[] instructions){
        if (newProcess.getParentId() == 0){ //Case its an orphan process
            if (instructions.length != newProcess.getLength()){
                return false;
            }
            
            int startingAddress = this.findMemory(newProcess.getLength());
            StringBuilder sb1 = new StringBuilder();
            sb1.append("Attempting to add a new process of length ");
            sb1.append(newProcess.getLength());
            sb1.append(". Total processes count is ");
            sb1.append(this.processesList.size());
            this.addLog(sb1.toString());
            if (startingAddress == -1){
                StringBuilder sb = new StringBuilder();
                sb.append("Out of memory; failed adding a new process");
                this.addLog(sb.toString());
                return false;
            }
            int pid = this.findId();
            newProcess.setId(pid);
            newProcess.setStartingAddress(startingAddress);
            
            this.readyProcesses.add(newProcess);
            this.processesList.add(newProcess);

            for (int i=0; i<instructions.length; i++){
                this.instructionMemory[i+startingAddress] = instructions[i];
                this.instructionMemory[i+startingAddress].setOwner(pid);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Added new process ");
            sb.append(pid);
            sb.append(" with starting address at ");
            sb.append(startingAddress);
            this.addLog(sb.toString());
            return true;
        }
        else { //This is just a child of another process. Feel free to squeeze its cheeks. 
            int pid = this.findId();
            newProcess.setId(pid);
            StringBuilder sb = new StringBuilder();
            sb.append("Added new child process ");
            sb.append(pid);
            sb.append(" from parent ");
            sb.append(newProcess.getParentId());
            this.addLog(sb.toString());
            return true;
        }
    }
    
    /**
     * Finds the starting address of a memory gap that fits a desired length.
     * A naive algorithm that I think runs in O(n).
     * @param length The required gap length.
     * @return The starting address of the memory gap. -1 If none found.
     */
    private int findMemory(int length){
        int count = 0;
        for (int i = 0; i<this.instructionMemory.length; i++){
            if (this.instructionMemory[i].isEmpty()){
                count++;
                
                if (count == length) {
                    return i-count+1;
                } //Success! Return the address of the first step.
            }
            else {
                count = 0; //reset...
            }
        }
        return -1; //Failed.
    }

    /**
     * Finds an pid that is currently not in use by another process.
     * @return the PID.
     */
    private int findId() {
        int pid = 1;
        while (true){
            if (this.getProcess(pid) == null){
                return pid;
            }
            else {
                pid++;
            }
        }
    }
    
}
