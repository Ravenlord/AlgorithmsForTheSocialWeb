/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Coefficient;

/**
 * Specialized class to prepare the Jester Data Set for M/R computing.
 *
 * @see <a href="http://www.ieor.berkeley.edu/~goldberg/jester-data/">Anonymous Ratings Data from the Jester Online Joke Recommender System</a>
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class PrepareJesterData {


	// ----------------------------------------------------------------------------------------------------------------- Properties


	/**
	 * The Apache hadoop job configuration.
	 */
	private Configuration conf;

	/**
	 * The original input path to the Jester Data Set CSV file.
	 */
	private Path path;

	/**
	 * The path to the input file as passed to the Apache hadoop command.
	 */
	private String inputPath;


	// ----------------------------------------------------------------------------------------------------------------- Constructor


	/**
	 * Construct new instance of PrepareJesterData class.
	 *
	 * @param conf
	 *   The Apache hadoop job configuration.
	 * @param path
	 *   Path instance of the input file.
	 * @param inputPath
	 *   Path to the input file as string.
	 */
	public PrepareJesterData(Configuration conf, Path path, String inputPath) {
		this.conf = conf;
		this.path = path;
		this.inputPath = inputPath;
	}


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Our data set has the wrong format for an easy M/R implementation, therefor we rearrange the data before we start
	 * our hadoop job.
	 *
	 * @return
	 *   New path instance pointing to the prepared data.
	 * @throws IOException
	 *   If something goes wrong during reading or writing on the input files.
	 */
	public Path prepareDataSet() throws IOException {
		// Get hadoop file system configuration.
		FileSystem fs = FileSystem.get(this.conf);

		// Check if a single file was referenced, otherwise our implementation can't handle it.
		if (!fs.exists(this.path)) throw new IOException(Coefficient.EOL + "You have to specify the complete absolute path to the test dataset!" + Coefficient.EOL +Coefficient.EOL);

		FSDataInputStream is = null;
		FSDataOutputStream os = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		Path newPathForPreparedData = new Path(inputPath + "-prepared");
		Map<Integer, Integer[]> userMap = new HashMap<Integer, Integer[]>();

		// We enclose this in a try/finally-block to ensure that the lock from the file always gets removed.
		try {
			is = fs.open(this.path);
			os = fs.create(newPathForPreparedData, true);
			br = new BufferedReader(new InputStreamReader(is, Coefficient.CHARSET), Coefficient.BUFFER_SIZE);
			bw = new BufferedWriter(new OutputStreamWriter(os, Coefficient.CHARSET), Coefficient.BUFFER_SIZE);
			String line, rating;
			String[] jokeRating;
			int i = 0;
			StringTokenizer tokenizer;

			// Read each line of the file, each line represents a single user.
			while ((line = br.readLine()) != null) {
				// Prepare the ratings array within our user map. We initialize each offset with the invalid rating 99.
				userMap.put(i, new Integer[Coefficient.MAX_RATINGS]);
				for (int j = 0; j < Coefficient.MAX_RATINGS; j++) userMap.get(i)[j] = 99;

				// Tokenize each line into little chunks splitted at the character ";".
				tokenizer = new StringTokenizer(line, ";");

				while (tokenizer.hasMoreTokens()) {
					// Split the tokenized string at "," and check if we have two offsets. We also check it the
					// extracted rating contains whitespaces, is empty ("") or null, as these are illegal values for a
					// rating.
					if ((jokeRating = tokenizer.nextToken().split(",")).length == 2 && !(StringUtils.isBlank(rating = StringUtils.trim(jokeRating[0])))) {
						// If the rating is -0 insert 0, otherwise we'll get a NumberFormatException later on.
						userMap.get(i)[Integer.valueOf(jokeRating[1]) - 1] = rating.equals("-0") ? 0 : Integer.valueOf(rating);
					}
				}

				// The iterator represents the line and each line represents a user, by incrementing the iterator we
				// increase the user count / ID.
				i++;
			}

			// Loop again over all ratings and users, we have to create a file that is usable by the hadoop M/R job. The
			// final anatomy of the prepared data file looks as follows:
			//
			// 0;1;2;...;n;
			// 0;1;2;...;n;
			//
			// Each value represents a rating, each column represents a user, and each line represents a joke. The jokes
			// (lines) are already ordered for easy M/R computation.
			for (int j = 0; j < Coefficient.MAX_RATINGS; j++) {
				for (int userID = 0; userID < userMap.size(); userID++) {
					bw.append((new StringBuilder()).append(userMap.get(userID)[j]).append(";").toString());
				}
				bw.newLine();
			}
		} finally {
			br.close();
			bw.close();
			is.close();
			os.close();
		}

		// We have to delete the prepared data file. It's not needed anymore and if another job is started the file
		// shouldn't be present in the file system.
		FileSystem.get(this.conf).deleteOnExit(newPathForPreparedData);

		return newPathForPreparedData;
	}


}