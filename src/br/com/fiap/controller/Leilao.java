package br.com.fiap.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.JFileChooser;

import br.com.fiap.model.Item;
import br.com.fiap.model.Lance;
import br.com.fiap.view.TelaItem;
import br.com.fiap.view.TelaLance;
import br.com.fiap.view.TelaLeilao;
import br.com.fiap.view.TelaResumo;

public class Leilao {

	private Item item;
	private List<Lance> lances;
	private int duracao;
	private TelaLeilao tl;
	private TelaResumo tr;
	private LocalDateTime inicioLeilao;
	private LocalDateTime fimLeilao;
	private List<String> resumo;
	private DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

	public Leilao() {

	}

	public void iniciarLeilao() {
		//Obter Item
		TelaItem ti = new TelaItem();
		if (ti.obterItem()) {
			item = new Item(ti.getItem(), ti.getValorMinimo());
			duracao = ti.getDuracao() * 60;
			//API: Collections
			lances = new ArrayList<Lance>();
			// Inicial Leilão
			tl = new TelaLeilao(item) {

				@Override
				public void darLanceButtonClick() {
					darLance();
				}

			};
			tl.setDuracao(duracao);
			tl.setVisible(true);

			//API: LocalDateTime
			inicioLeilao = LocalDateTime.now();

			// APIs: Threads e Lambda
			Runnable cronometro = () -> {
				while (duracao > 0) {
					//API: Exceptions
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					duracao--;
					tl.setDuracao(duracao);
				}
				finalizarLeilao();
			};

			Thread thread = new Thread(cronometro);
			thread.start();
		}
	}

	private void finalizarLeilao() {
		System.out.println("Leilão acabou!");
		tl.setVisible(false);
		tl.dispose();
		//API: LocalDateTime
		fimLeilao = LocalDateTime.now();

		resumoLeilao();
	}

	private void resumoLeilao() {
		resumo = new ArrayList<>();
		resumo.add("Leilão de "+item.getDescricao());
		//API: LocalDateTime
		resumo.add("Iniciado em "+inicioLeilao.format(formatador));
		resumo.add("Finalizado em "+fimLeilao.format(formatador));
		long duracaoEfetiva = ChronoUnit.SECONDS.between(inicioLeilao, fimLeilao);
		long duracaoEfetivaSegundos = duracaoEfetiva % 60;
		duracaoEfetiva = duracaoEfetiva / 60;
		long duracaoEfetivaMinutos = duracaoEfetiva % 60;
		long duracaoEfetivaHoras = duracaoEfetiva / 60;
		resumo.add("Duração: "+String.format("%02d:%02d:%02d", duracaoEfetivaHoras, duracaoEfetivaMinutos, duracaoEfetivaSegundos));
		Lance vencedor = getVencedor();
		if (vencedor == null) {
			resumo.add("Não houve lance vencedor!");
		} else {
			resumo.add("Lance vencedor:");
			resumo.add(vencedor.toString());
		}
		resumo.add("");
		resumo.add("Lances válidos:");
		//APIs: Stream e Lambda
		lances.stream().filter(l -> l.getValor() > item.getValorMinimo()).forEach(l -> resumo.add(l.toString()));
		resumo.add("");
		resumo.add("Lances inválidos:");
		//APIs: Stream e Lambda
		lances.stream().filter(l -> l.getValor() <= item.getValorMinimo()).forEach(l -> resumo.add(l.toString()));

		//API: Lambda
		resumo.forEach(System.out::println);
		tr = new TelaResumo(resumo) {

			@Override
			public void salvarResumoButtonClick() {
				salvarResumo();
			}

			@Override
			public void fecharButtonClick() {
				tr.setVisible(false);
				tr.dispose();
			}
		};
		tr.setVisible(true);
	}

	private void darLance() {
		TelaLance telaLance = new TelaLance();
		if (telaLance.efetuarLance()) {
			duracao = (duracao < 20 ? 20: duracao);
			Lance lance = new Lance(telaLance.getCompetidor(), telaLance.getLance());
			addLance(lance);
			System.out.println(lance.toString());

		}
	}

	private void addLance(Lance lance) {
		lances.add(lance);
		tl.addLanceHistorico(lance);
		//API: Collection Comparable
		Collections.sort(lances, Collections.reverseOrder());

		Lance vencedor = getVencedor();
		if (vencedor != null) {
			tl.setVencedor(vencedor);
		}
	}

	private Lance getVencedor() {
		//API: Stream
		Optional<Lance> vencedor = lances.stream().filter(l -> l.getValor() > item.getValorMinimo()).findFirst();
		return (vencedor.isPresent()? vencedor.get() : null);
	}

	private void salvarResumo() {
		System.out.println("Salvar Resumo");
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			//APIs: IO e Exceptions
			try {
				File file = fileChooser.getSelectedFile();
				FileWriter fw = new FileWriter(file);
				PrintWriter out = new PrintWriter(fw);
				resumo.forEach(l -> out.write(l));
				out.flush();
				out.close();				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
