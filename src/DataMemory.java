
import java.util.Queue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oabahuss
 */
class DataMemory {
    Queue<Integer> processQueue;
    int lockerProcessId;
    
    
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
}

