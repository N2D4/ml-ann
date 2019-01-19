package com.n2d4.rachel.vectorization.derivatives;

import com.n2d4.rachel.vectorization.UnrolledData;

public class UnrolledParameterErrors extends UnrolledData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SafeVarargs
	public UnrolledParameterErrors(ParameterError<?>... errors) {
		super(errors);
	}
	
}
