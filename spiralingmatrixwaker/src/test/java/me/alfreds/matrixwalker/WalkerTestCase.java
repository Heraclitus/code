package me.alfreds.matrixwalker;

import org.junit.jupiter.params.provider.Arguments;

public class WalkerTestCase implements Arguments {
	int expectedTotalElements;
	String expectedResult;
	MatrixWalkerApplication matrixWalker = new MatrixWalkerApplication();
	int[][] input;

	public WalkerTestCase(int[][] input, String expectedResult, int expectedTotalElements) {
		this.input = input;
		this.expectedTotalElements = expectedTotalElements;
		this.expectedResult = expectedResult;
	}

	String walk() {
		return matrixWalker.walkMatrix(input);
	}

	int getTotalElements() {
		return matrixWalker.getElementsWalked();
	}

	@Override
	public Object[] get() {
		return new Object[]{this};
	}
}