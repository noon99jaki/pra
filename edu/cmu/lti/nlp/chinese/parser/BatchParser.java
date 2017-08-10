/**
 * 
 */
package edu.cmu.lti.nlp.chinese.parser;


/**
 * @author nlao
 *
 */
public class BatchParser {
	/*
	public  MyQueue<Tree> readTestTree() {
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

	private  MyQueue<Tree> constructQueue(List<String[]> wordAndPOS) {
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

	public  String processInput(List<String[]> wordAndPOS) {
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

	


	public static void argErr() {
		System.err.println("Usage: java Parser lang=(en_US|zh_CN)");
		System.exit(-1);//CLang.zh_CN		
	}
	public  void processInput() {
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
	public void run() {
		testFileReader =  new BufferedReader(new InputStreamReader(
				System.in));//, "UTF8"
		//initialize();
		processInput();
		process.destroy();		
	}
	public static void main(String[] args) {
		if (args.length != 1) argErr();

		try {
			Param.overwrite(args);
			MaxEntParser.getInstance().run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	*/
}
