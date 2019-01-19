package com.n2d4.rachel.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import com.n2d4.rachel.util.Requirements.FormatStrings;

/**
 * Provides utility functions for Iterables, Iterators and iteration.
 * <p>
 * Sub-classing is supported.
 * 
 * @author N2D4
 */
public class Iterables {
	
	private Iterables() {
		Exceptions.privateConstructor();
	}
	
	/**
	 * Returns an {@link Iterator} for the object passed.
	 * <p>
	 * If the object implements an {@link java.util.Iterator Iterator}, the object itself will be returned.<br>
	 * If the object implements {@link java.util.Iterable Iterable} (this includes all {@link java.util.Collection Collections}, {@link java.util.List Lists} and {@link java.util.Set Sets}), the default iterator will be used.<br>
	 * If the object extends Object[], an Iterator that follows the natural order of the array will be created.<br>
	 * If the object is a primitive array, Reflection will be used to create an Iterator of their reference types that follows the natural order of the array.<br>
	 * If the object is null, an error will be thrown.<br>
	 * Anything else will return null.
	 * <p>
	 * The safest way to use this function is to set the generic type T to {@link java.util.Object Object}:
	 * <p>
	 * {@code Iterator<Object> iterator = Iterables.iterator(new int[5]);}
	 * <p>
	 * If it is anything else, the return value will automatically be cast. A {@link java.lang.ClassCastException} may be thrown:
	 * <p>
	 * {@code Iterator<String> successful1 = Iterables.iterator(new String[5]); // String == String}<br>
	 * {@code Iterator<Integer> successful2 = Iterables.iterator(new int[5]); // int => Integer}<br>
	 * {@code Iterator<Number> successful3 = Iterables.iterator(new int[5]); // Integer extends Number}<br>
	 * {@code Iterator<String> fail = Iterables.iterator(new Integer[5]); // Integer != String!}
	 * 
	 * @param obj the object.
	 * @return the Iterator for the object.
	 * @throws ClassCastException if the returned Iterator can't be casted
	 * @see #iterable(Object)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> iterator(Object obj) throws ClassCastException {
		Requirements.nonNull(obj, "obj");
		
		Iterator<? extends T> result;
		if (obj instanceof Iterator) {
			result = (Iterator<? extends T>) obj;
		} else if (obj instanceof Iterable) {
			result = ((Iterable<? extends T>) obj).iterator();
		} else if (obj instanceof Object[]) {
			result = new ArrayIterator<T>((T[]) obj);
		} else if (obj.getClass().isArray()) {
			result = new ReflectionArrayIterator<T>(obj);
		} else {
			return null;
		}
		
		return ConvertedIterator.convertIterator(result);
	}
	
	
	/**
	 * Returns an Iterable for the object passed. The object may be an Iterable, Iterator, or any other object accepted by {@link #iterator(Object) Iterables.iterator(...)}.
	 * 
	 * @param obj the object.
	 * @return an Iterable for iterating through the object.
	 * @throws ClassCastException if the object's iterator is not of type T
	 * @see #iterator(Object)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> iterable(Object obj) throws ClassCastException {
		Requirements.nonNull(obj, "obj");
		
		if (obj instanceof Iterable) {
			try {
				return (Iterable<T>) obj;
			} catch (ClassCastException e) {
				// While obj is an Iterable, its type is not T, so just continue
			}
		}
		
		Iterator<T> iterator = iterator(obj);
		return iterator == null ? null : new GenericIterable<T>(iterator);
	}
	
	
	
	
	
	/**
	 * Returns the passed Iterable, Iterator, or other object accepted by {@link #iterator(Object) Iterables.iterator(...)}, as an array.
	 * 
	 * @param t the object.
	 * @return the array, or null if the object can't be iterated through.
	 * @throws ClassCastException if the object's iterator is not of type T
	 * @see #iterator(Object)
	 */
	public static <T> T[] asArray(Object obj, T[] tarr) throws ClassCastException {
		Requirements.nonNull(obj, "obj");
		
		ArrayList<T> result = new ArrayList<T>();
		Iterable<T> ts = Iterables.iterable(obj);
		for (T t : ts) {
			result.add(t);
		}
		
		
		return result.toArray(tarr);
	}
	
	
	
	
	
