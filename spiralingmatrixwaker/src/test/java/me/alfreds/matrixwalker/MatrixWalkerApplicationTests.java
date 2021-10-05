package me.alfreds.matrixwalker;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MatrixWalkerApplicationTests {

	@ParameterizedTest(name = "#{index} - Test with {0} and {1}")
	@ArgumentsSource(MyArgumentsProvider.class)
	void WalkingCollect_Parameterized_StringResultMatches(String testName, WalkerTestCase walkerTestCase) {
		String entries = walkerTestCase.walk();
		assertEquals(walkerTestCase.expectedTotalElements, walkerTestCase.getTotalElements(), "Element count represented should be equal to WxH");
		assertEquals(walkerTestCase.expectedResult, entries, "Collected result didn't match expectations!");
	}

	public static class MyArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
			return
				Stream.of(
					arguments("WalkingCollect_NulledMatrix_StringResultMatches", new WalkerTestCase(null, "", 0)),
					arguments("WalkingCollect_OneNulledMatrix_StringResultMatches", new WalkerTestCase(new int[][]{null}, "", 0)),
					arguments("WalkingCollect_TwoNulledMatrix_StringResultMatches", new WalkerTestCase(new int[][]{null, null}, "", 0)),
					arguments("WalkingCollect_SingleElementArrayMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{1},
										},
										"1",
										1 /* 1 items x 1 arrays */)),
					arguments("WalkingCollect_SingleElementTwoArraysMatrix_StringResultMatches", new WalkerTestCase(
											new int[][] {
												new int[]{1},
												new int[]{2},
											},
											"1, 2",
											2 /* 1 items x 2 arrays */)),
					arguments("WalkingCollect_SingleEmptyArrayMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{},
										},
										"",
										0 /* largest width (0) * number of arrays (1) */)),
					arguments("WalkingCollect_MixedNullEmptyArrayTwoArraysMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											null,
											new int[]{},
										},
										"",
										0 /* largest width (0) * number of arrays (2) */)),
					arguments("WalkingCollect_MixedNullEndingEmptyArrayTwoArraysMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{},
											null,
										},
										"",
										0 /* largest width (0) * number of arrays (1) */)),
					arguments("WalkingCollect_SingleStartingEmptyArrayTwoArraysMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{},
											new int[]{2},
										},
										"-, 2",
										2 /* largest width (1) * number of arrays (2) */)),
					arguments("WalkingCollect_SingleEndingEmptyArrayTwoArraysMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{1},
											new int[]{},
										},
										"1, -",
										2 /* largest width (1) * number of arrays (2) */)),
					arguments("WalkingCollect_SingleMiddleEmptyArrayThreeArraysMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{1, 2},
											new int[]{},
											new int[]{3}
										},
										"1, 2, -, -, 3, -",
										6 /* largest width (2) * number of arrays (3) */)),
					arguments("WalkingCollect_SingleElementTwoArraysOneNullMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											null,
											new int[]{2},
										},
										"-, 2",
										2 /* 1 items x 2 arrays */)),
					arguments("WalkingCollect_SparseNulledMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{2, 3, 4, 8},
											null,
											new int[]{1, 0, 6, 10}
										},
										"2, 3, 4, 8, -, 10, 6, 0, 1, -, -, -",
										12 /* 4 items x 3 arrays */)),
					arguments("WalkingCollect_CommonMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{2, 3, 4, 8},
											new int[]{5, 7, 9, 12},
											new int[]{1, 0, 6, 10}
										},
										"2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9",
										12 /* 4 items x 3 arrays */)),
					arguments("WalkingCollect_SingleArrayMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{-2, Integer.MIN_VALUE, 4, 8},
										},
										"-2, -2147483648, 4, 8",
										4 /* 4 items x 1 arrays */)),
					arguments("WalkingCollect_FirstArrayNullMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											null,
											new int[]{-2, Integer.MAX_VALUE, 4, 8},
										},
										"-, -, -, -, 8, 4, 2147483647, -2",
										8 /* 4 items x 2 arrays */)),
					arguments("WalkingCollect_DoubleArrayMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{1, 1, 1, 1},
											new int[]{-2, 0, 4, 8},
										},
										"1, 1, 1, 1, 8, 4, 0, -2",
										8 /* 4 items x 2 arrays */)),
					arguments("WalkingCollect_LastArrayNullMatrix_StringResultMatches", new WalkerTestCase(
										new int[][] {
											new int[]{1, 1, 1, 1},
											null,
										},
										"1, 1, 1, 1, -, -, -, -",
										8 /* 4 items x 2 arrays */))
				);
		}
	}
}
