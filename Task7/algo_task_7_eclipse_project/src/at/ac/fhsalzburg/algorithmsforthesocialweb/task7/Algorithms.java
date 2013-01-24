/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7;

/**
 * Algorithms enumerations are the third argument that is provided when calling the Apache haddop job. Currently we
 * support two different algorithms: Pearson and Spearman correlation. There is a short and a long argument version for
 * both of them defined within this class.
 * 
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public enum Algorithms {
	/**
	 * Short notation for “pearson”.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">Wikipedia</a>
	 */
	p,

	/**
	 * Use pearson correlation for computation.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">Wikipedia</a>
	 */
	pearson,

	/**
	 * Short notation for “spearman”.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient">Wikipedia</a>
	 */
	s,

	/**
	 * Use spearman correlation for computation.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient">Wikipedia</a>
	 */
	spearman
}