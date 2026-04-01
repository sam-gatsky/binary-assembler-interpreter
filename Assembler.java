import java.util.Scanner;

public class Assembler {
    /**
     * 24 bit memory module -- RAM
     * setMemory sets new data in memory, the setter
     */
    static String[] memory = new String[24];
    /**
     * additional debugging feature, see answer sheet for more info
     * TO ENTER DEBUG MODE, FIRST 4 BITS MUST BE 1100, THE REST DON'T MATTER
     * Example string to enter debugging: 110001000110111010111010
     */
    public static boolean debug = false;
    public static boolean toggleDebug(){
        System.out.println("Debug mode is set to: " + !debug);
        return debug = !debug;
    }

    /**
     * By default, called when running the assembler for the first time ensuring atomic values
     */
    public static void memoryWipe(){
        for(int i = 0; i <= 23; i++){
            memory[i] = "000000000000000000000000";
        }
    }

    /**
     * Prints all contents of the memory module in the terminal
     */
    public static void showMemory(){
        if(debug) {
            System.out.println();
            System.out.println("__-- Memory Module --__");
        }
        for(int i = 0; i <= 23; i++){
            int index = i + 1;
            if(debug) {
                System.out.println("[Reg" + index + "] " + memory[i]);
            }
            else {
                System.out.println(memory[i]);
            }
        }
    }
    /**
     * Values of all the registers.
     * Each register has a memory slot allocated to it.
     */
    // Static register indexes
    static int reg1 = 0; // reg
    static int reg2 = 1; // addr
    static int reg3 = 2; // val
    static int reg4 = 3; // dreg
    static int reg5 = 4; // sreg
    static int reg6 = 5; // counter

    public static void setMemory(int index, String data){
        memory[index] = data;
        if (debug) {
            System.out.println("memory index: [" + index + "] updated to " + data);
        }
        else{
            System.out.println(data);
        }
    }

    public static String getMemory(int index){
        String value = memory[index];
        return value;
    }

    public static String grabValue(int index){
        char[] regArr = getMemory(index).toCharArray(); // gets record from register (reg)
        char[] regValueArr = new char[16];
        for(int i = 7, j = 0; i < 23; i++, j++){ // grabs the value field from the binary string
            regValueArr[j] = regArr[i];
        }
        String regValue = new String(regValueArr);
        return regValue;
    }

    public static void main(String[] args) {
        memoryWipe(); // fills the memory with 0s
        Scanner scanner = new Scanner(System.in); // 24 bit instruction input
        String binaryNum;

        Test test = new Test(); // import test class
        test.testAll(); // run test class

        // below code is for direct commandline input support

        do {
            binaryNum = scanner.nextLine(); // scoops input from terminal

            if (binaryNum.length() != 24){
                System.out.println("Only 24 bit strings are accepted, exiting...");
                break;
            }

            /*
             * Checks the opcode, the first 4 bits
             */
            char[] arrBinary = binaryNum.toCharArray(); // TARGET THIS to change bits
            char[] opcodeArr = new char[4];
            for(int i = 0; i <= 3; i++){
                opcodeArr[i] = arrBinary[i];
            }
            String opcode = new String(opcodeArr); // opcode is now accessible
            System.out.println(opcode + " is the opcode");

            /*
             * Checks operands, the next bits
             */
            char[] operandArr1 = new char[4]; // allows for 4 bit registers
            char[] operandArr2 = new char[16]; // allows for 16 bit addresses

            /*
             * Extracts both operand values
             * Operand 1 == 4 bit register value
             * Operand 2 == 16 bit address value
             */
            // Operand 1
            try {
                for (int sourceI = 4, destI = 0; sourceI <= 7 && destI <= 3; sourceI++, destI++) {
                    operandArr1[destI] = arrBinary[sourceI];
                }
                // Operand 2
                for (int sourceI = 8, destI = 0; sourceI <= arrBinary.length - 1 && destI <= operandArr2.length - 1; sourceI++, destI++) {
                    operandArr2[destI] = arrBinary[sourceI];
                }
                String operand1 = new String(operandArr1); // opcode is now accessible
                String operand2 = new String(operandArr2);
                if (debug){
                    System.out.println(operand1 + " is Register1");
                    System.out.println(operand2 + " is Register2");
                }

                /*
                 * Variables are parsed to the controller which will create new memory segments
                 */
                controller(opcode, operand1, operand2);
            }
            catch(Exception e){
                System.out.println("Invalid input" + e.getMessage());
            }
        }
        while (binaryNum != null) ;
    }

