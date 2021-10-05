package me.alfreds.matrixwalker;

import java.util.stream.IntStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MatrixWalker will walk a two dimensional int array in clockwise spiraling-in direction.
 * <p>
 * <b>LIMITATIONS:</b> This implementation may not lend itself well to ...<p>
 *  1. modifying the direction of traversal.<br>
 *  2. modifying the starting position.<br>
 *  3. using one instance with multiple threads.<br>
 *
 * @NotThreadSafe
 */
@SpringBootApplication
public class MatrixWalkerApplication {

	private final CharSequence nullValueReplacement;
	private final CharSequence defaultNullValueReplacement = "-";
	private final int algorithmInceptionX;
	private final int algorithmInceptionY;

	private final Position algorithmInceptionPosition;

	private final CharSequence separator;
	private final CharSequence defaultSeparator = ", ";

	private int widestArray;
	private int countElementsWalked = 0;
	private int maxTheoreticalElementCount = 0;

	static class Position {
		final int x, y;
		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Indicates the direction of travel
	 */
	public enum Direction {
		/**
		 * Left
		 */
		L,
		/**
		 * Right
		 */
		R,
		/**
		 * Up
		 */
		U,
		/**
		 * Down
		 */
		D,
		/**
		 * <b>N</b>one, E<b>n</b>d, Do<b>n</b>e
		 */
		N,
	}

	/**
	 * Default constructor assumes (0,0) starting point and ", " separator between appended elements
	 */
	public MatrixWalkerApplication() {
		this.algorithmInceptionX = 0;
		this.algorithmInceptionY = 0;
		this.nullValueReplacement = this.defaultNullValueReplacement;
		this.algorithmInceptionPosition = new Position(algorithmInceptionX,algorithmInceptionY);
		this.separator = defaultSeparator;
	}

	/**
	 * Optional constructor allows caller to define initial position and separator value
	 *
	 * @param initialX the x-axis starting spot for the traversal walking, negative values will result in undefined behavior.
	 * @param initialY the y-axis starting spot for the traversal walking, negative values will result in undefined behavior.
	 * @param separator the value to place between elements of the structure. A null value will result in default separator
	 * @param nullValueReplacement the value used in the output string to represent the absence of an element due to a null array.
	 */
	public MatrixWalkerApplication(int initialX, int initialY, CharSequence separator, CharSequence nullValueReplacement) {
		this.algorithmInceptionX = initialX;
		this.algorithmInceptionY = initialY;
		this.algorithmInceptionPosition = new Position(algorithmInceptionX,algorithmInceptionY);
		if (separator == null) {
			this.separator = defaultSeparator;
		} else {
			this.separator = separator;
		}
		if (nullValueReplacement == null) {
			this.nullValueReplacement = defaultNullValueReplacement;
		} else {
			this.nullValueReplacement = nullValueReplacement;
		}
	}

	/**
	 * Prints usage via maven commands.
	 */
	public static void main(String[] args) {
		SpringApplication.run(MatrixWalkerApplication.class, args);
		System.out.println("Please use maven: [mvn test or mvn javadoc:javadoc] to invoke this program or use the local shell/cmd files to execute maven.");
	}

	/**
	 * Returns a string representing a clockwise order outer-edge-first traversal to the center, starting at (0,0)
	 * of the provided input 2D array. <p>
	 * Sparsely populated nulls are supported and will result in either default replacement character or
	 * constructor provided replacement character being substituted in place of where non-null arrays would have existed.<br>
	 * SPARSE NULL EXAMPLE:<br>
	 * <pre>
	 *    new int[][] {
	 *	    new int[]{2, 3, 4, 8},
	 *	    null,
	 *	    new int[]{1, 0, 6, 10}
	 *	  }
	 * RESULT = "2, 3, 4, 8, -, 10, 6, 0, 1, -, -, -",
	 * </pre>
	 *
	 * ALL NULL ELEMENTS: dimensions can't be assumed from other non-null elements so this case produces empty string, nothing is traversed.<br>
	 * ALL EMPTY ELEMENTS: dimensions are zero, expect empty string<br>
	 * <p>
	 * Inconsistent array lengths are supported and will be treated as if all arrays are equal; missing values replaced in left-justified manner with replacement character similar to null case.
	 * <p>
	 * Instance bound state is managed for traversal. This method is <b>NOT</b> thread safe.<br>
	 *
	 * @param input two-dimensional array, Null will result in empty string return. Sparse Null arrays are allowed, inconsistent length allowed.
	 * @return String collected by walking the 2D Array
	 * @NotThreadSafe
	 */
	public String walkMatrix(int[][] input) {
		StringBuilder result = new StringBuilder();

		if ( input != null ) {
			calcNullSafeDimensions(input);
			int offset = 0;
			String directionResult = walkDirection(Direction.R, algorithmInceptionPosition, input, offset);
			result.append(directionResult);
		}// else case is no valid array

		return result.toString();
	}

	/**
	 * Set internal object state to capture dimensions of the 2D array.
	 *
	 * @param input 2D array, null allowed, sparse-null allowed
	 */
	private void calcNullSafeDimensions(int[][] input) {
		int widestLength = getWidestLength(input, algorithmInceptionY);

		if ( widestLength >= 0) {
			this.widestArray = widestLength;
			this.maxTheoreticalElementCount = widestArray * input.length;
		}
	}

