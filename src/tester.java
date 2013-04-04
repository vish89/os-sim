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
        mem[0].setMemory(0, 4, 0);
        mem[1].setMemory(0, 4, 1);
        mem[2].setMemory(0, 5, 0);
        
        InstructionMemory[] mem2 = new InstructionMemory[3]; //3 CPU bound insts
        mem2[0] = new InstructionMemory();
        mem2[1] = new InstructionMemory();
        mem2[2] = new InstructionMemory();
        mem2[0].setMemory(0, 4, 1);
        mem2[1].setMemory(0, 4, 0);
        mem2[2].setMemory(0, 5, 1);
        
        
        Process p1 = new Process(1,3); //Priority 1, length 3
        Process p2 = new Process(1,3);
   
        
        sim.addProcess(p1, mem);
        sim.addProcess(p2, mem2);
      
        
        sim.jumpClock(15);
        Object[] log = sim.getLog();
        for (int i = 0; i<log.length; i++){
           System.out.println(log[i]);
        }
        
        //InstructionMemory[] inst = sim.getInstructionMemory();
        //for (int i = 0; i<10; i++){
        //    System.out.println(inst[i].getOwner());
        //}
    }
}
