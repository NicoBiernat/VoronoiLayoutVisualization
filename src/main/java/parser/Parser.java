package parser;

import java.util.Random;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.emf.common.util.EList;

public class Parser {

    public static ElkNode parse(String file) throws IOException {
        Random rnd = new Random();
        ElkNode graph = ElkGraphUtil.createGraph();
        graph.setIdentifier("root");
        FileReader datei = new FileReader(file);

        String line = "";

        Scanner sc = new Scanner(datei).useDelimiter("\n");
        while (sc.hasNext()) {
            line = sc.next();
            Scanner statement = new Scanner(line);
            if (line.matches("^\\s*node\\s+(\\S+)\\s*$|^\\s*edge\\s+(\\S+)\\s*->\\s*(\\S+)\\s*$")) {
                String s = statement.next();
                switch (s) {
                    case "node":
                        ElkNode node = ElkGraphUtil.createNode(graph);
                        node.setIdentifier(statement.next());
                        node.setX(rnd.nextInt(100) + 30);
                        node.setY(rnd.nextInt(100) + 30);
                        break;
                    case "edge":
                        String nodeName1 = statement.next();
                        statement.next();
                        String nodeName2 = statement.next();

                        ElkNode node1 = null;
                        ElkNode node2 = null;

                        EList<ElkNode> children = graph.getChildren();
                        for (ElkNode n : children) {
                            String ni = n.getIdentifier();
                            if (ni.equals(nodeName1)) {
                                node1 = n;
                            } else if (ni.equals(nodeName2)) {
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