	/**
	 * Find the widest width of non-null array in the first dimension of your input 2D array.
	 *
	 * @param input 2D array, can be null or sparsely null
	 * @param initialY a location in the first array to start looking
	 * @return -1 is indicating no valid element could be found to determine width, positive value indicates width of widest array.
	 */
	private int getWidestLength(int[][] input, int initialY) {
		int ret = -1;
		if ( input != null ) {
			for (int i = initialY;  i < input.length; i++){
				if (input[i] != null && input[i].length > ret ) {
					ret = input[i].length;
				}
			}
		}

		return ret;
	}

	/**
	 * internal self-calling algorithm performs the actual work of traversal on behalf of entry point caller.
	 *
	 * @param direction which direction do you want to travel the 2D input array?
	 * @param initPosition the initial position to starting walking in the direction provided.
	 * @param input the 2D array (sparse null okay).
	 * @param offset how far away form the edge (Up, Down, Left, Right) to walk up to before stopping.
	 * @return a string with textual representation of each walked element in clockwise, edge-first traversal. Null rows are interpolated into replacement characters.
	 */
	private String walkDirection(Direction direction, Position initPosition, int[][] input, int offset) {
		StringBuilder result = new StringBuilder();
		CharSequence replaceAfterFirstIteration = "";
		OptionalAppender appender = new OptionalAppender(separator);

		// We are done
		if (countElementsWalked == maxTheoreticalElementCount) {
			direction = Direction.N;
		}

		switch (direction) {
			case L:
				{
					// Y stays constant, X changes lower
					int[] arrayToWalk = input[initPosition.y];
					if (arrayToWalk == null) {
						// we don't care that range is going up, we just need the correct number of elements.
						IntStream.range(offset, initPosition.x + 1)
							.forEach(i ->
							{
								CharSequence replaceAfterFirstIterationInner = (i > offset ? separator : "");
								result.append(replaceAfterFirstIterationInner).append(nullValueReplacement);
								tallyElement();
							});
						Position nextPos = new Position(offset, initPosition.y-1);
						appender.append(result, walkDirection(Direction.U, nextPos, input, offset));
					} else {
						for(int i=initPosition.x; i >= offset; i--) {
							if (i <= arrayToWalk.length - 1) {
								int j = arrayToWalk[i];
								result.append(replaceAfterFirstIteration).append(Integer.toString(j));
							} else {
								result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							}
							replaceAfterFirstIteration = separator;
							tallyElement();
						}
						Position nextPos = new Position(offset, initPosition.y-1);
						appender.append(result, walkDirection(Direction.U, nextPos, input, offset+1));
					}
				}
				break;
			case R:
				{
					// Y stays constant, X changes higher
					int[] arrayToWalk = input[initPosition.y];
					if (arrayToWalk == null || arrayToWalk.length == 0) {
						int xEnd = widestArray - offset;
						IntStream.range(initPosition.x, xEnd)
							.forEach(i ->
							{
								CharSequence replaceAfterFirstIterationInner = (i > initPosition.x ? separator : "");
								result.append(replaceAfterFirstIterationInner).append(nullValueReplacement);
								tallyElement();
							});
						Position nextPos = new Position(xEnd-1, initPosition.y+1);
						appender.append(result, walkDirection(Direction.D, nextPos, input, offset));
					} else {
						int xEnd = arrayToWalk.length - offset;
						for (int i=initPosition.x; i < xEnd; i++){
							if ( i <= arrayToWalk.length - 1 ) {
								result.append(replaceAfterFirstIteration).append(Integer.toString(arrayToWalk[i]));
							} else {
								result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							}
							replaceAfterFirstIteration = separator;
							tallyElement();
						}
						Position nextPos = new Position(xEnd-1, initPosition.y+1);
						appender.append(result, walkDirection(Direction.D, nextPos, input, offset));
					}
				}
				break;
			case U:
				{
					// Y will change lower, X is constant
					int yEnd = 0;
					for(int i = initPosition.y; i >= offset; i--) {
						int[] arrayToWalk = input[i];
						if (arrayToWalk == null){
							result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							tallyElement();
						} else {
							if ( initPosition.x <= arrayToWalk.length - 1) {
								int currentVal = arrayToWalk[initPosition.x];
								result.append(replaceAfterFirstIteration).append(Integer.toString(currentVal));
							} else {
								result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							}
							tallyElement();
						}

						replaceAfterFirstIteration = separator;
						yEnd = i;
					}
					Position nextPos = new Position(initPosition.x + 1, yEnd);
					appender.append(result, walkDirection(Direction.R, nextPos, input, offset));
				}
				break;
			case D:
				{
					// Y will change higher, X is constant
					int yEnd = input.length - 1 - offset;
					for (int i = initPosition.y; i <= yEnd; i++) {
						int[] arrayToWalk = input[i];
						if (arrayToWalk == null){
							result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							tallyElement();
						} else {
							if (initPosition.x <= arrayToWalk.length - 1 ) {
								int currentVal = arrayToWalk[initPosition.x];
								result.append(replaceAfterFirstIteration).append(Integer.toString(currentVal));
							} else {
								result.append(replaceAfterFirstIteration).append(nullValueReplacement);
							}
							tallyElement();
						}
						replaceAfterFirstIteration = separator;
					}
					Position nextPos = new Position(initPosition.x - 1, yEnd);
					appender.append(result, walkDirection(Direction.L, nextPos, input, offset));
				}
				break;
			default:
				break;
		}
		return result.toString();
	}

	/**
	 * The number returned is the sum of both elements seen and elements interpolated due to sparse null in the 2D array.
	 *
	 * @return number walked-and-interpreted
	 */
	public int getElementsWalked() {
		return this.countElementsWalked;
	}

	/**
	 * Tally up an element either seen or interpolated due to spare-nulls in array.
	 */
	private void tallyElement() {
		this.countElementsWalked++;
	}
}