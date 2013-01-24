/**
 * Algorithms for the Social Web Task 7: Apache Hadoop M/R
 */
package at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;

import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Algorithms;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.Coefficient;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.inplace.InPlaceMapper;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.inplace.InPlaceReducer;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare.PreparationMapper;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare.PreparationReducer;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.approach.prepare.PrepareJesterData;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.DoubleArrayWritable;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserIdPairWritable;
import at.ac.fhsalzburg.algorithmsforthesocialweb.task7.datatype.UserRatingPairWritable;

/**
 * Facotry which generates the desired M/R task job configuration.
 *
 * @author Richard Fussenegger <rfussenegger.mmt-m2012@fh-salzburg.ac.at>
 * @author Markus Deutschl <mdeutschl.mmt-m2012@fh-salzburg.ac.at>
 * @version 1.0
 * @since 2012-19-12
 */
public class JobConfFactory {


	// ----------------------------------------------------------------------------------------------------------------- Public Methods


	/**
	 * Get job configuration for chosen approach (or use default approach).
	 *
	 * @param conf
	 *   The configuration object for accessing the hadoop file system.
	 * @param args
	 *   Arguments as passed to the executable.
	 * @param callerClass
	 *   The class of the caller (e.g. <tt>Coefficient.class</tt>).
	 * @return
	 *   The altered job configuration for the chosen approach (or default approach).
	 * @throws IOException
	 *   If an error occurres during file access an IOException is thrown.
	 */
	@SuppressWarnings("rawtypes")
	public static JobConf getJobConf(Configuration conf, String[] args, Class callerClass) throws IOException {
		// Directly create the path instances with the given paths and store them for later usage.
		Path input = new Path(args[0]);
		Path output = new Path(args[1]);

		// Create new generic job configuration.
		JobConf desiredJob = new JobConf(conf, callerClass);

		// Create new hadoop job and set basic configuration parameters.
		desiredJob.setJobName(Coefficient.HADOOP_JOB_NAME);
		desiredJob.setSessionId(Coefficient.HADOOP_JOB_NAME);

		// Tell hadoop where to get the altered input data and where to put the output data.
		FileInputFormat.setInputPaths(desiredJob, input);
		FileOutputFormat.setOutputPath(desiredJob, output);

		// The third console argument determines which correlation algorithm should be used for computing the
		// correlation during this job.
		desiredJob.setEnum(Coefficient.HADOOP_VARIABLE_ALGORITHMS_KEY, Algorithms.valueOf(args[2]));

		// If the arguments array is missing the fourth offset use the faster approach by default.
		switch (args.length != 4 ? Coefficient.DEFAULT_APPROACH : Approaches.valueOf(args[3])) {
		case i:
		case inplace:
			desiredJob = getInPlaceJobConf(desiredJob, args[0], conf);
			break;
		case p:
		case preparation:
		default:
			desiredJob = getPreparationJobConf(desiredJob, args[0], conf);
			break;
		}

		return desiredJob;
	}


	// ----------------------------------------------------------------------------------------------------------------- Private Methods


	/**
	 * Get the job configuration for the in-place approach.
	 *
	 * @param job
	 *   The job configuration object that should be altered with the in-place approach.
	 * @param input
	 *   The path to the input file as passed to the executable.
	 * @param conf
	 *   The configuration object for hadoop file system access.
	 * @return
	 *   The altered job configuration for the in-place approach.
	 * @throws IOException
	 *   If an error occurres during file access an IOException is thrown.
	 */
	private static JobConf getInPlaceJobConf(JobConf job, String input, Configuration conf) throws IOException {
		// We have to count the lines in our input file for the mapper task.
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream is = null;
		BufferedReader br = null;
		try {
			is = fs.open(new Path(input));
			br = new BufferedReader(new InputStreamReader(is, Coefficient.CHARSET), Coefficient.BUFFER_SIZE);
			int lines = 0;
			while (br.readLine() != null) lines++;
			job.setInt(Coefficient.HADOOP_VARIABLE_MAX_USERS, lines);
		} finally {
			is.close();
			br.close();
		}

		// Tell hadoop which classes we've created for the M/R phases.
		job.setMapperClass(InPlaceMapper.class);
		job.setReducerClass(InPlaceReducer.class);

		// Tell hadoop what kind of output our map class generates.
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(DoubleArrayWritable.class);

		// Tell hadoop what kind of output our reduce class generates.
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job;
	}

	/**
	 * Get the job confgiration for the preparation approach.
	 *
	 * @param job
	 *   The job configuration object that should be altered with the preparation appraoch.
	 * @param input
	 *   The path to the input file as passed to the executable.
	 * @param conf
	 *   The configuration object for hadoop file system access.
	 * @return
	 *   The altered job configuration for the preparation approach.
	 * @throws IOException
	 *   If an error occurres during file access an IOException is thrown.
	 */
	private static JobConf getPreparationJobConf(JobConf job, String input, Configuration conf) throws IOException {
		// We are using the approach to prepare the data set here. Tell hadoop where to get the altered input data and
		// where to put the output data.
		FileInputFormat.setInputPaths(job, new PrepareJesterData(conf, FileInputFormat.getInputPaths(job)[0], input).prepareDataSet());

		// Tell hadoop which classes we've created for the M/R phases.
		job.setMapperClass(PreparationMapper.class);
		job.setReducerClass(PreparationReducer.class);

		// Tell hadoop what kind of output our map class generates.
		job.setMapOutputKeyClass(UserIdPairWritable.class);
		job.setMapOutputValueClass(UserRatingPairWritable.class);

		// Tell hadoop what kind of output our reduce class generates.
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job;
	}

	
}