    public static void controller(String opcode, String operand1, String operand2){
        /**
         * Additional debug settings, see answer sheet for more info
         */
        if(opcode.contains("1100")){ // debug
            toggleDebug();
        }
        if(debug){ // when debug is true, extracts and presents information from the binary string
            System.out.println("[opcode] " + opcode + " | [reg1] " + operand1 + " | [reg2] " + operand2);
        }
        if(opcode.contains("1101")){ // showMemory
            showMemory();
        }

        /**
         * Assembler functions
         */
        if(opcode.contains("0000") || opcode.contains("0001")){ // loadR, loadI
            writeMemory(opcode, operand1, operand2);
        }
        if(opcode.contains("0010") || opcode.contains("0011") || opcode.contains("0100")){ // add, sub
            arithmetic(opcode, operand1, operand2);
        }
        if(opcode.contains("0101")){ // storeR
            writeMemory(opcode, operand1, operand2);
        }
        if(opcode.contains("0110") || opcode.contains("0111")){ // inc, dec
            incrementDecrement(opcode, operand1, operand2);
        }
        if(opcode.contains("1000") || opcode.contains("1001") || opcode.contains("1010")){ // jump, jumpOnZero, jumpOnNonZero
            jump(opcode, operand1);
        }
        if(opcode.contains("1011")){ // stop
            stopAssembler(opcode);
        }
    }

    /**
     * @param address Address of the memory
     * Load the value in memory[addr] into reg.
     */
    public static void writeMemory(String opcode, String register, String address){
        Assembler assembler = new Assembler();
        String data = new String();
        if (opcode.contains("0000") || opcode.contains("0001")){ // LoadR and LoadI
            data = opcode + register + address;
            setMemory(reg1, data);
        }
        if (opcode.contains("0101")){ // storeR
            String r1ArrayLine = getMemory(0); // get the value of reg/r1

            char[] r1Arr = r1ArrayLine.toCharArray(); // string line from r1
            char[] r1ValueArr = new char[16]; // blank array to take on the values we want
            for(int i = 7, j = 0; i <= 23 && j <= 15; i++, j++){ // these can't use the same index
                r1ValueArr[j] = r1Arr[i]; // fixes out of bounds issue
            }
            String r1Values = new String(r1ValueArr); // values are now accessible


            data = opcode + register + r1Values; // combine it with the opcode
            if (debug){
                System.out.println("[0]" + memory[0] + " is replacing [1] with: " + data);
            }
            setMemory(reg2, data); // write to r2
        }
    }

    /**
     * adds a 4 bit number with up to a 16 bit number
     */
    public static void arithmetic(String opcode, String dReg, String sReg){
        //Assembler assembler = new Assembler();

        int num1 = Integer.parseInt(dReg, 2);
        // converting binary string into integer(decimal number)

        int num2 = Integer.parseInt(sReg, 2);
        // converting binary string into integer(decimal number)

        int sum = 0;
        /**
         * depending on opcode, the function will do a different operation
         */
        if (opcode.contains("0010")) { // If opcode is add...
            sum = num1 + num2;
            if(debug) {
                System.out.println(dReg + " + " + sReg);
            }
        }
        if (opcode.contains("0011")) { // If opcode is sub...
            if(num1 >= num2){ // first number is always higher
                sum = num1 - num2;
                if(debug) {
                    System.out.println(dReg + " - " + sReg);
                }
            }
            else {
                sum = num2 - num1;
                if(debug) {
                    System.out.println(sReg + " - " + dReg);
                }
            }
        }
        if (opcode.contains("0100")){ //If opcode is mul...
            sum = num1 * num2;
            if(debug){
                System.out.println(dReg + " * " + sReg);
            }
        }
        /**
         * packing up and exporting the binary string to memory
         */
        String result = Integer.toBinaryString(sum); // won't include first value if 0
        String paddedSum = String.format("%20s", result).replace(' ', '0'); // enforces 20bit result
        String data = opcode + paddedSum; // 24 bit result
        setMemory(reg4, data); // leaves the result in dreg
    }

