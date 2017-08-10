package edu.cmu.lti.nlp.chinese.parser;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.StringTokenizer;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.Interfaces.ISynxParseTaggedSent;
import edu.cmu.lti.nlp.chinese.parser.MaxEntParserService.IServer;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.util.system.MyProcess;

/**
 * This class read in test files and output new trees with head information
 * marked in a pretty printing format. Optionally, this class reads in a
 * gold-standard file(s), and compare them against the test files
 */
public class MaxEntParser extends MyProcess implements ISynxParseTaggedSent, IServer {
	private static MaxEntParser instance = null;

	public static MaxEntParser getInstance() {
		if (instance == null) instance = new MaxEntParser();
		return instance;
	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String lang;
		private static String maxent;
		private static String model;

		public Param() {
			super(MaxEntParser.class);
			parse();
		}

		public void parse() {
			maxent = getString("maxent");
			model = path_data + "/model";
			// model = getString("model");
			// model = FFile.getCanonicalPath(model);
			// .getAbsolutePath();
			lang = getString("lang","zh_CN");
		}
	}
	public Param p = new Param();
	private BufferedReader testFileReader;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	private boolean CH_initialized = false;
	private int unaryCount = 0;
	private final int UNARY_LIMIT = 4;

	protected MaxEntParser() {
		// p = new Param();
		start(p.maxent, "-p", "-m", p.model);
	}

	protected String classify(List<String> featureList, int queueSize) {// throws
																																			// Exception{
		String line = null;
		try {
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
			out.write(str, 0, str.length());
			out.flush();
			line = null;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) continue;
				StringTokenizer st = new StringTokenizer(line);
				String action = st.nextToken().trim();
				while (true) {
					if (action.startsWith("U:")) {
						if (unaryCount >= UNARY_LIMIT) {
							while (st.hasMoreTokens() && action.startsWith("U:")) {
								action = st.nextToken().trim();
								st.nextToken();
							}
							unaryCount = 0;
							if (action.startsWith("U:")) return null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("ERROR: classifier returning null");
		return null;
	}
	public static int lang = Tree.CHINESE;

	protected Tree parse(MyQueue<Tree> queue) {
		// public Tree parse(TVector<Tree> queue) {
		// TVector<Tree> stack = new TVector<Tree>(Tree.class);
		// stack.ensureCapacity(queue.size());
		MyStack<Tree> stack = new MyStack<Tree>();
		for (int i = 0; i < queue.size() || stack.size() > 1;) {
			String action = null;
			if (stack.isEmpty()) {
				action = "S";
				Tree queueItem = queue.poll();// get(i); ++i;
				if (queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")) {
					FeatureExtractor.bracketCount++;
				} else if (queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")) {
					FeatureExtractor.bracketCount--;
				}
				stack.add(queueItem);
				continue;
			}
			List<String> feature = FeatureExtractor.extractFeatureList(stack, queue);
			action = classify(feature, queue.size());
			if (action == null) {
				break;
			}
			// System.out.println(action);
			if (action.equals("S")) {
				Tree queueItem = queue.poll();
				if (queueItem == null) {
					break;
				}
				if (queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")) {
					FeatureExtractor.bracketCount++;
				} else if (queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")) {
					FeatureExtractor.bracketCount--;
				}
				stack.add(queueItem);
			} else {
				try {
					String direction = action.substring(0, action.indexOf(":"));
					String nonterminal = action.substring(action.indexOf(":") + 1, action.length());
					if (direction.equals("U")) {
						if (stack.size() == 0) {
							break;
						}
						Tree top = stack.pop();
						Tree newTree = Tree.newNode(nonterminal, new Tree[] { top });
						newTree.setHeadNode(top.getHeadNode());
						newTree.setHeadNodeChildIndex(0);
						stack.add(newTree);
					} else {
						if (stack.size() <= 1) {
							break;
						}
						Tree top = stack.pop();
						Tree secondTop = stack.pop();
						Tree newTree = Tree.newNode(nonterminal, new Tree[] { secondTop, top });
						TreeHelper.markHeadNode(newTree);
						stack.add(newTree);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
			}
			FeatureExtractor.lastAction = action;
		}
		Tree[] parsedTrees;
		if (queue.size() != 0 || stack.size() != 1) {
			Tree[] stackTrees = stack.popAll(new Tree[0]);
			int queueSize = queue.size();
			parsedTrees = new Tree[stackTrees.length + queueSize];
			int i = 0;
			for (i = 0; i < stackTrees.length; i++) {
				parsedTrees[i] = stackTrees[i];
			}
			for (int j = 0; j < queueSize; j++) {
				parsedTrees[i + j] = queue.poll();
			}
		} else parsedTrees = new Tree[] { stack.pop() };
		if (parsedTrees != null) {
			if (parsedTrees.length > 1) {
				Tree pTree = null;
				if (lang == Tree.CHINESE) pTree = Tree.newNode("IP", parsedTrees);
				else pTree = Tree.newNode("S", parsedTrees);
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

	public TreeSyntax synxParseTaggedSentRMI(VectorToken vt) throws RemoteException {
		return synxParseTaggedSent(vt);
	}

	/**
	 * eat POS tagged sentence, spit parse tree 2008.4.14 dare not to change Tree
	 * structure this week
	 */
	public TreeSyntax synxParseTaggedSent(VectorToken vt) {
		TreeSyntax pt = new TreeSyntax();
		if (vt.size()==0) 
			return pt;
		// TVector<Tree> queue = new TVector<Tree>(Tree.class);
		// queue.ensureCapacity(vt.size());
		MyQueue<Tree> queue = new MyQueue<Tree>();
		for (int i = 0; i < vt.size(); i++) {
			// if (word.equals("(")) word = "（";
			// else if (word.equals(")")) word = "）";
			Token t = vt.get(i);
			Tree w = Tree.newPreterminal(t.getPOS(), t.getText(), Tree.CHINESE);
			w.sequenceNo = i;
			queue.add(w);
		}
		Tree tree = parse(queue);
		String s = tree.getBracketedFormat();
		System.out.println("MaxEntParser:" + s + "\n");
		pt.parseBracketString(s);
		// pt.v_token = vt;
		for (int i = 0; i < vt.size(); ++i) {
			int id = pt.viNode.get(i);
			Node n = pt.getNode(id);
			// n.t.ms.addOn(vt.get(i).ms);
			n.t.addOn(vt.get(i));
			// pt.v_token.add(n.t);
		}
		// System.out.println(pt.toString());
		return pt;
	}
}