	/**
	 * Checks if the passed object has an iterator attached, or is an iterator itself. If true, it can be passed to {@link #iterator(Object) Iterables.iterator(...)}.
	 * 
	 * @param obj the object to check.
	 * @return whether the passed object has an iterator.
	 */
	public static boolean hasIterator(Object obj) {
		return obj instanceof Iterable || obj instanceof Iterator || obj.getClass().isArray();
	}
	
	
	
	
	
	
	
	
	/**
	 * A fast and general-purpose array iterator for non-primitive elements.
	 *
	 * @see Iterables.ReflectionArrayIterator
	 * @see Iterables#iterator(Object)
	 */
	public static class ArrayIterator<T> implements Iterator<T> {
		protected int index = 0;
		protected Object[] arr;
		
		public ArrayIterator(T[] array) {
			this(array, false);
		}
		
		protected ArrayIterator(Object[] array, boolean isObject) {
			Requirements.nonNull(array, "array");
			
			this.arr = array;
			
			for (Object obj : arr) {
				@SuppressWarnings({ "unused", "unchecked" }) T throwexc = (T) obj;
			}
		}

		@Override public boolean hasNext() {
			return index < arr.length;
		}

		@SuppressWarnings("unchecked")
		@Override public T next() {
			return hasNext() ? (T) arr[index++] : null;
		}
		
		
	}
	
	/**
	 * A general-purpose primitive or non-primitive array iterator. Constructing this Iterator is considerably slower than {@link Iterables.ArrayIterator} because it uses {@link java.reflect.Array Array Reflection} - caution is advised when using this.
	 * <p>
	 * Whenever you know that the elements are non-primitive (of type Object or a subclass), you should use ArrayIterator.
	 * <p>
	 * It is better practice to let {@link Iterables#iterator(Object) Iterables.iterator(...)} construct the iterator - this will ensure maximal performance.
	 * 
	 * @see Iterables.ArrayIterator
	 * @see Iterables#iterator(Object)
	 */
	protected static class ReflectionArrayIterator<T> extends ArrayIterator<T> {
		
		public ReflectionArrayIterator(Object array) throws ClassCastException {
			super(getArray(array), true);
		}
		
		
		private final static Object[] getArray(Object obj) {
			Requirements.throwErrorIf(!obj.getClass().isArray(), FormatStrings.mustBe, "argument", "an array of the type given");
			
			Object[] result = new Object[Array.getLength(obj)];
			for (int i = 0; i < result.length; i++) {
				result[i] = Array.get(obj, i);
			}
			
			return result;
		}
		
	}
	
	
	
	/**
	 * A general-purpose Iterator implementation that simply repeats a given value a set amount of times.
	 */
	public static class RepeatingIterator<T> implements Iterator<T> {
		private int i = 0;
		private final int max;
		private final T t;
		
		public RepeatingIterator(T value, int repeats) {
			this.max = repeats;
			this.t = value;
		}

		@Override
		public boolean hasNext() {
			return i < max;
		}

		@Override
		public T next() {
			i++;
			return t;
		}
	} 
	
	
	
	/**
	 * A generic class implementing Iterable which takes an Iterator argument.
	 * 
	 * @see Iterables#iterable(Object)
	 */
	protected static class GenericIterable<T> implements Iterable<T> {
		private Iterator<T> iterator;
		
		public GenericIterable(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override public Iterator<T> iterator() {
			return iterator;
		}
	}
	
	
	protected static final class ConvertedIterator<T> implements Iterator<T> {
		private final Iterator<? extends T> iterator;
		
		private ConvertedIterator(Iterator<? extends T> iterator) {
			this.iterator = iterator;
		}
		
		@SuppressWarnings("unchecked")
		public static <T> Iterator<T> convertIterator(Iterator<? extends T> iterator) {
			try {
				return (Iterator<T>) iterator;
			} catch (ClassCastException e) {
				return new ConvertedIterator<T>(iterator);
			}
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() throws ClassCastException {
			return (T) iterator.next();
		}
		
	}
	
}
