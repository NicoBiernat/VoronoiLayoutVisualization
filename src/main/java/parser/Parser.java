package parser;

import java.util.Iterator;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.util.EList;

public class Parser {

	public static ElkNode parse(String file) throws IOException {
		ElkNode graph = ElkGraphUtil.createGraph();
		FileReader datei = new FileReader(file);

		String line = "";

		Scanner sc = new Scanner(datei).useDelimiter("\n");
		while (sc.hasNext()) {
			line = sc.next();
			Scanner statement = new Scanner(line);
			if (statement.hasNext()) {
				String s = statement.next();
				switch (s) {
				case "node":
					ElkNode node = ElkGraphUtil.createNode(graph);
					node.setIdentifier(statement.next());
					break;
				case "edge":
					String nodeName1 = statement.next();
					if (!statement.next().equals("->")) {
						break;
					}
					String nodeName2 = statement.next();
					
					ElkNode node1 = null;
					ElkNode node2 = null;
					
					EList<ElkNode> children = graph.getChildren();
					for (Iterator<ElkNode> i = children.iterator(); i.hasNext();) {
						ElkNode n = i.next();
						String ni = n.getIdentifier();
						if (ni.equals(nodeName1)) {
							node1 = n;
						}else if (ni.equals(nodeName2)) {
							node2 = n;
						}
					}
					if (node1 != null && node2 != null) {
						ElkGraphUtil.createSimpleEdge(node1, node2);
					}
					break;
				default:
					break;
				}
			}

		}
		return graph;
	}
}