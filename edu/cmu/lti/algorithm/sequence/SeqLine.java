package edu.cmu.lti.algorithm.sequence;

import java.io.BufferedReader;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;

public class SeqLine extends Seq<String> {

	String file_pattern_;
	VectorS file_names_;
  int next_file_;
  boolean skip_title_;
	public static String title = null;
 
	private BufferedReader reader_ = null;
	public static String sSkipLine = null; //skip lines starting with this prefix

	public SeqLine(String fn) {
		this(fn, false);
	}

	public SeqLine(String file_pattern, boolean skip_title) {//Class c,
		super(String.class);
		file_names_ =	FFile.getFilesCheck(file_pattern);
	  next_file_ = 0;
	  skip_title_ = skip_title;
	  file_pattern_ = file_pattern;
	  
//		reader_ = FFile.newReader(file_pattern);
//		if (reader_ == null) return;
//		if (skip_title) title = FFile.readLine(reader_);
	}

	@Override public boolean hasNext() {
	  while (true) {
	    if (reader_ == null) {
	      // Open the next file.
	      if (next_file_ >= file_names_.size()) return false;
	      String next_file = file_names_.get(next_file_);
	      //if (!FFile.exist(next_file)) FSystem.die("file not foun);
	      
	      reader_ = FFile.newReader(next_file);
	      if (skip_title_) title = FFile.readLine(reader_);

	      ++next_file_;
	    }

	    // Read next line from the current file.
	    x = FFile.readLine(reader_);
	    if (x != null) return true;

	    // No more lines in file. Switch to next file.
	    FFile.close(reader_);
	    reader_ = null;
	  }
	}
}
