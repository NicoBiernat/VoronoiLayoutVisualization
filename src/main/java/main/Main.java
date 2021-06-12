package main;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import algorithm.LloydRelaxation;
import view.MainView;

import java.io.IOException;
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try {
		new MainView();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
