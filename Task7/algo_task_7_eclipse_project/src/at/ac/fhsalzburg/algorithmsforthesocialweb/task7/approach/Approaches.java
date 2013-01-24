/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach;

/**
 * Approaches enumerations contain the optional fourth argument values. We are currently providing two different
 * approaches to solve the correlation coefficient calculation. There is a short and a long version defined for both of
 * them.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public enum Approaches {

	/**
	 * Short notation for “inplace”.
	 */
	i,

	/**
	 * Use in-place calculation M/R task.
	 */
	inplace,

	/**
	 * Short notation for “preparation”.
	 */
	p,

	/**
	 * Prepare data before running M/R task.
	 */
	preparation

}