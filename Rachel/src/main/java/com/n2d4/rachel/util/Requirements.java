package com.n2d4.rachel.util;

import java.util.Iterator;

/** 
 * Inspired from Java's and Guava's util methods for argument verification. Didn't stop me from making my own.
 * <p>
 * Sub-classing is supported - but be sure to make the constructor private. Methods should follow the methodName(T argument, String name) structure when possible to make {@link #all(Requirement, T[], String) all(...)} as simple as possible.
 * <p>
 * When sub-classing, make use of the methods {@link #throwError(String, Object...) throwError(...)} and {@link #throwErrorIf(boolean, String, Object...) throwErrorIf(...)}. See {@link #throwError(String, Object...) throwError(...)} for more.
 * 
 * @author N2D4
 * 
 * @see #throwError(String, Object...)
 * @see #all(Requirement, String, T...)
 * @see java.util.Objects#nonNull(Object)
 */
public class Requirements {
	
	private Requirements() {
		Exceptions.privateConstructor();
	}
	
	/** 
	 * Similar to {@link #throwError(String, Object...) throwError(...)}, but only throws an error if condition is true.
	 * 
	 * @param condition Whether the error should be thrown or not.
	 * @return Whether the error was thrown or not. Depending on the implementation, this method might never return true because an exception might've interrupted the flow.
	 * 
	 * @see #throwError(String, Object...)
	 */
	public static final boolean throwErrorIf(boolean condition, String messageType, Object... args) {
		if (condition) throwError(messageType, args);
		return condition;
	}
	
	/**
	 * Throws an error. The default implementation will throw an IllegalArgumentException - however, this is not guaranteed, and sub-classes might override this behavior.
	 * <p>
	 * Make use of the class {@link Requirements.FormatStrings FormatStrings} as message types.
	 * 
	 * @param messageType A format String
	 * @param args Arguments to format messageType with
	 * 
	 * @see #throwErrorIf(boolean, String, Object...)
	 * @see #FormatStrings
	 * @see String#format(String, Object...)
	 */
	protected static void throwError(String messageType, Object... args) {
		throw new IllegalArgumentException(Strings.substringToUpperCase(String.format(messageType, args), 0, 1));
	}
	
	
	
	
	/**
	 * Iterates through an object and checks every item for a specific requirement.
	 * <p>
	 * {@link Iterables#iterator(Object) Iterables.iterator(...)} will be used.
	 * <p>
	 * One should use method references whenever possible. For example, if you want to check the array arr for {@code null} values, do the following:
	 * <p>
	 * {@code Requirements.all(Requirements::nonNull, array, "array")}
	 * <p>
	 * Some methods (with arguments other than {@code T, String}) may need a Lambda expression:
	 * <p>
	 * {@code Requirements.all((i, name) -> Requirements.largerThan(T, name, 5), array, "array")}
	 * 
	 * @param <T> the object's type.
	 * @param <U> the requirement's type.
	 * @param requirement the requirement. Lambda expressions and method references should be used.
	 * @param array the array (or Iterable) itself.
	 * @param arrayName name of the array.
	 * @return the array itself.
	 * @throws ClassCastException when the passed object can't be cast to the required class.
	 * 
	 */
	public static final <T, U> T all(Requirement<U> requirement, T array, String arrayName) throws ClassCastException {
		nonNull(array, arrayName);
		
		Iterator<U> iterator = Iterables.iterator(array);
		
		int i = 0;
		while (iterator.hasNext()) {
			try {
				U next = iterator.next();
				requirement.check(next, arrayName + "[" + i++ + "]");
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Second argument is of generic type T, but elements not of generic type U (as required by the first argument)", e);
			}
		}
		return array;
	}
	
	
	
	
	
	/**
	 * Checks if the given object is null, throwing an error if this is the case.
	 * 
	 * @param object The object to check.
	 * @param name The object's name.
	 * @return The object.
	 */
	public static <T extends Object> T nonNull(T object, String name) {
		throwErrorIf(object == null, FormatStrings.mustNotBe, name, "null");
		return object;
	}
	
	
	
	
	
	/**
	 * Checks if the given array is null or has a size of 0, throwing an error if either is the case.
	 * 
	 * @param object The object to check.
	 * @param name The object's name.
	 * @return The object.
	 */
	public static <T extends Object> T[] nonEmpty(T[] object, String name) {
		Requirements.nonNull(object, name);
		Requirements.positive(object.length, name + " length");
		return object;
	}
	
	
	
	
	
	
	/**
	 * Checks if the given object is null, throwing an error if this is the case.
	 * 
	 * @param object the object to check.
	 * @param name the object's name.
	 * @return the object for chaining.
	 */
	public static <T extends Object> T hasIterator(T object, String name) {
		throwErrorIf(!Iterables.hasIterator(object), FormatStrings.mustBe, name, "iterable");
		return object;
	}
	
	
	
	
	