    /**
     * Add/Subtract 1 to the value in register reg using 2’s complement arithmetic.
     * __--- STEPS ---__
     * 1: Flip all binary bits
     * 2: Add 1 to the base value
     * 3: Increment/Decrement
     * @param opcode will either increment(0110) or decrement(0111)
     * @param register  states specific instruction register
     * @param value binary value in 2s complement arithmetic
     */
    public static void incrementDecrement(String opcode, String register, String value){
        char[] lineValues = value.toCharArray(); // individual binary bits from the value into an array
        char[] indvValues = new char[16]; // array to write to with new bits

        for(int i = 0; i < indvValues.length; i++){ // invert bits
            if(lineValues[i] == '0'){
                indvValues[i] = '1'; // 0s are flipped to 1 and sent to "individual values" array
            }
            else if(lineValues[i] == '1'){
                indvValues[i] = '0';
            }
        }
        String invertedString = new String(indvValues); // string of the original inverted value

        String result = "";
        if (opcode.contains("0110")){ // Increment instruction
            int decimal = Integer.parseInt(invertedString, 2);
            int newDecimal = decimal + 1; // add one
            result = Integer.toBinaryString(newDecimal);
        }
        else if (opcode.contains("0111")) { // Decrement instruction
            int decimal = Integer.parseInt(invertedString, 2);
            int newDecimal = decimal - 1; // sub one
            result = Integer.toBinaryString(newDecimal);
        }
        else{
            return; // invalid opcode
        }
        String paddedResult = String.format("%16s", result).replace(' ', '0'); // void safety
        // 0 replaces null for Two's Complement integrity


        String data = opcode + register + paddedResult;// 24 bit result
        setMemory(reg1, data); // leaves the result in reg
    }
    /**
     * Grabs the value from R2 (the address) and stores it in R6 (the counter)
     * @param opcode decides on the exact jump operation
     * @param register states specific instruction register
     */

    public static void jump(String opcode, String register){

        String addressValue = grabValue(reg2);
        String regValue = grabValue(reg1);

        try {
            if (opcode.contains("1000")) { // given jump opcode
                setMemory(reg6, opcode + register + addressValue); // store address in counter
            }
            if (opcode.contains("1001")) { // given jumpOnZero opcode
                if (regValue.equals("0000000000000000")) { // if value in reg is zero
                    setMemory(reg6, opcode + register + addressValue); // store address in counter
                }
                else if(debug){
                    System.out.println("reg / R1 is NOT zero, no action was performed");
                }
            }
            if (opcode.contains("1010")) { // given jumpOnNonZero opcode
                if (!regValue.equals("0000000000000000")) { // if value in reg is NOT zero
                    setMemory(reg6, opcode + register + addressValue); // store address in counter
                }
                else if(debug){
                    System.out.println("reg / R1 IS zero, no action was performed");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param opcode field must be 1011 for a successful termination of the assembler
     */
    public static void stopAssembler(String opcode){
        try{
            if(opcode.contains("1011")){
                System.out.println("Stop code acknowledged, terminating");
                System.exit(0); // 0 will terminate the script given the right opcode
            }
            else {
                System.out.println("Invalid opcode, unsuccessful termination");
                System.exit(1);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
