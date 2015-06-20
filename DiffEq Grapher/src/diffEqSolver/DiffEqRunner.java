package diffEqSolver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.tools.JavaCompiler;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * This class runs the program, positions the components on the main JFrame using a GridBagLayout
 * TODO: add scrolling
 * @author Hank
 *
 */

//Currently can only input as dy/dx = *things* because of processing of both sides
public class DiffEqRunner {

	private static JTextField inputToTextField;
	private static Graph graph;
	private static String equation;
	private static int scale = 5;

	public static void main(String[] args) {
		//Some relics of scrolling in main
		//All JScrollPane is me trying to get scrolling working
		JFrame main = new JFrame();
		main.setLayout(new BorderLayout());
		main.add(getEq(), BorderLayout.NORTH);
		JButton generate = new JButton("Generate");
		graph = new Graph(0, 0, scale);
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(graph);
		scrollPane.setBounds(0, 0, 1435, 720);
		JPanel contentPane = new JPanel(null);
		contentPane.setPreferredSize(new Dimension(500, 400));
		contentPane.add(scrollPane);
		scrollPane.getViewport().setMaximumSize(new Dimension(200,200));
		scrollPane.revalidate();
		//very strange behavior with this line, may be the source of error
		graph.setVisible(false);
		//when its pressed, show the graph
		generate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graph.setVisible(true);
			}

		});
		centerPanel.add(contentPane, BorderLayout.CENTER);
		main.add(generate, BorderLayout.SOUTH);
		main.add(centerPanel, BorderLayout.CENTER);
		main.pack();
		main.setSize(0, 0);
		main.setExtendedState(JFrame.MAXIMIZED_BOTH);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
	}

	public static JPanel getEq() {
		JButton button;
		inputToTextField = new JTextField();
		JPanel inputEq = new JPanel();
		inputEq.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel("dy/dx=");
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		inputEq.add(label, c);

		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		inputEq.add(inputToTextField, c);

		button = new JButton("...");
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				graph.setScale(Integer.parseInt(JOptionPane
						.showInputDialog("Enter an Integer of the number of slopes you wish to generate")));
				graph.revalidate();
				graph.repaint();
			}

		});
		inputEq.add(button);

		button = new JButton("Confirm");
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				equation = inputToTextField.getText();
				graph.setEquation(equation);
				graph.revalidate();
				graph.repaint();
			}
		});
		inputEq.add(button);
		return inputEq;
	}

}