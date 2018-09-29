package br.com.caelum.ingresso.model.desconto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.ingresso.model.Filme;
import br.com.caelum.ingresso.model.Ingresso;
import br.com.caelum.ingresso.model.Sala;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.descontos.SemDesconto;

public class DescontoTest {
	@Test
	public void naoDeveConcederDescontoParaIngressoComum(){
		Sala sala = new Sala("Sala 3", new BigDecimal("15.00"));
		Filme filme = new Filme("It", Duration.ofMinutes(150), "Terror", new BigDecimal("15.00"));
		
		Sessao sessao = new Sessao(LocalTime.parse("18:00:00"), filme, sala);
		Ingresso ingresso = new Ingresso(sessao, new SemDesconto());
		
		BigDecimal precoEsperado = new BigDecimal("30.00");
		
		Assert.assertEquals(precoEsperado, ingresso.getPreco());
		
	}
}
