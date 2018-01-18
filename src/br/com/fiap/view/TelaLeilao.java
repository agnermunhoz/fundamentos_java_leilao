package br.com.fiap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import br.com.fiap.model.Lance;

public abstract class TelaLeilao extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JLabel lblCronometro;
	private JTextArea taHistorico;
	private JLabel lblVencedor;

	public TelaLeilao(String itemLeilao) {
		setLayout(new BorderLayout());
		setSize(500, 375);
		setTitle("Leilão");

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		this.getContentPane().add(panelMain, BorderLayout.NORTH);
		
		JPanel panelInfo = new JPanel();
		panelInfo.setSize(500, 200);
		panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelMain.add(panelInfo, BorderLayout.CENTER);
		panelInfo.setLayout(new BorderLayout());
		JLabel lblItem = new JLabel(itemLeilao);
		lblItem.setFont(new Font(Font.SERIF, Font.BOLD, 25));
		lblItem.setHorizontalAlignment(SwingConstants.CENTER);
		panelInfo.add(lblItem, BorderLayout.CENTER);
		lblCronometro = new JLabel("00:00");
		lblCronometro.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
		lblCronometro.setForeground(Color.RED);
		panelInfo.add(lblCronometro, BorderLayout.EAST);
		
		JPanel panelControl = new JPanel();
		panelMain.add(panelControl, BorderLayout.SOUTH);
		panelControl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelControl.setLayout(new BorderLayout());
		JButton buttonLance = new JButton("Dar Lance");
		buttonLance.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//darLance();
				darLanceButtonClick();
			}

		});
		panelControl.add(buttonLance, BorderLayout.EAST);
		lblVencedor = new JLabel();
		lblVencedor.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
		lblVencedor.setForeground(Color.BLUE);
		panelControl.add(lblVencedor, BorderLayout.CENTER);
		
		JPanel panelHistorico = new JPanel();		
		this.getContentPane().add(panelHistorico, BorderLayout.CENTER);
		panelHistorico.setSize(500, 200);
		panelHistorico.setLayout(new BorderLayout());
		panelHistorico.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelHistorico.add(new JLabel("Histórioco de lances"), BorderLayout.NORTH);
		taHistorico = new JTextArea();
		taHistorico.setSize(500, 300);
		panelHistorico.add(taHistorico, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		//setVisible(true);
	}

	/*
	protected void darLance() {
		TelaLance dadosLance = new TelaLance();
		if (dadosLance.efetuarLance()) {
			System.out.println(dadosLance.getCompetidor()+ " " + dadosLance.getLance());
		}
	}
	*/
	public abstract void darLanceButtonClick();
	
	public void setDuracao(int segundos) {
		int minutos = segundos / 60;
		segundos = segundos % 60;
		lblCronometro.setText(String.format("%02d:%02d", minutos, segundos));
	}
	
	public void setVencedor(String vencedor) {
		lblVencedor.setText(vencedor);
	}
	
	public void addLanceHistorico(Lance lance) {
		taHistorico.setText(lance.toString() + "\n" + taHistorico.getText());
	}
	
}
