package take_home_test;

import java.util.*;

public class TreeSerializer_Q1 extends Node implements TreeSerializer {

	Node root;

	@Override
	public String serialize(Node root) {
		if (root == null) {
			return null;
		}
		Stack<Node> stack = new Stack<>();
		stack.push(root);
		List<String> list = new ArrayList<>();
		while (!stack.isEmpty()) {
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

	// inorder traversal used for testing constructed tree
	static void inorder(Node root) {
		if (root != null) {
			inorder(root.left);
			System.out.print(root.num + " ");
			inorder(root.right);
		}
	}

	public static void main(String args[]) {
		TreeSerializer_Q1 tree = new TreeSerializer_Q1();
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
		tree.root.left.right.right.num = 14;

		String serialized = tree.serialize(tree.root);
		System.out.println("Serialized view of the tree:");
		System.out.println(serialized);
		System.out.println();
		Node t = tree.deserialize(serialized);

		inorder(t);
	}
}
