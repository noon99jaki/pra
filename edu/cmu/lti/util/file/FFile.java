/**
 * 
 */
package edu.cmu.lti.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.cmu.lti.algorithm.Interfaces.IRead;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqLine;
import edu.cmu.lti.algorithm.sequence.SeqS;
import edu.cmu.lti.algorithm.sequence.SeqVS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeSVS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeVSS;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *
 */
public class FFile {
	public static String getFilePath(String path) {
		int i = path.lastIndexOf("/");
		return path.substring(0, i);
	}

	public static boolean mkdirsTrimFile(String path) {
		return mkdirs(getFilePath(path));
	}

	public static void renameFile(String file, String toFile) {
		File f1 = new File(file);
		if (!f1.exists() || f1.isDirectory()) {
			System.out.println("File does not exist: " + file);
			return;
		}
		File newFile = new File(toFile);
		if (!f1.renameTo(newFile)) System.out
				.println("Error renmaing file=" + file);
	}

	public static synchronized boolean mkdirs(String path) {
		//System.out.println("making dir="+path);
		
		if (FFile.exist(path)) return false;
		
		boolean b = new File(path).mkdirs();
		if (!b) {
			b = new File(path).mkdirs();
			if (!b) FSystem.die("failed to create dir=" + path);
		}
		return b;
	}

	public static boolean silient_ = false;
	public static Counter read_counter_ = new Counter(100000, 'r', new Counter(50, '\n'));
	public static Counter write_counter_ = new Counter(100000, 'w', new Counter(50, '\n'));

	// TODO: this thing is not thread safe
	//public static String line_ = null;
	public static int num_lines_ = 0;

