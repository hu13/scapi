/**
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
* Copyright (c) 2012 - SCAPI (http://crypto.biu.ac.il/scapi)
* This file is part of the SCAPI project.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
* to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
* and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
* FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* We request that any publication and/or code referring to and/or based on SCAPI contain an appropriate citation to SCAPI, including a reference to
* http://crypto.biu.ac.il/SCAPI.
* 
* SCAPI uses Crypto++, Miracl, NTL and Bouncy Castle. Please see these projects for any further licensing issues.
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
*/
package edu.biu.scapi.circuits.garbledCircuit;

import java.security.InvalidKeyException;
import java.util.HashMap;

import edu.biu.scapi.circuits.circuit.BooleanCircuit;
import edu.biu.scapi.primitives.hash.CryptographicHash;
import edu.biu.scapi.primitives.prg.PseudorandomGenerator;

/**
 * A concrete implementation of GarbledBooleanCircuit that is generated by a pseudo random generator and a seed. <p>
 * This implementation is common for all types of circuits.<p>
 * It gets an input object in the constructor that defines which specific type of a circuit it really is.
 * 
 * @author Cryptography and Computer Security Research Group Department of Computer Science Bar-Ilan University (Moriya Farbstein)
 *
 */
public class GarbledBooleanCircuitSeedGenerationImp extends GarbledBooleanCircuitImp implements GarbledBooleanCircuitSeedGeneration{

	/**
	 * Constructor that gets an input object and creates the circuit with its contents.<P>
	 * The created circuit will be "empty", without garbled tables. <p>
	 * After this constructor the circuit is not complete, one of the generateKeysAndSetTables functions should be called in order to 
	 * create the underlying gates.
	 * @param input Specifies which concrete type of a circuit to implement.
	 */
	public GarbledBooleanCircuitSeedGenerationImp(CircuitInput input) {
		super(input);
	}
	
	/**
	 * Constructor that gets an input object, garbled tables and translation tables and creates the circuit with them.<p>
	 * After this constructor the circuit is complete and ready to be used.
	 * @param input input specifies which concrete type of circuit to implement.
	 * @param garbledTables 
	 * @param outputWireValues
	 */
	public GarbledBooleanCircuitSeedGenerationImp(CircuitInput input, byte[][] garbledTables, HashMap<Integer, Byte> outputWireValues){
		super(input, garbledTables, outputWireValues);
	}
	
	@Override
	public CircuitSeedCreationValues garble(BooleanCircuit ungarbledCircuit, PseudorandomGenerator prg, byte[] seed, CryptographicHash hash) throws InvalidKeyException {
		//Call the utility class to generate the keys and create the garbled tables.
		CircuitSeedCreationValues values = util.garble(ungarbledCircuit, garbledTablesHolder, gates, prg, seed, hash);
		outputWireValues = values.getOutputWireValues();
		return values;
	}
	
	@Override
	public boolean verify(BooleanCircuit ungarbledCircuit, PseudorandomGenerator prg, byte[] seed, CryptographicHash hash, byte[] hashedCircuit) throws InvalidKeyException {
		//Call the utility class to get the result of the hash function on the garbled tables created from the boolean circuit, prg and seed.
		byte[] hashedTables = util.getHashedTables(ungarbledCircuit, prg, seed, hash);
		
		//Verify that the calculated hash result is equal to the given hashedCircuit.
		return checkHashedTablesEquality(hashedCircuit, hashedTables);
	}
		
	@Override
	public boolean verifyGarbledTables(CryptographicHash hash, byte[] hashedCircuit) {
		byte[][] garbledTables = garbledTablesHolder.getGarbledTables();
		int size = garbledTables.length;
		//put each garbled table in the hash.
		for (int i=0; i<size; i++){
			if (garbledTables[i] != null){
				hash.update(garbledTables[i], 0, garbledTables[i].length);
			}
		}
		
		//compute the hash function on the garbled tables.
		byte[] hashedTables = new byte[hash.getHashedMsgSize()];
		hash.hashFinal(hashedTables, 0);
		
		//Verify that the calculated hash result is equal to the given hashedCircuit.
		return checkHashedTablesEquality(hashedCircuit, hashedTables);
	}
		
	/**
	 * Compares between two byte arrays.
	 * @param hashedCircuit
	 * @param hashedTables
	 * @return true if the arrays are equal; false, otherwise.
	 */
	private boolean checkHashedTablesEquality(byte[] hashedCircuit, byte[] hashedTables) {
		int size = hashedCircuit.length;
		//Compare the lengths.
  		if (size!=hashedTables.length){
  			return false;
  		}
  		
  		//compare each byte.
  		for (int i=0; i<size; i++)
  		{
  			if (hashedCircuit[i] != hashedTables[i]){
  				return false;
  			}
  		}
  		return true;
	}

}
