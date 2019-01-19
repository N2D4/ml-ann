package com.n2d4.rachel.vectorization;

public interface InOutSet<I extends ValueSet & InputValueSet, O extends ValueSet & OutputValueSet> {
	I getInputSet();
	O getOutputSet();
}
