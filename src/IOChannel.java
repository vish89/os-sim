
import java.util.LinkedList;
import java.util.Queue;


/**
 * A class that simulates an IO channel, to be used in conjunction with the whole
 * library.
 * @author oabahuss
 */
class IOChannel {
    private int delay;
    private int timeLeft;
    private Queue<Process> processQueue;
    
    /**
     * A class constructor.
     * @param delay The delay this IO channel takes process a process.
     */
    public IOChannel(int delay){
        this.delay = delay;
        processQueue = new LinkedList<>();
    }
    
    /**
     * Adds a process to the IO queue.
     * @param pid 
     */
    public void addProcess(Process pid){
        if (!(this.processQueue.isEmpty())){
                this.timeLeft = this.delay;
        }
        this.processQueue.add(pid);
    }
    
    /**
     * Updates the IO channel time and returns a process if one is done.
     * @return The process that ended using the IO, null if none.
     */
    public Process decrementTimeLeft() {
        if (this.timeLeft > 0){
            this.timeLeft --;
            return null;
        }
        else {
            Process pid = this.processQueue.poll();
            if (!(this.processQueue.isEmpty())){
                this.timeLeft = this.delay;
            }
            return pid;
        }
    }
    
}
