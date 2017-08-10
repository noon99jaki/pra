package edu.cmu.lti.nlp.chinese.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;

/**
 * This class read in test files and output new trees with head information marked
 * in a pretty printing format
 * Optionally, this class reads in a gold-standard file(s), and compare them against the test files
 */

public class Parser {

	//private static Logger log = Logger.getLogger( Parser.class );
	//buffered reader to wrap STDIN
	private static BufferedReader testFileReader;

	//need to change this from hard-coded to user input
	private static PrintWriter socketOut;
	private static BufferedReader socketIn;

	private static boolean CH_initialized = false;
	private static ProcessBuilder classifierPB;

	private static Process classifier;
	private static BufferedWriter cInput;
	private static BufferedReader cOutput;

	private static int unaryCount = 0;
	private static final int UNARY_LIMIT = 4;

	private static String maxentLocation;
	private static String parserModelLocation;

	//default language Chinese
	private static int LANG = Tree.CHINESE;

	private static void initialize() {//init block
		if (CH_initialized)
			return;
		try {
			loadProperties();
			classifierPB = new ProcessBuilder(maxentLocation, "-p", "-m",
					parserModelLocation);
			classifier = classifierPB.start();
			cOutput = new BufferedReader(new InputStreamReader(classifier
					.getInputStream()));
			cInput = new BufferedWriter(new OutputStreamWriter(classifier
					.getOutputStream(), "UTF8"));
			classifier.getErrorStream().close();
			CH_initialized = true;
		} catch (Exception ex) {
			System.err
					.println("Error in static initialization of Chinese Parser");
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private static void loadProperties() {
		Properties properties = new Properties();
		try {
			File userProperties = new File(System.getProperty("javelin.home")
					+ "/conf", Parser.class.getName() + ".properties");
			if (!userProperties.exists())
				throw new IOException("Missing properties file for "
						+ Parser.class.getName());
			properties.load(new FileInputStream(userProperties));

			String propertyFileLocation = System
					.getProperty("chineseParser.configuration");
			properties.load(new FileInputStream(propertyFileLocation));
			maxentLocation = ((String) properties.get("maxent")).trim();
			parserModelLocation = ((String) properties.get("parser_model"))
					.trim();
			parserModelLocation = (new File(parserModelLocation))
					.getAbsolutePath();
		} catch (Exception e) {
			System.err.println("Caught exception while loading properties: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public static String classify(List<String> featureList, int queueSize) {
		try {
			String line = null;

			StringBuffer sb = new StringBuffer();
			sb.append("? ");
			for (int i = 0; i < featureList.size(); i++) {
				String feature = featureList.get(i);
				sb.append((i + 1) + "-");
				sb.append(feature);
				sb.append(" ");
			}
			sb.append("\n");
			String str = sb.toString();
			cInput.write(str, 0, str.length());
			cInput.flush();

			line = null;
			while ((line = cOutput.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				StringTokenizer st = new StringTokenizer(line);
				String action = st.nextToken().trim();

				while (true) {
					if (action.startsWith("U:")) {
						if (unaryCount >= UNARY_LIMIT) {
							while (st.hasMoreTokens()
									&& action.startsWith("U:")) {
								action = st.nextToken().trim();
								st.nextToken();
							}
							unaryCount = 0;
							if (action.startsWith("U:"))
								return null;
						} else {
							unaryCount++;
							break;
						}
					} else {
						unaryCount = 0;
						break;
					}
				}

				return action;
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		System.err.println("ERROR: classifier returning null");
		return null;
	}

	/**
	 * For English, use Charniak parser to get the POS and parse tree of the input sentence
	 */
	/*
	 public static Tree parse(List<String> wordList, int lang){
	 if(lang == Tree.CHINESE){
	 List<String> POSList = Tagger.tag(wordList, Tree.CHINESE);
	 return parse(wordList, POSList, lang);
	 }else{
	 System.err.println("English parsing not supported");
	 }
	 }
	 */

	public static Tree parse(List<String> wordList, List<String> POSList,
			int lang) {
		if (wordList.size() != POSList.size()) {
			System.err
					.println("Error in input to parser, wordList.size() != POSList.size()");
			return null;
		}
		if (CH_initialized == false)
			initialize();
		MyQueue<Tree> queue = new MyQueue<Tree>();
		for (int i = 0; i < wordList.size(); i++) {
			String word = wordList.get(i).trim();
			String POS = POSList.get(i).trim();
			if (word.equals("("))
				word = "（";
			else if (word.equals(")"))
				word = "）";
			Tree preterminal = Tree.newPreterminal(POS, word, lang);
			preterminal.sequenceNo = i;
			queue.push(preterminal);
		}
		Tree[] parsedTrees = parse(queue);
		if (parsedTrees != null) {
			if (parsedTrees.length > 1) {
				Tree pTree = null;
				if (lang == Tree.CHINESE)
					pTree = Tree.newNode("IP", parsedTrees);
				else
					pTree = Tree.newNode("S", parsedTrees);
				TreeHelper.markHeadNode(pTree);
				TreeHelper.detransform(pTree);
				return pTree;
			} else {
				TreeHelper.markHeadNode(parsedTrees[0]);
				TreeHelper.detransform(parsedTrees[0]);
				return parsedTrees[0];
			}
		} else {
			System.err.println("Parse Tree is NULL");
			return null;
		}
	}

	public static void main(String[] args) {
		if (args.length > 1) {
			System.err.println("Usage: java Parser [LANG(E|C)]");
			System.exit(-1);
		}

		try {
			//Language options, E for English, C for Chinese
			LANG = Tree.CHINESE;
			if (args.length > 0) {
				if (args[0].equals("E"))
					LANG = Tree.ENGLISH;
				else if (args[0].equals("C"))
					LANG = Tree.CHINESE;
				else {
					System.err.println("Usage: language options: E|C");
					System.exit(-1);
				}
			}

			if (LANG == Tree.CHINESE)
				testFileReader = new BufferedReader(new InputStreamReader(
						System.in, "UTF8"));
			else
				testFileReader = new BufferedReader(new InputStreamReader(
						System.in));

			//initialize parser  
			initialize();

			processInput();

			classifier.destroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static MyQueue<Tree> readTestTree() {
		String line = null;
		try {
			while ((line = testFileReader.readLine()) != null) {
				if (line.startsWith("<S")) {
					List<String> preterminalList = new ArrayList<String>();
					while (true) {
						line = testFileReader.readLine();
						if (line == null) {
							//System.err.println("Error in parseOnly method: Ill-formated input file, <S> tag not closed by </S>");
							System.err
									.println("Error in parseOnly method: Ill-formated input file, <S> tag not closed by </S>");
							//System.exit(1);
							return null;
						}
						if (line.equals("</S>")) {
							break;
						}
						preterminalList.add(line);
					}

					if (preterminalList.size() > 0) {
						MyQueue<Tree> queue = new MyQueue<Tree>();
						for (int i = 0; i < preterminalList.size(); i++) {
							String str = preterminalList.get(i);
							String[] parts = str.split("@X#@X#");
							if (parts.length != 2) {
								//System.err.println("Error in parseOnly: POS@X#@X#Word format not matched: "+str);
								System.err
										.println("Error in parseOnly: POS@X#@X#Word format not matched: "
												+ str);
								//System.exit(1);
							} else {
								if (parts[1].trim().equals("("))
									parts[1] = "（";
								else if (parts[1].trim().equals(")"))
									parts[1] = "）";
								Tree preterminal = Tree.newPreterminal(
										parts[0], parts[1], LANG);
								preterminal.sequenceNo = i;
								queue.push(preterminal);
							}
						}
						return queue;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static MyQueue<Tree> constructQueue(List<String[]> wordAndPOS) {
		MyQueue<Tree> queue = new MyQueue<Tree>();
		for (int i = 0; i < wordAndPOS.size(); i++) {
			String[] parts = wordAndPOS.get(i);
			if (parts.length != 2) {
				//System.err.println("Error in parseOnly: POS@X#@X#Word format not matched: "+str);
				System.err
						.println("Error in parseOnly: Word+POS format not matched");
				//System.exit(1);
			} else {
				if (parts[0].trim().equals("("))
					parts[0] = "（";
				else if (parts[0].trim().equals(")"))
					parts[0] = "）";
				Tree preterminal = Tree
						.newPreterminal(parts[1], parts[0], LANG);
				preterminal.sequenceNo = i;
				queue.push(preterminal);
			}
		}
		return queue;
	}

	public static String processInput(List<String[]> wordAndPOS) {
		MyQueue<Tree> queue = constructQueue(wordAndPOS);
		Tree[] parsedTrees = parse(queue);
		if (parsedTrees == null || parsedTrees.length != 1) {
			//System.err.println("Error in parseOnly: Failed to parse a test tree");
		}
		if (parsedTrees != null) {
			StringWriter outputWriter = new StringWriter();
			if (parsedTrees.length > 1) {
				Tree pTree = null;
				if (LANG == Tree.CHINESE)
					pTree = Tree.newNode("IP", parsedTrees);
				else
					pTree = Tree.newNode("S", parsedTrees);
				TreeHelper.markHeadNode(pTree);
				TreeHelper.detransform(pTree);
				pTree.printBracket(outputWriter);
			} else {
				TreeHelper.markHeadNode(parsedTrees[0]);
				TreeHelper.detransform(parsedTrees[0]);
				parsedTrees[0].printBracket(outputWriter);
			}
			return outputWriter.getBuffer().toString();
		} else {
			//System.err.println("Parse Tree is NULL");
			System.err.println("Parse Tree is NULL");
			return "( )";
		}
	}

	public static void processInput() {
		while (true) {
			MyQueue<Tree> queue = readTestTree();
			if (queue == null)
				break;
			Tree[] parsedTrees = parse(queue);
			if (parsedTrees == null || parsedTrees.length != 1) {
				//System.err.println("Error in parseOnly: Failed to parse a test tree");
			}
			if (parsedTrees != null) {
				if (parsedTrees.length > 1) {
					Tree pTree = null;
					if (LANG == Tree.CHINESE)
						pTree = Tree.newNode("IP", parsedTrees);
					else
						pTree = Tree.newNode("S", parsedTrees);
					TreeHelper.markHeadNode(pTree);
					TreeHelper.detransform(pTree);
					pTree.printBracket();
				} else {
					TreeHelper.markHeadNode(parsedTrees[0]);
					TreeHelper.detransform(parsedTrees[0]);
					parsedTrees[0].printBracket();
				}
			} else {
				//System.err.println("Parse Tree is NULL");
				System.err.println("Parse Tree is NULL");
				System.out.println("( )");
			}
		}
	}

	private static Tree[] parse(MyQueue<Tree> queue) {
		MyStack<Tree> stack = new MyStack<Tree>();
		while (queue.peek() != null || stack.size() > 1) {
			String action = null;
			if (stack.isEmpty()) {
				action = "S";
				Tree queueItem = queue.poll();
				if (queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")) {
					FeatureExtractor.bracketCount++;
				} else if (queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")) {
					FeatureExtractor.bracketCount--;
				}
				stack.push(queueItem);
			} else {
				List<String> feature = FeatureExtractor.extractFeatureList(
						stack, queue);
				action = classify(feature, queue.size());
				if (action == null) {
					break;
				}
				//System.out.println(action);
				if (action.equals("S")) {
					Tree queueItem = queue.poll();
					if (queueItem == null) {
						break;
					}
					if (queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")) {
						FeatureExtractor.bracketCount++;
					} else if (queueItem.getHeadWord()
							.matches("[’”〕〉》」』）］｝〗】]")) {
						FeatureExtractor.bracketCount--;
					}
					stack.push(queueItem);
				} else {
					try {
						String direction = action.substring(0, action
								.indexOf(":"));
						String nonterminal = action.substring(action
								.indexOf(":") + 1, action.length());
						if (direction.equals("U")) {
							if (stack.size() == 0) {
								break;
							}
							Tree top = stack.pop();
							Tree newTree = Tree.newNode(nonterminal,
									new Tree[] { top });
							newTree.setHeadNode(top.getHeadNode());
							newTree.setHeadNodeChildIndex(0);
							stack.push(newTree);
						} else {
							if (stack.size() <= 1) {
								break;
							}
							Tree top = stack.pop();
							Tree secondTop = stack.pop();
							Tree newTree = Tree.newNode(nonterminal,
									new Tree[] { secondTop, top });
							TreeHelper.markHeadNode(newTree);
							stack.push(newTree);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
				}
				FeatureExtractor.lastAction = action;
			}
		}

		if (queue.size() != 0 || stack.size() != 1) {
			Tree[] stackTrees = stack.popAll(new Tree[0]);
			int queueSize = queue.size();
			Tree[] returnTrees = new Tree[stackTrees.length + queueSize];
			int i = 0;
			for (i = 0; i < stackTrees.length; i++) {
				returnTrees[i] = stackTrees[i];
			}
			for (int j = 0; j < queueSize; j++) {
				returnTrees[i + j] = queue.poll();
			}
			return returnTrees;
		}
		return new Tree[] { stack.pop() };
	}

}
