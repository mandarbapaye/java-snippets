import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

// Given a set of words and a search string, return all the words that match the search-string pattern.
// eg. words: "mat", "met", "mine", "no", "nine", "winely", search-string: "m*t"
// should return "mat" and "met".
// NOTE that: * refers to wild card for a single character, so something like . in regex. 

public class PrefixTreeSearch {
	
	private static class Node {
		private Character nodeChar_;
		private boolean endNode_;
		private Node parent_;
		private Map<Character, Node> children_;
		
		public Node(Character nodeChar) {
			this.nodeChar_ = nodeChar;
			this.endNode_ = false;
			children_ = new HashMap<Character, Node>();
		}
		
		public boolean hasChild(Character c) {
			return children_ != null && children_.containsKey(c);
		}
		
		public Node getChild(Character c) {
			return children_.get(c);
		}
		
		public void setParent(Node parent) {
			this.parent_ = parent;
		}
		
		public Node getParent() {
			return this.parent_;
		}
		
		public void markAsEnder() {
			this.endNode_ = true;
		}
		
		public boolean isEndNode() {
			return endNode_;
		}
		
		public Character getValue() {
			return nodeChar_;
		}
		
		public Collection<Node> getChildren() {
			return children_.values();
		}
		
		public Node appendChild(Character c) {
			Node childNode = new Node(c);
			this.children_.put(c, childNode);
			childNode.setParent(this);
			return childNode;
		}

	}
	
	private Node rootNode_;
	
	public PrefixTreeSearch() {
		rootNode_ = new Node('#');
	}
	
	private void createTree(String word) {
		int i = 0;
		Node currentNode = rootNode_;
		while (i < word.length()) {
			char character = word.charAt(i);
			Node charNode = !currentNode.hasChild(character) ? 
							currentNode.appendChild(character) :
							currentNode.getChild(character);
			currentNode = charNode;
			i++;
		}
		
		if (currentNode != rootNode_)
			currentNode.markAsEnder();
	}
	
	private void traverseTree() {
		Queue<Node> traversalQ = new LinkedList<>();
		Node currentNode = rootNode_;
		
		while (currentNode != null) {
			System.out.print(currentNode.getValue());
			System.out.print(currentNode.isEndNode() ? "*," : ",");
			traversalQ.addAll(currentNode.getChildren());
			currentNode = traversalQ.poll();
		}
	}
	
	private List<Node> searchForPattern(Node parent, String searchString, int idx) {
		List<Node> results = new ArrayList<Node>();
		
		char lookupChar = searchString.charAt(idx);
		boolean lastCharacter = idx == searchString.length() - 1;
		
		if (lookupChar == '*') {
			Collection<Node> children = parent.getChildren();
			if (lastCharacter) {
				results.addAll(children);
			} else {
				for (Node child : children) {
					results.addAll(searchForPattern(child, searchString, idx + 1));
				}
			}
		} else {
			if (parent.hasChild(lookupChar)) {
				Node child = parent.getChild(lookupChar);
				if (lastCharacter) {
					if (child.isEndNode())
						results.add(child);
				} else {
					results.addAll(searchForPattern(child, searchString, idx + 1));
				}
			}
		}
		
		return results;
	}
	
	private String getWord(Node node) {
		if (node.parent_ == rootNode_) {
			return String.valueOf(node.getValue());
		}
		
		String parent = getWord(node.parent_);
		return parent + String.valueOf(node.getValue());
	}
	
	
	private void search(String str) {
		if (str != null && !str.isEmpty()) {
			System.out.println("=== Results:");
			List<Node> endNodes = searchForPattern(this.rootNode_, str, 0);
			for (Node endNode : endNodes) {
				System.out.println(getWord(endNode));
			}
		}
	}
	
	public static void main(String[] args) {
		// words list
		String[] words = {"mat", "met", "mine", "no", "nine", "winely"};
		PrefixTreeSearch treeSearch = new PrefixTreeSearch();

		// for each word, populate tree
		for (String word : words) {
			treeSearch.createTree(word);
		}
		treeSearch.traverseTree();

		// search pattern in tree and print	
		treeSearch.search("m*t");
	}

}
