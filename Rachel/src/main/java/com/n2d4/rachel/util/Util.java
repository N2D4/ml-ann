package com.n2d4.rachel.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * General utility class.
 * <p>
 * Sub-classing is supported.
 * 
 * @author N2D4
 */
public class Util {
	
	private static final Random random = new N2Random();
	
	private Util() {
		Exceptions.privateConstructor();
	}
	
	/**
	 * Returns a static {@link java.util.Random Random} object with a fixed seed. Attempting to modify the seed will throw a runtime exception.
	 * 
	 * @return a static Random object.
	 */
	public static Random getRandom() {
		return random;
	}
	
	private static class N2Random extends Random {
		private static final long serialVersionUID = 1L;
		private boolean locked = false;
		
		protected N2Random() {
			super();
			locked = true;
		}
		
		@Override synchronized public void setSeed(long seed) {
			if (locked) throw new UnsupportedOperationException("Can't change the seed of a Random object created by N2D4's Util class.");
			super.setSeed(seed);
	    }
	}
	
	
	
	/**
	 * Creates a {@link java.util.Set Set} from the given array.
	 * 
	 * @param arr The array.
	 * @return The HashSet.
	 */
	@SafeVarargs
	public static <T> Set<T> asSet(T... arr) {
		Set<T> result = new HashSet<T>(arr.length);
		for (T a : arr) {
			result.add(a);
		}
		return result;
	}
	
	
	/**
	 * Returns the first argument given and trashes the other arguments. This is useful in certain situations where the compiler requires you that certain things happen on a single line of code, eg. in constructors of sub-classes. For example, the following would compile:
	 * <pre>{@code public class MyClass extends MySuperClass {
	 *    public MyClass(SomeOtherClass c) {
	 *        super(Util.returnFirst(c, someFunction(c)));
	 *    }
	 * }</pre>
	 * 
	 * While the following would not:
	 * 
	 * <pre>{@code public class MyClass extends MySuperClass {
	 *    public MyClass(SomeOtherClass c) {
	 *        someFunction(c);
	 *        super(c);
	 *    }
	 * }</pre>
	 * 
	 * The order of execution is the same in both scenarios (unless c is a function itself) - someFunction(...) will execute first, second the super-class constructor.
	 * 
	 * 
	 * @param ret the argument to be returned.
	 * @param args an array of arguments that will be thrown away.
	 * @return the first argument passed.
	 */
	public static <T> T returnFirst(T ret, Object... args) {
		return ret;
	}
	
	
	
	/**
	 * Increments the first element of the second array by 1. If said element is equal to or larger to the respective element in the first array, it gets set to 0 and the step repeats with the second element. Returns {@code false} if the recursion stopped because {@code max.length} has been reached, or {@code true} otherwise.
	 * <br>
	 * This is most useful in combination with loops:
	 * <pre>{@code int[] max = new int[] {5, 10, 2, 8};
	 * int[] cur = elementWiseLoop(max.length);
	 * while (Util.elementWiseIncrement(max, cur)) {
	 *     // do something
	 * }</pre>
	 * 
	 * 
	 * @param cur the iterator array
	 * @param max the max value array
	 * @return a boolean determining if max has been reached
	 * @see Util#elementWiseLoopInit(int)
	 */
	public static boolean elementWiseIncrement(int[] cur, int[] max) {
		Requirements.nonNull(max, "max value array");
		Requirements.nonNull(cur, "iterator array");
		Requirements.all(Requirements::positive, max, "max");
		Requirements.equal(cur.length, max.length, "iterator array length");
		
		for (int i = 0; i < cur.length; i++) {
			if (++cur[i] >= max[i]) cur[i] = 0;
			else return true;
		}
		return false;
	}
	
	
	/**
	 * Returns an array that can be used in conjunction with {@link Util#elementWiseIncrement(int[], int[]) Util.elementWiseIncrement(...)}.
	 * 
	 * @param length the length of the returned array
	 * @return the array
	 * @see Util#elementWiseIncrement(int[], int[])
	 */
	public static int[] elementWiseLoopInit(int length) {
		Requirements.nonNegative(length, "length");
		
		int[] result = new int[length];
		result[0] = -1;
		return result;
	}
	
	
	/**
	 * Returns an integer equal to cur[n] * max[n-1] * max[n-2] * ... * max[0] + cur[1] * max[2] * max[3] * ... * max[n] + ... + cur[1] * max[0] + cur[0]. {@code Util.elementWiseIncrement(cur, max); Util.toFlatInt(cur, max)} is always equal to {@code Util.toFlatInt(cur, max) + 1} (if {@code cur} has not yet reached {@code max}).
	 * 
	 * @param cur the array to convert
	 * @param max the max value array
	 * @return an integer that is unique for every combination of cur returned by {@link Util#elementWiseIncrement(int[], int[]) Util.elementWiseIncrement(...)}
	 * @see Util#elementWiseIncrement(int[], int[])
	 */
	public static int toFlatInt(int[] cur, int[] max) { // TODO Fix javadocs
		Requirements.nonNull(max, "max value array");
		Requirements.nonNull(cur, "iterator array");
		Requirements.equal(cur.length, max.length, "iterator array length");
		
		int result = 0;
		for (int i = cur.length - 1; i >= 0; i--) {
			int mr = cur[i];
			for (int j = i - 1; j >= 0; j--) {
				mr *= max[j];
			}
			result += mr;
		}
		return result;
	}
	
}
