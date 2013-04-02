/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oabahuss
 */
public class tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulator sim = new Simulator(1, 0,1);
        
        //Process 1
        InstructionMemory[] mem = new InstructionMemory[3]; //3 CPU bound insts
        mem[0] = new InstructionMemory();
        mem[1] = new InstructionMemory();
        mem[2] = new InstructionMemory();
        Process p1 = new Process(1,3); //Priority 1, length 3
        Process p2 = new Process(1,3);
        Process p3 = new Process(1,3);
        
        sim.addProcess(p1, mem);
        sim.addProcess(p2, mem);
        sim.addProcess(p3, mem);
      
        
        sim.jumpClock(9);
        Object[] log = sim.getLog();
        for (int i = 0; i<log.length; i++){
           System.out.println(log[i]);
        }
    }
}
