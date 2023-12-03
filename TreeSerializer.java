package take_home_test;

public interface TreeSerializer {
	
	String serialize(Node root);
	
	Node deserialize(String str);
}
