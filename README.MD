The driver classes for this project are SequentialProcessing.java and ParallelVersion.java.
These are both set up to accept command line parameters as per the spec. They are to be run in the following ways respectively:

java SequentialProcessing input_file_name output_filename

java ParallelVersion input_file_name output_filename sequential_cutoff

I also set up a bash script, runfile.sh, which automates the process of running the java files multiple times. It is quite user friendly and asks for input 
one value at a time. It also uses a python script to extract just the time we're interested in. It then outputs the average of the ten runs. These are both in the bin directory.   