	public static String readLine(BufferedReader br) {
		String line_ = null;
		if (!silient_)	read_counter_.step();
		try {
			line_ = br.readLine();
			if (line_ != null ) 		++ num_lines_;

			return line_;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writePrint(BufferedWriter writer, String str) {
		write(writer, str);
		System.out.print(str);
	}

	public static void writeln(BufferedWriter writer, String str) {
		write(writer, str);
		write(writer, "\n");
	}

	public static void write(BufferedWriter writer, String str) {
		if (writer == null) return;
		
		if (!silient_) write_counter_.step();

		try {
			writer.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(BufferedWriter writer, String format, Object... args) {
		write(writer, String.format(format, args));//(Object[])
	}

	public static void close(BufferedReader reader) {
		try {
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void close(BufferedWriter writer) {
		if (writer == null) return;
		try {
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void flush(BufferedWriter writer) {
		try {
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write file content to the end of a file
	 * @param fileContent
	 * @param filePath
	 */
	public static void appendToFile(String fileContent, String filePath) {
		try {
//			FileOutputStream fos = ;
//			OutputStreamWriter osw =
			BufferedWriter writer = 
					new BufferedWriter( new OutputStreamWriter(
					new FileOutputStream(filePath, true), "UTF-8"));
			writer.write(fileContent);
			writer.close();
//			osw.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedWriter newWriter(String file_name) {
		return newWriter(file_name, false);
	}

	public static BufferedWriter newWriter(String file_name, boolean bAppend) {
		return newWriter(file_name, bAppend, "UTF-8");
	}

	// create append
	public static BufferedWriter newWriterCA(String file_name) {
		return newWriter(file_name, FFile.exist(file_name));
	}

	public static BufferedWriter newWriter(String file_name, boolean bAppend,
			String encoding) {

		if (!FFile.silient_)
			System.out.println("Open to write file=" + file_name);
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_name,
					bAppend), encoding));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> boolean save(Iterable<T> v, String fn, String sep) {
		return save(v, fn, sep, null);
	}

	public static <T> boolean save(Iterable<T> v, String fn, String sep,
			String title) {
		BufferedWriter bw = FFile.newWriter(fn);
		if (title != null) FFile.writeln(bw, title);
		for (T k : v) {
			if (k == null) continue;
			FFile.write(bw, k.toString());
			if (sep != null) FFile.write(bw, sep);
		}
		FFile.flush(bw);
		FFile.close(bw);
		return true;
	}

	public static void load(IRead x, String fn) throws IOException {
		BufferedReader w = newReader(fn);
		x.read(w);
		return;
	}
	
	public static void saveObject(Object x, String object_file) {
		System.out.println("saving object to " + object_file);
		try {
			ObjectOutputStream out = 
				new ObjectOutputStream(
				new BufferedOutputStream(
				//	new DataOutputStream(
				new GZIPOutputStream(
				new FileOutputStream(object_file.trim()))));

			out.writeObject(x); // Write the entire object
			out.flush(); // Always flush the output.
			out.close(); // And close the stream.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object loadObject(String object_file) {
		System.out.println("loading stored object from " + object_file);
		try {
		  ObjectInputStream input = 
		  	new ObjectInputStream(
		  	new BufferedInputStream(
//		  	new DataInputStream(
		  	new GZIPInputStream(
		  	new FileInputStream(object_file.trim()))));
		   
			if (input == null) return null;
			Object x = input.readObject();
			input.close();
			return x;
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(e);
		}
		return null;
	}

	public static BufferedReader newReader(String file_name) {
		return newReader(file_name, "UTF-8");
	}

	public static BufferedReader newReader(String file_name, String encoding) {
		if (!FFile.silient_)
			System.out.println("Open to read file=" + file_name);
		try {
			return new BufferedReader(new InputStreamReader(new DataInputStream(
					new FileInputStream(file_name))));
		} catch (Exception e) {
			System.err.println("cannot open read file=" + file_name);
			return null;
		}
	}

	/** Recursively delete files anchored at <code>f</code>	 * 
	 * @param file Directory or file to delete.
	 */
	public static void rmRecur(File file) {
		if (file.isDirectory()) {
			File[] g = file.listFiles();
			for (int i = 0; i < g.length; i++)
				rmRecur(g[i]);
		}
		boolean deleted = file.delete();
		// logger.info("deleted: "+f+" "+(deleted?"-done":
		// "-not present!"));
	}

	//TODO: it somehow does not work
	public static boolean exist(String file_name) {
		return (new File(file_name)).exists();
	}

	public static void checkExist(String file_name) {
		FSystem.checkTrue(exist(file_name), "Missing file=" + file_name);
	}
	
	public static String getFileName(String filePath) {
		return (new File(filePath)).getName();
	}

	public static String getCanonicalPath(String file_name) {
		try {
			return (new File(file_name)).getCanonicalPath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String cwd() {
		try {
			return getCanonicalPath(".");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String parentFolder() {
		try {
			return getCanonicalPath("..");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Gets a list of String filenames present in a directory.
	// Returns an empty list if the directory is empty, if the
	// filename is not a directory, or if an exception occurred.
	public static List<String> ls(String dir) {
		List<String> files = new ArrayList<String>();
		File d = null;
		try {
			d = new File(dir);
			if (d.isDirectory()) {
				File contents[] = d.listFiles();
				for (int i = 0; i < contents.length; i++)
					files.add(contents[i].getCanonicalPath());
			}
		} catch (Exception e) {
			System.err.println("Caught exception listing contents of " + dir + ":"
					+ e.getMessage());
			return Collections.emptyList(); // Exception caught.
		}
		Collections.sort(files);
		return files;
	}

	// Takes a list of string filenames, and returns one as well.
	public static List<String> lsRecur(List<String> input) throws IOException {
		List<String> output = new ArrayList<String>();
		for (Iterator<String> i = input.iterator(); i.hasNext();) {
			try {
				String filename = i.next();
				File f = new File(filename);
				if (f.isDirectory()) {
					output.addAll(lsRecur(ls(filename)));
				} else {
					// System.err.println("adding:"+filename);
					output.add(filename);
				}
			} catch (Exception e) {
				System.err.println("(lsRecur)" + e.getMessage());
			}
		}
		return output;
	}

	public static String getClassPath(Class c) {
		URL url = c.getProtectionDomain().getCodeSource().getLocation();
		//Workaround for windows' problematic paths 
		//such as "C:\Documents%20and%20Settings\"
		return url.getPath().replaceAll("%20", " ");
	}

	public static String getClassPath2(Class cls) {
		if (cls == null) return null;
		try {
			String name = cls.getName().replace('.', '/');
			URL loc = cls.getResource("/" + name + ".class");
			File f = new File(loc.getFile());
			// Class file is inside a jar file.
			if (f.getPath().startsWith("file:")) {
				String s = f.getPath();
				int index = s.indexOf('!');
				// It confirm it is a jar file
				if (index != -1) {
					f = new File(s.substring(5).replace('!', File.separatorChar));
					return f.getPath();
				}
			}
			f = f.getCanonicalFile();
			return f.getPath();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	public static byte[] loadByte(String file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			byte[] data = new byte[(int) (fc.size())];
			// fc.size returns the size of the file which backs the channel
			ByteBuffer bb = ByteBuffer.wrap(data);
			fc.read(bb);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean saveByte(byte[] vb, String file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(vb);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<File> getSortedFiles(String dirPath) throws Exception {
		File dir = new File(dirPath);
		if (!dir.exists()) { throw new Exception("Directory doesn't exist: "
				+ dir.getAbsolutePath()); }

		File[] fileArray = dir.listFiles();

		ArrayList<File> files = new ArrayList<File>(fileArray.length);
		for (File f : fileArray) {
			files.add(f);
		}
		Collections.sort(files);
		return files;
	}

	public static ArrayList<File>[] getSortedFiles(String dirPath1,
			String dirPath2) throws Exception {
		File dir1 = new File(dirPath1);
		if (!dir1.exists()) { throw new Exception("Directory doesn't exist: "
				+ dir1.getAbsolutePath()); }
		File dir2 = new File(dirPath2);
		if (!dir2.exists()) { throw new Exception("Directory doesn't exist: "
				+ dir2.getAbsolutePath()); }

		File[] fileArray1 = dir1.listFiles();
		File[] fileArray2 = dir2.listFiles();

		ArrayList<String> fileNameList1 = new ArrayList<String>(fileArray1.length);
		ArrayList<String> fileNameList2 = new ArrayList<String>(fileArray2.length);

		HashSet<String> set = new HashSet<String>(fileArray1.length
				+ fileArray2.length);
		for (File f : fileArray1) {
			fileNameList1.add(f.getName());
			set.add(f.getName());
		}
		for (File f : fileArray2) {
			fileNameList2.add(f.getName());
			set.add(f.getName());
		}

		HashSet<String> setUpdated = new HashSet<String>(set.size());

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String fileName = it.next();
			if (fileNameList1.contains(fileName) && fileNameList2.contains(fileName)) {
				setUpdated.add(fileName);
			}
		}

		ArrayList<File> fileList1 = new ArrayList<File>(setUpdated.size());
		ArrayList<File> fileList2 = new ArrayList<File>(setUpdated.size());

		it = setUpdated.iterator();
		while (it.hasNext()) {
			String fileName = it.next();
			fileList1.add(new File(dirPath1 + "/" + fileName));
			fileList2.add(new File(dirPath2 + "/" + fileName));
		}
		Collections.sort(fileList1);
		Collections.sort(fileList2);

		ArrayList<File>[] sortedFileLists = new ArrayList[2];
		sortedFileLists[0] = fileList1;
		sortedFileLists[1] = fileList2;

		return sortedFileLists;
	}

	public static void printProgress(PrintStream out, int i) {
		out.print("*" + (i % 10 == 9 ? " " : "") + (i % 50 == 49 ? "\n" : ""));
	}



	//	 * Returns the files in the given directory 
	//	 * (only normal files, no subdirectories).	  
	//	 * @param dir a directory
	//	 * @return files in the directory
	public static VectorS getFileNames(String folder) {
		return getFileNames(folder, ".*");
	}

	public static VectorS getFilesCheck(String file_patterns) {
		VectorS files = getFilesByPatterns(file_patterns);
		FSystem.checkTrue(files.size()>=0, 
				"No file is matched by pattern=" + file_patterns); 
		return files;
	}
		
	public static VectorS getFilesByPatterns(String file_patterns) {
		VectorS files = new VectorS();
		for (String pattern: file_patterns.split("\\|"))
			files.addAll(getFiles(pattern));
		return files;
	}

	public static VectorS getFiles(String file_pattern) {
		int p = file_pattern.lastIndexOf('/');
		
		if (p >= 0) return getFiles(file_pattern.substring(0, p), file_pattern
				.substring(p + 1), false);
		else return getFiles("./", file_pattern, false);
	}

	// only return normal files, no subdirectories
	public static VectorS getFileNames(String dir, String regex) {
		return getFiles(dir, regex, true);
	}
	
	public static void getFilesRecursive(String dir, String regex, VectorS files) {
		files.addAll(getFiles(dir, regex, false));
		for (String folder: getFolders(dir,".*", true))
			getFilesRecursive(dir +"/"+folder, regex, files);
	}
	
	public static VectorS getFilesRecursive(String dir, String regex) {
		VectorS files= new VectorS();
		getFilesRecursive(dir, regex, files);
		return files;
	}
	
	public static VectorS getFiles(String dir, String regex, boolean names_only) {
		return getFiles(dir, regex, names_only, 0);
	}
	public static VectorS getFolders(String dir, String regex, boolean names_only) {
		return getFiles(dir, regex, names_only, 1);
	}
	public static VectorS getFolderNames(String dir) {
		return getFolders(dir, ".*", true);
	}
	// type: 0=file, 1=folder, -1=any
	public static VectorS getFiles(String dir, String regex, boolean names_only, int type) {
		// TODO (nlao): use java 1.7
		//PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + regex);
		
		VectorS files = new VectorS();
		
		File[] list = new File(dir).listFiles();
		if (list == null) return files;
		for (File file : list) {
			if (type==0)	if (!file.isFile()) continue;
			if (type==1)	if (!file.isDirectory()) continue;
			
			if (!file.getName().matches(regex)) continue;
			
			if (names_only) files.add(file.getName());
			else files.add(file.getAbsolutePath());//.getPath());
		}
		return files;
	}

	
	/**
	 * Returns the subdirectories of the given directory.
	 * 
	 * @param dir a directory
	 * @return subdirectories
	 */
	public static File[] getSubdirs(String dir) {
		File[] filesOrDirs = new File(dir).listFiles();
		ArrayList<File> subdirs = new ArrayList<File>();

		// only return subdirectories, no files
		for (File fileOrDir : filesOrDirs)
			if (!fileOrDir.isFile()) subdirs.add(fileOrDir);

		return subdirs.toArray(new File[subdirs.size()]);
	}

	public static String loadString(String fileName) {
		return loadString(fileName, "UTF-8");
	}

	public static String loadLastLine(String fn) {
		String rlt = null;
		for (String line : FFile.enuLines(fn))
			rlt = line;
		return rlt;
	}

	public static String loadFirstLine(String fn) {
		return loadNthLine(fn, 0);
	}

	public static String loadNthLine(String fn, int n) {
		int i = 0;
		for (String line : FFile.enuLines(fn)) {
			if (i == n) return line;
			++i;
		}
		return null;
	}

	/**
	 * Reads the entire contents of the file with the specified name, using the 
	   * specified encoding.
	 * 
	 * @param fileName the name of the file to read
	 * @param encoding the encoding to use
	 * @return the contents of the file in a String
	 */
	public static String loadString(String fileName, String encoding) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			StringBuffer sb = new StringBuffer();
			for (String nextLine; (nextLine = br.readLine()) != null;) {
				sb.append(nextLine + "\n");
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes the specified String to a file with the specified name, using the
	   * specified encoding.
	 * 
	 * @param data the String to write to the file
	 * @param fileName the name of the file to write to
	 * @param encoding the encoding to use
	 */
	public static void saveString(String fileName, String data, String encoding) {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(data));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), encoding), true);
			for (String nextLine; (nextLine = reader.readLine()) != null;) {
				writer.println(nextLine);
			}
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write file content to a file
	 * @param txt
	 * @param filePath
	 */
	//public static void saveString(String filePath, String txt){
	//saveString(filePath,txt, "UTF-8");	}
	public static void saveStringF(String filePath, String txt, Object... args) {
		saveString(filePath, String.format(txt, args), "UTF-8");
	}
	public static void saveString(String filePath, String txt) {
		saveString(filePath, txt, "UTF-8");
	}

	
//	public static void copy(String in, String out) {
//		try {
//			FileInputStream fis = new FileInputStream(in);
//			FileOutputStream fos = new FileOutputStream(out);
//			byte[] buf = new byte[1024];
//			int i = 0;
//			while ((i = fis.read(buf)) != -1) {
//				fos.write(buf, 0, i);
//			}
//			fis.close();
//			fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void copyFile(String oldPath, String newPath) {
		System.out.println("copy " + oldPath + "\nto " + newPath + "\n");
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					//System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error copying file ");
			e.printStackTrace();
		}
	}

	public static boolean delete(String fn) {
		return (new File(fn)).delete();
	}

	public static void mergeFiles(String folder, String regex, String fn) {
		mergeFiles(folder, FFile.getFileNames(folder, regex), fn);
	}

	public static void mergeFiles(String folder, VectorS vf, String fn) {
		BufferedWriter bw = FFile.newWriter(fn);
		for (String fn1 : vf) {
			//System.out.println("merge file "+fn1);
			//if (!FFile.exist(fn1))				continue;
			FFile.write(bw, FFile.loadString(folder + "/" + fn1));
		}
		FFile.close(bw);
		System.out.println(vf.size() + " files merged");
	}

	public static boolean move(String srcFile, String destPath) {
		File file = new File(srcFile); 	
		return file.renameTo(new File(destPath));//, file.getName()));
	}


	/** Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.*/
	public static boolean deleteDir(String dir1) {
		File dir = new File(dir1);
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(dir + "/" + children[i]);
				if (!success) { return false; }
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

	public static boolean toLower(String file_name) {
		return toLower(file_name, file_name + ".lower");
	}

	public static boolean toLower(String file_name, String file_name1) {
		BufferedWriter bw = FFile.newWriter(file_name1);
		if (bw == null) return false;
		for (String line : FFile.enuLines(file_name))
			FFile.writeln(bw, line.toLowerCase());

		FFile.close(bw);
		return true;
	}

	public static VectorS loadLines(String file_name) {
		return loadLines(file_name, false);
	}

	public static VectorS loadLines(String file_name, boolean bSkipTilte) {
		VectorS lines = new VectorS(); //	vs.load(fn);
		for (String line : FFile.enuLines(file_name, bSkipTilte)) {
			//if (bBreakSharp)				if (line.startsWith("#"))					break;
			lines.add(line);
		}
		return lines;
	}

	public static void removeNonAsc(String fn) {
		removeNonAsc(fn, fn + ".ascii");
	}

	public static void removeNonAsc(String fn, String fnOut) {
		BufferedWriter bw = FFile.newWriter(fnOut);
		for (String line : FFile.enuLines(fn)) {
			line = FString.removeNonAsc(line);
			FFile.writeln(bw, line);
		}
		FFile.close(bw);
	}

	public static SeqS enuACol(String file_pattern, int column) {
		return new SeqS(enuRows(file_pattern), new PipeVSS(column));
	}

	//enumerate lines of a file, splited into columns
	public static SeqVS enuRows(String file_pattern) {
		return enuRows(file_pattern, "\t");
	}
	
	// TODO: don't know how to make it produce VectorS
	public static Seq<VectorX<String>> enuSections(String file_pattern) {
		return FFile.enuLines(file_pattern).splitBy("");
	}	
	
	public static Seq<VectorX<VectorS>> enuGroups(String file_pattern) {
		return enuGroups(file_pattern, "\t", 0);
	}	
	public static Seq<VectorX<VectorS>> enuGroups(String file_pattern, String sep, int column) {
		return FFile.enuRows(file_pattern,sep).groupBy(new PipeVSS(column));
	}		

	public static SeqVS enuRows(String file_pattern, String sep) {
		return enuRows(file_pattern, sep, false);
	}

	public static SeqVS enuRows(String file_pattern, boolean bSkipTitle) {
		return enuRows(file_pattern, "\t", bSkipTitle);
	}

	public static SeqVS enuRows(String file_pattern, String sep,
			boolean bSkipTitle) {
		return new SeqVS(enuLines(file_pattern, bSkipTitle), new PipeSVS(sep));
//		return new SeqTransform<VectorS>(null, enuLines(file_pattern, bSkipTitle),			new PipeSVS(sep));
	}

	public static SeqS enuLines(String file_pattern) {
		return enuLines(file_pattern, false);
	}

	public static SeqS enuLines(String file_pattern, boolean bSkipTitle) {
		return new SeqS(new SeqLine(file_pattern, bSkipTitle));
	}
	
	public static void compareTwoLines(String file) {
		VectorS lines = FFile.loadLines(file);
		SetS set1= new SetS(lines.get(0).split(" "));
		SetS set2= new SetS(lines.get(1).split(" "));
		System.out.println("set1.size=" + set1.size());
		System.out.println("set2.size=" + set2.size());
		System.out.println("(set1&set2).size=" + set1.and(set2).size());
	}
	
	public static void main(String[] args) {
		if (args.length <1) 
			FSystem.die("Expect task name");
		String task = args[0];
		if (task.equals("compareTwoLines"))	compareTwoLines(args[1]);
	}

}
