package take_home_test;

import java.util.*;

public class JSON_Parser {

	public static void main(String[] args) {
		System.out.println("JSON Parser");
		String input = "{\n" + "\"debug\" : \"on\",\n" + "\"window\" : {\n" + "\"title\" : \"sample\",\n"
				+ "\"size\": -500.123e+10\n" + "}\n" + "\"colors\": [\"red\", \"green\", \"blue\"],\n"
				+ "\"numbers\": [\"foo\", \"bar\", [4, 5]],\n" + "\"emptyArray\": []\n" + "}";
		try {
			for (Object str : JSON_Parser.lex(input)) {
				System.out.print(str);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		try {
			Map<String, Object> output = parse(input);
			printMap(output);
			System.out.println("map test1: " + ((Map<String, Object>) (output.get("window"))).get("size"));
			System.out.println("map test2: " + ((Map<String, Object>) (output.get("window"))).get("title"));
			System.out.println("arr test1: " + ((List<Object>) output.get("colors")).get(0));
			System.out.println("arr test2: " + ((List<Object>) output.get("numbers")).get(2));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Map<String, Object> outer = new HashMap<>(); Map<String, Object> inner = new
		 * HashMap<>(); outer.put("test", inner); System.out.println(outer);
		 */

	}

	public static Map<String, Object> parse(String json) throws Exception {
		List<Object> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		try {
			list = lex(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Stack<Object> braceStack = new Stack<>();
		Stack<Map<String, Object>> objectStack = new Stack<>();
		Stack<Object> keyStack = new Stack<>();
		objectStack.push(map);
		int index = 0;
		braceStack.push(list.get(0));
		Map<String, Object> currentMap = map;
		List<Object> arr = new ArrayList<>();
		List<Object> nested = new ArrayList<>();
		boolean isNested = false;
		boolean keyIndex = true;
		int arrayOrObject = 1; // 1 = object, 2 = array
		String key = null;
		// when stack is empty, the json object has been parsed
		while (!braceStack.isEmpty()) {
			Object current = list.get(index);
			// begin new object
			if (current.equals("{")) {
				braceStack.push(current);
				objectStack.push(currentMap);
				keyStack.push(key);
				keyIndex = true;
				Map<String, Object> tempMap = new HashMap<>();
				currentMap = tempMap;
				arrayOrObject = 1;
				index++;
				continue;
			}
			// begin new array
			else if (current.equals("[")) {
				braceStack.push(current);
				objectStack.push(currentMap);
				keyStack.push(key);
				keyIndex = false;
				if (arrayOrObject == 2) {
					nested = new ArrayList<>();
					isNested = true;
				} else {
					arr = new ArrayList<>();
				}
				arrayOrObject = 2;
				index++;
				continue;
			}

			else if (current.equals("}")) {
				Object popped = braceStack.pop();
				if (!popped.equals("{")) {
					throw new Exception("Invalid Object notation in json string. Expected }, but got " + popped);
				}
				if (index >= (list.size() - 1)) {
					continue;
				}
				key = (String) keyStack.pop();
				Map<String, Object> tempMap = currentMap;
				currentMap = objectStack.pop();
				currentMap.put(key, tempMap);
				index++;
				continue;
			} else if (current.equals("]")) {
				Object popped = braceStack.pop();
				if (!popped.equals("[")) {
					throw new Exception("Invalid Array notation in json string. Expected ], but got " + popped);
				}
				if (isNested) {
					arr.add(nested);
					isNested = false;
				} else {
					key = (String) keyStack.pop();
					currentMap = objectStack.pop();
					currentMap.put(key, arr);
					keyIndex = true;
					arrayOrObject = 1;
				}
				index++;
				continue;
			}
			// deal with values that are not objects and arrays
			else if (current.equals(":") || current.equals(",")) {
				index++;
				continue;
			} /*
				 * else if (list.get(index + 1).equals("}")) { Object popped = braceStack.pop();
				 * if (!popped.equals("{")) { throw new
				 * Exception("Invalid Object notation in json string. Expected }, but got " +
				 * popped); } } else if (list.get(index + 1).equals("]")) { Object popped =
				 * braceStack.pop(); if (!popped.equals("[")) { throw new
				 * Exception("Invalid Object notation in json string. Expected ], but got " +
				 * popped); } }
				 */

			if (keyIndex && current instanceof String) {
				keyIndex = false;
				key = (String) current;
				currentMap.put(key, "placeholder");
			} else if (arrayOrObject == 1) {
				currentMap.put(key, current);
				keyIndex = true;
			} else if (arrayOrObject == 2) {
				if (isNested) {
					nested.add(current);
				} else {
					arr.add(current);
				}
			}
			index++;
		}
		map = currentMap;
		return map;

	}

	public static List<Object> lex(String str) throws Exception {
		List<Object> tokens = new ArrayList<>();
		for (int i = 0; i < str.length(); i++) {
			String curr = String.valueOf(str.charAt(i));

			// Store special symbols
			if (curr.equals("[") || curr.equals("{") || curr.equals(":") || curr.equals(",") || curr.equals("}")
					|| curr.equals("]")) {
				tokens.add(curr);
			}

			// String check
			// TODO: Check for backslash characters
			else if (curr.equals("\"")) {
				int j = i + 1;
				String next = String.valueOf(str.charAt(j));
				// Check for next end quote or end of whole string itself
				while (!next.equals("\"")) {
					if (j >= str.length()) {
						throw new Exception("Invalid string");
					}
					j++;
					next = String.valueOf(str.charAt(j));
				}

				// end quote found at j, check if string is true, false, or null, otherwise add
				// to list as string
				if (str.substring(i, j + 1).equals("true")) {
					tokens.add(true);
				} else if (str.substring(i, j + 1).equals("false")) {
					tokens.add(false);
				} else if (str.substring(i, j + 1).equals("null")) {
					tokens.add(null);
				} else
					tokens.add(str.substring(i + 1, j));
				i = j;
			}

			// Number check
			// TODO: Check for negative
			// TODO: Check if decimal
			// TODO: Check if using scientific notation
			else if (isNum(curr) || curr.equals("-")) {
				int j = i + 1;
				boolean isDecimal = false;
				boolean isExponent = false;
				String next = String.valueOf(str.charAt(j));

				// Check if number is one digit
				if (!isNum(next)) {
					tokens.add(curr);
				}

				else {
					while (isNum(next)) {
						if (j >= str.length()) {
							throw new Exception("Invalid number");
						}

						j++;
						next = String.valueOf(str.charAt(j));
						if (next.equals(".")) {
							j++;
							next = String.valueOf(str.charAt(j));
							isDecimal = true;
						}
						if (next.equals("E") || next.equals("e")) {
							j++;
							next = String.valueOf(str.charAt(j));
							if (!(next.equals("-") || next.equals("+"))) {
								throw new Exception(
										"Incorrect scientific notation format. Expected + or -, but got: " + next);
							}
							j++;
							next = String.valueOf(str.charAt(j));
							isExponent = true;
						}
					}
					if (isExponent) {
						tokens.add(str.substring(i, j));
					} else if (isDecimal) {
						System.out.println(str.substring(i, j));
						tokens.add(Float.valueOf(str.substring(i, j)));
					} else {
						tokens.add(Integer.valueOf(str.substring(i, j)));
					}
					i = j;
				}
			}

			// Ignore all kinds of whitespace
			else if (curr.equals(" ") || curr.equals("\n") || curr.equals("\r") || curr.equals("\t"))
				continue;

			// Throw error for everything else for now
			else {
				throw new Exception("Unexpected character: " + curr);
			}
		}
		return tokens;

	}

	public static boolean isNum(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	public static void printMap(Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			System.out.println(key + ": " + value);
		}
	}

}