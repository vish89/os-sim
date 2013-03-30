/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oabahuss
 */
public class InstructionMemory {
    int pid;
    
    /**
     * Unlocks the memory if the given pid is the same as the locker.
     * @param pid 
     */
    public boolean unlock(int pid) {
        if (this.pid == pid){
            this.pid = 0;
            return true;
        }
        return false;
    }
    
}
