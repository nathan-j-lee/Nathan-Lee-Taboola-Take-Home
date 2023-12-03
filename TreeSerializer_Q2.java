package take_home_test;

import java.util.*;

public class TreeSerializer_Q2 extends Node implements TreeSerializer {

	Node root;
	static HashSet<Node> set = new HashSet<>();

	@Override
	public String serialize(Node root) {
		if (root == null) {
			return null;
		}
		Stack<Node> stack = new Stack<>();
		stack.push(root);
		List<String> list = new ArrayList<>();
		while (!stack.isEmpty()) {
			if (!set.add(stack.peek())) {
				throw new RuntimeException("Tree is cyclic");
			}
			Node node = stack.pop();
			// if current node is null, store marker
			if (node == null) {
				list.add("#");
			} else {
				// else store current node and recur for its children
				list.add("" + node.num);
				stack.push(node.right);
				stack.push(node.left);
			}
		}
		return String.join(",", list);
	}

	private static int index;

	@Override
	public Node deserialize(String str) {
		if (str == null)
			return null;
		index = 0;
		String[] arr = str.split(",");
		return helper(arr);
	}

	public static Node helper(String[] arr) {
		if (arr[index].equals("#"))
			return null;

		// create node with this item and recur for children
		Node root = new Node();
		root.num = Integer.parseInt(arr[index]);
		index++;
		root.left = helper(arr);
		index++;
		root.right = helper(arr);
		return root;
	}

	// preorder traversal used for testing constructed tree
	static void preorder(Node root) {
		if (root != null) {
			System.out.print(root.num + " ");
			preorder(root.left);
			preorder(root.right);
		}
	}

	public static void main(String args[]) {
		TreeSerializer_Q2 tree = new TreeSerializer_Q2();
		tree.root = new Node();
		tree.root.num = 20;
		tree.root.left = new Node();
		tree.root.left.num = 8;
		tree.root.right = new Node();
		tree.root.right.num = 22;
		tree.root.left.left = new Node();
		tree.root.left.left.num = 4;
		tree.root.left.right = new Node();
		tree.root.left.right.num = 12;
		tree.root.left.right.left = new Node();
		tree.root.left.right.left.num = 10;
		tree.root.left.right.right = new Node();
		tree.root.left.right.right.num = 30;

		String serialized = tree.serialize(tree.root);
		System.out.println("Serialized view of tree1:");
		System.out.println(serialized);
		System.out.println("Deserialized view of tree1:");
		Node t = tree.deserialize(serialized);
		preorder(t);
		set = new HashSet<>();
		System.out.println();
		System.out.println();
		
		TreeSerializer_Q2 tree2 = new TreeSerializer_Q2();
		tree2.root = new Node();
		tree2.root.num = 20;
		tree2.root.left = new Node();
		tree2.root.left.num = 8;
		tree2.root.right = new Node();
		tree2.root.right.num = 22;
		tree2.root.left.left = new Node();
		tree2.root.left.left.num = 4;
		tree2.root.left.right = new Node();
		tree2.root.left.right.num = 12;
		tree2.root.left.right.left = new Node();
		tree2.root.left.right.left.num = 10;
		tree2.root.left.right.right = new Node();
		tree2.root.left.right.right.num = 12;
		String serialized2 = tree2.serialize(tree2.root);
		System.out.println("Serialized view of tree2:");
		System.out.println(serialized2);
		
		System.out.println("Deserialized view of tree2:");
		Node t2 = tree2.deserialize(serialized2);
		preorder(t2);
	}
}