package com.n2d4.rachel.util.math;

import org.nd4j.linalg.api.buffer.DataBuffer.Type;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.n2d4.rachel.util.Exceptions;

/**
 * A util class for the library Nd4j.
 * <p>
 * Sub-classing is supported.
 * 
 * @author N2D4
 *
 */
public class Nd4jUtils {
	
	private Nd4jUtils() {
		Exceptions.privateConstructor();
	}
	
	/**
	 * Returns the element-wise logarithm where negative infinity values are replaced by the smallest finite value.
	 * 
	 * @param arr the array.
	 * @return the result.
	 */
	public static INDArray zLog(INDArray arr) {
		INDArray result = Transforms.log(arr);
		BooleanIndexing.applyWhere(result, Conditions.isInfinite(), getMaxNegativeValue(result.data().dataType()));
		return result;
	}
	
	/**
	 * Returns the maximum value for a given type.
	 * 
	 * @param type the type
	 * @return
	 */
	public static Number getMaxValue(Type type) {
		switch (type) {
		case DOUBLE:
			return Double.MAX_VALUE;
		case FLOAT:
			return Float.MAX_VALUE;
		case INT:
			return Integer.MAX_VALUE;
		default:
			throw new UnsupportedOperationException("Only doubles, floats and ints are supported at the moment.");
		}
	}
	
	/**
	 * Returns the maximum value for a given type, but negative. (Smallest finite negative value, furthest away from 0)
	 * 
	 * @param type the type
	 * @return
	 */
	public static Number getMaxNegativeValue(Type type) {
		switch (type) {
		case DOUBLE:
			return -Double.MAX_VALUE;
		case FLOAT:
			return -Float.MAX_VALUE;
		case INT:
			return Integer.MIN_VALUE;
		default:
			throw new UnsupportedOperationException("Only doubles, floats and ints are supported at the moment.");
		}
	}
	
	/**
	 * Creates a {@link org.nd4j.linalg.api.rng.Random} from a {@link java.util.Random}. Technically, it will read a long value from the passed random, then use that for the Nd4j seed. This means that calling the function twice will result in two entirely different outputs.
	 * 
	 * @param javaRandom the java.util.Random.
	 * @return a org.nd4j.linalg.api.rng.Random.
	 */
	public static org.nd4j.linalg.api.rng.Random nextNd4jRandom(Random javaRandom) {
		return new org.nd4j.linalg.api.rng.DefaultRandom(javaRandom.nextLong());
	}
}
