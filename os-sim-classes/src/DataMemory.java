
import java.util.LinkedList;
import java.util.Queue;

/**
 * A data memory with lock. To be used in conjunction with the whole simulation package.
 * @author oabahuss
 */
class DataMemory {
    private Queue<Integer> processQueue;
    private int lockerProcessId;
    private int authorProcessId;
    
    /**
     * A constructor. Initiates everything to zero.
     */
    public DataMemory(){
        this.authorProcessId = 0;
        this.lockerProcessId = 0;
        this.processQueue = new LinkedList<>();
    }
    
    /**
     * Checks if the memory is locked or not.
     * @return True if it is locked. 
     */
    public boolean isLocked(){
        if (this.lockerProcessId == 0){
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Locks the memory and sets the lockerProcessId to the param pid.
     * Adds the pid to a queue if it was locked by a different process.
     * @param pid The new locker pid.
     * @return False if it was already locked by a different process.
     */
    public boolean lock(int pid){
        if ((this.lockerProcessId == 0)||(this.lockerProcessId == pid)){
            this.lockerProcessId = pid;
            return true;
        }
        else {
            this.processQueue.add(pid);
            return false;
        }
    }
    
    /**
     * Unlocks the memory and returns the next popped pid from the processQueue.
     * If the given pid matches the lockerProcessId.
     * @param pid A claiming locker.
     * @return Next pid from processQueue, 0 if none. -1 if lock doesn't match.
     */
    public int unlock(int pid){
        if (lockerProcessId != pid){
            return -1;
        }
        else{
            if (processQueue.peek()== null){
                return 0;
            }
            else {
                lockerProcessId = processQueue.poll();
                return lockerProcessId;
            }
        }
    }
    
    /**
     * Writes on the memory by the calling process. (sets authorProcessId to pid)
     * Adds the pid to a queue if it was locked by a different process.
     * @param pid The new authorProcessId.
     * @return False if it was already locked by a different process.
     */
    public boolean write(int pid){
        if ((this.lockerProcessId == 0)||(this.lockerProcessId == pid)){
            this.authorProcessId = pid;
            return true;
        }
        else {
            this.processQueue.add(pid);
            return false;
        }
    }
    
    /**
     * Reads the memory by the calling process. (sets authorProcessId to pid)
     * Adds the pid to a queue if it was locked by a different process.
     * @param pid The reader's pid
     * @return False if it was already locked by a different process.
     */
    public boolean read(int pid){
        if ((this.lockerProcessId == 0)||(this.lockerProcessId == pid)){
            return true;
        }
        else {
            this.processQueue.add(pid);
            return false;
        }
    }
}