	/**
	 * Checks if the given integer is negative, throwing an error if this is the case.
	 * 
	 * @param integer The integer to check.
	 * @param name The integer's name.
	 * @return The integer.
	 */
	public static int nonNegative(Integer integer, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i < 0, FormatStrings.mustNotBe, name, "negative");
		return i;
	}

	/**
	 * Checks if the given integer is zero, throwing an error if this is the case.
	 * 
	 * @param integer The integer to check.
	 * @param name The integer's name.
	 * @return The integer.
	 */
	public static int nonZero(Integer integer, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i == 0, FormatStrings.mustNotBe, name, "zero");
		return i;
	}
	
	/**
	 * Checks if the given integer is positive, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param name The integer's name.
	 * @return The integer.
	 */
	public static int positive(Integer integer, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i <= 0, FormatStrings.mustBe, name, "positive");
		return i;
	}
	
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the first given integer is larger than the second integer, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The array.
	 */
	public static int largerThan(Integer integer, int than, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i <= than, FormatStrings.mustBe, name, "larger than " + than);
		return i;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the first given integer is larger or equal than the second integer, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The integer that was checked.
	 */
	public static int largerOrEqual(Integer integer, int than, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i < than, FormatStrings.mustBe, name, "larger or equal than " + than);
		return i;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the first given integer is smaller than the second integer, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The integer that was checked.
	 */
	public static int smallerThan(Integer integer, int than, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i >= than, FormatStrings.mustBe, name, "smaller than " + than);
		return i;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the first given integer is smaller or equal than the second integer, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The integer that was checked.
	 */
	public static int smallerOrEqual(Integer integer, int than, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i > than, FormatStrings.mustBe, name, "smaller or equal than " + than);
		return i;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the first given integer is equal to the second integer, throwing an error if this is not the case.
	 * 
	 * @param integer The integer to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The integer that was checked.
	 */
	public static int equal(Integer integer, int than, String name) {
		int i = nonNull(integer, name);
		throwErrorIf(i != than, FormatStrings.mustBe, name, "equal to " + than);
		return i;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the given array's length is equal to the integer, throwing an error if this is not the case.
	 * 
	 * @param arr The array to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The array.
	 */
	public static Object[] lengthEqual(Object[] arr, int than, String name) {
		Requirements.nonNull(arr, name);
		Requirements.equal(arr.length, than, name + ".length");
		return arr;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the given array's length is equal to the integer, throwing an error if this is not the case.
	 * 
	 * @param arr The array to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The array.
	 */
	public static int[] lengthEqual(int[] arr, int than, String name) {
		Requirements.nonNull(arr, name);
		Requirements.equal(arr.length, than, name + ".length");
		return arr;
	}
	
	/**
	 * Cannot be used as a method reference to substitute a Requirement in {@link #all(Requirement, T[], String) Requirements.all(Requirement, T[], String)}. Use Lambda expressions instead.
	 * 
	 * Checks if the given array's length is equal to the integer, throwing an error if this is not the case.
	 * 
	 * @param arr The array to check.
	 * @param than the integer to compare i to.
	 * @param name the integer's name.
	 * @return The array.
	 */
	public static double[] lengthEqual(double[] arr, int than, String name) {
		Requirements.nonNull(arr, name);
		Requirements.equal(arr.length, than, name + ".length");
		return arr;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Defines a requirement. Mostly used in conjunction with Lambda expressions/method references and the {@link Requirements#all(Requirement, String, T...) Requirements.all(...)} method.
	 */
	public interface Requirement<T> {
		public void check(T t, String argumentName);
	}
	
	/**
	 * Lists several {@linkPlain java.util.Formatter format strings} that may be used in {@link Requirements.throwError(String, Object) Requirements.throwError(...)}.
	 * <p>
	 * Arguments must be passed as additional arguments to the throwError(...) method.
	 * 
	 * @see java.util.Formatter
	 */
	public static final class FormatStrings {
		/**
		 * A format string. See {@link FormatStrings} for more information.
		 * <p>
		 * Default value: {@value}
		 * 
		 * @param name (String)
		 * @param value (String)
		 * 
		 * @see FormatStrings
		 */
		public static final String mustBe = "%s must be %s";
		/**
		 * A format string. See {@link FormatStrings} for more information.
		 * <p>
		 * Default value: {@value}
		 * 
		 * @param name (String)
		 * @param value (String)
		 * 
		 * @see FormatStrings
		 */
		public static final String mustNotBe = "%s must not be %s";
		
		/**
		 * A format string. See {@link FormatStrings} for more information.
		 * <p>
		 * Default value: {@value}
		 * 
		 * @param name (String)
		 * @param value (String)
		 * 
		 * @see FormatStrings
		 */
		public static final String isEqual = "%s == %s";
		/**
		 * A format string. See {@link FormatStrings for more information.
		 * <p>
		 * Default value: {@value}
		 * 
		 * @param name (String)
		 * @param value (String)
		 * 
		 * @see FormatStrings
		 */
		public static final String notEqual = "%s != %s";
	}
}
