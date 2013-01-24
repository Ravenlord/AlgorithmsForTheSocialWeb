/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7;

import java.util.Date;

import org.joda.time.Period;

/**
 * Micro benchmark for measuring the execution time of our Apache hadoop M/R task.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class Benchmark {

	/**
	 * Contains the time when the Benchmark was instanciated.
	 */
	long start = new Date().getTime();

	/**
	 * Stop the Benchmark and return the result.
	 *
	 * @return
	 *   Formatted string of the execution time in the format "hh:mm:ss.ms".
	 */
	public String stop() {
		Period period = new Period(this.start, new Date().getTime());
		return String.format("%02d:%02d:%02d.%d", period.getHours(), period.getMinutes(), period.getSeconds(), period.getMillis());
	}

}