package com.n2d4.rachel.util;

/**
 * Utility class for throwing exceptions quickly.
 * <p>
 * Sub-classing is supported. However, the expected outcome for most methods is to throw an exception an therefore interrupt the normal flow - caution is advised when overriding these.
 * 
 * @author N2D4
 *
 */
public class Exceptions {
	
	private Exceptions() {
		Exceptions.privateConstructor();
	}
	
	
	/**
	 * Throws an exception stating that a private constructor was called, interrupting the normal flow.
	 */
	public static void privateConstructor() {
		throw new PrivateConstructorException();
	}
	
	
	
	
	
	
	
	
	private static class PrivateConstructorException extends UnsupportedOperationException {
		private static final long serialVersionUID = 1L;
		
		public PrivateConstructorException() {
			super("A private constructor was accessed.");
		}
	}
	
}
