public class Test {

    public void testAll() {
        Assembler assembler = new Assembler();
        /**
         * All the test strings are ran here
         */

        runTest(assembler, "110001000110111010111010"); // debug to TRUE
        runTest(assembler, "000000000111111111111111"); // loadR
        runTest(assembler, "000100010111111111111111"); // loadL

        runTest(assembler, "001000100110111010111010"); // add
        runTest(assembler, "001100110110111010111010"); // sub
        runTest(assembler, "010001000110111010111010"); // mul

        runTest(assembler, "010101010110111010111010"); // storeR

        runTest(assembler, "011001100110101011101010"); // inc
        runTest(assembler, "011101110110101011101010"); // dec
        runTest(assembler, "011001100000000000000011"); // inc the number 3
        runTest(assembler, "011101110000000000000011"); // dec the number 3

        runTest(assembler, "100010000000000000000011"); // jump
        //runTest(assembler, "110001000110111010111010"); // debug to FALSE
        runTest(assembler, "100110010000000000000011"); // jumpOnZero
        runTest(assembler, "101010100000000000000011"); // jumpOnNonZero

        runTest(assembler, "110110100000000000000011"); // showMemory

        runTest(assembler, "101110100000000000000011"); // stop
    }

    private void runTest(Assembler assembler, String binaryNum) {
        try {
            System.out.println("\nRunning test input: " + binaryNum);
            char[] arrBinary = binaryNum.toCharArray();

            // Extract opcode (first 4 bits)
            String opcode = binaryNum.substring(0, 4);

            // Extract operand 1 (bits 4 to 7 — 4-bit register)
            String operand1 = binaryNum.substring(4, 8);

            // Extract operand 2 (bits 8 to 23 — 16-bit value)
            String operand2 = binaryNum.substring(8, 24);

            assembler.controller(opcode, operand1, operand2);

        } catch (Exception e) {
            System.out.println("Error invoked by test class: " + e.getMessage());
        }
    }
}
